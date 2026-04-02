import { WebPlugin } from '@capacitor/core'
import type { CapacitorBarcodeScannerOptions, CapacitorBarcodeScannerPlugin, CapacitorBarcodeScannerScanResult } from './definitions'

export class CapacitorBarcodeScannerWeb extends WebPlugin implements CapacitorBarcodeScannerPlugin {
  async scanBarcode(_options: CapacitorBarcodeScannerOptions): Promise<CapacitorBarcodeScannerScanResult> {
    throw this.unimplemented(
      'ZXing scanner is not available on web. Use html5-qrcode or another web-based QR scanner as a fallback.'
    )
  }
}
