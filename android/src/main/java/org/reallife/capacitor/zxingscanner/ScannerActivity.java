package org.reallife.capacitor.zxingscanner;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Size;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScannerActivity extends AppCompatActivity {

    public static final String EXTRA_SCAN_RESULT = "SCAN_RESULT";
    public static final String EXTRA_SCAN_INSTRUCTIONS = "SCAN_INSTRUCTIONS";

    private ExecutorService cameraExecutor;
    private volatile boolean resultDelivered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout root = new FrameLayout(this);
        root.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));

        // Camera preview
        PreviewView previewView = new PreviewView(this);
        previewView.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));
        root.addView(previewView);

        // Scan overlay (darkened background with transparent scan box + corner brackets)
        ScanOverlayView overlay = new ScanOverlayView(this);
        overlay.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));
        root.addView(overlay);

        // Instruction text above scan box
        String instructions = getIntent().getStringExtra(EXTRA_SCAN_INSTRUCTIONS);
        if (instructions == null || instructions.isEmpty()) {
            instructions = "Code scannen";
        }
        TextView instructionText = new TextView(this);
        instructionText.setText(instructions);
        instructionText.setTextColor(Color.WHITE);
        instructionText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        instructionText.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams textParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        textParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        textParams.topMargin = (int) (getResources().getDisplayMetrics().heightPixels * 0.25f);
        instructionText.setLayoutParams(textParams);
        root.addView(instructionText);

        // Close button (top right)
        ImageView closeButton = new ImageView(this);
        closeButton.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        closeButton.setColorFilter(Color.WHITE);
        closeButton.setPadding(dp(12), dp(12), dp(12), dp(12));
        closeButton.setBackground(createCircleBackground());
        FrameLayout.LayoutParams closeParams = new FrameLayout.LayoutParams(dp(48), dp(48));
        closeParams.gravity = Gravity.TOP | Gravity.END;
        closeParams.topMargin = dp(48);
        closeParams.rightMargin = dp(24);
        closeButton.setLayoutParams(closeParams);
        closeButton.setOnClickListener(v -> deliverCancel());
        root.addView(closeButton);

        setContentView(root);

        cameraExecutor = Executors.newSingleThreadExecutor();
        startCamera(previewView);
    }

    private android.graphics.drawable.GradientDrawable createCircleBackground() {
        android.graphics.drawable.GradientDrawable drawable = new android.graphics.drawable.GradientDrawable();
        drawable.setShape(android.graphics.drawable.GradientDrawable.OVAL);
        drawable.setColor(Color.argb(100, 0, 0, 0));
        return drawable;
    }

    private int dp(int value) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics()
        );
    }

    private void startCamera(PreviewView previewView) {
        ListenableFuture<ProcessCameraProvider> future = ProcessCameraProvider.getInstance(this);
        future.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = future.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                        .build();

                imageAnalysis.setAnalyzer(cameraExecutor, this::processImage);

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(
                        this,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageAnalysis
                );
            } catch (ExecutionException | InterruptedException e) {
                deliverCancel();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void processImage(@NonNull ImageProxy imageProxy) {
        if (resultDelivered) {
            imageProxy.close();
            return;
        }

        int width = imageProxy.getWidth();
        int height = imageProxy.getHeight();
        byte[] bytes = extractLuminance(imageProxy.getPlanes()[0], width, height);

        PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(
                bytes, width, height, 0, 0, width, height, false
        );
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
        hints.put(DecodeHintType.POSSIBLE_FORMATS, Collections.singletonList(BarcodeFormat.QR_CODE));
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);

        try {
            Result result = new MultiFormatReader().decode(bitmap, hints);
            deliverResult(result.getText());
        } catch (NotFoundException e) {
            // No barcode in this frame — continue scanning
        } finally {
            imageProxy.close();
        }
    }

    /**
     * Copies the Y plane into a tightly packed width*height array.
     *
     * PlanarYUVLuminanceSource assumes packed rows (row length == width). Many
     * camera HALs (notably Huawei) pad each row (rowStride > width) and some
     * use pixelStride > 1 — feeding the raw buffer then shears every row and
     * ZXing can never decode. This copy honours both strides.
     */
    private static byte[] extractLuminance(ImageProxy.PlaneProxy yPlane, int width, int height) {
        ByteBuffer buffer = yPlane.getBuffer();
        int rowStride = yPlane.getRowStride();
        int pixelStride = yPlane.getPixelStride();

        if (pixelStride == 1 && rowStride == width && buffer.remaining() >= width * height) {
            byte[] packed = new byte[width * height];
            buffer.get(packed, 0, width * height);
            return packed;
        }

        byte[] packed = new byte[width * height];
        byte[] row = new byte[rowStride];
        for (int y = 0; y < height; y++) {
            buffer.position(y * rowStride);
            // Die letzte Zeile kann kuerzer als rowStride sein (kein Padding).
            int rowLength = Math.min(rowStride, buffer.remaining());
            buffer.get(row, 0, rowLength);
            if (pixelStride == 1) {
                System.arraycopy(row, 0, packed, y * width, width);
            } else {
                for (int x = 0; x < width; x++) {
                    packed[y * width + x] = row[x * pixelStride];
                }
            }
        }
        return packed;
    }

    private void deliverResult(String text) {
        if (resultDelivered) return;
        resultDelivered = true;
        Intent intent = new Intent();
        intent.putExtra(EXTRA_SCAN_RESULT, text);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void deliverCancel() {
        if (resultDelivered) return;
        resultDelivered = true;
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            deliverCancel();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
    }

    /**
     * Custom overlay view: semi-transparent background with a clear scan box
     * and white corner brackets.
     */
    private static class ScanOverlayView extends View {
        private final Paint dimPaint = new Paint();
        private final Paint clearPaint = new Paint();
        private final Paint bracketPaint = new Paint();
        private final RectF scanBox = new RectF();

        public ScanOverlayView(android.content.Context context) {
            super(context);
            setLayerType(LAYER_TYPE_SOFTWARE, null);

            dimPaint.setColor(Color.argb(140, 0, 0, 0));
            dimPaint.setStyle(Paint.Style.FILL);

            clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

            bracketPaint.setColor(Color.WHITE);
            bracketPaint.setStyle(Paint.Style.STROKE);
            bracketPaint.setStrokeWidth(4f);
            bracketPaint.setAntiAlias(true);
            bracketPaint.setPathEffect(new CornerPathEffect(8f));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            int w = getWidth();
            int h = getHeight();
            float boxSize = w * 0.7f;
            float left = (w - boxSize) / 2f;
            float top = h * 0.32f;
            scanBox.set(left, top, left + boxSize, top + boxSize);

            // Draw semi-transparent overlay
            canvas.drawRect(0, 0, w, h, dimPaint);

            // Cut out scan box (transparent)
            canvas.drawRoundRect(scanBox, 16f, 16f, clearPaint);

            // Draw corner brackets
            float bracketLen = boxSize * 0.12f;
            drawCornerBrackets(canvas, scanBox, bracketLen);
        }

        private void drawCornerBrackets(Canvas canvas, RectF box, float len) {
            Path path = new Path();

            // Top-left
            path.moveTo(box.left, box.top + len);
            path.lineTo(box.left, box.top);
            path.lineTo(box.left + len, box.top);

            // Top-right
            path.moveTo(box.right - len, box.top);
            path.lineTo(box.right, box.top);
            path.lineTo(box.right, box.top + len);

            // Bottom-right
            path.moveTo(box.right, box.bottom - len);
            path.lineTo(box.right, box.bottom);
            path.lineTo(box.right - len, box.bottom);

            // Bottom-left
            path.moveTo(box.left + len, box.bottom);
            path.lineTo(box.left, box.bottom);
            path.lineTo(box.left, box.bottom - len);

            canvas.drawPath(path, bracketPaint);
        }
    }
}
