package com.wpt.rn.pack;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.wpt.rn.module.CommonModule;
import com.wpt.rn.module.ImagePickerModule;
import com.wpt.rn.module.NavigationModule;
import com.wpt.rn.module.PublishModule;
import com.wpt.rn.module.ShareModule;
import com.wpt.rn.module.ToastModule;
import com.wpt.rn.module.UserModule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

public class CustomPackage implements ReactPackage {
    public NavigationModule mNavigationModule;

    @Nonnull
    @Override
    public List<NativeModule> createNativeModules(@Nonnull ReactApplicationContext reactApplicationContext) {
        List<NativeModule> modules = new ArrayList<>();
        modules.add(new ToastModule(reactApplicationContext));
        modules.add(new ImagePickerModule(reactApplicationContext));
        mNavigationModule = new NavigationModule(reactApplicationContext);
        modules.add(mNavigationModule);
        modules.add(new UserModule(reactApplicationContext));
        modules.add(new CommonModule(reactApplicationContext));
        modules.add(new PublishModule(reactApplicationContext));
        modules.add(new ShareModule(reactApplicationContext));

        return modules;

    }

    @Nonnull
    @Override
    public List<ViewManager> createViewManagers(@Nonnull ReactApplicationContext reactApplicationContext) {
        return Collections.emptyList();
    }
}
