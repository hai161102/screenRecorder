package com.example.screenrecorderv2.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.screenrecorderv2.R;
import com.example.screenrecorderv2.base.BaseRecyclerAdapter;
import com.example.screenrecorderv2.base.BaseViewHolder;
import com.example.screenrecorderv2.databinding.ItemEditBinding;
import com.example.screenrecorderv2.ui.editvideo.ItemEdit;
import com.example.screenrecorderv2.utils.ViewUtils;

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
