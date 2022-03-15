package org.exthmui.microlauncher.duoqin.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.exthmui.microlauncher.duoqin.R;
import org.exthmui.microlauncher.duoqin.misc.RestartTool;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    TextView back;
    boolean reload_flag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ActionBar actionBar = this.getSupportActionBar();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        back=findViewById(R.id.settings_back);
        back.setOnClickListener(new backFunc());
    }

    class backFunc implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            rebootLauncher(reload_flag);
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // When the home button is pressed, take the user back to the MainActivity
        if (id == android.R.id.home) {
            //NavUtils.navigateUpFromSameTask(this);
            rebootLauncher(reload_flag);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_HOME){
            rebootLauncher(reload_flag);
            finish();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            rebootLauncher(reload_flag);
        }
        return super.onKeyDown(keyCode,event);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        switch (s){
            case "switch_preference_lunar":
            case "switch_preference_carrier_name":
            case "switch_preference_app_list_func":
                back.setText("重启桌面");
                reload_flag=true;
                break;
        }
    }

    private void rebootLauncher(boolean isReboot){
        if(isReboot){
            RestartTool.restartApp(getApplicationContext(),100);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        rebootLauncher(reload_flag);
    }
}
