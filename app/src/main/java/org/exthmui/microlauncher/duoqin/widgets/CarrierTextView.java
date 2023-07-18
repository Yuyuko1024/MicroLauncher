package org.exthmui.microlauncher.duoqin.widgets;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import org.exthmui.microlauncher.duoqin.R;

@SuppressLint("AppCompatCustomView")
public class CarrierTextView extends TextView {

    public final static String ACTION_SIM_STATE_CHANGED = "android.intent.action.SIM_STATE_CHANGED";

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
        registerSimReceiver(context);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (simStateReceiver != null){
            getContext().unregisterReceiver(simStateReceiver);
            simStateReceiver = null;
        }
    }

    @Override
    public void dispatchWindowVisibilityChanged(int visibility) {
        super.dispatchWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) {
            updateText(getContext());
            if (simStateReceiver == null){
                registerSimReceiver(getContext());
            }
        } else if (visibility == GONE || visibility == INVISIBLE) {
            if (simStateReceiver != null){
                getContext().unregisterReceiver(simStateReceiver);
                simStateReceiver = null;
            }
        }
    }

    private void updateText(Context context) {
        String carrierText = getCarrier(context);
        if (TextUtils.isEmpty(carrierText)) {
            carrierText = context.getString(R.string.carrier_no_sim_text);
        }
        setText(carrierText);
        setTextColor(Color.WHITE);
        setTextSize(14);
    }

    private void registerSimReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_SIM_STATE_CHANGED);
        context.registerReceiver(simStateReceiver, filter);
    }

    private BroadcastReceiver simStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm.getSimState() == TelephonyManager.SIM_STATE_READY) {
                updateText(context);
            } else {
                setText(context.getString(R.string.carrier_no_sim_text));
            }
        }
    };

    private String getCarrier(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String carrierName = tm.getSimOperatorName();
        String networkOperator = tm.getNetworkOperator();
        switch (networkOperator) {
            case "46001":
            case "46006":
            case "46009":
                carrierName = context.getString(R.string.carrier_china_unicom);
                break;
            case "46000":
            case "46002":
            case "46004":
            case "46007":
                carrierName = context.getString(R.string.carrier_cmcc);
                break;
            case "46003":
            case "46005":
            case "46011":
                carrierName = context.getString(R.string.carrier_chn_ct);
                break;
        }
        return carrierName;
    }
}
