package com.mtg.screenrecorder.view.adapter.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.mtg.screenrecorder.base.BaseRecyclerAdapter;
import com.mtg.screenrecorder.base.BaseViewHolder;
import com.mtg.screenrecorder.databinding.ItemSelectedBinding;
import com.mtg.screenrecorder.utils.setting.ItemSelected;
import com.mtg.screenrecorder.view.dialog.DialogSingleSelected;

import java.util.List;

public class SelectedAdapter extends BaseRecyclerAdapter<ItemSelected> {
    private String selected = "";
    private DialogSingleSelected.CallBackDialog callBackDialog;
    private DialogSingleSelected dialogSingleSelected;

    public void setCallBackDialog(DialogSingleSelected.CallBackDialog callBackDialog) {
        this.callBackDialog = callBackDialog;
    }

    public void setDialogSingleSelected(DialogSingleSelected dialogSingleSelected) {
        this.dialogSingleSelected = dialogSingleSelected;
    }

    public String getSelected() {
        return selected;
    }

    public SelectedAdapter(List<ItemSelected> list, Context context, String selected) {
        super(list, context);
        this.selected = selected;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemSelectedBinding.inflate(LayoutInflater.from(context)));
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            binData(list.get(position), (ViewHolder) holder);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void binData(ItemSelected item, ViewHolder viewHolder) {
        if (item == null)
            return;
        if (!TextUtils.isEmpty(context.getResources().getString(item.getEntry()))) {
            viewHolder.binding.tvTitle.setText(context.getResources().getString(item.getEntry()));
        }
        if (TextUtils.isEmpty(selected)) {
            selected = item.getValue();
        }
        if (item.getDescription() != 0) {
            viewHolder.binding.tvDescription.setVisibility(View.VISIBLE);
            viewHolder.binding.tvDescription.setText(context.getResources().getString(item.getDescription()));
        }
        viewHolder.binding.rdSelect.setChecked(item.getValue().equals(selected));

        viewHolder.binding.getRoot().setOnClickListener(v -> {
            selected = item.getValue();
            onClickItem(item);
            callBackDialog.onOK(selected);
            dialogSingleSelected.dismiss();
            notifyDataSetChanged();
        });
    }

    public class ViewHolder extends BaseViewHolder<ItemSelectedBinding> {

        public ViewHolder(ItemSelectedBinding binding) {
            super(binding);
        }
    }
}
