// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorZxingScanner",
    platforms: [.iOS(.v15)],
    products: [
        .library(
            name: "CapacitorZxingScanner",
            targets: ["CapacitorZxingScanner"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", from: "6.0.0")
    ],
    targets: [
        .target(
            name: "CapacitorZxingScanner",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "Sources/CapacitorZxingScanner"
        )
    ]
)
