package com.mtg.screenrecorder.view.splash;

import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;

import com.mtg.screenrecorder.base.BaseActivity;
import com.mtg.screenrecorder.databinding.ActivitySplashBinding;
import com.mtg.screenrecorder.view.main.MainActivity;

public class SplashActivity extends BaseActivity<ActivitySplashBinding> {
    private final int SPLASH_DELAY = 3500;

    @Override
    protected ActivitySplashBinding getViewBinding() {
        return ActivitySplashBinding.inflate(LayoutInflater.from(this));
    }

    @Override
    protected void initView() {
        new Handler().postDelayed(() -> startToMainActivity(), SPLASH_DELAY);
    }

    @Override
    protected void initControl() {

    }

    public void startToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
