package com.example.screenrecorderv2.ui.setting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import com.bumptech.glide.Glide;
import com.example.screenrecorderv2.R;
import com.example.screenrecorderv2.base.BaseRecyclerAdapter;
import com.example.screenrecorderv2.base.BaseViewHolder;
import com.example.screenrecorderv2.databinding.ItemAppBinding;

import java.util.List;

public class ListAppAdapter extends BaseRecyclerAdapter<Apps> {

    public ListAppAdapter(List<Apps> list, Context context) {
        super(list, context);
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemAppBinding.inflate(LayoutInflater.from(context)));
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        Apps app = list.get(position);
        if (holder instanceof ViewHolder) {
            ((ViewHolder) holder).binding.getRoot().setOnClickListener(v -> {
                callBackAdapter.onClickItem(list.get(position));
            });
            ((ViewHolder) holder).binding.tvName.setText(app.getAppName());
            Glide.with(context).load(app.getAppIcon()).into(((ViewHolder) holder).binding.imvIcon);
            if (app.isSelectedApp())
                ((ViewHolder) holder).binding.imvCheck.setVisibility(View.VISIBLE);
            else
                ((ViewHolder) holder).binding.imvCheck.setVisibility(View.INVISIBLE);

        }
    }

    class ViewHolder extends BaseViewHolder<ItemAppBinding> {

        public ViewHolder(ItemAppBinding binding) {
            super(binding);
        }
    }
}
