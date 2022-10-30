package org.exthmui.microlauncher.duoqin.adapter;

import android.view.View;

public interface OnItemCallback {
    public void onFocusChange(View v, boolean hasFocus,int position);
    public void onItemClick(View v, int position);
}
