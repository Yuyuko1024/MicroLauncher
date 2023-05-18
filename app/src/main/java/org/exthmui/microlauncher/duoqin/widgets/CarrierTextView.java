package org.exthmui.microlauncher.duoqin.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.widget.TextView;

@SuppressLint("AppCompatCustomView")
public class CarrierTextView extends TextView {

        public CarrierTextView(Context context) {
            super(context);
            init(context);
        }

        public CarrierTextView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init(context);
        }

        public CarrierTextView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init(context);
        }

        private void init(Context context){
            updateText(context);
        }

        private void updateText(Context context) {
            setText(getCarrier(context));
            setTextColor(Color.WHITE);
            setTextSize(14);
        }

        private String getCarrier(Context context) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getSimOperatorName();
        }
}
