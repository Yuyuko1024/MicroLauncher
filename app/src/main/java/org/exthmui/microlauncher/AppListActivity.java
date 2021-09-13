package org.exthmui.microlauncher;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AppListActivity extends AppCompatActivity {
    private RecyclerView mAppRecyclerView;
    private List<Application> mApplicationList;
    private final static String TAG = "MenuActivity";
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
    }
    class funClick implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if(v == back) {
                finish();
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
        if(keyCode == KeyEvent.KEYCODE_HOME){
            Intent home = new Intent(AppListActivity.this,MainActivity.class);
            startActivity(home);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
