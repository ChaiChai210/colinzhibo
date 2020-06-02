package com.huaxin.library.adapter;

import android.content.Context;

import com.donkingliang.groupedadapter.adapter.GroupedRecyclerViewAdapter;

import java.util.List;

public abstract class BaseGroupedRecyclerViewAdapter<T> extends GroupedRecyclerViewAdapter {
    public List<T> mData;

    public BaseGroupedRecyclerViewAdapter(Context context) {
        super(context);
    }

    public BaseGroupedRecyclerViewAdapter(Context context, boolean useBinding) {
        super(context, useBinding);
    }

    public void replaceData(List<T> data) {
        if (data != this.mData) {
            this.mData.clear();
            this.mData.addAll(data);
        }
        notifyDataSetChanged();
    }

    public void addData(List<T> data) {
        if (data != this.mData) {
            this.mData.clear();
            this.mData.addAll(data);
        }
        this.mData.addAll(data);
        notifyItemRangeInserted(this.mData.size() - data.size(), data.size());
        notifyDataSetChanged();
    }
}
