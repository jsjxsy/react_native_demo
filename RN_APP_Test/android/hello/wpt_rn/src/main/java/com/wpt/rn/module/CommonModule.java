package com.wpt.rn.module;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;

import javax.annotation.Nonnull;

public class CommonModule extends ReactContextBaseJavaModule {
    public CommonModule(@Nonnull ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Nonnull
    @Override
    public String getName() {
        return "WPTRNCommonModule";
    }

    @ReactMethod
    public String serverTime() {
        return "";
    }

    @ReactMethod
    public String userAgent() {
        return "";
    }

    @ReactMethod
    public String cookie() {
        return "";
    }

    @ReactMethod
    public String env() {
        return "";
    }


    @ReactMethod
    public void eventUpload(ReadableMap components) {
    }

    @ReactMethod
    public String startQRCode() {
        return "";
    }

    @ReactMethod
    public String photoBrowser(ReadableMap components) {
        return "";
    }


}
