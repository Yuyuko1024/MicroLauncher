package org.exthmui.microlauncher.duoqin.activity;

import android.annotation.SuppressLint;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.exthmui.microlauncher.duoqin.R;
import org.exthmui.microlauncher.duoqin.adapter.AppAdapter;
import org.exthmui.microlauncher.duoqin.utils.Application;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class AppList3rdActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
    private final static String TAG = "AppListActivity";
    private PkgDelReceiver mPkgDelReceiver;
    private TextView menu,back;
    private String app_list_style;
    private String pwdCustom;
    private boolean isSimpleList,isEnablePwd,pwdUseKeyguard;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_list_activity);
        menu=findViewById(R.id.app_menu);
        back=findViewById(R.id.app_back);
        back.setOnClickListener(new funClick());
        menu.setOnClickListener(new funClick());
        sharedPreferences = getSharedPreferences(getPackageName()+"_preferences",Context.MODE_PRIVATE);
        loadSettings(sharedPreferences);
        if (isEnablePwd) {
            if (pwdUseKeyguard){
                Intent start_it = getIntent();
                boolean veri_success = start_it.getBooleanExtra("result",false);
                boolean no_pwd = start_it.getBooleanExtra("no_password",false);
                if (!veri_success){
                    if (isEnablePwd) {
                        startVerification();
                        finish();
                    }
                }
                if (no_pwd){
                    Toasty.warning(getApplicationContext(),getString(R.string.system_no_password),Toasty.LENGTH_LONG).show();
                }
                init();
            } else if (!TextUtils.isEmpty(pwdCustom)){
                EditText editText = new EditText(this);
                editText.setMaxLines(1);
                editText.setHint(R.string.password_title);
                editText.setInputType(129);
                editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
                new MaterialAlertDialogBuilder(this)
                        .setTitle(R.string.password_title)
                        .setView(editText)
                        .setCancelable(false)
                        .setPositiveButton(getString(android.R.string.ok), (dialog, which) -> {
                            if (editText.getText().toString().equals(pwdCustom)){
                                init();
                                dialog.dismiss();
                            } else {
                                Toasty.error(getApplicationContext(),getString(R.string.password_wrong),Toasty.LENGTH_LONG).show();
                                finish();
                            }
                        })
                        .setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> {
                            dialog.dismiss();
                            finish();
                        })
                        .show();
            }
        } else {
            init();
        }
    }

    private void init(){
        loadApp();
        receiveSyscast();
    }

    private void startVerification(){
        if (pwdUseKeyguard){
            Intent intent = new Intent(AppList3rdActivity.this, KeyguardVerificationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void loadSettings(SharedPreferences sp){
        app_list_style=sp.getString("app_list_func","grid");
        isSimpleList=sp.getBoolean("switch_preference_app_list_func",false);
        isEnablePwd=sp.getBoolean("enable_toolbox_password",false);
        pwdUseKeyguard=sp.getBoolean("toolbox_password_use_keyguard",true);
        pwdCustom=sp.getString("toolbox_password_use_custom","");
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        loadSettings(sharedPreferences);
    }

    private void receiveSyscast(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addDataScheme("package");
        if (mPkgDelReceiver == null) {
            mPkgDelReceiver = new PkgDelReceiver();
            registerReceiver(mPkgDelReceiver, intentFilter);
        }
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
        List<Application> mApplicationList = new ArrayList<>();

        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resolveInfos) {
            activityInfo = resolveInfo.activityInfo;
            applicationInfo = activityInfo.applicationInfo;
            appIcon = activityInfo.loadIcon(packageManager);
            appLabel = activityInfo.loadLabel(packageManager);
            isSystemApp = (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1;
            appIntent = new Intent().setClassName(activityInfo.packageName, activityInfo.name);
            pkgName = activityInfo.packageName;
            application = new Application(appIcon, appLabel, isSystemApp, appIntent, pkgName);
            if(appLabel!=getString(R.string.trd_apps) && appLabel!=getString(R.string.app_name) && !isSystemApp){ mApplicationList.add(application);}
        }
        RecyclerView mAppRecyclerView = findViewById(R.id.app_list);
        //如果是网格布局
        if(app_list_style.equals("grid")){
            //      设置布局管理器
            mAppRecyclerView.setLayoutManager(new GridLayoutManager(this,3));
            //      设置适配器
            mAppRecyclerView.setAdapter(new AppAdapter(mApplicationList, 1));
        }else{
            mAppRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
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
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.setClassName("com.android.settings",
                            "com.android.settings.applications.ManageApplications");
                    startActivity(i);
                    break;
                case R.id.menu_about_phone:
                    Log.e("Device Info","Device SDK="+Build.VERSION.SDK_INT);
                    if (Build.VERSION.SDK_INT >= 28){
                        Intent ia = new Intent();
                        ia.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        ia.setClassName("com.android.settings",
                                "com.android.settings.Settings$MyDeviceInfoActivity");
                        startActivity(ia);
                    }else{
                        Intent ia = new Intent();
                        ia.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        ia.setClassName("com.android.settings",
                                "com.android.settings.Settings$DeviceInfoSettingsActivity");
                        startActivity(ia);}
                    break;
                case R.id.menu_launcher_option:
                    Intent menu = new Intent(AppList3rdActivity.this, MenuActivity.class);
                    menu.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(menu);
                    finish();
                    break;
                case R.id.menu_volume_changer:
                    Intent vol_it = new Intent(AppList3rdActivity.this, VolumeChanger.class);
                    vol_it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(vol_it);
                    break;
            }
            return true;
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
                Intent menu = new Intent(AppList3rdActivity.this, MenuActivity.class);
                startActivity(menu);
                finish();
                break;
            case R.id.menu_volume_changer:
                Intent vol_it = new Intent(AppList3rdActivity.this, VolumeChanger.class);
                startActivity(vol_it);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPkgDelReceiver != null) unregisterReceiver(mPkgDelReceiver);
    }
}
