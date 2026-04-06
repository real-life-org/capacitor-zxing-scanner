/**
 * Compatible replacement for @capacitor/barcode-scanner.
 * Exports the same names so existing code can change only the import path.
 */
export declare enum CapacitorBarcodeScannerTypeHint {
    QR_CODE = 0,
    AZTEC = 1,
    CODABAR = 2,
    CODE_39 = 3,
    CODE_93 = 4,
    CODE_128 = 5,
    DATA_MATRIX = 6,
    EAN_8 = 7,
    EAN_13 = 8,
    ITF = 9,
    MAXICODE = 10,
    PDF_417 = 11,
    RSS_14 = 12,
    RSS_EXPANDED = 13,
    UPC_A = 14,
    UPC_E = 15,
    UPC_EAN_EXTENSION = 16,
    ALL = 17
}
export declare enum CapacitorBarcodeScannerCameraDirection {
    BACK = 1,
    FRONT = 2
}
export declare enum CapacitorBarcodeScannerAndroidScanningLibrary {
    /** Uses ZXing (Apache 2.0, F-Droid compatible). */
    ZXING = "zxing",
    /** Alias for ZXING — MLKIT is not available in this plugin. Falls back silently. */
    MLKIT = "mlkit"
}
export interface CapacitorBarcodeScannerAndroidOptions {
    scanningLibrary?: CapacitorBarcodeScannerAndroidScanningLibrary;
}
export interface CapacitorBarcodeScannerOptions {
    hint: CapacitorBarcodeScannerTypeHint;
    cameraDirection?: CapacitorBarcodeScannerCameraDirection;
    scanInstructions?: string;
    scanButton?: boolean;
    scanText?: string;
    android?: CapacitorBarcodeScannerAndroidOptions;
}
export interface CapacitorBarcodeScannerScanResult {
    ScanResult: string;
}
export interface CapacitorBarcodeScannerPlugin {
    scanBarcode(options: CapacitorBarcodeScannerOptions): Promise<CapacitorBarcodeScannerScanResult>;
}
export declare const CapacitorBarcodeScanner: CapacitorBarcodeScannerPlugin;
