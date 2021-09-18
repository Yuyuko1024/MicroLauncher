package org.exthmui.microlauncher.activity;

import android.annotation.SuppressLint;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.exthmui.microlauncher.R;
import org.exthmui.microlauncher.misc.AdminReceive;
import org.exthmui.microlauncher.misc.ChineseCale;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private final static String TAG = "ML_MainActivity";
    private final static String dateFormat = "yyyy年MM月dd日";
    private final static int ENABLE_ADMIN = 1;
    private final Calendar calendar = Calendar.getInstance();
    private String week;
    private ComponentName mAdminName = null;
    private DevicePolicyManager mDPM ;
    private boolean lock_enable = true;
    private boolean recent_enable = true;
    Class serviceManagerClass;
    Button menu,contact;
    TextView dateView,lunar;
    RelativeLayout clock;
    TextClock text_clock;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT>=21)
        {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE); //使背景图与状态栏融合到一起，这里需要在setcontentview前执行
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        mAdminName = new ComponentName(this, AdminReceive.class);
        mDPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        if(!mDPM.isAdminActive(mAdminName)){
            showAdminGrant();
        }
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date date = new Date(System.currentTimeMillis());
        dateView=findViewById(R.id.date_text);
        lunar=findViewById(R.id.lunar_cale);
        clock=findViewById(R.id.clock);
        text_clock=findViewById(R.id.text_clock);
        printDayOfWeek();
        dateView.setText(sdf.format(date)+" "+week);
        lunar.setText(getDayLunar());
        menu=findViewById(R.id.menu);
        contact=findViewById(R.id.contact);
        contact.setOnClickListener(new mClick());
        menu.setOnClickListener(new mClick());
        loadSettings();
    }

    private void loadSettings(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        Boolean lunar_isEnable= (sharedPreferences.getBoolean("switch_preference_lunar",true));
        if(lunar_isEnable){
            lunar.setVisibility(View.VISIBLE);
        }else{
            lunar.setVisibility(View.INVISIBLE);
        }
        Boolean lock_isEnabled = (sharedPreferences.getBoolean("preference_main_lockscreen",true));
        if(lock_isEnabled){
            lock_enable=true;
        }else{
            lock_enable=false;
        }
        String clock_locate = (sharedPreferences.getString("list_preference_clock_locate","reimu"));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(clock.getLayoutParams());
        switch (clock_locate){
            case "reimu":
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                params.addRule(RelativeLayout.ALIGN_PARENT_START);
                clock.setLayoutParams(params);
                break;
            case "marisa":
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                clock.setLayoutParams(params);
                break;
            case "renko":
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                clock.setLayoutParams(params);
                break;
            case "maribel":
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                clock.setLayoutParams(params);
                break;
        }
        String clock_size = (sharedPreferences.getString("list_preference_clock_size","58"));
        text_clock.setTextSize(Float.parseFloat(clock_size));
        Boolean recent_isEnabled = (sharedPreferences.getBoolean("switch_preference_recent_apps",true));
        if(recent_isEnabled){
            recent_enable=true;
        }else{
            recent_enable=false;
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("switch_preference_lunar")){
            Boolean lunar_isEnable= (sharedPreferences.getBoolean("switch_preference_lunar",true));
            if(lunar_isEnable){
                lunar.setVisibility(View.VISIBLE);
            }else{
                lunar.setVisibility(View.INVISIBLE);
            }
        }else if(key.equals("preference_main_lockscreen")){
            Boolean lock_isEnabled = (sharedPreferences.getBoolean("preference_main_lockscreen",true));
            if(lock_isEnabled){
                lock_enable=true;
            }else{
                lock_enable=false;
            }
        }else if(key.equals("list_preference_clock_locate")){
            String clock_locate = (sharedPreferences.getString("list_preference_clock_locate","reimu"));
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(clock.getLayoutParams());
            switch (clock_locate){
                case "reimu":
                    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    params.addRule(RelativeLayout.ALIGN_PARENT_START);
                    clock.setLayoutParams(params);
                break;
                case "marisa":
                    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    clock.setLayoutParams(params);
                break;
                case "renko":
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    clock.setLayoutParams(params);
                break;
                case "maribel":
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    clock.setLayoutParams(params);
                break;
            }
        }else if(key.equals("list_preference_clock_size")){
            String clock_size = (sharedPreferences.getString("list_preference_clock_size","58"));
            text_clock.setTextSize(Float.parseFloat(clock_size));
        }else if(key.equals("switch_preference_recent_apps")){
            Boolean recent_isEnabled = (sharedPreferences.getBoolean("switch_preference_recent_apps",true));
            if(recent_isEnabled){
                recent_enable=true;
            }else{
                recent_enable=false;
            }

        }
    }

    class mClick implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.contact:
                    Intent i = new Intent();
                    i.setAction("android.intent.action.MAIN");
                    i.addCategory("android.intent.category.APP_CONTACTS");
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    break;
                case R.id.menu:
                    Intent menu_it = new Intent(MainActivity.this, MenuActivity.class);
                    startActivity(menu_it);
                    break;
            }
        }
    }
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        Log.d(TAG,"这个按键的KeyCode是 "+keyCode);
           if (keyCode == KeyEvent.KEYCODE_BACK) {
               //ifHasDefaultActivity();
               Intent it = new Intent();
               it.setAction("android.intent.action.MAIN");
               it.addCategory("android.intent.category.APP_CONTACTS");
               it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
               startActivity(it);
               return true;}
            else if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN){
                String methodName = "expandNotificationsPanel";
                doInStatusBar(getApplicationContext(), methodName);
                return true;}
            else if (keyCode == KeyEvent.KEYCODE_DPAD_UP){
               Intent it = new Intent();
                it.setClassName("com.android.settings",
                        "com.android.settings.Settings");
                startActivity(it);
                return true;}
            else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
               Intent it = new Intent();
               it.setAction("android.intent.action.MAIN");
               it.addCategory("android.intent.category.APP_MUSIC");
               it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
               startActivity(it);
                Log.d(TAG,"5,4,3,2,1,三倍ice cream!!!!!");
           return true;}
            else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
               Intent it = new Intent();
               it.setAction("android.intent.action.MAIN");
               it.addCategory("android.intent.category.APP_MESSAGING");
               it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
               startActivity(it);
           return true;}
           else if (keyCode == KeyEvent.KEYCODE_MENU ){
               Intent menu_it = new Intent(MainActivity.this, MenuActivity.class);
               startActivity(menu_it);
               return true;}
           //突然想起来得把音量控制面板绑定到#键上
           else if (keyCode == KeyEvent.KEYCODE_POUND ){
               Intent vol_it = new Intent(MainActivity.this, VolumeChanger.class);
               vol_it.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
               startActivity(vol_it);
               return true;}
           //暂时先不用这里
            /*else if (keyCode == KeyEvent.KEYCODE_1 ){
                Intent vol_it = new Intent(MainActivity.this, VolumeChanger.class);
                startActivity(vol_it);
           return true;}
           else if (keyCode == KeyEvent.KEYCODE_3 ){
               Intent vol_it = new Intent(MainActivity.this, VolumeChanger.class);
               startActivity(vol_it);
               return true;}*/
            else if (keyCode == KeyEvent.KEYCODE_0) {
               Log.d(TAG,"打开最近任务界面");
               Log.d(TAG,"当前设备SDK是："+Build.VERSION.SDK_INT);
               /*if(Build.VERSION.SDK_INT>=28) {
                   try {
                       Intent it = new Intent();
                       it.setAction("android.intent.action.QUICKSTEP_SERVICE");
                       it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                       startActivity(it);
               } catch (Exception e) {
                   e.printStackTrace();
                   Toasty.error(this, R.string.error_not_support_recent_app, Toast.LENGTH_LONG, true).show();
               }
               }else{*/
               if(recent_enable) {
                   try {
                       serviceManagerClass = Class.forName("android.os.ServiceManager");
                       Method getService = serviceManagerClass.getMethod("getService", String.class);
                       IBinder retbinder = (IBinder) getService.invoke(serviceManagerClass, "statusbar");
                       assert retbinder != null;
                       Class statusBarClass = Class.forName(retbinder.getInterfaceDescriptor());
                       Object statusBarObject = statusBarClass.getClasses()[0].getMethod("asInterface", IBinder.class).invoke(null, new Object[]{retbinder});
                       Method clearAll = statusBarClass.getMethod("toggleRecentApps");
                       clearAll.setAccessible(true);
                       clearAll.invoke(statusBarObject);
                   } catch (ClassNotFoundException | IllegalArgumentException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | RemoteException e) {
                       e.printStackTrace();
                       Toasty.error(this, R.string.error_not_support_recent_app, Toast.LENGTH_LONG, true).show();
                   }
               }else{
                   Log.d(TAG,"Recent is disabled.");
               }
               //}
               return true;}
            else if(keyCode == KeyEvent.KEYCODE_STAR){
               if(lock_enable){
               if(mDPM.isAdminActive(mAdminName)){
                   mDPM.lockNow();
               }else{
                   Toasty.error(this,R.string.error_lock_phone,Toast.LENGTH_LONG,true).show();
                   Log.e(TAG,"Lock phone error!");
               }}else{
                   Log.d(TAG,"Lock screen is disabled");
               }
           return true;}
        return false;
    }


    private static void doInStatusBar(Context mContext, String methodName) {
        try {
            @SuppressLint("WrongConstant") Object service = mContext.getSystemService("statusbar");
            Method expand = service.getClass().getMethod(methodName);
            expand.invoke(service);
            Log.i(TAG,"");
        } catch (Exception e) {
            Log.e(TAG,"Expand NotificationPanel Error");
            e.printStackTrace();
        }
    }
    /**
     * 获取现在农历的日期
     */
    public static String getDayLunar() {
        ChineseCale lunarCalender = new ChineseCale();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DATE);
        String lunarAnimal = lunarCalender.animalsYear(year);
        String lunarGanZhi = lunarCalender.cyclical(year,month,day);
        String lunarString = lunarCalender.getLunarString(year, month, day);
        return "农历"+"  "+lunarGanZhi+lunarAnimal+" "+lunarString;
    }

    public void ifHasDefaultActivity(){
        PackageManager pm = getPackageManager();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("tel://10010"));
        ResolveInfo info = pm.resolveActivity(intent,PackageManager.MATCH_DEFAULT_ONLY);
        Log.e(TAG,"getDefaultActivity info=" + info + ";pkgName = " + info.activityInfo.packageName);
    }

    private void showAdminGrant(){
        Intent grant_it = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        grant_it.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,mAdminName);
        grant_it.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,R.string.admin_summary);
        startActivityForResult(grant_it,ENABLE_ADMIN);
    }

    private void printDayOfWeek() {
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.SUNDAY:
                week="周日";
                break;
            case Calendar.MONDAY:
                week="周一";
                break;
            case Calendar.TUESDAY:
                week="周二";
                break;
            case Calendar.WEDNESDAY:
                week="周三";
                break;
            case Calendar.THURSDAY:
                week="周四";
                break;
            case Calendar.FRIDAY:
                week="周五";
                break;
            case Calendar.SATURDAY:
                week="周六";
                break;
            default:
                break;
        }
    }



}