# AI Agent Instructions

This repository is a Capacitor plugin for barcode and QR code scanning. It is designed to be an F-Droid compatible replacement for the official `@capacitor/barcode-scanner`, utilizing ZXing on Android instead of Google ML Kit.

## Key Technical Details

*   **Plugin Name:** `@real-life-org/capacitor-zxing-scanner`
*   **Android Implementation:** Uses `CameraX` for the camera preview and `ZXing` (`com.google.zxing`) for barcode decoding. The entry point is `ZxingScannerPlugin.java`, which launches `ScannerActivity.java`.
*   **iOS Implementation:** Uses native `AVFoundation` (`AVCaptureMetadataOutput`). The UI is managed in `ScannerViewController.swift`.
*   **Web Implementation:** Not implemented. Throws an `unimplemented` error.
*   **API:** Defined in `src/definitions.ts`. The primary method is `scanBarcode(options)`. It aims for API compatibility with `@capacitor/barcode-scanner`.

## Current State & Known Limitations

*   While the API definitions (`CapacitorBarcodeScannerTypeHint`) list many barcode formats (EAN, UPC, Code 128, etc.), the native implementations are currently hardcoded to focus on **QR Codes**.
    *   **Android (`ScannerActivity.java`):** `hints.put(DecodeHintType.POSSIBLE_FORMATS, Collections.singletonList(BarcodeFormat.QR_CODE));`
    *   **iOS (`ScannerViewController.swift`):** `metadataOutput.metadataObjectTypes = [.qr]`
*   The plugin handles camera permissions automatically on Android.

## Development Workflows

*   **Build:** `npm run build` (compiles TypeScript and bundles via Rollup).
*   **Lint/Format:** `npm run lint` and `npm run fmt`.
*   **Documentation:** `npm run docgen` generates API documentation.

When working on this repository, prioritize maintaining the F-Droid compatibility (no proprietary Google Mobile Services dependencies) and ensure any changes to the scanning logic are reflected in both the Android and iOS implementations if applicable.
