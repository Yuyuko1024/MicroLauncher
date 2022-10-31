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
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

import org.exthmui.microlauncher.duoqin.R;
import org.exthmui.microlauncher.duoqin.misc.ChineseCale;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private final static String TAG = "ML_MainActivity";
    private final static String dateFormat = "yyyy年MM月dd日";
    private final Calendar calendar = Calendar.getInstance();
    private String week;
    private static final int grant_int=1;
    private boolean carrier_enable = true;
    private boolean xiaoai_enable = true;
    private boolean dialpad_enable = true;
    private boolean torch = false;
    private CameraManager manager;
    String pound_func;
    TextView dateView,menu,contact;
    LinearLayout clock;
    TextClock text_clock;

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showFirstLogcat();
        checkDevice();
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE); //使背景图与状态栏融合到一起，这里需要在setcontentview前执行
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date date = new Date(System.currentTimeMillis());
        dateView = findViewById(R.id.date_text);
        clock = findViewById(R.id.clock);
        text_clock = findViewById(R.id.text_clock);
        printDayOfWeek();
        dateView.setText(sdf.format(date) + " " + week);
        menu = findViewById(R.id.menu);
        contact = findViewById(R.id.contact);
        contact.setOnClickListener(new mClick());
        menu.setOnClickListener(new mClick());
        GrantPermissions();
        clock.post(() -> {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) clock.getLayoutParams();
            params.bottomMargin = menu.getHeight();
            clock.setLayoutParams(params);
        });
        Log.e(TAG, "Carrier Name is " + getCarrierName(getApplicationContext()));
        loadSettings();
    }

    private void GrantPermissions(){
        if(PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA)){
            String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
            ActivityCompat.requestPermissions(this, perms,grant_int);
        }
    }

    private void loadSettings(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        boolean lunar_isEnable= (sharedPreferences.getBoolean("switch_preference_lunar",true));
        if(lunar_isEnable){
            Log.d(TAG, "Enable lunar");
            removeItem("lunar");
            TextView lunar = new TextView(this);
            lunar.setTextColor(Color.WHITE);
            lunar.setTextSize(14);
            lunar.setId(R.id.lunar);
            clock.addView(lunar, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lunar.setText(getDayLunar());
        }else{
            Log.d(TAG, "Disable lunar");
            removeItem("lunar");
        }
        String clock_locate = (sharedPreferences.getString("list_preference_clock_locate","reimu"));
        setClockLocate(clock_locate);
        pound_func = (sharedPreferences.getString("preference_pound_func","volume"));
        String clock_size = (sharedPreferences.getString("list_preference_clock_size","58"));
        text_clock.setTextSize(Float.parseFloat(clock_size));
        carrier_enable = sharedPreferences.getBoolean("switch_preference_carrier_name",true);
        xiaoai_enable = sharedPreferences.getBoolean("preference_main_xiaoai_ai",true);
        dialpad_enable = sharedPreferences.getBoolean("preference_dial_pad",true);
        if(carrier_enable){
            Log.d(TAG, "Enable carrier name");
            removeItem("carrier");
            TextView carrier_name = new TextView(this);
            carrier_name.setTextColor(Color.WHITE);
            carrier_name.setTextSize(16);
            carrier_name.setId(R.id.carrier_name);
            clock.addView(carrier_name, LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            carrier_name.setText(getCarrierName(getApplicationContext()));
        }else{
            Log.d(TAG, "Disable carrier name");
            removeItem("carrier");
        }
    }

    private void setClockLocate(String clockLocate) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) clock.getLayoutParams();
        switch (clockLocate){
            case "reimu":
                clock.setGravity(Gravity.START);
                params.gravity = Gravity.START | Gravity.TOP;
                break;
            case "marisa":
                clock.setGravity(Gravity.START);
                params.gravity = Gravity.START | Gravity.BOTTOM;
                break;
            case "renko":
                clock.setGravity(Gravity.END);
                params.gravity = Gravity.END | Gravity.TOP;
                break;
            case "maribel":
                clock.setGravity(Gravity.END);
                params.gravity = Gravity.END | Gravity.BOTTOM;
                break;
        }
        clock.setLayoutParams(params);
    }

    private void checkDevice(){
        Log.d(TAG, "checkDevice: "+Build.BOARD);
        if(!Build.BOARD.equals("k61v1_64_bsp")){
            Toasty.info(this,"非多亲设备，部分功能可能无法使用。",Toasty.LENGTH_SHORT).show();
        }
    }

    public static String getCarrierName(Context context){
        TelephonyManager teleMgr  = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return teleMgr.getSimOperatorName();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case "switch_preference_lunar":
                boolean lunar_isEnable = (sharedPreferences.getBoolean("switch_preference_lunar", true));
                if (lunar_isEnable) {
                    Log.d(TAG, "Enable lunar");
                    removeItem("lunar");
                    TextView lunar = new TextView(this);
                    lunar.setTextColor(Color.WHITE);
                    lunar.setTextSize(14);
                    lunar.setId(R.id.lunar);
                    clock.addView(lunar, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lunar.setText(getDayLunar());
                } else {
                    Log.d(TAG, "Disable lunar");
                    removeItem("lunar");
                }
                break;
            case "list_preference_clock_locate":
                String clock_locate = (sharedPreferences.getString("list_preference_clock_locate", "reimu"));
                setClockLocate(clock_locate);
                break;
            case "list_preference_clock_size":
                String clock_size = (sharedPreferences.getString("list_preference_clock_size", "58"));
                text_clock.setTextSize(Float.parseFloat(clock_size));
                break;
            case  "switch_preference_carrier_name":
                carrier_enable = sharedPreferences.getBoolean("switch_preference_carrier_name",true);
                if(carrier_enable){
                    Log.d(TAG,"Enable carrier name");
                    removeItem("carrier");
                    TextView carrier_name = new TextView(this);
                    carrier_name.setTextColor(Color.WHITE);
                    carrier_name.setTextSize(16);
                    carrier_name.setId(R.id.carrier_name);
                    clock.addView(carrier_name, LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    carrier_name.setText(getCarrierName(getApplicationContext()));
                }else{
                    Log.d(TAG,"Disable carrier name");
                    removeItem("carrier");
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

    private void removeItem(String item){
        if(item.equals("lunar")){
            try{
                clock.removeViewAt(R.id.lunar);
            }catch (Exception e){
                Log.e(TAG,"该控件或许已被移除");
            }
        }else if(item.equals("carrier")){
            try{
                clock.removeViewAt(R.id.carrier_name);
            }catch (Exception e){
                Log.e(TAG,"该控件或许已被移除");
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
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
               Snackbar.make(dateView,R.string.loading,Snackbar.LENGTH_SHORT).show();
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