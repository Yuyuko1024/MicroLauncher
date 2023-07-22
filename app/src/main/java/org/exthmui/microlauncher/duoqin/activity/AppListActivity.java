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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.exthmui.microlauncher.duoqin.R;
import org.exthmui.microlauncher.duoqin.adapter.AppAdapter;
import org.exthmui.microlauncher.duoqin.databinding.AppListActivityBinding;
import org.exthmui.microlauncher.duoqin.utils.Application;
import org.exthmui.microlauncher.duoqin.utils.PinyinComparator;
import org.exthmui.microlauncher.duoqin.utils.PinyinUtils;
import org.exthmui.microlauncher.duoqin.widgets.AppRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class AppListActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
    private final static String TAG = "AppListActivity";
    private AppListActivityBinding binding;
    private PkgDelReceiver mPkgDelReceiver;
    private PinyinComparator mComparator;
    private SharedPreferences sharedPreferences;
    private String app_list_style;
    private boolean isSimpleList;
    private boolean isSortByPinyin = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AppListActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.appBack.setOnClickListener(new funClick());
        binding.appMenu.setOnClickListener(new funClick());
        sharedPreferences = getSharedPreferences(getPackageName()+"_preferences",Context.MODE_PRIVATE);
        loadSettings(sharedPreferences);
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

    private void loadSettings(SharedPreferences sharedPreferences){
        app_list_style=sharedPreferences.getString("app_list_func","grid");
        isSimpleList=sharedPreferences.getBoolean("switch_preference_app_list_func",false);
        isSortByPinyin=sharedPreferences.getBoolean("switch_preference_app_list_sort",false);
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
            if(v == binding.appBack) {
                finish();
            }else if(v == binding.appMenu){
                showMenu(v);
            }
        }
    }

    private void loadApp() {
        PackageManager packageManager = getPackageManager();
        mComparator = new PinyinComparator();
        Application application;
        Intent appIntent;
        Intent intent = new Intent().setAction(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER);
        ActivityInfo activityInfo;
        ApplicationInfo applicationInfo;
        String pkgName;
        Drawable appIcon;
        CharSequence appLabel;
        boolean isSystemApp;
        String pinyin;
        String sortString;
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
            //如果使用按拼音排序
            if (isSortByPinyin) {
                pinyin = PinyinUtils.getPingYin(appLabel.toString());
                sortString = pinyin.substring(0, 1).toUpperCase();
                if (sortString.matches("[A-Za-z]")) {
                    application.setLetters(sortString.toUpperCase());
                } else {
                    application.setLetters("#");
                }
            }
            if(isSimpleList) {
                if(appLabel != getString(R.string.app_name) && isSystemApp || appLabel == getString(R.string.trd_apps)){
                    mApplicationList.add(application);
                }
            }else{
                if(appLabel != getString(R.string.app_name) && appLabel != getString(R.string.trd_apps)){
                    mApplicationList.add(application);
                }
            }
        }
        //如果使用按拼音排序
        if (isSortByPinyin) {
            mApplicationList.sort(mComparator);
        }
        AppRecyclerView mAppRecyclerView = findViewById(R.id.app_list);
        //如果是网格布局
        if(app_list_style.equals("grid")){
            //      设置布局管理器
            mAppRecyclerView.setLayoutManager(new GridLayoutManager(this,3));
            //      设置适配器
            mAppRecyclerView.setAdapter(new AppAdapter(mApplicationList, 1));
        }else{
            //列表布局
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
                    Intent menu = new Intent(AppListActivity.this, MenuActivity.class);
                    menu.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(menu);
                    finish();
                    break;
                case R.id.menu_volume_changer:
                    Intent vol_it = new Intent(AppListActivity.this, VolumeChanger.class);
                    vol_it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(vol_it);
                    break;
                case R.id.menu_app_sort_pinyin:
                    isSortByPinyin = true;
                    sharedPreferences.edit().putBoolean("switch_preference_app_list_sort",true).apply();
                    loadApp();
                    break;
                case R.id.menu_app_sort_default:
                    isSortByPinyin = false;
                    sharedPreferences.edit().putBoolean("switch_preference_app_list_sort",false).apply();
                    loadApp();
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
                Intent menu = new Intent(AppListActivity.this, MenuActivity.class);
                startActivity(menu);
                finish();
                break;
            case R.id.menu_volume_changer:
                Intent vol_it = new Intent(AppListActivity.this, VolumeChanger.class);
                startActivity(vol_it);
                break;
            case R.id.menu_app_sort_pinyin:
                isSortByPinyin = true;
                sharedPreferences.edit().putBoolean("switch_preference_app_list_sort",true).apply();
                loadApp();
                break;
            case R.id.menu_app_sort_default:
                isSortByPinyin = false;
                sharedPreferences.edit().putBoolean("switch_preference_app_list_sort",false).apply();
                loadApp();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPkgDelReceiver != null){
            unregisterReceiver(mPkgDelReceiver);
        }
    }
}
