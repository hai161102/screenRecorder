package com.mtg.screenrecorder.view.activity.screenshot;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.mtg.screenrecorder.base.rx.RxBusHelper;
import com.mtg.screenrecorder.service.MyService;
import com.mtg.screenrecorder.view.activity.guide.GuidePermissionOverlayActivity;
import com.mtg.screenrecorder.utils.Config;
import com.mtg.screenrecorder.utils.PreferencesHelper;

public class ScreenShotActivity extends AppCompatActivity {
    private static final int REQUEST_MEDIA_PROJECTION = 826;
    private static final int REQUEST_STORAGE = 827;
    private static final int REQUEST_SETTING_OVERLAY_PERMISSION = 830;

    private MediaProjectionManager mMediaProjectionManager;

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mMediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        }
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void activeScreenCapture() {
        startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
    }

    private void askPermissionStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                                , Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE);
            } else {
                activeScreenCapture();
            }
        } else {
            activeScreenCapture();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    activeScreenCapture();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case REQUEST_MEDIA_PROJECTION:
                if (resultCode != RESULT_OK) {
                    RxBusHelper.sendScreenShot("");
                    new Handler().postDelayed(this::finish, 200);
                    return;
                }
                new Handler().postDelayed(() -> startCaptureScreen(resultCode, intent), 200);
                finish();
                break;
            case REQUEST_SETTING_OVERLAY_PERMISSION:
                new Handler().postDelayed(() -> {
                    if (isSystemAlertPermissionGranted(ScreenShotActivity.this)) {
                        PreferencesHelper.putBoolean(PreferencesHelper.KEY_FLOATING_CONTROL, true);
                        askPermissionStorage();
                    } else {
                        finish();
                    }
                }, 200);
                break;
        }
    }

    private void startCaptureScreen(int resultcode, Intent intentData) {
        Intent intent = new Intent(this, MyService.class);
        intent.setAction(Config.ACTION_SCREEN_SHOT_START);
        intent.putExtra(Config.KEY_SCREEN_SHOT_RESULT_CODE, resultcode);
        intent.putExtra(Config.KEY_SCREEN_SHOT_INTENT, intentData);
        startService(intent);
    }
}
