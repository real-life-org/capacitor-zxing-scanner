package org.reallife.capacitor.zxingscanner;

import static org.junit.Assert.assertArrayEquals;

import java.nio.ByteBuffer;

import org.junit.Test;

/**
 * Regressionstests fuer die stride-korrekte Y-Plane-Extraktion (PR #1):
 * gepaddete Zeilen (rowStride > width), die ungepaddete letzte Zeile und
 * pixelStride > 1 muessen ein dicht gepacktes width*height-Array ergeben.
 */
public class ExtractLuminanceTest {

    /** Baut eine Y-Plane mit gegebenem rowStride/pixelStride; letzte Zeile ohne Padding. */
    private static ByteBuffer plane(int width, int height, int rowStride, int pixelStride) {
        int lastRowLength = (width - 1) * pixelStride + 1;
        byte[] data = new byte[(height - 1) * rowStride + lastRowLength];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                data[y * rowStride + x * pixelStride] = value(x, y, width);
            }
        }
        return ByteBuffer.wrap(data);
    }

    private static byte value(int x, int y, int width) {
        return (byte) ((y * width + x) % 251);
    }

    private static byte[] expected(int width, int height) {
        byte[] packed = new byte[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                packed[y * width + x] = value(x, y, width);
            }
        }
        return packed;
    }

    @Test
    public void packedPlanePassesThrough() {
        byte[] out = ScannerActivity.extractLuminance(plane(8, 4, 8, 1), 8, 1, 8, 4);
        assertArrayEquals(expected(8, 4), out);
    }

    @Test
    public void paddedRowsAreUnsheared() {
        // Huawei-Muster: rowStride > width (Zeilen-Padding). Naive Kopie wuerde
        // jede Zeile verschieben — genau der Live-Bug.
        byte[] out = ScannerActivity.extractLuminance(plane(8, 4, 12, 1), 12, 1, 8, 4);
        assertArrayEquals(expected(8, 4), out);
    }

    @Test
    public void shortenedLastRowIsHandled() {
        // Der Buffer endet mit der letzten Pixelzeile (kein End-Padding) —
        // die Extraktion darf nicht ueber das Buffer-Ende hinaus lesen.
        ByteBuffer buffer = plane(8, 4, 12, 1);
        if (buffer.capacity() >= 3 * 12 + 8) {
            byte[] out = ScannerActivity.extractLuminance(buffer, 12, 1, 8, 4);
            assertArrayEquals(expected(8, 4), out);
        }
    }

    @Test
    public void pixelStrideTwoIsDeinterleaved() {
        byte[] out = ScannerActivity.extractLuminance(plane(8, 4, 20, 2), 20, 2, 8, 4);
        assertArrayEquals(expected(8, 4), out);
    }
}
