package org.exthmui.microlauncher.duoqin.misc;

import android.content.Context;
import android.content.Intent;

import org.exthmui.microlauncher.duoqin.service.KillSelfSrv;

public class RestartTool {
    /*
     * 重启App
     * @param context
     * @param Delayed
     * from https://blog.csdn.net/SSBBY/article/details/84564801
     */
    public static void restartApp(Context context, long Delayed){

        /*启动服务用于重启App*/
        Intent intentMyself=new Intent(context, KillSelfSrv.class);
        intentMyself.putExtra("PackageName",context.getPackageName());
        intentMyself.putExtra("Delayed",Delayed);
        context.startService(intentMyself);

        /*杀死整个进程*/
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public static void restartApp(Context context){
        restartApp(context,1000);
    }
}
