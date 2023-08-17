package org.exthmui.microlauncher.duoqin.icons;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceFragment.OnPreferenceStartFragmentCallback;
import androidx.preference.PreferenceFragment.OnPreferenceStartScreenCallback;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import org.exthmui.microlauncher.duoqin.R;
import org.exthmui.microlauncher.duoqin.utils.PackageManagerHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IconPackSettingsActivity extends AppCompatActivity implements
        OnPreferenceStartFragmentCallback, OnPreferenceStartScreenCallback {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            // Display the fragment as the main content.
            getSupportFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new IconPackSettingsFragment()).commit();
        }
    }

    private boolean startFragment(PreferenceFragmentCompat fragment, Bundle args, String key) {
        if (getFragmentManager().isStateSaved()) {
            // Sometimes onClick can come after onPause because of being posted on the handler.
            // Skip starting new fragments in that case.
            return false;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, fragment)
                .addToBackStack(key)
                .commit();
        return true;
    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragment preferenceFragment,
                                             Preference pref) {
        return startFragment(new IconPackSettingsFragment(), pref.getExtras(), pref.getKey());
    }

    @Override
    public boolean onPreferenceStartScreen(PreferenceFragment caller, PreferenceScreen pref) {
        Bundle args = new Bundle();
        args.putString(PreferenceFragment.ARG_PREFERENCE_ROOT, pref.getKey());
        return startFragment(new IconPackSettingsFragment(),
                args, pref.getKey());
    }

    @Override
    public boolean onNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_icon_pack, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.menu_icon_pack) {
            return openMarket();
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private boolean openMarket() {
        final String query = getString(R.string.icon_pack_title);
        final Intent intent = PackageManagerHelper.getMarketSearchIntent(this, query);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (intent.resolveActivity(getPackageManager()) == null) {
            Toast.makeText(this, R.string.icon_pack_no_market, Toast.LENGTH_LONG)
                    .show();
            return false;
        }

        startActivity(intent);
        return true;
    }

}
