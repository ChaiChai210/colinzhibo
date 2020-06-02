package com.huaxin.usercenter.adapter;

import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huaxin.library.utils.ImageUtils;
import com.huaxin.usercenter.R;
import com.huaxin.usercenter.entity.PayBean;

import java.util.List;

/**
 * 支付方式
 */
public class PayTypeAdapter extends BaseQuickAdapter<PayBean.PayListBean, BaseViewHolder> {
    private int mClickPos = 0;

    public PayTypeAdapter(@Nullable List<PayBean.PayListBean> data) {
        super(R.layout.item_pay_type, data);
    }

    public void setClickPos(int mClickPos) {
        this.mClickPos = mClickPos;
        notifyDataSetChanged();
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, @Nullable PayBean.PayListBean item) {
        holder.setText(R.id.tv_pay_name, item.getTitle());
        ImageUtils.displayImage(getContext(), item.getIcon(), R.drawable.ic_placeholder, holder.getView(R.id.iv_logo));
        ImageView imageView = holder.getView(R.id.iv_checked);
        if(holder.getLayoutPosition() == mClickPos){
            imageView.setImageResource(R.drawable.ic_cb_checked);
        }else {
            imageView.setImageResource(R.drawable.ic_cb_unchecked);
        }

//        checkBox.setOnClickListener(v -> {
//                    mClickPos = holder.getLayoutPosition();
//                    notifyDataSetChanged();
//                }
//        );
    }
}
