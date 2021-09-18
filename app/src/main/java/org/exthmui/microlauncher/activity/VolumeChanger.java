package org.exthmui.microlauncher.activity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.exthmui.microlauncher.R;

import es.dmoral.toasty.Toasty;

public class VolumeChanger extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
    TextView media,ring,alarm;
    SeekBar media_sek,ring_sek,alarm_sek;
    private AudioManager mAudioManager;
    private int maxVolume, currentVolume;
    private boolean lock_enable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.volume_dialog);
        media=findViewById(R.id.vol_media_text);
        ring=findViewById(R.id.vol_ring_text);
        alarm=findViewById(R.id.vol_alarm_text);
        media_sek=findViewById(R.id.vol_media_seek);
        ring_sek=findViewById(R.id.vol_ring_seek);
        alarm_sek=findViewById(R.id.vol_alarm_seek);
        loadSettings();
        //获取系统的Audio管理者
        media_ctrl();
        ring_ctrl();
        alarm_ctrl();
    }
    public void media_ctrl(){
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
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
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
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
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
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
}
