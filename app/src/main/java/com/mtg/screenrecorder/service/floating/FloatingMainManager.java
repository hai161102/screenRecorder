package com.mtg.screenrecorder.service.floating;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;

import com.mtg.screenrecorder.R;
import com.mtg.screenrecorder.base.rx.RxBusHelper;
import com.mtg.screenrecorder.service.layout.LayoutBlurManager;
import com.mtg.screenrecorder.service.layout.LayoutMainLeftManager;
import com.mtg.screenrecorder.service.layout.LayoutMainRightManager;
import com.mtg.screenrecorder.service.base.BaseFloatingManager;
import com.mtg.screenrecorder.service.layout.LayoutToolsManager;
import com.mtg.screenrecorder.view.activity.MainActivity;
import com.mtg.screenrecorder.utils.Config;
import com.mtg.screenrecorder.utils.Toolbox;

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
