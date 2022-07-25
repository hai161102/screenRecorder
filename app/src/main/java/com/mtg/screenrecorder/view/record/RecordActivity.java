package com.mtg.screenrecorder.view.record;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.mtg.screenrecorder.service.MyService;
import com.mtg.screenrecorder.view.guide.GuidePermissionOverlayActivity;
import com.mtg.screenrecorder.utils.Config;
import com.mtg.screenrecorder.utils.PreferencesHelper;

public class RecordActivity extends AppCompatActivity {
    private static final int SCREEN_RECORD_REQUEST_CODE = 832;
    private static final int REQUEST_STORAGE = 827;
    private static final int REQUEST_SETTING_OVERLAY_PERMISSION = 829;

    private MediaProjectionManager mProjectionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
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
            askPermissionStorage();
        } else {
            startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getApplicationContext().getPackageName())), REQUEST_SETTING_OVERLAY_PERMISSION);
            startActivity(new Intent(this, GuidePermissionOverlayActivity.class));
        }
    }

    private void askPermissionStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                                , Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE);
            } else {
                activeScreenRecord();
            }
        } else {
            activeScreenRecord();
        }
    }

    private void activeScreenRecord() {
        startActivityForResult(mProjectionManager.createScreenCaptureIntent(), SCREEN_RECORD_REQUEST_CODE);
    }

    private void startRecordScreen(int resultcode, Intent intentData) {
        Intent intent = new Intent(this, MyService.class);
        intent.setAction(Config.ACTION_SCREEN_RECORDING_START);
        intent.putExtra(Config.KEY_SCREEN_RECORD_INTENT, intentData);
        intent.putExtra(Config.KEY_SCREEN_RECORD_RESULT_CODE, resultcode);
        startService(intent);
        new Handler().postDelayed(this::finishAffinity,2000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    activeScreenRecord();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCREEN_RECORD_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    startRecordScreen(resultCode, data);
                } else {
                    finish();
                }
                break;
            case REQUEST_SETTING_OVERLAY_PERMISSION:
                new Handler().postDelayed(() -> {
                    if (isSystemAlertPermissionGranted(RecordActivity.this)) {
                        PreferencesHelper.putBoolean(PreferencesHelper.KEY_FLOATING_CONTROL, true);
                        askPermissionStorage();
                    } else {
                        finish();
                    }
                }, 200);
                break;
        }
    }
}
