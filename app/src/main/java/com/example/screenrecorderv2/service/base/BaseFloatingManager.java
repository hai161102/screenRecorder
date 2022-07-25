package com.example.screenrecorderv2.service.base;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.example.screenrecorderv2.R;
import com.example.screenrecorderv2.utils.PreferencesHelper;

import jp.co.recruit_lifestyle.android.floatingview.FloatingViewListener;
import jp.co.recruit_lifestyle.android.floatingview.FloatingViewManager;

public abstract class BaseFloatingManager implements FloatingViewListener {
    protected Context context;
    protected FloatingViewManager mFloatingViewManager;
    protected View rootView;
    protected FloatingViewManager.Options options;
    protected WindowManager windowManager;
    protected LayoutInflater inflater;
    protected DisplayMetrics metrics;
    protected Rect rect;

    public BaseFloatingManager(Context context,Rect rect) {
        this.rect = rect;
        this.context = context;
        init();
        initLayout();
    }

    private void init() {
        metrics = new DisplayMetrics();
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        inflater = LayoutInflater.from(context);
        rootView = inflater.inflate(getRootViewID(), null, false);
        mFloatingViewManager = new FloatingViewManager(context, this);
        mFloatingViewManager.setFixedTrashIconImage(R.drawable.ic_trash_fixed);
        mFloatingViewManager.setActionTrashIconImage(R.drawable.ic_trash_action);
        mFloatingViewManager.setSafeInsetRect(rect);
        setUpOptionsFloating();
    }

    protected abstract void initLayout();

    protected abstract int getRootViewID();

    protected void setUpOptionsFloating() {
        options = new FloatingViewManager.Options();
    }

    public void addFloatingView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context))
            return;
        if (mFloatingViewManager != null && options != null) {
            mFloatingViewManager.addViewToWindow(rootView, options);
        }
    }

    public void removeFloatingView() {
        if (mFloatingViewManager != null) {
            mFloatingViewManager.removeAllViewToWindow();
            mFloatingViewManager = null;
        }
    }

    public void showFloatingView() {
        if (rootView != null) {
            rootView.setVisibility(View.VISIBLE);
            rootView.setClickable(true);
        }
    }

    public void hideFloatingView() {
        if (rootView != null) {
            rootView.setClickable(false);
            rootView.setVisibility(View.INVISIBLE);
        }
    }

    public void vibrateWhenClick() {
        if (PreferencesHelper.getBoolean(PreferencesHelper.KEY_VIBRATE, false)) {
            Vibrator vibrate = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrate.vibrate(50);
        }
    }

    public void updatePositionFloatingView(int x, int y) {
        if (mFloatingViewManager != null) {
            mFloatingViewManager.updatePositionFloatingView(x, y);
        }
    }

    public WindowManager.LayoutParams getWindowParamsFloatingView() {
        if (mFloatingViewManager != null) {
            return mFloatingViewManager.getWindowParamsFloatingView();
        }
        return null;
    }

    @Override
    public void onFinishFloatingView() {
        removeFloatingView();
    }

    @Override
    public void onTouchFinished(boolean isFinishing, int x, int y) {

    }
}
