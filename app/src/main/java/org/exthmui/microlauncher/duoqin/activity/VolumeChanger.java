package org.exthmui.microlauncher.duoqin.activity;

import static org.exthmui.microlauncher.duoqin.utils.Constants.launcherSettingsPref;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.exthmui.microlauncher.duoqin.BuildConfig;
import org.exthmui.microlauncher.duoqin.R;
import org.exthmui.microlauncher.duoqin.databinding.VolumeDialogBinding;

import es.dmoral.toasty.Toasty;

public class VolumeChanger extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener{
    private VolumeDialogBinding binding;
    private AudioManager mAudioManager;
    private NotificationManager notificationManager;
    private SharedPreferences sharedPreferences;
    private int maxVolume, currentVolume, modeStatus;
    private int memMediaVol, memRingVol, memAlarmVol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = VolumeDialogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        binding.volumeBack.setOnClickListener(v -> finish());
        sharedPreferences = getSharedPreferences(launcherSettingsPref,Context.MODE_PRIVATE);
        loadSettings(sharedPreferences);
        //获取系统的Audio管理者
        initVolumeControl();
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
                    modeStatus = AudioManager.RINGER_MODE_NORMAL;
                    // 设置正常模式
                    mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,memMediaVol,0);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_RING, memRingVol, 0);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, memAlarmVol, 0);
                    binding.volMediaSeek.setProgress(memMediaVol);
                    binding.volRingSeek.setProgress(memRingVol);
                    binding.volAlarmSeek.setProgress(memAlarmVol);
                } else if (checkedId == R.id.mode_vibrate) {
                    modeStatus = AudioManager.RINGER_MODE_VIBRATE;
                    // 设置震动模式
                    mAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                    binding.volRingSeek.setProgress(0);
                } else if (checkedId == R.id.mode_dnd) {
                    modeStatus = AudioManager.RINGER_MODE_SILENT;
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
            Toasty.info(getApplicationContext(),R.string.permission_dnd_require,Toasty.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivity(intent);
        }
    }

    private void initVolumeControl(){
        setDeviceVolume(AudioManager.STREAM_MUSIC, binding.volMediaSeek, binding.volMediaText);
        setDeviceVolume(AudioManager.STREAM_RING, binding.volRingSeek, binding.volRingText);
        setDeviceVolume(AudioManager.STREAM_ALARM, binding.volAlarmSeek, binding.volAlarmText);
    }

    private void setDeviceVolume(int streamType, SeekBar seekBar, TextView textView) {
        //获取最大和当前音量
        maxVolume = mAudioManager.getStreamMaxVolume(streamType);
        currentVolume = mAudioManager.getStreamVolume(streamType);

        //设置seekbar最大值和当前进度
        seekBar.setMax(maxVolume);
        textView.setText(currentVolume + "");
        seekBar.setProgress(currentVolume);

        //设置拖动监听
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //设置音量
                mAudioManager.setStreamVolume(streamType, progress, 0);
                currentVolume = mAudioManager.getStreamVolume(streamType);
                textView.setText(currentVolume + "");
                seekBar.setProgress(currentVolume);
                if (modeStatus == AudioManager.RINGER_MODE_NORMAL){
                    writeMemVolume(streamType, seekBar.getProgress());
                    if ( BuildConfig.DEBUG ) Log.d("VolumeChanger",
                            "writeMemVolume: " + seekBar.getProgress()
                                + ", type: " + streamType);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void writeMemVolume(int type, int volume) {
        sharedPreferences.edit().putInt(
                type == AudioManager.STREAM_MUSIC ? "media_vol" :
                        type == AudioManager.STREAM_RING ? "ring_vol" :
                                "alarm_vol", volume).apply();
    }

    private void loadSettings(SharedPreferences sharedPreferences){
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        modeStatus = mAudioManager.getRingerMode();
        memMediaVol = sharedPreferences.getInt("media_vol", 6);
        memRingVol = sharedPreferences.getInt("ring_vol", 4);
        memAlarmVol = sharedPreferences.getInt("alarm_vol", 4);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        loadSettings(sharedPreferences);
    }
}
