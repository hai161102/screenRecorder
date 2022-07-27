package com.mtg.screenrecorder.service.floating;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;

import com.mtg.screenrecorder.R;
import com.mtg.screenrecorder.base.rx.RxBusHelper;
import com.mtg.screenrecorder.service.base.BaseFloatingManager;
import com.mtg.screenrecorder.utils.PreferencesHelper;
import com.mtg.screenrecorder.utils.ScreenRecordHelper;
import com.mtg.screenrecorder.utils.ScreenShotHelper;
import com.mtg.screenrecorder.utils.Toolbox;
import com.mtg.screenrecorder.view.activity.MainActivity;
import com.mtg.screenrecorder.view.activity.screenshot.ScreenShotActivity;

public class FloatingScreenShotManager extends BaseFloatingManager {
    private ScreenShotHelper screenShotHelper;
    private MainActivity mainActivity;

    private static FloatingScreenShotManager instance;

    public static FloatingScreenShotManager getInstance(Context context, Rect rect) {
        if (instance == null) {
            instance = new FloatingScreenShotManager(context,rect);
        }
        return instance;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public FloatingScreenShotManager(Context context, Rect rect) {
        super(context,rect);
        screenShotHelper = new ScreenShotHelper(context, windowManager, metrics);
    }

    @Override
    public void onTouchFinished(boolean isFinishing, int x, int y) {
        super.onTouchFinished(isFinishing, x, y);
        if (isFinishing){
            PreferencesHelper.putBoolean(PreferencesHelper.PREFS_TOOLS_SCREEN_SHOT, false);
            if (mainActivity != null){
                mainActivity.setViewTools();
            }
        }
    }

    @Override
    protected void setUpOptionsFloating() {
        super.setUpOptionsFloating();
        options.isEnableAlpha = true;
        options.overMargin = 16;
        options.floatingViewWidth = (int) context.getResources().getDimension(R.dimen.size_floating_icon);
        options.floatingViewHeight = (int) context.getResources().getDimension(R.dimen.size_floating_icon);
        options.floatingViewX = metrics.widthPixels - options.floatingViewWidth;
        options.floatingViewY = metrics.heightPixels / 2 + options.floatingViewHeight;
    }

    @Override
    protected void initLayout() {
        rootView.setOnClickListener(v -> {
            vibrateWhenClick();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startCaptureScreen();
            }
        });
    }

    @Override
    protected int getRootViewID() {
        return R.layout.floating_view_capture;
    }

    public void startCaptureScreen() {
        if (ScreenRecordHelper.STATE != ScreenRecordHelper.State.RECORDING) {
            openScreenShot();
        }
    }

    public void captureScreen(Intent resultData, int resultCode) {
        RxBusHelper.sendStartScreenShot();
        screenShotHelper.captureScreen(resultData, resultCode);
    }

    public void openScreenShot() {
        Intent intent = new Intent(context, ScreenShotActivity.class);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        Toolbox.startActivityAllStage(context, intent);
    }

    @Override
    public void onFinishFloatingView() {
        instance = null;
        PreferencesHelper.putBoolean(PreferencesHelper.PREFS_TOOLS_SCREEN_SHOT, false);
        super.onFinishFloatingView();
    }
}
