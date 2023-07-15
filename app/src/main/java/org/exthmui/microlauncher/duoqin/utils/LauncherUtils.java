package org.exthmui.microlauncher.duoqin.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Build;

import org.exthmui.microlauncher.duoqin.BuildConfig;

public class LauncherUtils {

    /**
     * 判断是否为默认桌面
     * @param context 默认Context
     * @return true为默认桌面，false则不是
     */
    public static boolean isDefaultLauncher(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo info = context.getPackageManager().resolveActivity(intent, 0);
        if (info != null) {
            return BuildConfig.APPLICATION_ID.equals(info.activityInfo.packageName);
        }
        return false;
    }

}
