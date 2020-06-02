package com.huaxin.community.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huaxin.community.R;
import com.huaxin.library.entity.LabelClassify;
import com.kproduce.roundcorners.RoundTextView;

import java.util.List;


public class CircleLabelAdapter extends BaseQuickAdapter<LabelClassify, BaseViewHolder> {
    private int clickPos;

    private OnLabelClick onLabelClick;
    public interface OnLabelClick {
        void onLabelClick(int pos);
    }

    public void setOnLabelClick(OnLabelClick onLabelClick) {
        this.onLabelClick = onLabelClick;
    }

    public CircleLabelAdapter(@Nullable List<LabelClassify> data) {
        super(R.layout.item_circle_label, data);
    }

    public void setClickPos(int pos) {
        this.clickPos = pos;
    }

    @Override
    protected void convert(BaseViewHolder helper, LabelClassify item) {
        RoundTextView label = helper.getView(R.id.cb_label);
        label.setText(item.getName());
        if (clickPos == helper.getLayoutPosition()) {
            label.setTextColor(getContext().getResources().getColor(R.color.white));
            label.setBackgroundColor(getContext().getResources().getColor(R.color.text_color));
        } else {
            label.setTextColor(getContext().getResources().getColor(R.color.text_color));
            label.setBackgroundColor(getContext().getResources().getColor(R.color.colorPrimary));
        }
        label.setOnClickListener(v ->
        {
            if (helper.getAdapterPosition() != clickPos) {
                clickPos = helper.getAdapterPosition();
                notifyDataSetChanged();
                onLabelClick.onLabelClick(clickPos);
            }
        });
    }
}
