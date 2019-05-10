package com.wpt.rn.module;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;

import javax.annotation.Nonnull;

public class PublishModule extends ReactContextBaseJavaModule {
    public PublishModule(@Nonnull ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Nonnull
    @Override
    public String getName() {
        return "WPTUploader";
    }

    @ReactMethod
    public void uploadImageWithLocalId(String localId, Promise promise){
    }

    @ReactMethod
    public void uploadImageWithCount(int count,Promise promise){
    }

    @ReactMethod
    public void publishGoods(){
    }

    @ReactMethod
    public void publishTopOne(ReadableMap params){
    }

    @ReactMethod
    public void uploadVideo(String operateId){
    }

}
