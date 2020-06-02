package com.huaxin.usercenter.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.huaxin.usercenter.R;
import com.huaxin.usercenter.entity.PayBean;
import com.xuexiang.xui.utils.DensityUtils;

import java.util.List;

public class ChargerAdapter extends ArrayAdapter<PayBean.PayListBean.ChargeRulesBean> {

    private Context mContext;
    private int layoutResourceId;
    private List<PayBean.PayListBean.ChargeRulesBean> mGridData;
    private int mClickPos;

    public ChargerAdapter(@NonNull Context context, int resource, @NonNull List<PayBean.PayListBean.ChargeRulesBean> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.layoutResourceId = resource;
        this.mGridData = objects;
    }

    public void setGridData(List<PayBean.PayListBean.ChargeRulesBean> mGridData) {
        this.mGridData = mGridData;
        notifyDataSetChanged();
    }

    public void setPosition(int position) {
        mClickPos = position;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RechargeViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
            holder = new RechargeViewHolder();
            holder.container = convertView.findViewById(R.id.rl_comtainer);
            holder.hot = convertView.findViewById(R.id.iv_hot);
            holder.extra = convertView.findViewById(R.id.tv_extra);
            holder.diamond = convertView.findViewById(R.id.tv_diamond);
            holder.recharge = convertView.findViewById(R.id.tv_recharge);
            holder.checkIcon = convertView.findViewById(R.id.iv_checked);
            convertView.setTag(holder);
        } else {
            holder = (RechargeViewHolder) convertView.getTag();
        }
        PayBean.PayListBean.ChargeRulesBean item = mGridData.get(position);
        if (item.getTag() == 1) {
            holder.hot.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.extra.getLayoutParams();
            layoutParams.setMarginStart(DensityUtils.dp2px(getContext(), 16));
        } else if (item.getTag() == 2) {
            holder.hot.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.extra.getLayoutParams();
            layoutParams.setMarginStart(DensityUtils.dp2px(getContext(), 16));
            holder.hot.setImageResource(R.drawable.user_wallet_discount);
        } else {
            holder.hot.setVisibility(View.GONE);
        }
        holder.diamond.setText(String.format("%d", item.getCoin()));
        if (item.getGive() > 0) {
            holder.extra.setText(String.format("送%d", item.getGive()));
        } else {
            holder.extra.setVisibility(View.GONE);
        }
        holder.recharge.setText(String.format("￥%s", item.getMoney()));

        if (mClickPos == position) {
            holder.container.setBackgroundResource(R.drawable.user_wallet_bg_checked);
            holder.checkIcon.setVisibility(View.VISIBLE);
            holder.checkIcon.setImageResource(R.drawable.ic_cb_checked);
        } else {
            holder.container.setBackgroundResource(R.drawable.user_wallet_bg_unchecked);
            holder.checkIcon.setVisibility(View.GONE);
        }

        return convertView;
    }


}
