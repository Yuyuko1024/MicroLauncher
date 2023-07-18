package org.exthmui.microlauncher.duoqin.widgets;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class AppRecyclerView extends RecyclerView {

    private static final String TAG = AppRecyclerView.class.getSimpleName();

    /**
     * 重写dispatchKeyEvent方法，处理焦点问题
     * 来自 <a href="https://www.jianshu.com/p/ac7e393689f9">重写RecyclerView使其在TV端运行</a>
     * @param context
     */

    public AppRecyclerView(@NonNull Context context) {
        super(context);
        initView();
    }

    public AppRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        setHasFixedSize(true);
        /**
         防止RecyclerView刷新时焦点不错乱bug的步骤如下:
         (1)adapter执行setHasStableIds(true)方法
         (2)重写getItemId()方法,让每个view都有各自的id
         (3)RecyclerView的动画必须去掉
         */
        setItemAnimator(null);
        setItemViewCacheSize(100);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        boolean result = super.dispatchKeyEvent(event);
        View focusView = this.findFocus();
        if (focusView == null) {
            return result;
        } else {
            int dy = 0;
            int dx = 0;
            if (getChildCount() > 0) {
                View firstView = this.getChildAt(0);
                dy = firstView.getHeight();
                dx = firstView.getWidth();
            }
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                switch (event.getKeyCode()) {
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                        View rightView = FocusFinder.getInstance().findNextFocus(this, focusView, View.FOCUS_RIGHT);
                        Log.i(TAG, "rightView is null:" + (rightView == null));
                        if (rightView != null) {
                            rightView.requestFocus();
                        } else {
                            this.smoothScrollBy(dx, 0);
                        }
                        return true;
                    case KeyEvent.KEYCODE_DPAD_LEFT:
                        View leftView = FocusFinder.getInstance().findNextFocus(this, focusView, View.FOCUS_LEFT);
                        Log.i(TAG, "leftView is null:" + (leftView == null));
                        if (leftView != null) {
                            leftView.requestFocus();
                        } else {
                            this.smoothScrollBy(-dx, 0);
                        }
                        return true;
                    case KeyEvent.KEYCODE_DPAD_DOWN:
                        View downView = FocusFinder.getInstance().findNextFocus(this, focusView, View.FOCUS_DOWN);
                        Log.i(TAG, " downView is null:" + (downView == null));
                        if (downView != null) {
                            downView.requestFocus();
                        } else {
                            this.smoothScrollBy(0, dy);
                        }
                        return true;
                    case KeyEvent.KEYCODE_DPAD_UP:
                        View upView = FocusFinder.getInstance().findNextFocus(this, focusView, View.FOCUS_UP);
                        Log.i(TAG, "upView is null:" + (upView == null));
                        if (event.getAction() != KeyEvent.ACTION_UP) {
                            if (upView != null) {
                                upView.requestFocus();
                            } else {
                                this.smoothScrollBy(0, -dy);
                            }
                        }
                        return true;
                }

            }

        }
        return result;
    }

    @Override
    protected void onDetachedFromWindow() {
        if (getLayoutManager() != null) {
            super.onDetachedFromWindow();
        }
    }
}
