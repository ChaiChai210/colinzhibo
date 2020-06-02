package com.huaxin.video.adapter;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ArrayUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.donkingliang.labels.LabelsView;
import com.example.librarybase.base.Event;
import com.example.librarybase.base.EventBusHelper;
import com.huaxin.library.utils.AntiShakeUtils;
import com.huaxin.library.utils.EventCode;
import com.huaxin.library.utils.ImageUtils;
import com.huaxin.video.R;
import com.huaxin.video.entity.TiktokEntity;
import com.huaxin.video.popup.CommentPopup;
import com.huaxin.video.views.DYLikeLayout;
import com.huaxin.video.views.likebutton.DYLikeView;
import com.huaxin.video.views.likebutton.OnLikeListener;
import com.lxj.xpopup.XPopup;

import java.util.List;


/**
 * 视频推荐分类
 */
public class TiktokAdapter extends BaseQuickAdapter<TiktokEntity, BaseViewHolder> {
    public TiktokAdapter(@Nullable List data) {
        super(R.layout.item_tiktok, data);
        addChildClickViewIds(R.id.tv_comment, R.id.tv_collection_count);
    }


    @Override
    protected void convert(@NonNull BaseViewHolder holder, @Nullable TiktokEntity item) {
        ImageUtils.displayImage(getContext(), item.getThumb(), R.drawable.ic_placeholder, holder.getView(R.id.iv_logo));
        holder.setText(R.id.tv_description, item.getIntro());
        holder.setText(R.id.tv_title, item.getTitle());
        if (!TextUtils.isEmpty(item.getTags())) {
            LabelsView labelsView = holder.getView(R.id.labels);
            labelsView.setLabels(ArrayUtils.asArrayList(item.getTags().split("\\|")));
        }

        holder.setText(R.id.tv_collection_count, item.getCollection_count() + "");
        holder.setText(R.id.tv_comment, item.getComment_count() + "");
        //点赞按钮
        DYLikeView dyLikeButton = holder.getView(R.id.dy_like_button);
        dyLikeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(DYLikeView likeView) {
                if (AntiShakeUtils.isInvalidClick(likeView))
                    return;
                EventBusHelper.post(new Event(EventCode.COLLECT_VIDEO, holder.getAdapterPosition()));
            }

            @Override
            public void unLiked(DYLikeView likeView) {
                if (AntiShakeUtils.isInvalidClick(likeView))
                    return;
                EventBusHelper.post(new Event(EventCode.DISCOLLECT_VIDEO, holder.getAdapterPosition()));
            }
        });
        if (item.isIs_collection()) {
            dyLikeButton.setLiked(true);
        } else {
            dyLikeButton.setLiked(false);
        }
        //屏幕点赞 效果
        DYLikeLayout dyLikeLayout = holder.getView(R.id.dy_like_layout);
        dyLikeLayout.setLikeClickCallBack(new DYLikeLayout.LikeClickCallBack() {
            @Override
            public void onLikeListener() {
                //多击监听
                if (!dyLikeButton.isLiked()) {
                    {
                        if (AntiShakeUtils.isInvalidClick(dyLikeButton))
                            return;
                        EventBusHelper.post(new Event(EventCode.COLLECT_VIDEO, holder.getAdapterPosition()));
                    }
                }
            }

            @Override
            public void onSingleListener() {
                //单击监听

            }
        });

        holder.getView(R.id.tv_comment).setOnClickListener(v -> {
            new XPopup.Builder(getContext())
                    .moveUpToKeyboard(false) //如果不加这个，评论弹窗会移动到软键盘上面
                    .asCustom(new CommentPopup(getContext(), item.getId(), item.getComment_count())/*.enableDrag(false)*/)
                    .show();
        });
    }
}
