/*
 * Copyright (C) 2019 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.huaxin.library.adapter;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.huaxin.library.R;
import com.huaxin.library.entity.BannerItem;
import com.huaxin.library.utils.AppUtils;
import com.huaxin.library.utils.ImageUtils;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;
import com.xuexiang.xui.adapter.recyclerview.BaseRecyclerAdapter;
import com.xuexiang.xui.adapter.recyclerview.RecyclerViewHolder;
import com.xuexiang.xui.widget.banner.recycler.BannerLayout;

import java.util.List;

import static com.blankj.utilcode.util.ActivityUtils.startActivity;

public class BannerAdapter extends BaseRecyclerAdapter<BannerItem> {


    private BannerLayout.OnBannerItemClickListener mOnBannerItemClickListener;

    public BannerAdapter(List<BannerItem> data) {
        super(data);
    }


    @Override
    protected void bindData(@NonNull RecyclerViewHolder holder, int position, BannerItem item) {
        ImageView imageView = holder.findViewById(R.id.iv_item);
        Glide.with(holder.getContext()).load(item.getMedia()).placeholder(R.mipmap.find_item_holder).into(imageView);
        imageView.setOnClickListener(v -> {
//                if (mOnBannerItemClickListener != null) {
//                    mOnBannerItemClickListener.onItemClick(position);
//                }
            if (AppUtils.isUrl(item.getUrl())) {
                Uri uri = Uri.parse(item.getUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            } else {
                ToastUtils.showShort("网页链接不正确");
            }
        });
    }


    /**
     * 适配的布局
     *
     * @param viewType
     * @return
     */
    @Override
    public int getItemLayoutId(int viewType) {
        return R.layout.adapter_banner;
    }


    public BannerAdapter setOnBannerItemClickListener(BannerLayout.OnBannerItemClickListener onBannerItemClickListener) {
        mOnBannerItemClickListener = onBannerItemClickListener;
        return this;
    }
}
