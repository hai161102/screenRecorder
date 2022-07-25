package com.mtg.screenrecorder.view.setting;




import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;

import com.mtg.screenrecorder.App;
import com.mtg.screenrecorder.base.BaseDialog;
import com.mtg.screenrecorder.base.rx.CallBackRxBus;
import com.mtg.screenrecorder.base.rx.CallbackEventView;
import com.mtg.screenrecorder.base.rx.RxBus;
import com.mtg.screenrecorder.base.rx.RxBusType;
import com.mtg.screenrecorder.databinding.DialogListAppBinding;
import com.mtg.screenrecorder.utils.PreferencesHelper;

import java.util.ArrayList;
import java.util.List;

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
        if (App.listTargetApp != null) {
            binding.rcvData.setVisibility(View.VISIBLE);
            binding.progress.setVisibility(View.GONE);
            listAppAdapter.addDatas((List<Apps>) App.listTargetApp);
        }else {
            binding.rcvData.setVisibility(View.VISIBLE);
            binding.progress.setVisibility(View.GONE);
        }
    }
}
