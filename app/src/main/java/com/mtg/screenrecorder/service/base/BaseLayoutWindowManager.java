package com.mtg.screenrecorder.service.base;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.view.ContextThemeWrapper;

import com.mtg.screenrecorder.R;
import com.mtg.screenrecorder.utils.PreferencesHelper;

public abstract class BaseLayoutWindowManager {
    protected Context context;
    protected WindowManager windowManager;
    protected WindowManager.LayoutParams mParams;
    protected LayoutInflater inflate;
    protected View rootView;

    public BaseLayoutWindowManager(Context context) {
        this.context = context;
        init();
        initLayout();
    }

    protected void init() {
        ContextThemeWrapper ctx = new ContextThemeWrapper(context, R.style.Theme_AppCompat);
        windowManager = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
        inflate = (LayoutInflater) ctx.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        rootView = inflate.inflate(getRootViewID(), null);
        mParams = new WindowManager.LayoutParams();
        mParams.type = Build.VERSION.SDK_INT < Build.VERSION_CODES.O ?
                WindowManager.LayoutParams.TYPE_PHONE :
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
        mParams.format = PixelFormat.TRANSLUCENT;
    }

    public void removeLayout() {
        if (windowManager != null) {
            windowManager.removeView(rootView);
            mParams = new WindowManager.LayoutParams();
            windowManager = null;
        }
    }

    public void addLayout() {
        if (windowManager != null && rootView != null) {
            windowManager.addView(rootView, mParams);
        }
    }

    public void vibrateWhenClick() {
        if (PreferencesHelper.getBoolean(PreferencesHelper.KEY_VIBRATE, false)) {
            Vibrator vibrate = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrate.vibrate(50);
        }
    }

    protected abstract int getRootViewID();

    protected abstract void initLayout();
}
