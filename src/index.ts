import { registerPlugin } from '@capacitor/core'

import type { CapacitorBarcodeScannerPlugin } from './definitions'
import { CapacitorBarcodeScannerWeb } from './web'

export * from './definitions'

const CapacitorBarcodeScanner = registerPlugin<CapacitorBarcodeScannerPlugin>(
  'CapacitorBarcodeScanner',
  {
    web: () => new CapacitorBarcodeScannerWeb(),
  }
)

export { CapacitorBarcodeScanner }
