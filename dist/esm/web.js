import { WebPlugin } from '@capacitor/core';
export class CapacitorBarcodeScannerWeb extends WebPlugin {
    async scanBarcode(_options) {
        throw this.unimplemented('ZXing scanner is not available on web. Use html5-qrcode or another web-based QR scanner as a fallback.');
    }
}
