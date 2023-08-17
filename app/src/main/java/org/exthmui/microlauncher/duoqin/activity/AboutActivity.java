package org.exthmui.microlauncher.duoqin.activity;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.exthmui.microlauncher.duoqin.BuildConfig;
import org.exthmui.microlauncher.duoqin.R;

import de.psdev.licensesdialog.LicensesDialog;
import de.psdev.licensesdialog.licenses.ApacheSoftwareLicense20;
import de.psdev.licensesdialog.licenses.GnuGeneralPublicLicense30;
import de.psdev.licensesdialog.licenses.MITLicense;
import de.psdev.licensesdialog.model.Notice;
import de.psdev.licensesdialog.model.Notices;
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
                .addItem(channelElement())
                .addItem(opensourceLicense())
                .addItem(debugElement())
                .addGroup(getString(R.string.connect_us))
                .addEmail("dingwenxuan4@gmail.com")
                .addWebsite("https://gitee.com/kira_rumia/MicroLauncher/")
                .addGitHub("Yuyuko1024")
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

    Element channelElement() {
        Element channelElement = new Element();
        final String channel = getString(R.string.channel_name)+BuildConfig.FLAVOR;
        channelElement.setTitle(channel);
        return channelElement;
    }

    Element debugElement() {
        Element debugElement = new Element();
        final String debug = getString(R.string.debug_type)+BuildConfig.BUILD_TYPE;
        debugElement.setTitle(debug);
        return debugElement;
    }

    Element opensourceLicense(){
        Element opensourceLicense = new Element();
        final String osl = getString(R.string.osl);
        opensourceLicense.setTitle(osl);
        opensourceLicense.setOnClickListener(v -> {
            final Notices notices = new Notices();
            notices.addNotice(new Notice("MicroLauncher","https://github.com/Yuyuko1024/MicroLauncher",
                    "Yuyuko1024",new ApacheSoftwareLicense20()));
            notices.addNotice(new Notice("Trebuchet Launcher", "https://github.com/LineageOS/android_packages_apps_Trebuchet",
                    "Copyright (C) 2018-2019 The LineageOS Project", new ApacheSoftwareLicense20()));
            notices.addNotice(new Notice("pinyin4j", "https://github.com/belerweb/pinyin4j/",
                    "lemann,belerweb", new GnuGeneralPublicLicense30()));
            notices.addNotice(new Notice("EasyPermissions", "https://github.com/googlesamples/easypermissions",
                    "googlesamples", new ApacheSoftwareLicense20()));
            notices.addNotice(new Notice("Gson", "https://github.com/google/gson",
                    "Copyright 2008 Google Inc", new ApacheSoftwareLicense20()));
            notices.addNotice(new Notice("Toasty","https://github.com/GrenderG/Toasty",
                    "GrenderG",new GnuGeneralPublicLicense30()));
            notices.addNotice(new Notice("android-about-page","https://github.com/medyo/android-about-page",
                    "medyo",new MITLicense()));
            new LicensesDialog.Builder(this)
                    .setNotices(notices)
                    .setIncludeOwnLicense(true)
                    .build()
                    .show();
        });
        return opensourceLicense;
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
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadSettings(){
        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName()+"_preferences",Context.MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        lock_enable = sharedPreferences.getBoolean("preference_main_lockscreen",true);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        lock_enable = sharedPreferences.getBoolean("preference_main_lockscreen",true);
    }
}