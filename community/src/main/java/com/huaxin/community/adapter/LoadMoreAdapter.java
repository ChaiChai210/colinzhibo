package com.huaxin.community.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.drakeet.multitype.ItemViewBinder;
import com.huaxin.community.R;

import org.jetbrains.annotations.NotNull;

public class LoadMoreAdapter extends ItemViewBinder<String, LoadMoreAdapter.ViewHolder> {

    public interface onItemClick {

        void onItemClick(int position);
    }

    private onItemClick onItemClick;

    public void setOnItemClick(LoadMoreAdapter.onItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull LayoutInflater layoutInflater, @NotNull ViewGroup parent) {
        return new LoadMoreAdapter.ViewHolder(layoutInflater.inflate(R.layout.item_comment_footer, parent, false));
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder viewHolder, String s) {
        viewHolder.text.setText(s);
        viewHolder.itemView.setOnClickListener(v -> {
            if (onItemClick != null) {
                onItemClick.onItemClick(viewHolder.getAdapterPosition());
            }
        });

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_arrow;
        TextView text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.tv_load_more);
            iv_arrow = itemView.findViewById(R.id.iv_arrow);
        }
    }
}
