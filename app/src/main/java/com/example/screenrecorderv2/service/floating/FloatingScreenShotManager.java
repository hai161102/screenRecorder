package com.example.screenrecorderv2.service.floating;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.view.Display;

import androidx.annotation.RequiresApi;

import com.example.screenrecorderv2.R;
import com.example.screenrecorderv2.base.rx.RxBusHelper;
import com.example.screenrecorderv2.service.base.BaseFloatingManager;
import com.example.screenrecorderv2.ui.screenshot.ScreenShotActivity;
import com.example.screenrecorderv2.utils.PreferencesHelper;
import com.example.screenrecorderv2.utils.ScreenRecordHelper;
import com.example.screenrecorderv2.utils.ScreenShotHelper;
import com.example.screenrecorderv2.utils.Storage;
import com.example.screenrecorderv2.utils.Toolbox;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.Buffer;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class FloatingScreenShotManager extends BaseFloatingManager {
    private ScreenShotHelper screenShotHelper;

    private static FloatingScreenShotManager instance;

    public static FloatingScreenShotManager getInstance(Context context, Rect rect) {
        if (instance == null) {
            instance = new FloatingScreenShotManager(context,rect);
        }
        return instance;
    }

    public FloatingScreenShotManager(Context context,Rect rect) {
        super(context,rect);
        screenShotHelper = new ScreenShotHelper(context, windowManager, metrics);
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
