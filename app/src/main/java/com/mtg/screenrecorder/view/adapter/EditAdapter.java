package com.mtg.screenrecorder.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.mtg.screenrecorder.base.BaseRecyclerAdapter;
import com.mtg.screenrecorder.base.BaseViewHolder;
import com.mtg.screenrecorder.databinding.ItemEditBinding;
import com.mtg.screenrecorder.view.editvideo.ItemEdit;
import com.mtg.screenrecorder.utils.ViewUtils;

import java.util.List;

public class EditAdapter extends BaseRecyclerAdapter<ItemEdit> {
    public EditAdapter(List<ItemEdit> list, Context context) {
        super(list, context);
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemEditBinding.inflate(LayoutInflater.from(context)));
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ViewUtils.loadImage(context, list.get(position).getIcon(), ((ViewHolder) holder).binding.imvIcon);
            ((ViewHolder) holder).binding.tv.setText(list.get(position).getTitle());
            ((ViewHolder) holder).binding.container.setOnClickListener(v -> onClickItem(list.get(position)));
        }

    }

    class ViewHolder extends BaseViewHolder<ItemEditBinding> {

        public ViewHolder(ItemEditBinding binding) {
            super(binding);
        }
    }
}
