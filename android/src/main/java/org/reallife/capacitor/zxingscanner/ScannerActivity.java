package org.reallife.capacitor.zxingscanner;

import android.content.Intent;
import android.graphics.ImageFormat;
import android.os.Bundle;
import android.util.Size;
import android.view.KeyEvent;
import android.widget.FrameLayout;

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

    private ExecutorService cameraExecutor;
    private volatile boolean resultDelivered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreviewView previewView = new PreviewView(this);
        previewView.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));
        setContentView(previewView);

        cameraExecutor = Executors.newSingleThreadExecutor();
        startCamera(previewView);
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

        ByteBuffer buffer = imageProxy.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        int width = imageProxy.getWidth();
        int height = imageProxy.getHeight();

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
}
