package org.exthmui.microlauncher.duoqin.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

public class KillSelfSrv extends Service {
    private static long stopDelayed=50;
    private Handler handler;
    private String pkgName;
    public KillSelfSrv() {
        handler= new Handler();
    }

    @Override
    public int onStartCommand(final Intent intent,int flags, int startId){
        stopDelayed=intent.getLongExtra("Delayed",50);
        pkgName=intent.getStringExtra("PackageName");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage(pkgName);
                startActivity(launchIntent);
                KillSelfSrv.this.stopSelf();
            }
        },stopDelayed);
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
}