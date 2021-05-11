package org.exthmui.microlauncher;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class VolumeChanger extends AppCompatActivity {
    TextView media,ring,alarm;
    SeekBar media_sek,ring_sek,alarm_sek;
    private AudioManager mAudioManager;
    private int maxVolume, currentVolume;
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
}
