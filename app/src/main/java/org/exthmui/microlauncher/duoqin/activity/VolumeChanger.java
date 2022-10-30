package org.exthmui.microlauncher.duoqin.activity;

import android.app.NotificationManager;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButtonToggleGroup;
import es.dmoral.toasty.Toasty;
import org.exthmui.microlauncher.duoqin.R;

public class VolumeChanger extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
    TextView media,ring,alarm,back;
    SeekBar media_sek,ring_sek,alarm_sek;
    private AudioManager mAudioManager;
    private MaterialButtonToggleGroup mModeToggleView;
    private NotificationManager notificationManager;
    private int maxVolume, currentVolume, modeStatus;
    private boolean lock_enable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.volume_dialog);
        media=findViewById(R.id.vol_media_text);
        ring=findViewById(R.id.vol_ring_text);
        alarm=findViewById(R.id.vol_alarm_text);
        back=findViewById(R.id.volume_back);
        media_sek=findViewById(R.id.vol_media_seek);
        ring_sek=findViewById(R.id.vol_ring_seek);
        alarm_sek=findViewById(R.id.vol_alarm_seek);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        back.setOnClickListener(v -> finish());
        loadSettings();
        //获取系统的Audio管理者
        media_ctrl();
        ring_ctrl();
        alarm_ctrl();
        PermissionGrant();
        initModeToggleView();
        initModeEvent();
    }

    private void initModeToggleView() {
        mModeToggleView = findViewById(R.id.mode_toggle_group);
        Log.d("Mode", String.valueOf(modeStatus));
        if (modeStatus == AudioManager.RINGER_MODE_NORMAL){
            mModeToggleView.check(R.id.mode_normal);
        } else if (modeStatus == AudioManager.RINGER_MODE_VIBRATE) {
            mModeToggleView.check(R.id.mode_vibrate);
        } else {
            mModeToggleView.check(R.id.mode_dnd);
        }
    }

    private void initModeEvent() {
        mModeToggleView.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            switch (checkedId){
                case R.id.mode_normal:
                    mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    //设置正常模式，媒体6，铃声和闹钟为4
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,6,0);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_RING, 4, 0);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, 4, 0);
                    media_sek.setProgress(6);
                    ring_sek.setProgress(4);
                    alarm_sek.setProgress(4);
                    break;
                case R.id.mode_vibrate:
                    mAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                    //只设置铃声为0
                    ring_sek.setProgress(0);
                    break;
                case R.id.mode_dnd:
                    mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    //将铃声，闹钟音量全部设为0
                    mAudioManager.setStreamVolume(AudioManager.STREAM_RING, 0, 0);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, 0, 0);
                    ring_sek.setProgress(0);
                    alarm_sek.setProgress(0);
                    break;
            }
        });
    }

    private void PermissionGrant(){
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !notificationManager.isNotificationPolicyAccessGranted()) {
            Toasty.info(getApplicationContext(),"请授予勿扰权限以用于开关勿扰权限",Toasty.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS));
        }
    }

    public void media_ctrl(){
        //最大音量
        maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //当前音量
        currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        //seekbar设置最大值为最大音量，这样设置当前进度时不用换算百分比了
        media_sek.setMax(maxVolume);
        //seekbar设置当前进度为当前音量
        media.setText(currentVolume + "");
        media_sek.setProgress(currentVolume);

        //seekbar设置拖动监听
        media_sek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar arg0, int progress, boolean fromUser) {
                //设置媒体音量为当前seekbar进度
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                media.setText(currentVolume + "");
                media_sek.setProgress(currentVolume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub


            }
        });
    }
    public void ring_ctrl(){
        //最大音量
        maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
        //当前音量
        currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
        //seekbar设置最大值为最大音量，这样设置当前进度时不用换算百分比了
        ring_sek.setMax(maxVolume);
        //seekbar设置当前进度为当前音量
        ring.setText(currentVolume + "");
        ring_sek.setProgress(currentVolume);

        //seekbar设置拖动监听
        ring_sek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar arg0, int progress, boolean fromUser) {
                //设置媒体音量为当前seekbar进度
                mAudioManager.setStreamVolume(AudioManager.STREAM_RING, progress, 0);
                currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
                ring.setText(currentVolume + "");
                ring_sek.setProgress(currentVolume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });
    }
    public void alarm_ctrl(){
        //最大音量
        maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        //当前音量
        currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);
        //seekbar设置最大值为最大音量，这样设置当前进度时不用换算百分比了
        alarm_sek.setMax(maxVolume);
        //seekbar设置当前进度为当前音量
        alarm.setText(currentVolume + "");
        alarm_sek.setProgress(currentVolume);

        //seekbar设置拖动监听
        alarm_sek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar arg0, int progress, boolean fromUser) {
                //设置媒体音量为当前seekbar进度
                mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, progress, 0);
                currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);
                alarm.setText(currentVolume + "");
                alarm_sek.setProgress(currentVolume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
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

    private void loadSettings(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        lock_enable = (sharedPreferences.getBoolean("preference_main_lockscreen",true));
        modeStatus = mAudioManager.getRingerMode();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("preference_main_lockscreen")){
            lock_enable = (sharedPreferences.getBoolean("preference_main_lockscreen",true));
        }
    }
}
