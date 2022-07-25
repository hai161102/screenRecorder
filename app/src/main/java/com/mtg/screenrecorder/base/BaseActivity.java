package com.mtg.screenrecorder.base;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewbinding.ViewBinding;


import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.mtg.screenrecorder.base.rx.CallBackRxBus;
import com.mtg.screenrecorder.base.rx.CallbackEventView;
import com.mtg.screenrecorder.base.rx.RxBus;
import com.mtg.screenrecorder.view.main.DialogAskPermission;

import java.util.concurrent.Callable;

import io.reactivex.rxjava3.disposables.Disposable;

public abstract class BaseActivity<B extends ViewBinding> extends LocalizationActivity implements CallbackEventView {
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 0;
    private static final int MY_PERMISSIONS_REQUEST_STORAGE = 1;
    private static final int MY_PERMISSIONS_REQUEST_RECORD = 2;
    private static final int MY_PERMISSIONS_REQUEST_ALL_FILE_MANAGE = 3;
    protected B binding;
    protected Callable<Void> callable;
    protected Disposable rxBusDisposable;
    protected DialogAskPermission dialogAskPermissionStorage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getViewBinding();
        setContentView(binding.getRoot());
        initRxBus();
        initView();
        initControl();
    }

    private void initRxBus() {
        rxBusDisposable = RxBus.getInstance().subscribe(new CallBackRxBus(this));
    }

    protected void setTitleToolbar(String title) {
        if (!TextUtils.isEmpty(title) && getToolbarTitle() != null)
            getToolbarTitle().setText(title);
    }

    protected void setNavigationIcon(int res) {
        if (getToolbar() != null)
            getToolbar().setNavigationIcon(res);
    }

    protected Toolbar getToolbar() {
        return null;
    }

    protected TextView getToolbarTitle() {
        return null;
    }

    public void toast(String content) {
        if (!TextUtils.isEmpty(content))
            Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }

    public void toast(@StringRes int resId) {
        Toast.makeText(this, getResources().getString(resId), Toast.LENGTH_SHORT).show();
    }

    protected void callListener() {
        try {
            this.callable.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected abstract B getViewBinding();

    protected abstract void initView();

    protected abstract void initControl();

    public void askPermissionCamera(Callable<Void> callable) {
        this.callable = callable;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
            } else {
                try {
                    callable.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                callable.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private DialogAskPermission dialogAskPermissionRecord;

    public void askPermissionRecord(Callable<Void> callable) {
        this.callable = callable;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                dialogAskPermissionRecord =  DialogAskPermission.getInstance(Manifest.permission.RECORD_AUDIO, () -> ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_REQUEST_RECORD));
                dialogAskPermissionRecord.show(getSupportFragmentManager(), DialogAskPermission.class.getName());
            } else {
                try {
                    callable.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                callable.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void askPermissionStorage(Callable<Void> callable) {
        this.callable = callable;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ) {
                dialogAskPermissionStorage = DialogAskPermission.getInstance(Manifest.permission.READ_EXTERNAL_STORAGE, () -> ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                                , Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_STORAGE));
                dialogAskPermissionStorage.show(getSupportFragmentManager(), DialogAskPermission.class.getName());
            } else {
                try {
                    callable.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                callable.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_RECORD:
            case MY_PERMISSIONS_REQUEST_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    callListener();
                break;
            case MY_PERMISSIONS_REQUEST_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callListener();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ALL_FILE_MANAGE:
                if (Environment.isExternalStorageManager()) {
                    callListener();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (rxBusDisposable != null) {
            rxBusDisposable.dispose();
        }
        if (dialogAskPermissionStorage!=null){
            dialogAskPermissionStorage.dismiss();
        }
        if (dialogAskPermissionRecord!=null){
            dialogAskPermissionRecord.dismiss();
        }
    }
}
