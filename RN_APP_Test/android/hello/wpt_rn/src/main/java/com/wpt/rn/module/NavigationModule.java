package com.wpt.rn.module;

import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import javax.annotation.Nonnull;

public class NavigationModule extends ReactContextBaseJavaModule {
    private ReactApplicationContext mReactContext;

    public NavigationModule(@Nonnull ReactApplicationContext reactContext) {
        super(reactContext);
        this.mReactContext = reactContext;
    }

    @Nonnull
    @Override
    public String getName() {
        return "WPTNavigation";
    }


    @ReactMethod
    public void pop(Promise promise) {
        promise.resolve(null);
    }

    @ReactMethod
    public void push(ReadableMap param, Promise promise) {
        //TODO:modify param
        promise.resolve(null);
    }

    @ReactMethod
    public void openWebview(ReadableMap param, Promise promise) {
        //TODO:modify param
        promise.resolve(null);
    }

    @ReactMethod
    public void canGoBack(Promise promise) {
        promise.resolve(null);
    }

    @ReactMethod
    public void resetRNRouter(ReadableMap components, Promise promise) {
        //TODO:modify param
        Log.e("xsy", components.toString());
        promise.resolve(null);
    }

    @ReactMethod
    public void shareUrl(String shareUrl, Promise promise) {
        promise.resolve(null);
    }

    @ReactMethod
    public void openAboutView(Promise promise) {
        promise.resolve(null);
    }

    @ReactMethod
    public void addressSelected(Promise promise) {
        promise.resolve(null);
    }


    public void sendEvent(String eventName) {
        //TODO: modify params
        WritableMap params = Arguments.createMap();
        params.putString("message", "msg");
        this.mReactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }


}
