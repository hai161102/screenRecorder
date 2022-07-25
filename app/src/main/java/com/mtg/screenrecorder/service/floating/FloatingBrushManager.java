package com.mtg.screenrecorder.service.floating;

import android.content.Context;
import android.graphics.Rect;

import com.mtg.screenrecorder.R;
import com.mtg.screenrecorder.service.base.BaseFloatingManager;
import com.mtg.screenrecorder.service.layout.LayoutBrushManager;
import com.mtg.screenrecorder.utils.PreferencesHelper;

public class FloatingBrushManager extends BaseFloatingManager {

    private static FloatingBrushManager instance;
    private LayoutBrushManager layoutBrushManager;

    public static FloatingBrushManager getInstance(Context context, Rect rect) {
        if (instance == null) {
            instance = new FloatingBrushManager(context,rect);
        }
        return instance;
    }

    public FloatingBrushManager(Context context, Rect rect) {
        super(context,rect);
    }

    @Override
    protected void setUpOptionsFloating() {
        super.setUpOptionsFloating();
        options.isEnableAlpha = true;
        options.overMargin = 16;
        options.floatingViewWidth = (int) context.getResources().getDimension(R.dimen.size_floating_icon);
        options.floatingViewHeight = (int) context.getResources().getDimension(R.dimen.size_floating_icon);
        options.floatingViewX = metrics.widthPixels - options.floatingViewWidth;
        options.floatingViewY = metrics.heightPixels / 2 - options.floatingViewHeight;
    }

    @Override
    protected void initLayout() {
        rootView.setOnClickListener(v -> {
            vibrateWhenClick();
            layoutBrushManager = new LayoutBrushManager(context);
        });
    }

    public void onRemoveLayoutBrush() {
        if (layoutBrushManager != null) {
            layoutBrushManager.removeLayout();
        }
    }

    @Override
    protected int getRootViewID() {
        return R.layout.floating_view_brush;
    }


    @Override
    public void onFinishFloatingView() {
        instance = null;
        PreferencesHelper.putBoolean(PreferencesHelper.PREFS_TOOLS_BRUSH, false);
        super.onFinishFloatingView();
    }
}
