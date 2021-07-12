package com.stavigilmonitoring;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.devs.acr.AutoErrorReporter;

import static cn.jzvd.JZMediaManager.start;

public class MyApplication extends Application {

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }
    @Override
    public void onCreate() {
        super.onCreate();
        //  Realm.init(this); //initialize other plugins

       /* int badgeCount = 1;
        ShortcutBadger.with(getApplicationContext()).count(badgeCount);
*/
        AutoErrorReporter.get(this).setEmailAddresses("vrittiisolutions@gmail.com")
                .setEmailSubject("STA crash report")
                .start();
    }
}



