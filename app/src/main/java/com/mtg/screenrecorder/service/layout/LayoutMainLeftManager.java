package com.mtg.screenrecorder.service.layout;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.mtg.screenrecorder.R;
import com.mtg.screenrecorder.service.base.BaseLayoutWindowManager;
import com.mtg.screenrecorder.utils.ViewUtils;

public class LayoutMainLeftManager extends BaseLayoutWindowManager {
    private CallBack callBack;
    private Handler handler;

    public LayoutMainLeftManager(Context context, WindowManager.LayoutParams mParams, CallBack callBack) {
        super(context);
        this.callBack = callBack;
        initParams(mParams);
        addLayout();
        handler = new Handler();
        handler.postDelayed(callBack::onClickLayout, 3000);
    }

    private void initParams(WindowManager.LayoutParams params) {
        mParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        mParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        mParams.gravity = Gravity.START | Gravity.BOTTOM;
        mParams.x = params.x;
        mParams.y = params.y - (int) context.getResources().getDimension(R.dimen.margin);
    }

    @Override
    protected int getRootViewID() {
        return R.layout.layout_floating_main_left;
    }

    @Override
    protected void initLayout() {
        rootView.findViewById(R.id.container_main).setOnClickListener(v -> {
            vibrateWhenClick();
            callBack.onClickLayout();
        });

        rootView.findViewById(R.id.imv_record).setOnClickListener(v -> {
            callBack.onClickRecord();
            vibrateWhenClick();
        });
        rootView.findViewById(R.id.imv_tools).setOnClickListener(v -> {
            callBack.onClickTools();
            vibrateWhenClick();
        });
        rootView.findViewById(R.id.imv_home).setOnClickListener(v -> {
            callBack.onClickHome();
            vibrateWhenClick();
        });
        rootView.findViewById(R.id.imv_setting).setOnClickListener(v -> {
            callBack.onClickSetting();
            vibrateWhenClick();
        });
        ViewUtils.scaleSelected(rootView.findViewById(R.id.container_main), rootView.findViewById(R.id.imv_record), rootView.findViewById(R.id.imv_tools), rootView.findViewById(R.id.imv_home), rootView.findViewById(R.id.imv_setting));
    }

    @Override
    public void removeLayout() {
        super.removeLayout();
        handler.removeCallbacksAndMessages(null);
    }

    public interface CallBack {
        void onClickLayout();

        void onClickRecord();

        void onClickTools();

        void onClickHome();

        void onClickSetting();
    }
}
