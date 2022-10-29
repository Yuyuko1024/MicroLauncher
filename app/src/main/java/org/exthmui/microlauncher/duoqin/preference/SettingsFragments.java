package org.exthmui.microlauncher.duoqin.preference;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.preference.*;

import org.exthmui.microlauncher.duoqin.R;

import java.util.Objects;

public class SettingsFragments extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener{

    public ListPreference clock_locate,clock_size,pound_func,app_list_style;
    SwitchPreference toolbox_pwd,pwd_keyguard;
    EditTextPreference pwd_custom;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preference_settings);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        boolean pwd_enabled = sharedPreferences.getBoolean("enable_toolbox_password",false);
        clock_size.setSummary(clock_size.getValue());
        clock_locate.setSummary(clock_locate.getEntry());
        pound_func.setSummary(pound_func.getEntry());
        app_list_style.setSummary(app_list_style.getEntry());
        pwd_keyguard.setEnabled(pwd_enabled);
        setPwdCustomEnabled(sharedPreferences);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
        if(Build.VERSION.SDK_INT<28){ ((PreferenceGroup) Objects.requireNonNull(findPreference("preference_func"))).removePreference(findPreference("preference_main_default_launcher")); }
        clock_locate= getPreferenceScreen().findPreference("list_preference_clock_locate");
        clock_size=getPreferenceScreen().findPreference("list_preference_clock_size");
        pound_func=getPreferenceScreen().findPreference("preference_pound_func");
        app_list_style=getPreferenceScreen().findPreference("app_list_func");
        toolbox_pwd=getPreferenceScreen().findPreference("enable_toolbox_password");
        pwd_keyguard=getPreferenceScreen().findPreference("toolbox_password_use_keyguard");
        pwd_custom=getPreferenceScreen().findPreference("toolbox_password_use_custom");
        clock_size.setSummary(clock_size.getValue());
        clock_locate.setSummary(clock_locate.getEntry());
        pound_func.setSummary(pound_func.getEntry());
        app_list_style.setSummary(app_list_style.getEntry());
        pwd_keyguard.setEnabled(sp.getBoolean("enable_toolbox_password",false));
        setPwdCustomEnabled(sp);
    }

    private void setPwdCustomEnabled(SharedPreferences sharedPreferences){
        boolean pwd_enabled = sharedPreferences.getBoolean("enable_toolbox_password",false);
        boolean pwd_keyguard = sharedPreferences.getBoolean("toolbox_password_use_keyguard",true);
        if (pwd_enabled){
            pwd_custom.setEnabled(!pwd_keyguard);
        }else{
            pwd_custom.setEnabled(false);
        }
    }

    //TODO:30.clean up (DONE)
    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
