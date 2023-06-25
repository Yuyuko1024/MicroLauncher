package org.exthmui.microlauncher.duoqin.widgets;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.exthmui.microlauncher.duoqin.R;

public class CallSmsCounter extends LinearLayout {

    private TextView mCallCounter;
    private TextView mSmsCounter;


    public CallSmsCounter(@NonNull Context context) {
        super(context);
        init();
    }

    public CallSmsCounter(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        Log.d("CallSmsCounter", "init: ");
        View view = LayoutInflater.from(getContext()).inflate(R.layout.call_sms_view, this, false);
        mCallCounter = view.findViewById(R.id.missed_call_count);
        mSmsCounter = view.findViewById(R.id.missed_sms_count);
        updateCallCounter();
        updateSmsCounter();
        addView(view);
    }

    private void updateCallCounter() {
        int unreadNumber = 0;
        ContentResolver localContentResolver = getContext().getContentResolver();
        Uri localUri = CallLog.Calls.CONTENT_URI;
        String[] arrayOfString = new String[1];
        arrayOfString[0] = "_id";
        Cursor localCursor = localContentResolver.query(localUri, arrayOfString, "type=3 and new<>0", null, null);

        if (localCursor != null) {
            try {
                unreadNumber = localCursor.getCount();
                localCursor.close();
            } finally {
                localCursor.close();
            }
        }
        mCallCounter.setText(String.valueOf(unreadNumber));
    }

    private void updateSmsCounter() {
        int unreadNumber = getUnreadSmsCount() + getUnreadMmsCount();
        mSmsCounter.setText(String.valueOf(unreadNumber));
    }

    private int getUnreadSmsCount() {
        int result = 0;
        Cursor csr = getContext().getContentResolver().query(Uri.parse("content://sms"), null,
                "type = 1 and read = 0", null, null);
        if (csr != null) {
            result = csr.getCount();
            csr.close();
        }
        return result;
    }

    private int getUnreadMmsCount() {
        int result = 0;
        Cursor csr = getContext().getContentResolver().query(Uri.parse("content://mms/inbox"), null,
                "read = 0", null, null);
        if (csr != null) {
            result = csr.getCount();
            csr.close();
        }
        return result;
    }

}
