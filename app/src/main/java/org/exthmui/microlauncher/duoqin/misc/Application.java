package org.exthmui.microlauncher.duoqin.misc;

import android.content.Intent;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

public class Application {
    private Drawable mAppIcon;              //App图标，Drawable类型
    private CharSequence mAppLabel;         //App标题，CharSequence类型，可强制转换为String类型
    private boolean mIsSystemApp;           //App类型，boolean类型，true为系统应用，false则为用户应用
    private Intent mAppIntent;              //App启动项，Intent类型，可用于启动App
    private String mPkgName;

    //    带参数的构造函数，可以自动生成
    public Application(Drawable mAppIcon, CharSequence mAppLabel, boolean mIsSystemApp, Intent mAppIntent, String mPkgName) {
        this.mAppIcon = mAppIcon;
        this.mAppLabel = mAppLabel;
        this.mIsSystemApp = mIsSystemApp;
        this.mAppIntent = mAppIntent;
        this.mPkgName = mPkgName;
    }

    //    Getter & Setter，可以自动生成
    public Drawable getAppIcon() {
        return this.mAppIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.mAppIcon = appIcon;
    }

    public CharSequence getAppLabel() {
        return this.mAppLabel;
    }

    public void setAppLabel(CharSequence appLabel) {
        this.mAppLabel = appLabel;
    }

    public boolean isSystemApp() {
        return this.mIsSystemApp;
    }

    public void setIsSystemApp(boolean isSystemApp) {
        this.mIsSystemApp = isSystemApp;
    }

    public Intent getAppIntent() {
        return this.mAppIntent;
    }

    public void setAppIntent(Intent appIntent) {
        this.mAppIntent = appIntent;
    }

    public String getPkgName() {return this.mPkgName;}

    //    toString()方法，可以自动生成
    @NonNull
    @Override
    public String toString() {
        return "Application{" +
                "mAppIcon=" + this.mAppIcon +
                ", mAppLabel=" + this.mAppLabel +
                ", mIsSystemApp=" + this.mIsSystemApp +
                ", mAppIntent=" + this.mAppIntent +
                '}';
    }
}
