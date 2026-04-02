import Foundation
import Capacitor
import AVFoundation

@objc(CapacitorBarcodeScannerPlugin)
public class ZxingScannerPlugin: CAPPlugin, CAPBridgedPlugin {
    public let identifier = "CapacitorBarcodeScannerPlugin"
    public let jsName = "CapacitorBarcodeScanner"
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "scanBarcode", returnType: CAPPluginReturnPromise)
    ]

    @objc func scanBarcode(_ call: CAPPluginCall) {
        AVCaptureDevice.requestAccess(for: .video) { [weak self] granted in
            guard let self = self else { return }
            guard granted else {
                call.reject("Camera permission denied")
                return
            }
            DispatchQueue.main.async {
                self.presentScanner(call)
            }
        }
    }

    private func presentScanner(_ call: CAPPluginCall) {
        let scanner = ScannerViewController()
        scanner.modalPresentationStyle = .fullScreen
        scanner.completion = { [weak self] result in
            guard let self = self else { return }
            if let text = result {
                call.resolve(["ScanResult": text, "format": "QR_CODE"])
            } else {
                call.reject("Scanner cancelled")
            }
        }
        bridge?.viewController?.present(scanner, animated: true)
    }
}
