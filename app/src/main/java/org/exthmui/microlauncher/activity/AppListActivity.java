package org.exthmui.microlauncher.activity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.exthmui.microlauncher.R;
import org.exthmui.microlauncher.adapter.AppAdapter;
import org.exthmui.microlauncher.misc.Application;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class AppListActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private RecyclerView mAppRecyclerView;
    private List<Application> mApplicationList;
    private final static String TAG = "AppListActivity";
    private boolean lock_enable = true;
    TextView menu,back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_list_activity);
        menu=findViewById(R.id.app_menu);
        back=findViewById(R.id.app_back);
        back.setOnClickListener(new funClick());
        menu.setOnClickListener(new funClick());
        loadApp();
        loadSettings();
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
    private void loadSettings(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        Boolean lock_isEnabled = (sharedPreferences.getBoolean("preference_main_lockscreen",true));
        if(lock_isEnabled){
            lock_enable=true;
        }else{
            lock_enable=false;
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("preference_main_lockscreen")){
            Boolean lock_isEnabled = (sharedPreferences.getBoolean("preference_main_lockscreen",true));
            if(lock_isEnabled){
                lock_enable=true;
            }else{
                lock_enable=false;
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
        Drawable appIcon;
        CharSequence appLabel;
        boolean isSystemApp;
        this.mApplicationList = new ArrayList<>();

        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, 0);

        for (ResolveInfo resolveInfo : resolveInfos) {
            activityInfo = resolveInfo.activityInfo;
            applicationInfo = activityInfo.applicationInfo;

            appIcon = activityInfo.loadIcon(packageManager);
            appLabel = activityInfo.loadLabel(packageManager);
            isSystemApp = (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1;
            appIntent = new Intent().setClassName(activityInfo.packageName, activityInfo.name);
            application = new Application(appIcon, appLabel, isSystemApp, appIntent);
            this.mApplicationList.add(application);
        }

        this.mAppRecyclerView = findViewById(R.id.app_list);
//      设置布局管理器
        this.mAppRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//      设置适配器
        this.mAppRecyclerView.setAdapter(new AppAdapter(this.mApplicationList, 0));
    }

    private void showMenu(View view){
        PopupMenu popupMenu = new PopupMenu(this,view);
        popupMenu.getMenuInflater().inflate(R.menu.app_option,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
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
                }
                return false;
            }
        });
        popupMenu.show();
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        //Toast.makeText(this,item.getTitle(),Toast.LENGTH_SHORT).show();
        switch (item.getItemId()){
            case 0:
                //TODO: Need implement method
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_option,menu);
        return true;
    }

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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG,"这个按键的KeyCode是 "+keyCode);
        if(keyCode == KeyEvent.KEYCODE_STAR){
            DevicePolicyManager mDPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
                if (lock_enable) {
                    mDPM.lockNow();
                } else {
                    Log.d("TAG", "Lock screen is disabled");
                }
            }

        return super.onKeyDown(keyCode,event);
    }

}
