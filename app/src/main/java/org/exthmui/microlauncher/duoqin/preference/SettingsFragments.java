package org.exthmui.microlauncher.duoqin.preference;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;

import org.exthmui.microlauncher.duoqin.R;

import java.util.Objects;

public class SettingsFragments extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener{

    public ListPreference clock_locate,clock_size,pound_func,app_list_style;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preference_settings);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        clock_size.setSummary(clock_size.getValue());
        clock_locate.setSummary(clock_locate.getEntry());
        pound_func.setSummary(pound_func.getEntry());
        app_list_style.setSummary(app_list_style.getEntry());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
        if(Build.VERSION.SDK_INT<28){ ((PreferenceGroup) Objects.requireNonNull(findPreference("preference_func"))).removePreference(findPreference("preference_main_default_launcher")); }
        clock_locate= getPreferenceScreen().findPreference("list_preference_clock_locate");
        clock_size=getPreferenceScreen().findPreference("list_preference_clock_size");
        pound_func=getPreferenceScreen().findPreference("preference_pound_func");
        app_list_style=getPreferenceScreen().findPreference("app_list_func");
        clock_size.setSummary(clock_size.getValue());
        clock_locate.setSummary(clock_locate.getEntry());
        pound_func.setSummary(pound_func.getEntry());
        app_list_style.setSummary(app_list_style.getEntry());
    }

    //TODO:30.clean up (DONE)
    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
