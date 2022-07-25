package com.example.screenrecorderv2.ui.setting;

import android.content.Context;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import com.example.screenrecorderv2.R;
import com.example.screenrecorderv2.base.BaseDialog;
import com.example.screenrecorderv2.databinding.DialogSingleSelectedBinding;
import com.example.screenrecorderv2.ui.setting.ItemSelected;
import com.example.screenrecorderv2.ui.setting.SelectedAdapter;

import java.util.List;

public class DialogSingleSelected extends BaseDialog<DialogSingleSelectedBinding> {
    private SelectedAdapter selectedAdapter;
    private CallBackDialog callBackDialog;

    public DialogSingleSelected(@NonNull Context context, String title, List<ItemSelected> itemSelecteds, String selected, CallBackDialog callBackDialog) {
        super(context);
        this.callBackDialog = callBackDialog;
        binding.tvTitle.setText(title);
        selectedAdapter = new SelectedAdapter(itemSelecteds, getContext(), selected);
        binding.rcvData.setAdapter(selectedAdapter);
    }

    @Override
    protected void initData() {
        binding.tvCancel.setOnClickListener(v -> dismiss());
        binding.tvOk.setOnClickListener(v -> {
            if (callBackDialog != null) {
                callBackDialog.onOK(selectedAdapter.getSelected());
                dismiss();
            }
        });
    }

    @Override
    protected DialogSingleSelectedBinding getViewBinding() {
        return DialogSingleSelectedBinding.inflate(LayoutInflater.from(getContext()));
    }

    public interface CallBackDialog {
        void onOK(String selected);
    }
}
