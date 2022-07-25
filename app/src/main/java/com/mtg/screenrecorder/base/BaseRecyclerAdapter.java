package com.mtg.screenrecorder.base;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {
    protected List<T> list;
    protected Context context;
    protected CallBackAdapter<T> callBackAdapter;

    public BaseRecyclerAdapter(List<T> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    public abstract BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType);

    @Override
    public abstract void onBindViewHolder(@NonNull BaseViewHolder holder, int position);

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public List<T> getList() {
        return list;
    }

    public void addDatas(List<T> datas) {
        list.clear();
        list.addAll(datas);
        notifyDataSetChanged();
    }

    public void addData(T item) {
        list.add(item);
        notifyItemChanged(getItemCount() - 1);
    }

    public void onClickItem(T item) {
        if (callBackAdapter != null) {
            callBackAdapter.onClickItem(item);
        }
    }

    public void setCallBackAdapter(CallBackAdapter<T> callBackAdapter) {
        this.callBackAdapter = callBackAdapter;
    }

    public interface CallBackAdapter<T> {
        void onClickItem(T item);
    }
}
