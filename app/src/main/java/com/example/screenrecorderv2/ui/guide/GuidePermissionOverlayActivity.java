package com.example.screenrecorderv2.ui.guide;

import android.view.LayoutInflater;

import com.example.screenrecorderv2.base.BaseActivity;
import com.example.screenrecorderv2.databinding.ActivityGuidePermissionOverlayBinding;


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