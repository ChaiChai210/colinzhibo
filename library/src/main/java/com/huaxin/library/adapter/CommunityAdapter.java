package com.huaxin.library.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huaxin.library.R;
import com.huaxin.library.entity.CommunityRecommend;
import com.huaxin.library.http.BaseEntity;
import com.huaxin.library.http.HttpUtils;
import com.huaxin.library.http.JsonCallback;
import com.huaxin.library.utils.ARConstants;
import com.huaxin.library.utils.AntiShakeUtils;
import com.huaxin.library.utils.AppUtils;
import com.huaxin.library.utils.IconStausUtils;
import com.huaxin.library.utils.ImageUtils;
import com.huaxin.library.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;


public class CommunityAdapter extends BaseQuickAdapter<CommunityRecommend.DataBean, BaseViewHolder> {

    private CollectListener collectListener;

    public void setCollectListener(CollectListener collectListener) {
        this.collectListener = collectListener;
    }


    public interface CollectListener {
        void onclick(int id, int type);
    }

    public CommunityAdapter(@NonNull List<CommunityRecommend.DataBean> data) {
        super(R.layout.item_community_recommend, data);
        addChildClickViewIds(R.id.con_video);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, @Nullable CommunityRecommend.DataBean data) {
//        helper.setText(R.id.iv_play_time, data.getVideoViewCount() + "次播放" + (TextUtils.isEmpty(data.getVideoDuration()) ? "" : "  "+data.getVideoDuration()));

        TextView mContentTv = helper.getView(R.id.tv_content);
        TextView duration = helper.getView(R.id.tv_duration);
        FrameLayout frameLayout = helper.getView(R.id.con_video);
        RecyclerView rv_gallery = helper.getView(R.id.rv_gallery);

//        ImageViewer imageViewer = helper.getView(R.id.imageViewer);
        helper.setText(R.id.tv_label, data.getCateName());
        helper.setText(R.id.tv_title, data.getTitle());

        String type = data.getType();
        //文字
        if (type.equals("1")) {
            mContentTv.setVisibility(View.VISIBLE);
            mContentTv.setText(data.getContent());
        } else {
            mContentTv.setVisibility(View.GONE);
        }

        if (type.equals("2")) {
            if (data.getContent() == null) {
                return;
            }

            String[] images = data.getContent().split(",");

            ArrayList<Object> imageList = new ArrayList<>();
            int size = images.length;
            for (int i = 0; i < size; i++) {
                imageList.add(images[i]);
            }
            if (imageList.isEmpty()) {
                rv_gallery.setVisibility(View.GONE);
            } else {
                rv_gallery.setVisibility(View.VISIBLE);
                rv_gallery.setHasFixedSize(true);


                GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
                rv_gallery.setLayoutManager(layoutManager);
                CommunityImageAdapter nestedAdapter = new CommunityImageAdapter(imageList);
                rv_gallery.setAdapter(nestedAdapter);
            }

        } else {
            rv_gallery.setVisibility(View.GONE);
        }
        if (type.equals("3")) {
            frameLayout.setVisibility(View.VISIBLE);
            ImageUtils.displayImage(getContext(), data.getCover(), R.drawable.ic_placeholder, helper.getView(R.id.iv_video));
            duration.setText(TimeUtils.getDuration(data.getDuration()));
        } else {
            frameLayout.setVisibility(View.GONE);
        }
        //添加个人信息
        helper.setText(R.id.tv_nickname, data.getNickname());
        ImageUtils.displayAvatar(getContext(), data.getThumb(), helper.getView(R.id.iv_avatar));
        TextView addressTv = helper.getView(R.id.tv_location);
        addressTv.setText(data.getCity());
        addressTv.setVisibility(TextUtils.isEmpty(data.getCity()) ? View.GONE : View.VISIBLE);


        ImageView share = helper.getView(R.id.iv_share);
        share.setOnClickListener(v ->
                {
                    if(collectListener != null){
                        collectListener.onclick(data.getId(), 1);
                    }
                });

        TextView comment = helper.getView(R.id.tv_comment);
        comment.setText(String.format("%d", data.getComment_count()));
//        comment.setOnClickListener(v -> );
        TextView like = helper.getView(R.id.tv_like);
        IconStausUtils.doLike(like, data.isIs_like());
        like.setOnClickListener(v -> {
//            if (collectListener != null) {
//                collectListener.onclick(helper.getLayoutPosition(), 2);
//            }
            if (data.isIs_like()) {
                if (AntiShakeUtils.isInvalidClick(like))
                    return;
                HttpUtils.postDisLike(data.getId(), AppUtils.getDeviceId(getContext()), 2, 2, new JsonCallback<BaseEntity>() {
                    @Override
                    protected void onSuccess(BaseEntity bean) {
                        data.setIs_like(false);
                        data.setLikes_count(data.getLikes_count() - 1);
                        notifyItemChanged(helper.getLayoutPosition());
                    }
                });
            } else {
                if (AntiShakeUtils.isInvalidClick(like))
                    return;
                HttpUtils.postLike(data.getId(), AppUtils.getDeviceId(getContext()), 2, 2, new JsonCallback<BaseEntity>() {
                    @Override
                    protected void onSuccess(BaseEntity bean) {
                        data.setIs_like(true);
                        data.setLikes_count(data.getLikes_count() + 1);
                        notifyItemChanged(helper.getLayoutPosition());
                    }
                });
            }
        });

        helper.setText(R.id.tv_like, data.getLikes_count() + "");

        helper.itemView.setOnClickListener(v ->
                ARouter.getInstance().build(ARConstants.PATH_COMMUNTIY_DETAIL).withInt("id", data.getId()).withInt("pos", helper.getLayoutPosition()).navigation());
    }
}
