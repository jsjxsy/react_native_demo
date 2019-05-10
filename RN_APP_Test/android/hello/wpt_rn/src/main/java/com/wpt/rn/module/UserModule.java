package com.wpt.rn.module;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import javax.annotation.Nonnull;

public final class UserModule extends ReactContextBaseJavaModule {
    public UserModule(@Nonnull ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Nonnull
    @Override
    public String getName() {
        return "WPTRNUserModule";
    }

    @ReactMethod
    public void userInfoAndCookie(){
    }

    @ReactMethod
    public void login(Promise promise) {
        promise.resolve(null);
    }

    @ReactMethod
    public void logout(Promise promise) {
        promise.resolve(null);
    }

}
