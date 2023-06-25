package org.exthmui.microlauncher.duoqin.widgets;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class ClockViewManager {

    private static final String TAG = ClockViewManager.class.getSimpleName();

    private ViewGroup mParent;

    private int mViewCount = 0;

    public ClockViewManager(ViewGroup mParent) {
        this.mParent = mParent;
    }

    // 插入或更新View
    public void insertOrUpdateView(int id, View view) {
        View existedView = mParent.findViewById(id);
        if (existedView == null) {
            // 不存在,插入新的View
            view.setId(id);
            mParent.addView(view);
            mViewCount++;
        } else {
            // 存在,更新View
            mParent.removeView(existedView);
            view.setId(id);
            mParent.addView(view);
        }
    }

    // 删除View
    public void removeView(int id) {
        View view = mParent.findViewById(id);
        if (view != null) {
            mParent.removeView(view);
        } else {
            Log.e(TAG, "removeView: unable to find view with id " + id);
        }
        mViewCount--;
    }

    public void setLayoutParams(int id,ViewGroup.LayoutParams params){
        View view = mParent.findViewById(id);
        if (view != null) {
            view.setLayoutParams(params);
        } else {
            Log.e(TAG, "setLayoutParams: unable to find view with id " + id);
        }
    }

    public int getViewCount() {
        return mViewCount;
    }
}
