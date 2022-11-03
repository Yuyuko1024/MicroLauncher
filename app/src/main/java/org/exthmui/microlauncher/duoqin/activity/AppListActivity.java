package org.exthmui.microlauncher.duoqin.activity;

import android.annotation.SuppressLint;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.exthmui.microlauncher.duoqin.R;
import org.exthmui.microlauncher.duoqin.adapter.AppAdapter;
import org.exthmui.microlauncher.duoqin.misc.Application;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class AppListActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
    private final static String TAG = "AppListActivity";
    private PkgDelReceiver mPkgDelReceiver;
    TextView menu,back;
    String app_list_style;
    boolean isSimpleList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_list_activity);
        menu=findViewById(R.id.app_menu);
        back=findViewById(R.id.app_back);
        back.setOnClickListener(new funClick());
        menu.setOnClickListener(new funClick());
        loadSettings();
        loadApp();
        receiveSyscast();
        changeTitle(isSimpleList);
    }

    private void changeTitle(boolean isSimpleTitle){
        if(isSimpleTitle){
            setTitle(R.string.menu);
            Log.d(TAG,"changeTitle true");
        }else{
            setTitle(R.string.app_list_title);
            Log.d(TAG,"changeTitle false");
        }
    }

    private void loadSettings(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        app_list_style=sharedPreferences.getString("app_list_func","grid");
        isSimpleList=sharedPreferences.getBoolean("switch_preference_app_list_func",false);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        app_list_style=sharedPreferences.getString("app_list_func","grid");
        isSimpleList=sharedPreferences.getBoolean("switch_preference_app_list_func",false);
    }

    private void receiveSyscast(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addDataScheme("package");
        mPkgDelReceiver = new PkgDelReceiver();
        registerReceiver(mPkgDelReceiver, intentFilter);
    }

    class PkgDelReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG,"detect package change...");
            Toasty.info(context,R.string.refreshing_pkg_list,Toasty.LENGTH_LONG).show();
            loadApp();
        }
    }

    class funClick implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if(v == back) {
                finish();
            }else if(v == menu){
                showMenu(menu);
            }
        }
    }

    private void loadApp() {
        PackageManager packageManager = getPackageManager();
        Application application;
        Intent appIntent;
        Intent intent = new Intent().setAction(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER);
        ActivityInfo activityInfo;
        ApplicationInfo applicationInfo;
        String pkgName;
        Drawable appIcon;
        CharSequence appLabel;
        boolean isSystemApp;
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, 0);
        List<Application> mApplicationList = new ArrayList<>();
        for (ResolveInfo resolveInfo : resolveInfos) {
            activityInfo = resolveInfo.activityInfo;
            applicationInfo = activityInfo.applicationInfo;
            appIcon = activityInfo.loadIcon(packageManager);
            appLabel = activityInfo.loadLabel(packageManager);
            isSystemApp = (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1;
            appIntent = new Intent().setClassName(activityInfo.packageName, activityInfo.name);
            pkgName = activityInfo.packageName;
            application = new Application(appIcon, appLabel, isSystemApp, appIntent, pkgName);
            Log.e(TAG, String.valueOf(appLabel));
            if(isSimpleList) {
                if(appLabel != getString(R.string.app_name) && isSystemApp ){
                    mApplicationList.add(application);
                }
                if(appLabel == getString(R.string.trd_apps)){
                    mApplicationList.add(application);
                }
            }else{
                if(appLabel != getString(R.string.app_name) && appLabel != getString(R.string.trd_apps)){
                    mApplicationList.add(application);
                }
            }
        }
        RecyclerView mAppRecyclerView = findViewById(R.id.app_list);
        //如果是网格布局
        if(app_list_style.equals("grid")){
            //      设置布局管理器
            mAppRecyclerView.setLayoutManager(new GridLayoutManager(this,3));
            //      设置适配器
            mAppRecyclerView.setAdapter(new AppAdapter(mApplicationList, 1));
        }else{
            //列表布局
            mAppRecyclerView.setLayoutManager(new GridLayoutManager(this,1));
            mAppRecyclerView.setAdapter(new AppAdapter(mApplicationList, 0));
        }
    }

    @SuppressLint("NonConstantResourceId")
    private void showMenu(View view){
        PopupMenu popupMenu = new PopupMenu(this,view);
        popupMenu.getMenuInflater().inflate(R.menu.app_option,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                case R.id.menu_app_manage:
                    Intent i = new Intent();
                    i.setClassName("com.android.settings",
                            "com.android.settings.applications.ManageApplications");
                    startActivity(i);
                    break;
                case R.id.menu_about_phone:
                    if (Build.VERSION.SDK_INT >= 28){
                        Log.e("Device Info","Device SDK="+Build.VERSION.SDK_INT);
                        Intent ia = new Intent();
                        ia.setClassName("com.android.settings",
                                "com.android.settings.Settings$MyDeviceInfoActivity");
                        startActivity(ia);
                    }else{
                        Log.e("Device Info","Device SDK="+Build.VERSION.SDK_INT);
                        Intent ia = new Intent();
                        ia.setClassName("com.android.settings",
                                "com.android.settings.Settings$DeviceInfoSettingsActivity");
                        startActivity(ia);}
                    break;
                case R.id.menu_launcher_option:
                    Intent menu = new Intent(AppListActivity.this, MenuActivity.class);
                    startActivity(menu);
                    finish();
                    break;
                case R.id.menu_volume_changer:
                    Intent vol_it = new Intent(AppListActivity.this, VolumeChanger.class);
                    startActivity(vol_it);
                    break;
            }
            return false;
        });
        popupMenu.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_option,menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_app_manage:
                Intent i = new Intent();
                i.setClassName("com.android.settings",
                        "com.android.settings.applications.ManageApplications");
                startActivity(i);
                break;
            case R.id.menu_about_phone:
                if (Build.VERSION.SDK_INT >= 28){
                    Log.e("Device Info","Device SDK="+Build.VERSION.SDK_INT);
                    Intent ia = new Intent();
                    ia.setClassName("com.android.settings",
                            "com.android.settings.Settings$MyDeviceInfoActivity");
                    startActivity(ia);
                }else{
                    Log.e("Device Info","Device SDK="+Build.VERSION.SDK_INT);
                    Intent ia = new Intent();
                    ia.setClassName("com.android.settings",
                        "com.android.settings.Settings$DeviceInfoSettingsActivity");
                    startActivity(ia);}
                break;
            case R.id.menu_launcher_option:
                Intent menu = new Intent(AppListActivity.this, MenuActivity.class);
                startActivity(menu);
                finish();
                break;
            case R.id.menu_volume_changer:
                Intent vol_it = new Intent(AppListActivity.this, VolumeChanger.class);
                startActivity(vol_it);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mPkgDelReceiver);
    }
}
