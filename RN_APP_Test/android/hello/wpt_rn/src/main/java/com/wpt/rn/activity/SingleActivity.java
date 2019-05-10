package com.wpt.rn.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;

import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.common.LifecycleState;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.shell.MainReactPackage;
import com.horcrux.svg.SvgPackage;
import com.swmansion.gesturehandler.react.RNGestureHandlerPackage;
import com.wpt.rn.BuildConfig;
import com.wpt.rn.MainApplication;
import com.wpt.rn.module.NavigationModule;
import com.wpt.rn.pack.CustomPackage;

/**
 * Created by guoshuyu on 2017/2/22.
 * <p>
 * 主要测试的加载远程js bundle 到本地
 * <p>
 * 需要注意是否支持原生代码在已经在Main那里注册了，这里是否支持用package使用。
 * 需要注意命名空间是否会重复问题。
 */

public class SingleActivity extends Activity implements DefaultHardwareBackBtnHandler {

    private final int OVERLAY_PERMISSION_REQ_CODE = 1;  // 任写一个值
    private ReactRootView mReactRootView;
    private ReactInstanceManager mReactInstanceManager;
    ReactContext reactContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mReactRootView = new ReactRootView(this);
        mReactInstanceManager = ReactInstanceManager.builder()
                .setApplication(getApplication())
                .setCurrentActivity(this)
                .setBundleAssetName("index.bundle")
//                .setBundleAssetName("index_0_0_5.android_5.jsbundle")
//                .setJSMainModulePath("index_0_0_5")
                .setJSMainModulePath("index")
                .addPackage(new MainReactPackage())
                .addPackage(new CustomPackage())//注意：必须加入否则报错
                .addPackage(new RNGestureHandlerPackage())
                .addPackage(new SvgPackage())
                .setUseDeveloperSupport(BuildConfig.DEBUG)
                .setInitialLifecycleState(LifecycleState.RESUMED)
                .build();
        // 注意这里的MyReactNativeApp必须对应“index.js”中的
        // “AppRegistry.registerComponent()”的第一个参数
        Bundle initialProps = new Bundle();
        initialProps.putString("router", "/promotion");
        mReactRootView.startReactApplication(mReactInstanceManager, "wptNative", initialProps);

        setContentView(mReactRootView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
            }
        }
        reactContext = new ReactContext(getApplicationContext());
    }

    @Override
    public void invokeDefaultOnBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    // SYSTEM_ALERT_WINDOW permission not granted

                }
            }
        }
        mReactInstanceManager.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        if (mReactInstanceManager != null) {
//
//            if(mReactInstanceManager.getCurrentReactContext() == null){
//                Log.e("xsy","is null");
//            }else{
//                Log.e("xsy","is not null");
//            }
////                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
////                    .emit("ViewWillAppearEmitter", null);
//        }
//        if (reactContext.hasActiveCatalystInstance()){
//            NavigationModule.onStart(reactContext);
//
//        }

    }


    @Override
    protected void onPause() {
        super.onPause();

        if (mReactInstanceManager != null) {
            mReactInstanceManager.onHostPause(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mReactInstanceManager != null) {
            mReactInstanceManager.onHostResume(this, this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mReactInstanceManager != null) {
            mReactInstanceManager.onHostDestroy(this);
        }
        if (mReactRootView != null) {
            mReactRootView.unmountReactApplication();
        }
    }

    @Override
    public void onBackPressed() {
        if (mReactInstanceManager != null) {
            mReactInstanceManager.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU && mReactInstanceManager != null) {
            mReactInstanceManager.showDevOptionsDialog();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    /**
     * 向RN发送消息
     */
    public void sendMsgToRN() {
        MainApplication.getReactPackage().mNavigationModule.sendEvent("hello");
    }

}
