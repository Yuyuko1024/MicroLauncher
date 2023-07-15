package org.exthmui.microlauncher.duoqin;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import org.exthmui.microlauncher.duoqin.utils.BuglyUtils;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName()+"_preferences", Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean("bugly_init",false)) {
            BuglyUtils.initBugly(this);
        }
    }
}
