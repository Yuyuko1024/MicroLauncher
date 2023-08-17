/*
 * Copyright (C) 2020 Shift GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.exthmui.microlauncher.duoqin.icons;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.util.AttributeSet;
import android.util.Log;

import androidx.core.content.res.TypedArrayUtils;
import androidx.preference.CheckBoxPreference;
import androidx.preference.PreferenceViewHolder;

import org.exthmui.microlauncher.duoqin.R;
import org.exthmui.microlauncher.duoqin.utils.Constants;
import org.exthmui.microlauncher.duoqin.utils.LauncherUtils;
import org.exthmui.microlauncher.duoqin.widgets.RadioHeaderPreference;

public class IconPackHeaderPreference extends RadioHeaderPreference implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "IconPackHeaderPreference";
    private static final int PREVIEW_ICON_NUM = 4;
    // This value has been selected as an average of usual "device profile-computed" values
    private static final int PREVIEW_ICON_DPI = 500;

    private final Context context;
    private String iconPackPkg;
    private SharedPreferences sharedPreferences;
    private ImageView[] icons = null;

    public IconPackHeaderPreference(Context context) {
        this(context, null);
    }

    public IconPackHeaderPreference(Context context, AttributeSet attrs) {
        this(context, attrs, TypedArrayUtils.getAttr(context,
                androidx.preference.R.attr.preferenceStyle,
                android.R.attr.preferenceStyle));
    }

    public IconPackHeaderPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;

        sharedPreferences = context.getSharedPreferences(Constants.launcherSettingsPref, Context.MODE_PRIVATE);
        setLayoutResource(R.layout.preference_widget_icons_preview);
        loadSettings(sharedPreferences);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        final ImageView[] imageViews = {
                (ImageView) holder.findViewById(R.id.pref_icon_a),
                (ImageView) holder.findViewById(R.id.pref_icon_b),
                (ImageView) holder.findViewById(R.id.pref_icon_c),
                (ImageView) holder.findViewById(R.id.pref_icon_d)
        };
        this.icons = imageViews;
        onRadioElementSelected(iconPackPkg);
    }

    @Override
    public void onDetached() {
        this.icons = null;
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onDetached();
    }

    private void loadSettings(SharedPreferences sharedPreferences) {
        iconPackPkg = sharedPreferences.getString(IconPackStore.KEY_ICON_PACK, context.getString(R.string.icon_pack_default_pkg));
    }

    @Override
    public void onRadioElementSelected(String key) {
        if (icons == null) {
            return;
        }

        Intent intent = new Intent().setAction(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER);
        int i = 0;
        for (ResolveInfo resolveInfo : context.getPackageManager().queryIntentActivities(intent, 0)) {
            if (i >= icons.length) {
                break;
            }
            if (key == null || key.equals(context.getString(R.string.icon_pack_default_pkg))) {
                icons[i].setImageDrawable(resolveInfo.loadIcon(context.getPackageManager()));
            } else {
                icons[i].setImageDrawable(LauncherUtils.getFromIconPack(context,
                        resolveInfo.loadIcon(context.getPackageManager()), resolveInfo.activityInfo.packageName));
            }
            i++;
        }

        /*final IconProvider iconProvider = new IconProvider(context);
        final PackageManager pm = context.getPackageManager();
        final LauncherApps launcherApps = context.getSystemService(LauncherApps.class);
        new GetLaunchableInfoTask(pm, launcherApps, PREVIEW_ICON_NUM, (aiList) -> {
            for (int i = 0; i < icons.length; i++) {
                icons[i].setImageDrawable(iconProvider.getIcon(
                        aiList.get(i), PREVIEW_ICON_DPI));
            }
        }).execute();*/
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        loadSettings(sharedPreferences);
        if (key.equals(IconPackStore.KEY_ICON_PACK)) {
            onRadioElementSelected(sharedPreferences.getString(key, context.getString(R.string.icon_pack_default_pkg)));
        }
    }
}
