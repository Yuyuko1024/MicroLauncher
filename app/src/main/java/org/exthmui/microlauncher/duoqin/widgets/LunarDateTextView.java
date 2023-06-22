package org.exthmui.microlauncher.duoqin.widgets;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.TextView;

import org.exthmui.microlauncher.duoqin.R;
import org.exthmui.microlauncher.duoqin.utils.ChineseCale;

import java.util.Calendar;

@SuppressLint("AppCompatCustomView")
public class LunarDateTextView extends TextView {

    private BroadcastReceiver receiver;

    public LunarDateTextView(Context context) {
        super(context);
        init(context);
    }

    public LunarDateTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LunarDateTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        registerReceiver();
        updateText(context);
    }

    private void updateText(Context context) {
        setText(getDayLunar(context));
        setTextColor(Color.WHITE);
        setTextSize(16);
    }

    /**
     * 获取现在农历的日期
     */
    public static String getDayLunar(Context context) {
        ChineseCale lunarCalender = new ChineseCale();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DATE);
        String lunarAnimal = lunarCalender.animalsYear(year);
        String lunarGanZhi = lunarCalender.cyclical(year,month,day);
        String lunarString = lunarCalender.getLunarString(year, month, day);
        return context.getResources().getString(R.string.chs_lunar_text,lunarGanZhi, lunarAnimal, lunarString);
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateText(context);
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
