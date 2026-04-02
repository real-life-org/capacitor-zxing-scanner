package org.reallife.capacitor.zxingscanner;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;

import androidx.activity.result.ActivityResult;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.ActivityCallback;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;

@CapacitorPlugin(
        name = "CapacitorBarcodeScanner",
        permissions = {
                @Permission(strings = {Manifest.permission.CAMERA}, alias = "camera")
        }
)
public class ZxingScannerPlugin extends Plugin {

    @PluginMethod
    public void scanBarcode(PluginCall call) {
        if (getPermissionState("camera") != com.getcapacitor.PermissionState.GRANTED) {
            requestPermissionForAlias("camera", call, "cameraPermissionCallback");
        } else {
            startScannerActivity(call);
        }
    }

    @PermissionCallback
    private void cameraPermissionCallback(PluginCall call) {
        if (getPermissionState("camera") == com.getcapacitor.PermissionState.GRANTED) {
            startScannerActivity(call);
        } else {
            call.reject("Camera permission denied");
        }
    }

    private void startScannerActivity(PluginCall call) {
        Intent intent = new Intent(getContext(), ScannerActivity.class);
        startActivityForResult(call, intent, "scanBarcodeResult");
    }

    @ActivityCallback
    private void scanBarcodeResult(PluginCall call, ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
            String text = result.getData().getStringExtra(ScannerActivity.EXTRA_SCAN_RESULT);
            if (text != null) {
                JSObject ret = new JSObject();
                ret.put("ScanResult", text);
                ret.put("format", "QR_CODE");
                call.resolve(ret);
            } else {
                call.reject("No scan result");
            }
        } else {
            call.reject("Scanner cancelled");
        }
    }
}
