package com.example.screenrecorderv2.service.floating;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;

import com.example.screenrecorderv2.R;
import com.example.screenrecorderv2.base.rx.RxBusHelper;
import com.example.screenrecorderv2.service.layout.LayoutBlurManager;
import com.example.screenrecorderv2.service.layout.LayoutMainLeftManager;
import com.example.screenrecorderv2.service.layout.LayoutMainRightManager;
import com.example.screenrecorderv2.service.base.BaseFloatingManager;
import com.example.screenrecorderv2.service.layout.LayoutTimerManager;
import com.example.screenrecorderv2.service.layout.LayoutToolsManager;
import com.example.screenrecorderv2.ui.main.MainActivity;
import com.example.screenrecorderv2.ui.record.RecordActivity;
import com.example.screenrecorderv2.utils.Config;
import com.example.screenrecorderv2.utils.PreferencesHelper;
import com.example.screenrecorderv2.utils.Toolbox;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class FloatingMainManager extends BaseFloatingManager {
    private LayoutMainLeftManager layoutMainLeftManager;
    private LayoutMainRightManager layoutMainRightManager;
    private LayoutBlurManager layoutBlurManager;

    private static FloatingMainManager instance;

    public static FloatingMainManager getInstance(Context context, Rect rect) {
        if (instance == null) {
            instance = new FloatingMainManager(context,rect);
        }
        return instance;
    }

    public FloatingMainManager(Context context,Rect rect) {
        super(context,rect);
        addFloatingView();
    }

    protected void initLayout() {
        rootView.setOnClickListener(v -> initLayoutMainManager());
    }

    @Override
    protected void setUpOptionsFloating() {
        super.setUpOptionsFloating();
        options.isEnableAlpha = true;
        options.overMargin = 16;
        options.floatingViewWidth = (int) context.getResources().getDimension(R.dimen.size_floating_icon);
        options.floatingViewHeight = (int) context.getResources().getDimension(R.dimen.size_floating_icon);
        options.floatingViewX = 0;
        options.floatingViewY = metrics.heightPixels / 2 - options.floatingViewHeight;
    }

    @Override
    protected int getRootViewID() {
        return R.layout.floating_view_main;
    }

    private void initLayoutMainManager() {
        if (mFloatingViewManager == null) return;
        vibrateWhenClick();
        hideFloatingView();
        showBlur();
        if (isFloatingViewInLeft()) {
            layoutMainLeftManager = new LayoutMainLeftManager(context, mFloatingViewManager.getWindowParamsFloatingView(), new LayoutMainLeftManager.CallBack() {
                @Override
                public void onClickLayout() {
                    clearStateShowMainLayout();
                }

                @Override
                public void onClickRecord() {
                    openRecord();
                    clearStateShowMainLayout();
                }

                @Override
                public void onClickTools() {
                    showTools();
                    clearStateShowMainLayout();
                }

                @Override
                public void onClickHome() {
                    openHome();
                    clearStateShowMainLayout();
                }

                @Override
                public void onClickSetting() {
                    openSetting();
                    clearStateShowMainLayout();
                }
            });
        } else {
            layoutMainRightManager = new LayoutMainRightManager(context, mFloatingViewManager.getWindowParamsFloatingView(), new LayoutMainRightManager.Callback() {
                @Override
                public void onClickLayout() {
                    clearStateShowMainLayout();
                }

                @Override
                public void onClickRecord() {
                    openRecord();
                    clearStateShowMainLayout();
                }

                @Override
                public void onClickTools() {
                    showTools();
                    clearStateShowMainLayout();
                }

                @Override
                public void onClickHome() {
                    openHome();
                    clearStateShowMainLayout();
                }

                @Override
                public void onClickSetting() {
                    openSetting();
                    clearStateShowMainLayout();
                }
            });
        }
    }

    private void openHome() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setAction(Config.ACTION_OPEN_MAIN);
        Toolbox.startActivityAllStage(context, intent);
    }

    private void openSetting() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setAction(Config.ACTION_OPEN_SETTING);
        Toolbox.startActivityAllStage(context, intent);
    }

    private void openRecord() {
        RxBusHelper.sendStartRecord();
    }

    private void showBlur() {
        layoutBlurManager = new LayoutBlurManager(context, this::clearStateShowMainLayout);
    }

    public void showTools() {
        new LayoutToolsManager(context);
    }

    private void clearStateShowMainLayout() {
        showFloatingView();
        if (layoutBlurManager != null) {
            layoutBlurManager.removeLayout();
        }
        if (layoutMainRightManager != null) {
            layoutMainRightManager.removeLayout();
        }
        if (layoutMainLeftManager != null) {
            layoutMainLeftManager.removeLayout();
        }
    }

    private boolean isFloatingViewInLeft() {
        if (mFloatingViewManager == null) return false;
        return mFloatingViewManager.getXFloatingView() < metrics.widthPixels / 2;
    }

    @Override
    public void onFinishFloatingView() {
        clearStateShowMainLayout();
        instance = null;
        super.onFinishFloatingView();
    }
}
