package org.exthmui.microlauncher;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "ML_MainActivity";
    private final static String dateFormat = "yyyy年MM月dd日";
    private String week;
    private Calendar c = Calendar.getInstance();
    private Context mContext;
    private MediaSessionManager mMediaSessionManager;
    private MediaController mMediaController;
    Class serviceManagerClass;
    Button menu,contact;
    ImageButton prev,play,next;
    TextView dateView,lunar,title,singer;;
    ImageView cover;
    Intent it = new Intent();

    private MediaController.Callback mMediaCallback = new MediaController.Callback() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onPlaybackStateChanged(@Nullable PlaybackState state) {
            super.onPlaybackStateChanged(state);
            if (state != null) updatePlayPauseStatus(state.getState());
        }

        @Override
        public void onSessionDestroyed() {
            super.onSessionDestroyed();
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onMetadataChanged(@Nullable MediaMetadata metadata) {
            updateMetaData(metadata);
        }
    };

    private MediaSessionManager.OnActiveSessionsChangedListener onActiveSessionsChangedListener = new MediaSessionManager.OnActiveSessionsChangedListener() {

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onActiveSessionsChanged(@Nullable List<MediaController> controllers) {
            if (mMediaController != null) mMediaController.unregisterCallback(mMediaCallback);
            if (controllers == null) return;
            for (MediaController controller : controllers) {
                mMediaController = controller;
                if (getMediaControllerPlaybackState(controller) == PlaybackState.STATE_PLAYING) {
                    break;
                }
            }
            if (mMediaController != null) {
                mMediaController.registerCallback(mMediaCallback);
                mMediaCallback.onMetadataChanged(mMediaController.getMetadata());
                mMediaCallback.onPlaybackStateChanged(mMediaController.getPlaybackState());
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMediaSessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
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
        title=findViewById(R.id.title);
        singer=findViewById(R.id.singer);
        prev=findViewById(R.id.prev);
        play=findViewById(R.id.play);
        next=findViewById(R.id.next);
        cover=findViewById(R.id.cover);
        prev.setOnClickListener(new Click());
        play.setOnClickListener(new Click());
        next.setOnClickListener(new Click());
        bindMediaListeners();
        contact.setOnClickListener(new mClick());
        menu.setOnClickListener(new mClick());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void bindMediaListeners() {
        ComponentName listener = new ComponentName(this, NotiGeter.class);
        mMediaSessionManager.addOnActiveSessionsChangedListener(onActiveSessionsChangedListener, listener);
        onActiveSessionsChangedListener.onActiveSessionsChanged(mMediaSessionManager.getActiveSessions(listener));
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
               } catch (ClassNotFoundException | IllegalArgumentException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | RemoteException e) {
                   e.printStackTrace();
               }
               return true;}
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void updateMetaData(MediaMetadata mediaMetadata) {
        if (mediaMetadata == null) return;
        title.setText(mediaMetadata.getText(MediaMetadata.METADATA_KEY_TITLE));
        singer.setText(mediaMetadata.getString(MediaMetadata.METADATA_KEY_ARTIST));
        // Try to get album cover
        if (!(setAlbumImgFromBitmap(mediaMetadata.getBitmap(MediaMetadata.METADATA_KEY_ART)) ||
                setAlbumImgFromBitmap(mediaMetadata.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART)) ||
                setAlbumImgFromUri(mediaMetadata.getString(MediaMetadata.METADATA_KEY_ART_URI)) ||
                setAlbumImgFromUri(mediaMetadata.getString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI)))) {
            cover.setImageResource(R.drawable.default_album_cover);
        }
    }

    private boolean setAlbumImgFromBitmap(Bitmap bitmap) {
        if (bitmap == null) return false;
        cover.setImageBitmap(bitmap);
        return true;
    }

    private boolean setAlbumImgFromUri(String uri) {
        try {
            Uri albumUri = Uri.parse(uri);
            InputStream is = mContext.getContentResolver().openInputStream(albumUri);
            if (is != null) {
                cover.setImageBitmap(BitmapFactory.decodeStream(is));
                is.close();
            }
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void updatePlayPauseStatus(int state) {
        if (state == PlaybackState.STATE_PLAYING) {
            play.setImageResource(R.drawable.ic_media_pause);
        } else {
            play.setImageResource(R.drawable.ic_media_play);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private int getMediaControllerPlaybackState(MediaController controller) {
        if (controller != null) {
            final PlaybackState playbackState = controller.getPlaybackState();
            if (playbackState != null) {
                return playbackState.getState();
            }
        }
        return PlaybackState.STATE_NONE;
    }

    class Click implements View.OnClickListener{

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onClick(View v) {
            if (v == prev) {
                mMediaController.getTransportControls().skipToPrevious();
            } else if (v == next) {
                mMediaController.getTransportControls().skipToNext();
            } else if (v == play) {
                if (getMediaControllerPlaybackState(mMediaController) == PlaybackState.STATE_PLAYING) {
                    mMediaController.getTransportControls().pause();
                } else {
                    mMediaController.getTransportControls().play();
                }
            }
        }
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