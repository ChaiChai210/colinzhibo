package com.huaxin.library.adapter;

import android.animation.Animator;
import android.content.Context;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.donkingliang.groupedadapter.holder.BaseViewHolder;
import com.huaxin.library.R;
import com.huaxin.library.entity.ChildComment;
import com.huaxin.library.entity.ComParentComment;
import com.huaxin.library.entity.UserInfo;
import com.huaxin.library.utils.ARConstants;
import com.huaxin.library.utils.AntiShakeUtils;
import com.huaxin.library.utils.AppUtils;
import com.huaxin.library.utils.IconStausUtils;
import com.huaxin.library.utils.ImageUtils;
import com.huaxin.library.utils.TimeUtils;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;

import java.util.List;

/**
 * 视频推荐列表适配器
 */
public class CommunityCommentAdapter extends BaseGroupedRecyclerViewAdapter<ComParentComment> {

    private static final int TYPE_CHILD_1 = 1;
    private static final int TYPE_CHILD_2 = 2;

    public interface LikeListener {
        void onClick(View view, int groupPosition, int childPosition);
    }

    private LikeListener likeListener;

    public void setLikeListener(LikeListener likeListener) {
        this.likeListener = likeListener;
    }

    public CommunityCommentAdapter(Context context, List<ComParentComment> data) {
        super(context);
        mData = data;
    }

    @Override
    public int getGroupCount() {
        return mData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (mData.get(groupPosition).getSubList() != null) {
            return mData.get(groupPosition).getSubList().getSubData().size();
        } else {
            return 0;
        }

    }

    @Override
    public boolean hasHeader(int groupPosition) {
        return true;
    }

    @Override
    public boolean hasFooter(int groupPosition) {
        return false;
    }

    @Override
    public int getHeaderLayout(int viewType) {
        return R.layout.com_item_parent_comment;
    }

    @Override
    public int getFooterLayout(int viewType) {
        return R.layout.item_comment_footer;
    }

    @Override
    public int getChildLayout(int viewType) {
        if (viewType == TYPE_CHILD_1) {
            return R.layout.com_item_child_comment;
        } else {
            return R.layout.item_comment_footer;
        }
    }

    @Override
    public void onBindHeaderViewHolder(BaseViewHolder holder, int groupPosition) {
        ComParentComment item = mData.get(groupPosition);
        if (item != null) {
            UserInfo userInfo = item.getUserInfo();
            ImageUtils.displayImage(mContext, userInfo.getThumb(), R.mipmap.find_item_holder, holder.get(R.id.iv_avatar));
            holder.setText(R.id.tv_nickname, userInfo.getNickname());
            holder.setText(R.id.tv_comment_detail, String.format("%s%s", item.getText(), TimeUtils.getTimeFormatText(item.getComment_date())));
            IconStausUtils.setLike(holder.get(R.id.tv_like), item.getIsLike() == 0);
            if (item.getLike_nums() > 0) {
                holder.setText(R.id.tv_like, item.getLike_nums() + "");
            }
            holder.get(R.id.tv_like).setOnClickListener(v -> {
//                if (AntiShakeUtils.isInvalidClick(v))
//                    return;
                if(AppUtils.isLogin()){
                    doLike(groupPosition, -1, v);
                }else {
                    ARouter.getInstance().build(ARConstants.PATH_LOGIN)
                            .navigation();
                }
            });
        }
    }

    private void doLike(int groupPosition, int childPosition, View v) {
        v.animate().scaleX(1.2f).scaleY(1.2f).setDuration(500).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                v.setScaleY(1.0f);
                v.setScaleX(1.0f);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
        if (likeListener != null) {
            likeListener.onClick(v, groupPosition, childPosition);
        }
    }


    @Override
    public void onBindFooterViewHolder(BaseViewHolder holder, int groupPosition) {
    }

    @Override
    public void onBindChildViewHolder(BaseViewHolder holder, int groupPosition, int childPosition) {
        int viewType = getChildViewType(groupPosition, childPosition);
        if (viewType == TYPE_CHILD_1) {
            if (!CollectionUtils.isEmpty(mData.get(groupPosition).getSubList().getSubData())) {
                ChildComment childData = mData.get(groupPosition).getSubList().getSubData().get(childPosition);
                UserInfo userInfo = childData.getUserInfo();
                if (userInfo != null) {
                    holder.setText(R.id.tv_nickname, userInfo.getNickname());
                    holder.setText(R.id.tv_comment_detail, String.format("%s%s", childData.getText(), TimeUtils.getTimeFormatText(childData.getComment_date())));
                    ImageUtils.displayImage(mContext, userInfo.getThumb(), R.mipmap.find_item_holder, holder.get(R.id.iv_avatar));
                }

                IconStausUtils.setLike(holder.get(R.id.tv_like), childData.getIsLike() == 0);
                if (childData.getLike_nums() > 0) {
                    holder.setText(R.id.tv_like, childData.getLike_nums() + "");
                }
                holder.get(R.id.tv_like).setOnClickListener(v -> {
                    if(AppUtils.isLogin()){
                        doLike(groupPosition, childPosition, v);
                    }else {
                        ARouter.getInstance().build(ARConstants.PATH_LOGIN)
                                .navigation();
                    }
                });
            }
        } else if (viewType == TYPE_CHILD_2) {
            int subSize = mData.get(groupPosition).getSubList().getCountNums() - 3;
            int state = mData.get(groupPosition).getState();
            if (state == 0) {
                String num = "展开" + subSize + "条回复";
                holder.setText(R.id.tv_load_more, num);
            } else if (state == 1) {
                holder.setText(R.id.tv_load_more, "展开更多回复");
            } else if (state == 2) {
                holder.setText(R.id.tv_load_more, "收起");
            }
        }

    }

    @Override
    public int getChildViewType(int groupPosition, int childPosition) {
        if (childPosition > 2 && childPosition == mData.get(groupPosition).getSubList().getSubData().size() - 1) {
            return TYPE_CHILD_2;
        } else {
            return TYPE_CHILD_1;
        }
    }

}
