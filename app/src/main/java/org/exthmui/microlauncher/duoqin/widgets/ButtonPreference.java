package org.exthmui.microlauncher.duoqin.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.TypedArrayUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.exthmui.microlauncher.duoqin.R;
import org.exthmui.microlauncher.duoqin.utils.RestartTool;

/**
 * Created by Maribel
 * Date: 2023-03-08
 * Class: org.yuyu.easylogin
 * Project: Easy ACDT WLAN Login
 * GitHub: https://github.com/Yuyuko1024
 **/
public class ButtonPreference extends Preference {
    private MaterialButton mButton;
    private Context context;
    private String btn_text;
    private Drawable btn_icon;
    private boolean isClickable;

    public ButtonPreference(@NonNull Context context) {
        this(context, null);
    }

    @SuppressLint("RestrictedApi")
    public ButtonPreference(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, TypedArrayUtils.getAttr(context,
                androidx.preference.R.attr.preferenceStyle,
                android.R.attr.preferenceStyle));
    }

    public ButtonPreference(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayoutResource(R.layout.preference_button);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ButtonPreference, defStyleAttr, 0);
        this.context = context;
        btn_text = ta.getString(R.styleable.ButtonPreference_btn_text);
        isClickable = ta.getBoolean(R.styleable.ButtonPreference_isClickable, true);
        btn_icon = ta.getDrawable(R.styleable.ButtonPreference_btn_icon);
        ta.recycle();
    }

    @Override
    public void onBindViewHolder(@NonNull PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        mButton = (MaterialButton) holder.findViewById(R.id.pref_btn);
        mButton.setText(btn_text);
        mButton.setIcon(btn_icon);
        mButton.setEnabled(isClickable);
        mButton.setOnClickListener(v -> {
            restartApp();
        });
    }

    @Override
    protected void onClick() {
        super.onClick();
        restartApp();
    }

    private void restartApp() {
        new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.status_reload_launcher)
                .setMessage(R.string.restart_dialog_msg)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    RestartTool.restartApp(getContext());
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }
}
