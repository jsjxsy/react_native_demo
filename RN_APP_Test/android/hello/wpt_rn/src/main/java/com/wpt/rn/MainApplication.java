package com.wpt.rn;

import android.app.Application;
import android.support.annotation.Nullable;

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;
import com.facebook.soloader.SoLoader;
import com.horcrux.svg.SvgPackage;
import com.swmansion.gesturehandler.react.RNGestureHandlerPackage;
import com.wpt.rn.constants.FileConstant;
import com.wpt.rn.pack.CustomPackage;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class MainApplication extends Application implements ReactApplication {

    private static MainApplication instance;
    private static CustomPackage mCustomPackage = new CustomPackage();

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        SoLoader.init(this,false);
    }

    private final ReactNativeHost mReactNativeHost = new ReactNativeHost(this) {

        @Override
        public boolean getUseDeveloperSupport() {
            return BuildConfig.DEBUG;
        }

        @Override
        protected List<ReactPackage> getPackages() {
            return Arrays.<ReactPackage>asList(
                    new MainReactPackage(),
                    mCustomPackage,
                    new RNGestureHandlerPackage(),
                    new SvgPackage()
            );
        }

        @Nullable
        @Override
        protected String getJSBundleFile() {
            File file = new File(FileConstant.JS_BUNDLE_LOCAL_PATH);
            if (file != null && file.exists()) {
                return FileConstant.JS_BUNDLE_LOCAL_PATH;
            } else {
                return super.getJSBundleFile();
            }
        }

    };


    @Override
    public ReactNativeHost getReactNativeHost() {
        return mReactNativeHost;
    }

    /**
     *包名
     */
    public String getAppPackageName() {
        return this.getPackageName();
    }

    /**
     * 获取Application实例
     */
    public static MainApplication getInstance() {
        return instance;
    }

    /**
     * 获取 reactPackage
     * @return
     */
    public static CustomPackage getReactPackage() {
        return mCustomPackage;
    }
}
