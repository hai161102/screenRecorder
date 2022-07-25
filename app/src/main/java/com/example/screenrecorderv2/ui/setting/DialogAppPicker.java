package com.example.screenrecorderv2.ui.setting;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.screenrecorderv2.MyApp;
import com.example.screenrecorderv2.R;
import com.example.screenrecorderv2.base.BaseDialog;
import com.example.screenrecorderv2.base.rx.CallBackRxBus;
import com.example.screenrecorderv2.base.rx.CallbackEventView;
import com.example.screenrecorderv2.base.rx.RxBus;
import com.example.screenrecorderv2.base.rx.RxBusType;
import com.example.screenrecorderv2.databinding.DialogListAppBinding;
import com.example.screenrecorderv2.utils.PreferencesHelper;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DialogAppPicker extends BaseDialog<DialogListAppBinding> {

    private ListAppAdapter listAppAdapter;

    public DialogAppPicker(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void initData() {
        listAppAdapter = new ListAppAdapter(new ArrayList<>(), getContext());
        listAppAdapter.setCallBackAdapter(item -> {
            PreferencesHelper.putString(PreferencesHelper.KEY_APP_SELECTED, item.getPackageName());
            dismiss();
        });
        binding.rcvData.setAdapter(listAppAdapter);
        binding.tvCancel.setOnClickListener(v -> dismiss());
        RxBus.getInstance().subscribe(new CallBackRxBus(new CallbackEventView() {
            @Override
            public void onReceivedEvent(RxBusType type, Object data) {
                switch (type){
                    case LOAD_TARGET_APP_FINISHED:
                        fetchTargetApp();
                        break;
                }
            }
        }));
        fetchTargetApp();
    }

    @Override
    protected DialogListAppBinding getViewBinding() {
        return DialogListAppBinding.inflate(LayoutInflater.from(getContext()));
    }

    private void fetchTargetApp() {
        if (MyApp.listTargetApp != null) {
            binding.rcvData.setVisibility(View.VISIBLE);
            binding.progress.setVisibility(View.GONE);
            listAppAdapter.addDatas(MyApp.listTargetApp);
        }else {
            binding.rcvData.setVisibility(View.VISIBLE);
            binding.progress.setVisibility(View.GONE);
        }
    }
}
