/**
 * Compatible replacement for @capacitor/barcode-scanner.
 * Exports the same names so existing code can change only the import path.
 */
export var CapacitorBarcodeScannerTypeHint;
(function (CapacitorBarcodeScannerTypeHint) {
    CapacitorBarcodeScannerTypeHint[CapacitorBarcodeScannerTypeHint["QR_CODE"] = 0] = "QR_CODE";
    CapacitorBarcodeScannerTypeHint[CapacitorBarcodeScannerTypeHint["AZTEC"] = 1] = "AZTEC";
    CapacitorBarcodeScannerTypeHint[CapacitorBarcodeScannerTypeHint["CODABAR"] = 2] = "CODABAR";
    CapacitorBarcodeScannerTypeHint[CapacitorBarcodeScannerTypeHint["CODE_39"] = 3] = "CODE_39";
    CapacitorBarcodeScannerTypeHint[CapacitorBarcodeScannerTypeHint["CODE_93"] = 4] = "CODE_93";
    CapacitorBarcodeScannerTypeHint[CapacitorBarcodeScannerTypeHint["CODE_128"] = 5] = "CODE_128";
    CapacitorBarcodeScannerTypeHint[CapacitorBarcodeScannerTypeHint["DATA_MATRIX"] = 6] = "DATA_MATRIX";
    CapacitorBarcodeScannerTypeHint[CapacitorBarcodeScannerTypeHint["EAN_8"] = 7] = "EAN_8";
    CapacitorBarcodeScannerTypeHint[CapacitorBarcodeScannerTypeHint["EAN_13"] = 8] = "EAN_13";
    CapacitorBarcodeScannerTypeHint[CapacitorBarcodeScannerTypeHint["ITF"] = 9] = "ITF";
    CapacitorBarcodeScannerTypeHint[CapacitorBarcodeScannerTypeHint["MAXICODE"] = 10] = "MAXICODE";
    CapacitorBarcodeScannerTypeHint[CapacitorBarcodeScannerTypeHint["PDF_417"] = 11] = "PDF_417";
    CapacitorBarcodeScannerTypeHint[CapacitorBarcodeScannerTypeHint["RSS_14"] = 12] = "RSS_14";
    CapacitorBarcodeScannerTypeHint[CapacitorBarcodeScannerTypeHint["RSS_EXPANDED"] = 13] = "RSS_EXPANDED";
    CapacitorBarcodeScannerTypeHint[CapacitorBarcodeScannerTypeHint["UPC_A"] = 14] = "UPC_A";
    CapacitorBarcodeScannerTypeHint[CapacitorBarcodeScannerTypeHint["UPC_E"] = 15] = "UPC_E";
    CapacitorBarcodeScannerTypeHint[CapacitorBarcodeScannerTypeHint["UPC_EAN_EXTENSION"] = 16] = "UPC_EAN_EXTENSION";
    CapacitorBarcodeScannerTypeHint[CapacitorBarcodeScannerTypeHint["ALL"] = 17] = "ALL";
})(CapacitorBarcodeScannerTypeHint || (CapacitorBarcodeScannerTypeHint = {}));
export var CapacitorBarcodeScannerCameraDirection;
(function (CapacitorBarcodeScannerCameraDirection) {
    CapacitorBarcodeScannerCameraDirection[CapacitorBarcodeScannerCameraDirection["BACK"] = 1] = "BACK";
    CapacitorBarcodeScannerCameraDirection[CapacitorBarcodeScannerCameraDirection["FRONT"] = 2] = "FRONT";
})(CapacitorBarcodeScannerCameraDirection || (CapacitorBarcodeScannerCameraDirection = {}));
export var CapacitorBarcodeScannerAndroidScanningLibrary;
(function (CapacitorBarcodeScannerAndroidScanningLibrary) {
    /** Uses ZXing (Apache 2.0, F-Droid compatible). */
    CapacitorBarcodeScannerAndroidScanningLibrary["ZXING"] = "zxing";
    /** Alias for ZXING — MLKIT is not available in this plugin. Falls back silently. */
    CapacitorBarcodeScannerAndroidScanningLibrary["MLKIT"] = "mlkit";
})(CapacitorBarcodeScannerAndroidScanningLibrary || (CapacitorBarcodeScannerAndroidScanningLibrary = {}));
