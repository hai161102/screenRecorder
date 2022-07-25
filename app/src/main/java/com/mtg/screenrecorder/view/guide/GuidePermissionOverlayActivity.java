package com.mtg.screenrecorder.view.guide;

import android.view.LayoutInflater;

import com.mtg.screenrecorder.base.BaseActivity;
import com.mtg.screenrecorder.databinding.ActivityGuidePermissionOverlayBinding;


public class GuidePermissionOverlayActivity extends BaseActivity<ActivityGuidePermissionOverlayBinding> {


    @Override
    protected ActivityGuidePermissionOverlayBinding getViewBinding() {
        return ActivityGuidePermissionOverlayBinding.inflate(LayoutInflater.from(this));
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initControl() {
        binding.tvOk.setOnClickListener(v -> finish());
    }
}