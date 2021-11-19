package com.locnavi.navigation.demo;

import android.app.Application;
import com.locnavi.websdk.LocNaviWebSDK;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LocNaviWebSDK.init(new LocNaviWebSDK.Configuration
                .Builder(this)
                .appKey(Constants.appKey)
                .debug(true)
                .build());
    }
}
