package com.example.screenrecorderv2.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.screenrecorderv2.R;
import com.example.screenrecorderv2.base.BaseRecyclerAdapter;
import com.example.screenrecorderv2.base.BaseViewHolder;
import com.example.screenrecorderv2.databinding.ItemPictureBinding;
import com.example.screenrecorderv2.utils.ViewUtils;

import java.util.List;

public class PictureAdapter extends BaseRecyclerAdapter<String> {
    public PictureAdapter(List<String> list, Context context) {
        super(list, context);
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemPictureBinding.inflate(LayoutInflater.from(context)));
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            Glide.with(context).load(list.get(position)).apply(new RequestOptions().override(200, 200)).into(((ViewHolder) holder).binding.imvPicture);
            ((ViewHolder) holder).binding.getRoot().setOnClickListener(v->callBackAdapter.onClickItem(list.get(position)));
        }
    }

    public class ViewHolder extends BaseViewHolder<ItemPictureBinding> {

        public ViewHolder(ItemPictureBinding binding) {
            super(binding);
        }
    }
}
