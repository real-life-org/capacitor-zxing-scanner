# Capacitor ZXing Scanner

An F-Droid compatible, drop-in replacement for the official `@capacitor/barcode-scanner` plugin.

## Why this plugin?

The official `@capacitor/barcode-scanner` relies on Google's ML Kit for Android. While performant, ML Kit is a proprietary Google Mobile Service (GMS) and is **not compatible with F-Droid** or "de-Googled" Android devices.

This plugin solves that problem by using the open-source **ZXing** library on Android and native **AVFoundation** on iOS, ensuring your app can be distributed freely without relying on proprietary blob dependencies.

## Features

*   **F-Droid Compatible:** Uses ZXing on Android (no Google Play Services required).
*   **Drop-in Replacement:** Exports the same API and names as `@capacitor/barcode-scanner`, allowing you to migrate by simply changing the import path.
*   **Native Performance:** Utilizes CameraX on Android and AVFoundation on iOS.
*   *Note: Web scanning is not supported. Please use a fallback like `html5-qrcode`.*

## Installation

```bash
npm install @real-life-org/capacitor-zxing-scanner
npx cap sync
```

## Usage

You can use the exported `CapacitorBarcodeScanner` exactly as you would the official plugin:

```typescript
import { CapacitorBarcodeScanner } from '@real-life-org/capacitor-zxing-scanner';

const scanResult = await CapacitorBarcodeScanner.scanBarcode({
  hint: 0 // Optional hint for barcode type
});

console.log('Scanned text:', scanResult.ScanResult);
```

## Limitations

*   **Currently hardcoded to QR Codes:** While the API defines various formats, the underlying native implementations currently only scan for QR Codes.
*   **Web Fallback:** No built-in web implementation.

## Development

```bash
# Install dependencies
npm install

# Build the plugin
npm run build

# Lint the code
npm run lint

# Format the code
npm run fmt
```

## License

MIT
