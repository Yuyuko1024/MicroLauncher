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
import org.exthmui.microlauncher.duoqin.databinding.VolumeDialogBinding;

public class VolumeChanger extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
    private VolumeDialogBinding binding;
    private AudioManager mAudioManager;
    private NotificationManager notificationManager;
    private int maxVolume, currentVolume, modeStatus;
    private boolean lock_enable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = VolumeDialogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        binding.volumeBack.setOnClickListener(v -> finish());
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
        Log.d("Mode", String.valueOf(modeStatus));
        if (modeStatus == AudioManager.RINGER_MODE_NORMAL){
            binding.modeToggleGroup.check(R.id.mode_normal);
        } else if (modeStatus == AudioManager.RINGER_MODE_VIBRATE) {
            binding.modeToggleGroup.check(R.id.mode_vibrate);
        } else {
            binding.modeToggleGroup.check(R.id.mode_dnd);
        }
    }

    private void initModeEvent() {
        binding.modeToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.mode_normal) {
                    // 设置正常模式
                    mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,6,0);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_RING, 4, 0);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, 4, 0);
                    binding.volMediaSeek.setProgress(6);
                    binding.volRingSeek.setProgress(4);
                    binding.volAlarmSeek.setProgress(4);
                } else if (checkedId == R.id.mode_vibrate) {
                    // 设置震动模式
                    mAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                    binding.volRingSeek.setProgress(0);
                } else if (checkedId == R.id.mode_dnd) {
                    // 设置免打扰模式
                    mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_RING, 0, 0);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, 0, 0);
                    binding.volRingSeek.setProgress(0);
                    binding.volAlarmSeek.setProgress(0);
                }
            }
        });
    }

    private void PermissionGrant(){
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (!notificationManager.isNotificationPolicyAccessGranted()) {
            Toasty.info(getApplicationContext(),"请授予勿扰权限以用于开关勿扰权限",Toasty.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivity(intent);
        }
    }

    public void media_ctrl(){
        //最大音量
        maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //当前音量
        currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        //seekbar设置最大值为最大音量，这样设置当前进度时不用换算百分比了
        binding.volMediaSeek.setMax(maxVolume);
        //seekbar设置当前进度为当前音量
        binding.volMediaText.setText(currentVolume + "");
        binding.volMediaSeek.setProgress(currentVolume);

        //seekbar设置拖动监听
        binding.volMediaSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar arg0, int progress, boolean fromUser) {
                //设置媒体音量为当前seekbar进度
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                binding.volMediaText.setText(currentVolume + "");
                binding.volMediaSeek.setProgress(currentVolume);
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
        binding.volRingSeek.setMax(maxVolume);
        //seekbar设置当前进度为当前音量
        binding.volRingText.setText(currentVolume + "");
        binding.volRingSeek.setProgress(currentVolume);

        //seekbar设置拖动监听
        binding.volRingSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar arg0, int progress, boolean fromUser) {
                //设置媒体音量为当前seekbar进度
                mAudioManager.setStreamVolume(AudioManager.STREAM_RING, progress, 0);
                currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
                binding.volRingText.setText(currentVolume + "");
                binding.volRingSeek.setProgress(currentVolume);
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
        binding.volAlarmSeek.setMax(maxVolume);
        //seekbar设置当前进度为当前音量
        binding.volAlarmText.setText(currentVolume + "");
        binding.volAlarmSeek.setProgress(currentVolume);

        //seekbar设置拖动监听
        binding.volAlarmSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar arg0, int progress, boolean fromUser) {
                //设置媒体音量为当前seekbar进度
                mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, progress, 0);
                currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);
                binding.volAlarmText.setText(currentVolume + "");
                binding.volAlarmSeek.setProgress(currentVolume);
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
        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName()+"_preferences",Context.MODE_PRIVATE);
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
