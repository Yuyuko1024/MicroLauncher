package org.exthmui.microlauncher.duoqin.widgets;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@SuppressLint("AppCompatCustomView")
public class DateTextView extends TextView {

    private String mDateFormat;
    private BroadcastReceiver receiver;

    public DateTextView(Context context) {
        super(context);
        init(context);
    }

    public DateTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DateTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        Resources res = context.getResources();
        String locale;
        Configuration config = res.getConfiguration();
        locale = config.getLocales().get(0).getLanguage()
                + "_" + config.getLocales().get(0).getCountry();

        if ("zh_CN".equals(locale)) {
            mDateFormat = "yyyy年MM月dd日 EEEE";
        } else {
            mDateFormat = "MMMM dd, yyyy EEEE";
        }
        registerReceiver();
        updateText();
    }

    public void setDateFormat(String format) {
        mDateFormat = format;
        updateText();
    }

    private void updateText() {
        SimpleDateFormat sdf = new SimpleDateFormat(mDateFormat, Locale.getDefault());
        String date = sdf.format(new Date());
        setText(date);
        setTextColor(Color.WHITE);
        setTextSize(16);
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateText();
            }
        };

        getContext().registerReceiver(receiver, filter);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getContext().unregisterReceiver(receiver);
    }
}
