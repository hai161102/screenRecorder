package com.mtg.screenrecorder.view.cameraview;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.mtg.screenrecorder.service.MyService;
import com.mtg.screenrecorder.view.guide.GuidePermissionOverlayActivity;
import com.mtg.screenrecorder.utils.Config;

public class CameraActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA_VIEW = 829;
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
            askPermissionCamera();
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
                if (isSystemAlertPermissionGranted(CameraActivity.this)) {
                    askPermissionCamera();
                } else {
                    finishAffinity();
                }
            }, 200);
        }
    }

    private void askPermissionCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_VIEW);
            } else {
                showCamera();
            }
        } else {
            showCamera();
        }
    }

    private void showCamera() {
        Intent intent = new Intent(this, MyService.class);
        intent.setAction(Config.ACTION_SHOW_CAMERA);
        startService(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_VIEW:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    showCamera();
                break;
        }
    }
}
