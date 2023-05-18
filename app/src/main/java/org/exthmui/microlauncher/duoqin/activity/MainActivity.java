package org.exthmui.microlauncher.duoqin.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

import org.exthmui.microlauncher.duoqin.R;
import org.exthmui.microlauncher.duoqin.databinding.ActivityMainBinding;
import org.exthmui.microlauncher.duoqin.misc.ChineseCale;
import org.exthmui.microlauncher.duoqin.widgets.CarrierTextView;
import org.exthmui.microlauncher.duoqin.widgets.ClockViewManager;
import org.exthmui.microlauncher.duoqin.widgets.DateTextView;
import org.exthmui.microlauncher.duoqin.widgets.LunarDateTextView;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private final static String TAG = "ML_MainActivity";
    private static final int grant_int=1;
    private boolean carrier_enable = true;
    private boolean xiaoai_enable = true;
    private boolean dialpad_enable = true;
    private boolean torch = false;
    private CameraManager manager;
    private ActivityMainBinding mainBinding;
    private ClockViewManager clockViewManager;
    private DateTextView date;
    private LunarDateTextView lunarDate;
    private CarrierTextView carrier;
    String pound_func;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        showFirstLogcat();
        checkDevice();
        GrantPermissions();
        clockViewManager = new ClockViewManager(mainBinding.clock.datesLayout);
        mainBinding.contact.setOnClickListener(new mClick());
        mainBinding.menu.setOnClickListener(new mClick());
        date = new DateTextView(this);
        lunarDate = new LunarDateTextView(this);
        carrier = new CarrierTextView(this);
        clockViewManager.insertOrUpdateView(1, date);
        loadSettings();
    }

    private void GrantPermissions(){
        if(PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA)){
            String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
            ActivityCompat.requestPermissions(this, perms,grant_int);
        }
    }

    private void loadSettings(){
        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName()+"_preferences",Context.MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        boolean lunar_isEnable= (sharedPreferences.getBoolean("switch_preference_lunar",true));
        if(lunar_isEnable){
            Log.d(TAG, "Enable lunar");
            clockViewManager.insertOrUpdateView(2, lunarDate);
        }else{
            Log.d(TAG, "Disable lunar");
            clockViewManager.removeView(2);
        }
        carrier_enable = sharedPreferences.getBoolean("switch_preference_carrier_name",true);
        if(carrier_enable){
            Log.d(TAG, "Enable carrier name");
            clockViewManager.insertOrUpdateView(3,carrier);
        }else{
            Log.d(TAG, "Disable carrier name");
            clockViewManager.removeView(3);
        }
        String clock_locate = (sharedPreferences.getString("list_preference_clock_locate","reimu"));
        setClockLocate(clock_locate);
        pound_func = (sharedPreferences.getString("preference_pound_func","volume"));
        String clock_size = (sharedPreferences.getString("list_preference_clock_size","58"));
        mainBinding.clock.textClock.setTextSize(Float.parseFloat(clock_size));
        xiaoai_enable = sharedPreferences.getBoolean("preference_main_xiaoai_ai",true);
        dialpad_enable = sharedPreferences.getBoolean("preference_dial_pad",true);
    }

    private void setClockLocate(String clockLocate) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mainBinding.clock.textClock.getLayoutParams();
        switch (clockLocate){
            case "left":
                params.gravity = Gravity.START;
                break;
            case "right":
                params.gravity = Gravity.END;
                break;
        }
        mainBinding.clock.textClock.setLayoutParams(params);
        for (int i = 1; i < 4; i++) {
            Log.d(TAG, "setClockLocate: "+i);
            clockViewManager.setLayoutParams(i, params);
        }
    }

    private void checkDevice(){
        Log.d(TAG, "checkDevice: "+Build.BOARD);
        if(!Build.BOARD.equals("k61v1_64_bsp")){
            Toasty.info(this,"非多亲设备，部分功能可能无法使用。",Toasty.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case "switch_preference_lunar":
                boolean lunar_isEnable = (sharedPreferences.getBoolean("switch_preference_lunar", true));
                if (lunar_isEnable) {
                    Log.d(TAG, "Enable lunar");
                    clockViewManager.insertOrUpdateView(2, lunarDate);
                } else {
                    Log.d(TAG, "Disable lunar");
                    clockViewManager.removeView(2);
                }
                break;
            case "list_preference_clock_locate":
                String clock_locate = (sharedPreferences.getString("list_preference_clock_locate", "reimu"));
                setClockLocate(clock_locate);
                break;
            case "list_preference_clock_size":
                String clock_size = (sharedPreferences.getString("list_preference_clock_size", "58"));
                mainBinding.clock.textClock.setTextSize(Float.parseFloat(clock_size));
                break;
            case  "switch_preference_carrier_name":
                carrier_enable = sharedPreferences.getBoolean("switch_preference_carrier_name",true);
                if(carrier_enable){
                    Log.d(TAG,"Enable carrier name");
                    clockViewManager.insertOrUpdateView(3,carrier);
                }else{
                    Log.d(TAG,"Disable carrier name");
                    clockViewManager.removeView(3);
                }
                break;
            case "preference_main_xiaoai_ai":
                xiaoai_enable = sharedPreferences.getBoolean("preference_main_xiaoai_ai",true);
                break;
            case "preference_dial_pad":
                dialpad_enable = sharedPreferences.getBoolean("preference_dial_pad",true);
                break;
            case "preference_pound_func":
                pound_func = sharedPreferences.getString("preference_pound_func","volume");
                break;
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
                    Intent menu_it = new Intent(MainActivity.this, AppListActivity.class);
                    startActivity(menu_it);
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        // TODO: 实现其他用户离开Activity焦点功能
    }

    public boolean onKeyUp(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent it = new Intent();
            it.setAction("android.intent.action.MAIN");
            it.addCategory("android.intent.category.APP_CONTACTS");
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(it);
            return true;}
        return false;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG,"这个按键的KeyCode是 "+keyCode);
           if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN){
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
                try{
                    Intent it = new Intent();
                    it.setAction("android.intent.action.MAIN");
                    it.addCategory("android.intent.category.APP_BROWSER");
                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(it);
                    Log.d(TAG,"5,4,3,2,1,三倍ice cream!!!!!");
                }catch (Exception e){
                    Log.d(TAG,"没有找到系统浏览器或者系统浏览器被禁用");
                }
           return true;}
            else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
                try{
                    Intent it = new Intent();
                    it.setAction("android.intent.action.MAIN");
                    it.addCategory("android.intent.category.APP_MESSAGING");
                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(it);
                }catch (Exception e){
                    Log.d(TAG,"没有找到系统短信或者系统短信被禁用");
                }
           return true;}
           else if (keyCode == KeyEvent.KEYCODE_MENU ){
               Snackbar.make(mainBinding.getRoot(),R.string.loading,Snackbar.LENGTH_SHORT).show();
               Timer timer = new Timer();
               timer.schedule(new TimerTask() {
                   @Override
                   public void run() {
                       Intent menu_it = new Intent(MainActivity.this, AppListActivity.class);
                       startActivity(menu_it);
                   }
               },500); // 延时0.5秒，不加延时的话应用列表的菜单误触我很难顶啊QAQ
               return true;}
           //突然想起来得把音量控制面板绑定到#键上
           else if (keyCode == KeyEvent.KEYCODE_POUND ){
               if(pound_func.equals("volume")){
                   Intent vol_it = new Intent(MainActivity.this, VolumeChanger.class);
                   vol_it.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                   startActivity(vol_it);
               }else{
                   if(PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA)){
                       if(torch){
                           try {
                               manager = (CameraManager) getApplicationContext().getSystemService(Context.CAMERA_SERVICE);
                               manager.setTorchMode("0", true);// "0"是主闪光灯
                           } catch (CameraAccessException e) {
                               e.printStackTrace();
                           }
                           torch=false;
                       }else{
                           try {
                               manager = (CameraManager) getApplicationContext().getSystemService(Context.CAMERA_SERVICE);
                               manager.setTorchMode("0", false);
                               manager = null;
                           } catch (Exception e) {
                               e.printStackTrace();
                           }
                           torch=true;
                       }
                   }else{
                       Toasty.error(this,R.string.permission_denied,Toasty.LENGTH_LONG).show();
                   }
               }
               return true;}
           else if(keyCode >=7 && keyCode <= 16){
               if(dialpad_enable){
                   try{
                       Intent it = new Intent();
                       it.setClassName("com.android.dialer","com.duoqin.dialer.DialpadActivity");
                       it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                       startActivity(it);
                   }catch (Exception e){
                       Intent it = new Intent();
                       it.setClassName("com.android.dialer","com.android.dialer.main.impl.MainActivity");
                       it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                       startActivity(it);
                       Log.e(TAG,"没有找到拨号盘");
                   }
               }
           }
            else if(keyCode == KeyEvent.KEYCODE_STAR){
                if(xiaoai_enable){
                    try{
                        Intent ai_intent = new Intent();
                        ai_intent.setClassName("com.duoqin.ai","com.duoqin.ai.MainActivity");
                        ai_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(ai_intent);
                    }catch (Exception e){
                        e.printStackTrace();
                        Toasty.error(getApplicationContext(),R.string.err_pkg_not_found,Toasty.LENGTH_LONG).show();
                    }
                }
           return true;}
        return false;
    }
    /*
    * 下拉通知栏
    */
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

    void showFirstLogcat(){
        Log.e(TAG, getPackageName()+" onCreate: Logcat start......");
        Log.i(TAG, "===================================================");
        Log.i(TAG, " ________ ___  ___  _____ ______   ________     \n");
        Log.i(TAG, "|\\  _____\\\\  \\|\\  \\|\\   _ \\  _   \\|\\   __  \\    \n");
        Log.i(TAG,"\\ \\  \\__/\\ \\  \\\\\\  \\ \\  \\\\\\__\\ \\  \\ \\  \\|\\  \\   \n");
        Log.i(TAG," \\ \\   __\\\\ \\  \\\\\\  \\ \\  \\\\|__| \\  \\ \\  \\\\\\  \\  \n");
        Log.i(TAG,"  \\ \\  \\_| \\ \\  \\\\\\  \\ \\  \\    \\ \\  \\ \\  \\\\\\  \\ \n");
        Log.i(TAG,"   \\ \\__\\   \\ \\_______\\ \\__\\    \\ \\__\\ \\_______\\\n");
        Log.i(TAG,"    \\|__|    \\|_______|\\|__|     \\|__|\\|_______|\n");
        Log.i(TAG, "===================================================");
        Log.e(TAG, " ᗜˬᗜ  Fumo enabled the Debug Mode.start to debugging~");
    }
}