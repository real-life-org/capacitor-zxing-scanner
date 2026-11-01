// swift-tools-version: 5.9
import PackageDescription

// Swift Package Manager manifest at the package root: the Capacitor CLI (`cap sync ios` with SPM)
// looks for Package.swift next to package.json and skips a plugin without one ("does not have a
// Package.swift"). The sources stay under ios/, as the podspec expects.
// `from: "8.0.0"` means 8.0.0 ..< 9.0.0 (SwiftPM's up-to-next-major), the range the Capacitor 8
// plugins declare; an app pins the exact Capacitor version in its own CapApp-SPM/Package.swift.
let package = Package(
    name: "CapacitorZxingScanner",
    platforms: [.iOS(.v15)],
    products: [
        .library(
            name: "CapacitorZxingScanner",
            targets: ["CapacitorZxingScanner"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", from: "8.0.0")
    ],
    targets: [
        .target(
            name: "CapacitorZxingScanner",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/CapacitorZxingScanner"
        )
    ]
)
