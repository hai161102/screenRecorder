package com.mtg.screenrecorder.service.layout;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.widget.SwitchCompat;

import com.mtg.screenrecorder.R;
import com.mtg.screenrecorder.base.rx.RxBusHelper;
import com.mtg.screenrecorder.base.rx.RxBusType;
import com.mtg.screenrecorder.service.base.BaseLayoutWindowManager;
import com.mtg.screenrecorder.utils.PreferencesHelper;

public class LayoutToolsManager extends BaseLayoutWindowManager {

    public LayoutToolsManager(Context context) {
        super(context);
        initParams();
        addLayout();
    }

    private void initParams() {
        mParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        mParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected int getRootViewID() {
        return R.layout.layout_tools;
    }

    @Override
    protected void initLayout() {
        ImageView imvClose = rootView.findViewById(R.id.imv_close);
        SwitchCompat swScreenShot = rootView.findViewById(R.id.sw_screen_shot);
        SwitchCompat swCamera = rootView.findViewById(R.id.sw_camera);
        SwitchCompat swBrush = rootView.findViewById(R.id.sw_brush);

        imvClose.setOnClickListener(v -> removeLayout());

        swScreenShot.setChecked(PreferencesHelper.getBoolean(PreferencesHelper.PREFS_TOOLS_SCREEN_SHOT, false));
        swCamera.setChecked(PreferencesHelper.getBoolean(PreferencesHelper.PREFS_TOOLS_CAMERA, false));
        swBrush.setChecked(PreferencesHelper.getBoolean(PreferencesHelper.PREFS_TOOLS_BRUSH, false));
        swScreenShot.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PreferencesHelper.putBoolean(PreferencesHelper.PREFS_TOOLS_SCREEN_SHOT, isChecked);
            RxBusHelper.sendCheckedTools(RxBusType.TOOLS_SCREEN_SHOT, isChecked);
        });

        swCamera.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PreferencesHelper.putBoolean(PreferencesHelper.PREFS_TOOLS_CAMERA, isChecked);
            RxBusHelper.sendCheckedTools(RxBusType.TOOLS_CAMERA, isChecked);
        });

        swBrush.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PreferencesHelper.putBoolean(PreferencesHelper.PREFS_TOOLS_BRUSH, isChecked);
            RxBusHelper.sendCheckedTools(RxBusType.TOOLS_BRUSH, isChecked);
        });
    }
}
