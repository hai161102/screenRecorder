package com.mtg.screenrecorder.service.floating;


import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.mtg.screenrecorder.R;
import com.mtg.screenrecorder.base.rx.RxBusHelper;
import com.mtg.screenrecorder.service.base.BaseFloatingManager;
import com.mtg.screenrecorder.service.layout.LayoutBlurManager;
import com.mtg.screenrecorder.service.layout.LayoutRecordLeftManager;
import com.mtg.screenrecorder.service.layout.LayoutRecordRightManager;
import com.mtg.screenrecorder.service.layout.LayoutTimerManager;
import com.mtg.screenrecorder.service.layout.LayoutToolsManager;
import com.mtg.screenrecorder.utils.PreferencesHelper;
import com.mtg.screenrecorder.utils.ScreenRecordHelper;
import com.mtg.screenrecorder.utils.ShakeEventManager;
import com.mtg.screenrecorder.utils.Toolbox;
import com.mtg.screenrecorder.utils.notification.ServiceNotificationManager;

import jp.co.recruit_lifestyle.android.floatingview.FloatingViewManager;

public class FloatingRecordManager extends BaseFloatingManager {

    private LayoutRecordRightManager layoutRecordRightManager;
    private LayoutRecordLeftManager layoutRecordLeftManager;
    private LayoutBlurManager layoutBlurManager;

    private int mResultCode;
    private Intent mResultData;
    private ScreenRecordHelper screenRecordHelper;
    private TextView tvTime;
    private ImageView imvTime;
    private int initX;
    private int initY;

    public FloatingRecordManager(Context context, Intent mResultData, int mResultCode, int initX, int initY, Rect rect) {
        super(context,rect);
        this.mResultCode = mResultCode;
        this.mResultData = mResultData;
        this.initX = initX;
        this.initY = initY;
        setUpOptionsFloating();
        initData();
    }

    @Override
    protected void initLayout() {
        mFloatingViewManager.setTrashViewEnabled(false);
        tvTime = rootView.findViewById(R.id.tv_time);
        imvTime = rootView.findViewById(R.id.imv_time);
        rootView.setOnClickListener(v -> {
            initLayoutMainManager();
        });
    }

    @Override
    protected void setUpOptionsFloating() {
        super.setUpOptionsFloating();
        options.isEnableAlpha = true;
        options.overMargin = 16;
        options.floatingViewWidth = (int) context.getResources().getDimension(R.dimen.size_floating_icon);
        options.floatingViewHeight = (int) context.getResources().getDimension(R.dimen.size_floating_icon);
        options.floatingViewX = initX;
        options.floatingViewY = initY;
        mFloatingViewManager.setCallBackStateFloating(new FloatingViewManager.CallBackStateFloating() {
            @Override
            public void onCollap() {
                setCollapsedIcon();
            }

            @Override
            public void onNormal() {
                imvTime.setImageResource(R.drawable.shape_floating);
                tvTime.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initData() {
        screenRecordHelper = new ScreenRecordHelper(context, metrics, new ScreenRecordHelper.CallBackRecordHelper() {
            @Override
            public void onTimeRun(int time) {
                setTime(time);
            }

            @Override
            public void onStopScreenRecording(String dstPath) {
                RxBusHelper.sendScreenRecordSuccess(dstPath);
                removeAllView();
            }

            @Override
            public void onError() {
                RxBusHelper.sendScreenRecordSuccess(null);
                removeAllView();
            }
        });
        // shake device to start/stop record
        if (PreferencesHelper.getBoolean(PreferencesHelper.KEY_SHAKE, false)) {
            ServiceNotificationManager.getInstance(context).showShakeNotification();
            ShakeEventManager.getInstance(context).setListener(() -> {
                if (ScreenRecordHelper.STATE != ScreenRecordHelper.State.RECORDING) {
                    ServiceNotificationManager.getInstance(context).hideShakeNotification();
                    showTimer();
                } else {
                    stopRecording();
                    ShakeEventManager.getInstance(context).stop();
                }
            });
        } else {
            showTimer();
        }
    }

    public void showTimer() {
        new LayoutTimerManager(context, () -> startRecording());
    }

    public void stopShakeManager() {
        ShakeEventManager.getInstance(context).stop();
        removeAllView();
    }

    private void vibrateWhenRecord() {
        Vibrator vibrate = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrate.vibrate(100);
    }

    private void initLayoutMainManager() {
        if (mFloatingViewManager == null) return;
        if (screenRecordHelper == null) return;
        vibrateWhenClick();
        hideFloatingView();
        showBlur();
        mFloatingViewManager.setStateWhenHideFloating();
        if (isFloatingViewInLeft()) {
            layoutRecordLeftManager = new LayoutRecordLeftManager(context, mFloatingViewManager.getWindowParamsFloatingView(), Toolbox.convertTime(screenRecordHelper.getTime() + ""), new LayoutRecordLeftManager.Callback() {
                @Override
                public void onClickLayout() {
                    clearStateShowMainLayout();
                }

                @Override
                public void onClickPauseOrPlay() {
                    pauseOrPlay();
                }

                @Override
                public void onClickTools() {
                    clearStateShowMainLayout();
                    showTools();
                }

                @Override
                public void onClickStop() {
                    stopRecording();
                }
            });
        } else {
            layoutRecordRightManager = new LayoutRecordRightManager(context, mFloatingViewManager.getWindowParamsFloatingView(), Toolbox.convertTime(screenRecordHelper.getTime() + ""), new LayoutRecordRightManager.Callback() {
                @Override
                public void onClickLayout() {
                    clearStateShowMainLayout();
                }

                @Override
                public void onClickPauseOrPlay() {
                    pauseOrPlay();
                }

                @Override
                public void onClickTools() {
                    clearStateShowMainLayout();
                    showTools();
                }

                @Override
                public void onClickStop() {
                    stopRecording();
                }
            });
        }
    }

    public void pauseOrPlay() {
        if (screenRecordHelper != null) {
            ScreenRecordHelper.State state = screenRecordHelper.togglePausePlay();
            RxBusHelper.sendUpdateSateNotificationRecord(state);
            if (layoutRecordRightManager != null) {
                layoutRecordRightManager.setPauseOrResume(state);
            }
            if (layoutRecordLeftManager != null) {
                layoutRecordLeftManager.setPauseOrResume(state);
            }
        }
    }

    public void stopRecording() {
        if (screenRecordHelper != null) {
            screenRecordHelper.stopScreenRecording();
        } else {
            RxBusHelper.sendScreenRecordSuccess(null);
            removeAllView();
        }
    }

    private void showBlur() {
        layoutBlurManager = new LayoutBlurManager(context, this::clearStateShowMainLayout);
    }

    public void showTools() {
        new LayoutToolsManager(context);
    }

    private void setTime(int time) {
        if (screenRecordHelper == null) return;
        String timeString = Toolbox.convertTime(String.valueOf(time));
        tvTime.setText(timeString);
        if (layoutRecordRightManager != null) {
            layoutRecordRightManager.setTime(timeString);
        }
        if (layoutRecordLeftManager != null) {
            layoutRecordLeftManager.setTime(timeString);
        }
    }

    public void removeAllView() {
        clearStateShowMainLayout();
        removeFloatingView();
    }

    private void clearStateShowMainLayout() {
        showFloatingView();
        if (mFloatingViewManager != null) {
            mFloatingViewManager.goToAlpha();
        }
        if (layoutBlurManager != null) {
            layoutBlurManager.removeLayout();
        }
        if (layoutRecordRightManager != null) {
            layoutRecordRightManager.removeLayout();
        }
        if (layoutRecordLeftManager != null) {
            layoutRecordLeftManager.removeLayout();
        }
    }

    private boolean isFloatingViewInLeft() {
        if (mFloatingViewManager == null) return false;
        return mFloatingViewManager.getXFloatingView() < metrics.widthPixels / 2;
    }

    private void startRecording() {
        ServiceNotificationManager.getInstance(context).showRecordingNotification();
        addFloatingView();
        FloatingMainManager.getInstance(context,rect).hideFloatingView();
        if (PreferencesHelper.getBoolean(PreferencesHelper.KEY_TARGET_APP, false)) {
            startAppBeforeRecording();
        }
        vibrateWhenRecord();
        screenRecordHelper.startRecording(mResultCode, mResultData);
    }

    private void startAppBeforeRecording() {
        String packagename = PreferencesHelper.getString(PreferencesHelper.KEY_APP_SELECTED, "");
        if (!TextUtils.isEmpty(packagename)) {
            Intent startAppIntent = context.getPackageManager().getLaunchIntentForPackage(packagename);
            context.startActivity(startAppIntent);
        }
    }

    public void setCollapsedIcon() {
        tvTime.setVisibility(View.GONE);
        imvTime.setVisibility(View.VISIBLE);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) imvTime.getLayoutParams();
        if (isFloatingViewInLeft()) {
            layoutParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
            imvTime.setLayoutParams(layoutParams);
            imvTime.setImageResource(R.drawable.shape_dot_left);
        } else {
            layoutParams.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
            imvTime.setLayoutParams(layoutParams);
            imvTime.setImageResource(R.drawable.shape_dot_right);
        }
        mFloatingViewManager.setCurrentState(FloatingViewManager.STATE.COLLAPSED);
    }

    @Override
    protected int getRootViewID() {
        return R.layout.floating_view_record;
    }
}
