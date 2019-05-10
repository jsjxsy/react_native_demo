package com.wpt.rn.module;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;

import javax.annotation.Nonnull;

public class ShareModule extends ReactContextBaseJavaModule {
    public ShareModule(@Nonnull ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Nonnull
    @Override
    public String getName() {
        return "WPTRNShareModule";
    }


    @ReactMethod
    public void share(ReadableMap shareParam) {
    }

    @ReactMethod
    public void doShareWithQRCode(ReadableMap shareParam) {
    }

    @ReactMethod
    public void doQuickShare(ReadableMap shareParam) {
    }

    @ReactMethod
    public void doShareToWechat(ReadableMap shareParam) {
    }

    @ReactMethod
    public void doShareToTimeLine(ReadableMap shareParam) {
    }

    @ReactMethod
    public void doShareToQQ(ReadableMap shareParam) {
    }


    @ReactMethod
    public void doShareToQQZone(ReadableMap shareParam) {
    }

    @ReactMethod
    public void doShareToSina(ReadableMap shareParam) {
    }
}
