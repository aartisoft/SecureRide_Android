package com.securide.custmer;

import android.app.Application;

import com.securide.custmer.preferences.SecuridePreferences;

/**
 * Created by pradeep.kumar on 3/9/16.
 */
public class App extends Application {
    private static App mAppInstance = null;

    public static App getInstance(){
        return mAppInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAppInstance = this;
        SecuridePreferences.init(mAppInstance);
    }
}
