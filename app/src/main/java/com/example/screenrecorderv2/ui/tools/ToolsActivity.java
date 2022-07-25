package com.example.screenrecorderv2.ui.tools;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.screenrecorderv2.service.MyService;
import com.example.screenrecorderv2.ui.guide.GuidePermissionOverlayActivity;
import com.example.screenrecorderv2.utils.Config;
import com.example.screenrecorderv2.utils.PreferencesHelper;

public class ToolsActivity extends AppCompatActivity {
    private static final int REQUEST_SETTING_OVERLAY_PERMISSION = 830;

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        askPermissionOverlay();
    }

    public static boolean isSystemAlertPermissionGranted(Context context) {
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }
        return Settings.canDrawOverlays(context);
    }

    public void askPermissionOverlay() {
        if (isSystemAlertPermissionGranted(this)) {
            PreferencesHelper.putBoolean(PreferencesHelper.KEY_FLOATING_CONTROL, true);
            showFloatingTools();
        } else {
            startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getApplicationContext().getPackageName())), REQUEST_SETTING_OVERLAY_PERMISSION);
            startActivity(new Intent(this, GuidePermissionOverlayActivity.class));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SETTING_OVERLAY_PERMISSION) {
            new Handler().postDelayed(() -> {
                if (isSystemAlertPermissionGranted(ToolsActivity.this)) {
                    PreferencesHelper.putBoolean(PreferencesHelper.KEY_FLOATING_CONTROL, true);
                    showFloatingTools();
                } else {
                    finish();
                }
            }, 200);
        }
    }

    private void showFloatingTools() {
        Intent intent = new Intent(this, MyService.class);
        intent.setAction(Config.ACTION_SHOW_TOOLS);
        startService(intent);
        finish();
    }
}
