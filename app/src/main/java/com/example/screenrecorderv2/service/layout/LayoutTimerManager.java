package com.example.screenrecorderv2.service.layout;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.screenrecorderv2.R;
import com.example.screenrecorderv2.service.base.BaseLayoutWindowManager;
import com.example.screenrecorderv2.utils.Config;
import com.example.screenrecorderv2.utils.PreferencesHelper;

public class LayoutTimerManager extends BaseLayoutWindowManager {
    private TextView tvTimer;
    private CallBack callBack;

    public LayoutTimerManager(Context context, CallBack callBack) {
        super(context);
        this.callBack = callBack;
        initParams();
        addLayout();
        handlerTimer();
    }

    private void initParams() {
        mParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        mParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected int getRootViewID() {
        return R.layout.layout_timer;
    }

    @Override
    protected void initLayout() {
        tvTimer = rootView.findViewById(R.id.tv_timer);
    }

    public void handlerTimer() {
        final Animation animation;
        animation = AnimationUtils.loadAnimation(context,
                R.anim.scale_tv_time);
        int x = Integer.parseInt(PreferencesHelper.getString(PreferencesHelper.KEY_TIMER, Config.itemsTimer[1].getValue()));
        if (x == 0) {
            removeLayout();
            callBack.onTimeEnd();
            return;
        }
        new CountDownTimer((x + 1) * 1000, 1000) {
            @Override
            public void onFinish() {
                tvTimer.setText("");
                removeLayout();
                new Handler().postDelayed(() -> callBack.onTimeEnd(),500);
            }

            @Override
            public void onTick(long millisUntilFinished) {
                tvTimer.setText(String.valueOf(millisUntilFinished / 1000));
                tvTimer.startAnimation(animation);
            }
        }.start();
    }

    public interface CallBack {
        void onTimeEnd();
    }
}
