package com.example.screenrecorderv2.service;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.example.screenrecorderv2.utils.Config.ACTION_SHOW_MAIN_FLOATING;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.IBinder;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.akexorcist.localizationactivity.ui.LocalizationService;
import com.example.screenrecorderv2.base.rx.CallBackRxBus;
import com.example.screenrecorderv2.base.rx.CallbackEventView;
import com.example.screenrecorderv2.base.rx.RxBus;
import com.example.screenrecorderv2.base.rx.RxBusType;
import com.example.screenrecorderv2.service.floating.FloatingBrushManager;
import com.example.screenrecorderv2.service.floating.FloatingCameraViewManager;
import com.example.screenrecorderv2.service.floating.FloatingMainManager;
import com.example.screenrecorderv2.service.floating.FloatingRecordManager;
import com.example.screenrecorderv2.service.floating.FloatingScreenShotManager;
import com.example.screenrecorderv2.service.layout.LayoutPreviewMediaManager;
import com.example.screenrecorderv2.ui.cameraview.CameraActivity;
import com.example.screenrecorderv2.ui.main.MainActivity;
import com.example.screenrecorderv2.ui.record.RecordActivity;
import com.example.screenrecorderv2.ui.tools.ToolsActivity;
import com.example.screenrecorderv2.utils.Config;
import com.example.screenrecorderv2.utils.PreferencesHelper;
import com.example.screenrecorderv2.utils.ScreenRecordHelper;
import com.example.screenrecorderv2.utils.Toolbox;
import com.example.screenrecorderv2.utils.notification.ServiceNotificationManager;

import io.reactivex.rxjava3.disposables.Disposable;

public class MyService extends LocalizationService implements CallbackEventView {
    private FloatingRecordManager floatingRecordManager;
    private FloatingCameraViewManager floatingCameraViewManager;
    private Disposable rxBusDisposable;
    private Intent mResultData;
    private int mResultCode = -100;
    private Rect rect;

    @Override
    public void onCreate() {
        super.onCreate();
        initRxBus();
        ServiceNotificationManager.getInstance(this).showMainNotification(this);
        if (PreferencesHelper.getBoolean(PreferencesHelper.PREFS_TOOLS_SCREEN_SHOT, false)) {
            showHideFloatingViewScreenShot(true);
        }
        if (PreferencesHelper.getBoolean(PreferencesHelper.PREFS_TOOLS_CAMERA, false)) {
            showHideFloatingViewCamera(true);
        }
        if (PreferencesHelper.getBoolean(PreferencesHelper.PREFS_TOOLS_BRUSH, false)) {
            showHideFloatingViewBrush(true);
        }
    }

    private void initRxBus() {
        rxBusDisposable = RxBus.getInstance().subscribe(new CallBackRxBus(this));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case Config.ACTION_SHOW_MAIN_FLOATING:
                    if (PreferencesHelper.getBoolean(PreferencesHelper.KEY_FLOATING_CONTROL, false)) {
                        rect = (Rect) intent.getExtras().get(ACTION_SHOW_MAIN_FLOATING);
                        actionShowMain();
                    }
                    ServiceNotificationManager.getInstance(this).showMainNotification(this);
                    break;
                case Config.ACTION_DISABLE_FLOATING:
                    disableFloating();
                    break;
                case Config.ACTION_SHOW_TOOLS:
                    FloatingMainManager.getInstance(this,rect).showTools();
                    break;
                case Config.ACTION_SHOW_CAMERA:
                    try {
                        floatingCameraViewManager = new FloatingCameraViewManager(this);
                    } catch (Exception e) {

                    }
                    break;
                case Config.ACTION_SCREEN_SHOT_START:
                    actionScreenShot(intent);
                    break;
                case Config.ACTION_SCREEN_RECORDING_START:
                    actionRecord(intent);
                    break;
                case Config.ACTION_STOP_SHAKE:
                    if (floatingRecordManager != null) {
                        stopRecord();
                        floatingRecordManager.stopShakeManager();
                    }
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void disableFloating() {
        if (floatingRecordManager != null) {
            floatingRecordManager.onFinishFloatingView();
        }
        if (floatingCameraViewManager != null) {
            floatingCameraViewManager.onFinishFloatingView();
        }
        FloatingScreenShotManager.getInstance(this,rect).onFinishFloatingView();
        FloatingBrushManager.getInstance(this,rect).onFinishFloatingView();
        FloatingMainManager.getInstance(this,rect).onFinishFloatingView();
    }

    private void actionShowMain() {
        try {
            FloatingMainManager.getInstance(this,rect);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void actionScreenShot(Intent intent) {
        Intent resultData = intent.getParcelableExtra(Config.KEY_SCREEN_SHOT_INTENT);
        int resultCode = intent.getIntExtra(Config.KEY_SCREEN_SHOT_RESULT_CODE, Activity.RESULT_OK);
        FloatingScreenShotManager.getInstance(this,rect).captureScreen(resultData, resultCode);
    }

    private void showHideFloatingViewScreenShot(boolean isShow) {
        if (isShow) {
            FloatingScreenShotManager.getInstance(this,rect).addFloatingView();
        } else {
            FloatingScreenShotManager.getInstance(this,rect).onFinishFloatingView();
        }
    }

    private void showHideFloatingViewCamera(boolean isShow) {
        if (isShow) {
            Intent intent = new Intent(this, CameraActivity.class);
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
            Toolbox.startActivityAllStage(this, intent);
        } else {
            if (floatingCameraViewManager != null) {
                floatingCameraViewManager.onFinishFloatingView();
            }
        }
    }

    private void showHideFloatingViewBrush(boolean isShow) {
        if (isShow) {
            FloatingBrushManager.getInstance(this,rect).addFloatingView();
        } else {
            FloatingBrushManager.getInstance(this,rect).onFinishFloatingView();
        }
    }

    private void actionRecord(Intent intent) {
        FloatingMainManager.getInstance(this,rect).showFloatingView();
        if (mResultData == null) {
            mResultData = intent.getParcelableExtra(Config.KEY_SCREEN_RECORD_INTENT);
        }
        if (mResultCode == -100) {
            mResultCode = intent.getIntExtra(Config.KEY_SCREEN_RECORD_RESULT_CODE, Activity.RESULT_OK);
        }
        floatingRecordManager = new FloatingRecordManager(this,
                mResultData,
                mResultCode,
                FloatingMainManager.getInstance(this,rect).getWindowParamsFloatingView().x,
                FloatingMainManager.getInstance(this,rect).getWindowParamsFloatingView().y,
                rect);
    }

    private void actionRecord() {
        FloatingMainManager.getInstance(this,rect).showFloatingView();
        floatingRecordManager = new FloatingRecordManager(this,
                mResultData,
                mResultCode,
                FloatingMainManager.getInstance(this,rect).getWindowParamsFloatingView().x,
                FloatingMainManager.getInstance(this,rect).getWindowParamsFloatingView().y,
                rect);
    }

    private void screenRecordSuccess() {
        if (floatingRecordManager != null) {
            ServiceNotificationManager.getInstance(this).showMainNotification(this);
            FloatingMainManager.getInstance(MyService.this,rect).updatePositionFloatingView(
                    floatingRecordManager.getWindowParamsFloatingView() == null ? 0 : floatingRecordManager.getWindowParamsFloatingView().x,
                    floatingRecordManager.getWindowParamsFloatingView() == null ? 0 : floatingRecordManager.getWindowParamsFloatingView().y
            );
            FloatingMainManager.getInstance(MyService.this,rect).showFloatingView();
        }
    }

    private void pauseOrPlayRecord(Object data) {
        ScreenRecordHelper.State state = (ScreenRecordHelper.State) data;
        if (state == ScreenRecordHelper.State.PAUSED) {
            ServiceNotificationManager.getInstance(this).showPausedNotification();
        } else if (state == ScreenRecordHelper.State.RECORDING) {
            ServiceNotificationManager.getInstance(this).showRecordingNotification();
        }
    }

    private void pauseRecord() {
        if (floatingRecordManager != null) {
            floatingRecordManager.pauseOrPlay();
        }
        ServiceNotificationManager.getInstance(this).showPausedNotification();
    }

    private void resumeRecord() {
        if (floatingRecordManager != null) {
            floatingRecordManager.pauseOrPlay();
        }
        ServiceNotificationManager.getInstance(this).showRecordingNotification();
    }

    private void stopRecord() {
        if (floatingRecordManager != null) {
            floatingRecordManager.stopRecording();
        }
    }

    private void startScreenshot() {
        FloatingMainManager.getInstance(this,rect).hideFloatingView();
        FloatingScreenShotManager.getInstance(this,rect).hideFloatingView();
        FloatingBrushManager.getInstance(this,rect).hideFloatingView();
    }

    private void screenshotSuccess() {
        FloatingMainManager.getInstance(this,rect).showFloatingView();
        FloatingScreenShotManager.getInstance(this,rect).showFloatingView();
        FloatingBrushManager.getInstance(this,rect).showFloatingView();
        FloatingBrushManager.getInstance(this,rect).onRemoveLayoutBrush();
    }

    private void showPreviewMedia(String path) {
        if (TextUtils.isEmpty(path))
            return;
        if (path.endsWith(".mp4")) {
            ServiceNotificationManager.getInstance(this).showScreenRecordSuccessNotification(path);
        } else {
            ServiceNotificationManager.getInstance(this).showScreenshotSuccessNotification(path);
        }
        Toolbox.scanFile(this, path);
        new LayoutPreviewMediaManager(this, path);
    }

    @Override
    public void onReceivedEvent(RxBusType type, Object data) {
        switch (type) {
            case STATE_PAUSE_OR_PLAY:
                pauseOrPlayRecord(data);
                break;
            case SCREEN_SHOT:
                screenshotSuccess();
                showPreviewMedia((String) data);
                break;
            case START_SCREEN_SHOT:
                startScreenshot();
                break;
            case SCREEN_RECORD_SUCCESS:
                if (data != null) {
                    showPreviewMedia((String) data);
                }
                screenRecordSuccess();
                break;
            case TOOLS_SCREEN_SHOT:
                showHideFloatingViewScreenShot((Boolean) data);
                break;
            case TOOLS_CAMERA:
                showHideFloatingViewCamera((Boolean) data);
                break;
            case TOOLS_BRUSH:
                showHideFloatingViewBrush((Boolean) data);
                break;
            case CLICK_SCREEN_SHOT_BRUSH:
                FloatingScreenShotManager.getInstance(this,rect).startCaptureScreen();
                break;
            case CLICK_NOTIFICATION_EXIT:
                stopSelf();
                break;
            case CLICK_NOTIFICATION_HOME:
                openHome();
                break;
            case CLICK_NOTIFICATION_TOOLS:
                openToolsActivity();
                break;
            case CLICK_NOTIFICATION_SCREEN_SHOT:
                FloatingScreenShotManager.getInstance(this,rect).startCaptureScreen();
                break;
            case CLICK_NOTIFICATION_SCREEN_RECORD:
            case RECORD:
                openRecord();
                break;
            case CLICK_NOTIFICATION_PAUSE:
                pauseRecord();
                break;
            case CLICK_NOTIFICATION_RESUME:
                resumeRecord();
                break;
            case CLICK_NOTIFICATION_STOP:
                stopRecord();
                break;
        }
    }

    private void openHome() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(Config.ACTION_OPEN_MAIN);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Toolbox.startActivityAllStage(this, intent);
    }

    private void openToolsActivity() {
        Intent intent = new Intent(this, ToolsActivity.class);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        Toolbox.startActivityAllStage(this, intent);
    }

    private void openRecord() {
        if (mResultData == null) {
            Intent intent = new Intent(this, RecordActivity.class);
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
            Toolbox.startActivityAllStage(this, intent);
        } else {
            actionRecord();
        }

    }

    @Override
    public void onDestroy() {
        try {
            unregisterReceiver(ServiceNotificationManager.getInstance(this).receiver);
            if (rxBusDisposable != null) {
                rxBusDisposable.dispose();
            }
            if (floatingRecordManager != null) {
                floatingRecordManager.onFinishFloatingView();
            }
            FloatingScreenShotManager.getInstance(this,rect).onFinishFloatingView();
            FloatingBrushManager.getInstance(this,rect).onFinishFloatingView();
            FloatingMainManager.getInstance(this,rect).onFinishFloatingView();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
