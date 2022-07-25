package com.example.screenrecorderv2.service.layout;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.screenrecorderv2.R;
import com.example.screenrecorderv2.service.base.BaseLayoutWindowManager;
import com.example.screenrecorderv2.service.floating.FloatingRecordManager;
import com.example.screenrecorderv2.utils.ScreenRecordHelper;
import com.example.screenrecorderv2.utils.ViewUtils;

public class LayoutRecordRightManager extends BaseLayoutWindowManager {
    private Callback callBack;
    private TextView tvTime;
    private Handler handler;

    public LayoutRecordRightManager(Context context, WindowManager.LayoutParams params, String time, Callback callBack) {
        super(context);
        this.callBack = callBack;
        initParams(params);
        addLayout();
        setTime(time);
        handler = new Handler();
        handler.postDelayed(callBack::onClickLayout, 3000);
    }

    private void initParams(WindowManager.LayoutParams params) {
        mParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        mParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        mParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
        mParams.x = (int) (params.x - (int) context.getResources().getDimension(R.dimen.margin));
        mParams.y = (int) (params.y - ((int) context.getResources().getDimension(R.dimen.margin) - Math.cos(30) * (int) context.getResources().getDimension(R.dimen.margin)));
    }

    @Override
    protected int getRootViewID() {
        return R.layout.layout_floating_recording_right;
    }

    public void setTime(String time) {
        tvTime.setText(time);
    }

    public void setPauseOrResume(ScreenRecordHelper.State state) {
        if (state == ScreenRecordHelper.State.RECORDING) {
            ((ImageView) rootView.findViewById(R.id.imv_pause)).setImageResource(R.drawable.ic_pause);
        } else if (state == ScreenRecordHelper.State.PAUSED) {
            ((ImageView) rootView.findViewById(R.id.imv_pause)).setImageResource(R.drawable.ic_play);
        }
    }

    @Override
    protected void initLayout() {
        tvTime = rootView.findViewById(R.id.tv_time);
        if (ScreenRecordHelper.STATE == ScreenRecordHelper.State.PAUSED) {
            ((ImageView) rootView.findViewById(R.id.imv_pause)).setImageResource(R.drawable.ic_play);
        } else if (ScreenRecordHelper.STATE == ScreenRecordHelper.State.RECORDING) {
            ((ImageView) rootView.findViewById(R.id.imv_pause)).setImageResource(R.drawable.ic_pause);
        }

        rootView.findViewById(R.id.container_main).setOnClickListener(v -> {
            callBack.onClickLayout();
            vibrateWhenClick();
        });
        rootView.findViewById(R.id.imv_pause).setOnClickListener(v -> {
            callBack.onClickPauseOrPlay();
            vibrateWhenClick();
        });
        rootView.findViewById(R.id.imv_stop).setOnClickListener(v -> {
            callBack.onClickStop();
            vibrateWhenClick();
        });
        rootView.findViewById(R.id.imv_tools).setOnClickListener(v -> {
            callBack.onClickTools();
            vibrateWhenClick();
        });
        ViewUtils.scaleSelected(rootView.findViewById(R.id.container_main), rootView.findViewById(R.id.imv_pause), rootView.findViewById(R.id.imv_stop), rootView.findViewById(R.id.imv_tools));
    }

    @Override
    public void removeLayout() {
        super.removeLayout();
        handler.removeCallbacksAndMessages(null);
    }

    public interface Callback {
        void onClickLayout();

        void onClickPauseOrPlay();

        void onClickTools();

        void onClickStop();
    }
}
