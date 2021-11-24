package org.exthmui.microlauncher.duoqin.activity;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.exthmui.microlauncher.duoqin.BuildConfig;
import org.exthmui.microlauncher.duoqin.R;

import es.dmoral.toasty.Toasty;
import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
    private static final int DELAY_TIMER_MILLIS = 500;
    private static final int ACTIVITY_TRIGGER_COUNT = 3;
    private final long[] mHits = new long[ACTIVITY_TRIGGER_COUNT];
    private boolean lock_enable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadSettings();
        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setDescription(getString(R.string.about_desc).replace("\\n","\n"))
                .setImage(R.drawable.ic_home)
                .addItem(versionElement())
                .addGroup(getString(R.string.connect_us))
                .addEmail("dingwenxuan4@gmail.com")
                .addWebsite("https://gitee.com/kira_rumia/MicroLauncher/")
                .addGitHub("GoogleChinaCEO")
                .create();

        setContentView(aboutPage);
        ActionBar actionBar = this.getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    Element versionElement(){
        Element versionElement = new Element();
        final String version = getString(R.string.version)+BuildConfig.VERSION_NAME;
        versionElement.setTitle(version);
        versionElement.setOnClickListener(v -> {
            arrayCopy();
            mHits[mHits.length - 1] = SystemClock.uptimeMillis();
            if (mHits[0] >= (SystemClock.uptimeMillis() - DELAY_TIMER_MILLIS)) {
                Toasty.success(AboutActivity.this,R.string.easter_egg_string, Toast.LENGTH_LONG,true).show();
                final Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://thwiki.cc/%E4%B8%9C%E6%96%B9Project"));
                startActivity(intent);
            }
        });
        return versionElement;
    }

    void arrayCopy() {
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_STAR){
            DevicePolicyManager mDPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
            if(lock_enable){
                    mDPM.lockNow();
            }else{
                Log.d("TAG","Lock screen is disabled");
            }
        }
        return super.onKeyDown(keyCode,event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // When the home button is pressed, take the user back to the MainActivity
        if (id == android.R.id.home) {
            //NavUtils.navigateUpFromSameTask(this);
            finish();
        }
        return super.onOptionsItemSelected(item);
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