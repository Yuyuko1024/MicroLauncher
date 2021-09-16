package org.exthmui.microlauncher.activity;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.exthmui.microlauncher.R;

import es.dmoral.toasty.Toasty;
import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutActivity extends AppCompatActivity {
    private static final int DELAY_TIMER_MILLIS = 500;
    private static final int ACTIVITY_TRIGGER_COUNT = 3;
    private final long[] mHits = new long[ACTIVITY_TRIGGER_COUNT];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    Element versionElement(){
        Element versionElement = new Element();
        final String version =getString(R.string.version);
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
            mDPM.lockNow();
        }
        return super.onKeyDown(keyCode,event);
    }
}