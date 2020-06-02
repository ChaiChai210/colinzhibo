package com.huaxin.library.adapter;

import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.huaxin.library.R;
import com.huaxin.library.entity.HotsSarchTag;

import java.util.List;

public class HotSearchAdapter extends BaseQuickAdapter<HotsSarchTag, BaseViewHolder> {
    public HotSearchAdapter(@Nullable List<HotsSarchTag> data) {
        super(R.layout.item_search, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, HotsSarchTag item) {
        TextView hotSearchItem = helper.getView(R.id.tv_tag);
        hotSearchItem.setText(item.getName());
        ViewGroup.LayoutParams lp = hotSearchItem.getLayoutParams();
        if (lp instanceof FlexboxLayoutManager.LayoutParams) {
            FlexboxLayoutManager.LayoutParams flexboxLp =
                    (FlexboxLayoutManager.LayoutParams) hotSearchItem.getLayoutParams();
            flexboxLp.setFlexGrow(1.0f);
        }
    }
}
