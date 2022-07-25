package com.example.screenrecorderv2.service.layout;

import android.content.Context;
import android.view.ViewGroup;

import com.example.screenrecorderv2.R;
import com.example.screenrecorderv2.service.base.BaseLayoutWindowManager;

public class LayoutBlurManager extends BaseLayoutWindowManager {
    private Callback callback;

    public LayoutBlurManager(Context context, Callback callback) {
        super(context);
        this.callback = callback;
        initParams();
        addLayout();
    }

    private void initParams() {
        mParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        mParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected int getRootViewID() {
        return R.layout.layout_main_blur;
    }

    @Override
    protected void initLayout() {
        rootView.setOnClickListener(v -> callback.onClickLayout());
    }

    public interface Callback {
        void onClickLayout();
    }
}
