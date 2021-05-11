package org.exthmui.microlauncher;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "ML_MainActivity";
    private final static String dateFormat = "yyyy年MM月dd日";
    private String week;
    private Calendar c = Calendar.getInstance();
    Class serviceManagerClass;
    Button menu,contact;
    TextView dateView,lunar;
    Intent it = new Intent();
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
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date date = new Date(System.currentTimeMillis());
        dateView=findViewById(R.id.date_text);
        lunar=findViewById(R.id.lunar_cale);
        printDayOfWeek();
        dateView.setText(sdf.format(date)+" "+week);
        lunar.setText(getDayLunar());
        menu=findViewById(R.id.menu);
        contact=findViewById(R.id.contact);
        contact.setOnClickListener(new mClick());
        menu.setOnClickListener(new mClick());
    }

    private void printDayOfWeek() {
        switch (c.get(Calendar.DAY_OF_WEEK)) {
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

    class mClick implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.contact:
                    Intent i = new Intent();
                    i.setClassName("com.android.contacts",
                            "com.android.contacts.activities.PeopleActivity");
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
               it.setClassName("com.android.contacts",
                       "com.android.contacts.activities.PeopleActivity");
               startActivity(it);
               return true;}
            else if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN){
                String methodName = (Build.VERSION.SDK_INT <= 16) ? "expand" : "expandNotificationsPanel";
                doInStatusBar(getApplicationContext(), methodName);
                return true;}
            else if (keyCode == KeyEvent.KEYCODE_DPAD_UP){
                it.setClassName("com.android.settings",
                        "com.android.settings.Settings");
                startActivity(it);
                return true;}
            else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                it.setClassName("com.android.music","com.android.music.MusicBrowserActivity");
                startActivity(it);
                Log.d(TAG,"5,4,3,2,1,三倍ice cream!!!!!");
           return true;}
            else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
                it.setClassName("com.android.mms","com.android.mms.ui.ConversationList");
                startActivity(it);
           return true;}
           else if (keyCode == KeyEvent.KEYCODE_MENU ){
               Intent menu_it = new Intent(MainActivity.this, MenuActivity.class);
               startActivity(menu_it);
               return true;}
           //突然想起来得把音量控制面板绑定到#键上
           else if (keyCode == KeyEvent.KEYCODE_POUND ){
               Intent vol_it = new Intent(MainActivity.this, VolumeChanger.class);
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
            else if (keyCode == KeyEvent.KEYCODE_STAR) {
               Log.d(TAG,"打开最近任务界面");
                try {
                   serviceManagerClass = Class.forName("android.os.ServiceManager");
                   Method getService = serviceManagerClass.getMethod("getService",
                           String.class);
                   IBinder retbinder = (IBinder) getService.invoke(
                           serviceManagerClass, "statusbar");
                   Class statusBarClass = Class.forName(retbinder
                           .getInterfaceDescriptor());
                   Object statusBarObject = statusBarClass.getClasses()[0].getMethod(
                           "asInterface", IBinder.class).invoke(null,
                           new Object[] { retbinder });
                   Method clearAll = statusBarClass.getMethod("toggleRecentApps");
                   clearAll.setAccessible(true);
                   clearAll.invoke(statusBarObject);
               } catch (ClassNotFoundException e) {
                   e.printStackTrace();
               } catch (NoSuchMethodException e) {
                   e.printStackTrace();
               } catch (IllegalArgumentException e) {
                   e.printStackTrace();
               } catch (IllegalAccessException e) {
                   e.printStackTrace();
               } catch (InvocationTargetException e) {
                   e.printStackTrace();
               } catch (RemoteException e) {
                   e.printStackTrace();
               }
               return true;}
        return false;
    }
    private static void doInStatusBar(Context mContext, String methodName) {
        try {
            Object service = mContext.getSystemService("statusbar");
            Method expand = service.getClass().getMethod(methodName);
            expand.invoke(service);
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

}