import { registerPlugin } from '@capacitor/core';
import { CapacitorBarcodeScannerWeb } from './web';
export * from './definitions';
const CapacitorBarcodeScanner = registerPlugin('CapacitorBarcodeScanner', {
    web: () => new CapacitorBarcodeScannerWeb(),
});
export { CapacitorBarcodeScanner };
