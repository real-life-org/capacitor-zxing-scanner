import { WebPlugin } from '@capacitor/core';
import type { CapacitorBarcodeScannerOptions, CapacitorBarcodeScannerPlugin, CapacitorBarcodeScannerScanResult } from './definitions';
export declare class CapacitorBarcodeScannerWeb extends WebPlugin implements CapacitorBarcodeScannerPlugin {
    scanBarcode(_options: CapacitorBarcodeScannerOptions): Promise<CapacitorBarcodeScannerScanResult>;
}
