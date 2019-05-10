package com.wpt.rn.activity;

import android.support.annotation.Nullable;

import com.facebook.react.ReactActivity;
import com.facebook.react.ReactActivityDelegate;
import com.facebook.react.ReactRootView;

public class MyReactActivity extends ReactActivity {
    @Nullable
    @Override
    protected String getMainComponentName() {
        return "wptNative";
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    //    @Override
//    protected ReactActivityDelegate createReactActivityDelegate() {
//        return new ReactActivityDelegate(this, getMainComponentName()) {
//            @Override
//            protected ReactRootView createRootView() {
//                return new RNGestureHandlerEnabledRootView(MyReactActivity.this);
//            }
//        };
//    }

}
