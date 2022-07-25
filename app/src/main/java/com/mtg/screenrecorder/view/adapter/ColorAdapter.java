package com.mtg.screenrecorder.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.mtg.screenrecorder.base.BaseRecyclerAdapter;
import com.mtg.screenrecorder.base.BaseViewHolder;
import com.mtg.screenrecorder.databinding.ItemColorBinding;

import java.util.List;

public class ColorAdapter extends BaseRecyclerAdapter<Integer> {
    private int itemCheck = Color.parseColor("#000000");

    public int getItemCheck() {
        return itemCheck;
    }

    public ColorAdapter(List<Integer> list, Context context) {
        super(list, context);
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemColorBinding.inflate(LayoutInflater.from(context)));
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ((ViewHolder) holder).binding.imvColor.setBackgroundColor(list.get(position));
            if (itemCheck == list.get(position)) {
                ((ViewHolder) holder).binding.imvCheck.setVisibility(View.VISIBLE);
            } else {
                ((ViewHolder) holder).binding.imvCheck.setVisibility(View.GONE);
            }
            ((ViewHolder) holder).binding.getRoot().setOnClickListener(v -> {
                itemCheck = list.get(position);
                callBackAdapter.onClickItem(list.get(position));
                notifyDataSetChanged();
            });
        }

    }

    public class ViewHolder extends BaseViewHolder<ItemColorBinding> {

        public ViewHolder(ItemColorBinding binding) {
            super(binding);
        }
    }
}
