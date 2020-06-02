package com.huaxin.video.popup;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.huaxin.library.adapter.CommunityCommentAdapter;
import com.huaxin.library.custom.CustomEditTextBottomPopup;
import com.huaxin.library.custom.DYLoadingView;
import com.huaxin.library.entity.ChildComment;
import com.huaxin.library.entity.ComParentComment;
import com.huaxin.library.entity.CommunitySubCommentRsp;
import com.huaxin.library.entity.LikeRsp;
import com.huaxin.library.entity.UserInfo;
import com.huaxin.library.http.BaseEntity;
import com.huaxin.library.http.HttpUtils;
import com.huaxin.library.http.JsonCallback;
import com.huaxin.library.utils.ARConstants;
import com.huaxin.library.utils.AntiShakeUtils;
import com.huaxin.library.utils.AppUtils;
import com.huaxin.library.utils.UrlConstants;
import com.huaxin.video.R;
import com.huaxin.library.entity.CommunityCommentRsp;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BottomPopupView;
import com.lxj.xpopup.util.XPopupUtils;
import com.lxj.xpopup.widget.VerticalRecyclerView;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Description: 仿知乎底部评论弹窗
 * Create by dance, at 2018/12/25
 */

public class CommentPopup extends BottomPopupView {
    private Context mContext;
    private SmartRefreshLayout srlContainer;
    VerticalRecyclerView rvComment;
    //底部评论
    private TextView tvComment;
    //评论列表
    private static int PAGE_SIZE = 10;
    private int pageNum = 1;
    private boolean parentHasMore = true;

    private int mVideoId;
    private TextView tv_comment_count;
    private ImageView delete;
    private int commentCount;
    private CustomEditTextBottomPopup mEditPop;
    DYLoadingView loadingView;

    private CommunityCommentAdapter communityCommentAdapter;
    private List<ComParentComment> mCommentList = new ArrayList<>();

    public CommentPopup(@NonNull Context context, int id, int comment_count) {
        super(context);
        mContext = context;
        mVideoId = id;
        commentCount = comment_count;
    }


    @Override
    protected int getImplLayoutId() {
        return R.layout.video_tiktok_comment_popup;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        tv_comment_count = findViewById(R.id.tv_comment_count);
        tv_comment_count.setText(String.format("全部%d条评论", commentCount));
        delete = findViewById(R.id.iv_delete1);
        delete.setOnClickListener(v -> dismiss());
        loadingView = findViewById(R.id.loading_view);

        rvComment = findViewById(R.id.recyclerView);
        srlContainer = findViewById(R.id.srl_common);
        rvComment.setLayoutManager(new LinearLayoutManager(mContext));
        rvComment.setNestedScrollingEnabled(true);
        rvComment.setHasFixedSize(true);
        communityCommentAdapter = new CommunityCommentAdapter(mContext, mCommentList);
        communityCommentAdapter.setLikeListener((view, groupPosition, childPosition) -> sendLikeRequest(groupPosition, childPosition));
        communityCommentAdapter.setOnHeaderClickListener((adapter, holder, groupPosition) -> {
            showCommentPopup(groupPosition, -1);
        });
        communityCommentAdapter.setOnChildClickListener((adapter, holder, groupPosition, childPosition) -> {
            //响应最后一个点击事件
            if (childPosition > 2 && childPosition == mCommentList.get(groupPosition).getSubList().getSubData().size() - 1
            ) {
                if (mCommentList.get(groupPosition).getState() == 2) {
                    //收回响应
                    mCommentList.get(groupPosition).setState(0);
                    List<ChildComment> subCommentItems = filterList(mCommentList.get(groupPosition).getSubList().getSubData());
                    mCommentList.get(groupPosition).getSubList().setSubData(subCommentItems);
                    communityCommentAdapter.notifyDataChanged();
                } else {
                    getSubComment(mCommentList.get(groupPosition).getId(), childPosition, groupPosition, childPosition);
                }

            } else {
//
                showCommentPopup(groupPosition, childPosition);
            }

        });

        rvComment.setAdapter(communityCommentAdapter);
        srlContainer.setEnableRefresh(false);
        srlContainer.setOnLoadMoreListener(refreshLayout -> {
            if (parentHasMore) {
                refreshLayout.finishLoadMore();
                getComment(mCommentList.size());
            } else {
                refreshLayout.finishLoadMoreWithNoMoreData();
                ToastUtils.showShort("没有更多数据");
            }
        });

        tvComment = findViewById(R.id.tv_comment_content);
        tvComment.setOnClickListener(v -> {
            showCommentPopup(tvComment.getText());
        });


        initData();
    }

    private void sendLikeRequest(int groupPosition, int childPosition) {
        HashMap params = new HashMap<>();
        int id;
        ComParentComment parentComment = mCommentList.get(groupPosition);
        ChildComment childComment = null;
        if (childPosition == -1) {
            id = parentComment.getId();
        } else {
            id = parentComment.getSubList().getSubData().get(childPosition).getId();
            childComment = parentComment.getSubList().getSubData().get(childPosition);
        }
        params.put("id", id);
        params.put("uid", AppUtils.getUid());
        ChildComment finalChildComment = childComment;
        HttpUtils.postData(UrlConstants.VideoLike, params, new JsonCallback<BaseEntity<LikeRsp>>() {
            @Override
            public void onSuccess(BaseEntity<LikeRsp> data) {
                hideLoading();
                if (childPosition == -1) {
                    parentComment.setLike_nums(data.getData().getLikeNums());
                    parentComment.setIsLike(parentComment.getIsLike() == 0 ? 1 : 0);
                    communityCommentAdapter.notifyGroupChanged(groupPosition);
                } else {
                    finalChildComment.setLike_nums(data.getData().getLikeNums());
                    finalChildComment.setIsLike(finalChildComment.getIsLike() == 0 ? 1 : 0);
                    communityCommentAdapter.notifyChildChanged(groupPosition, childPosition);
                }
            }

            @Override
            public void onStart(Request<BaseEntity<LikeRsp>, ? extends Request> request) {
                super.onStart(request);
                showLoading();
            }
        });
    }

    private void showCommentPopup(int groupPosition, int childPosition) {
        String hint;
        if (childPosition == -1) {
            hint = mCommentList.get(groupPosition).getUserInfo().getNickname();
        } else {
            hint = mCommentList.get(groupPosition).getSubList().getSubData().get(childPosition)
                    .getUserInfo().getNickname();
        }
        mEditPop = new CustomEditTextBottomPopup(mContext, tvComment.getText(), hint);
        //点击发送按钮
//                输入文字回传
        mEditPop.setCallback(context -> {
            if (tvComment != null) {
                tvComment.setText(context);
            }
        });
        mEditPop.setOnClickListener(v1 ->
                postReply(groupPosition, childPosition, mEditPop.getComment()));

        new XPopup.Builder(mContext)
                .autoOpenSoftInput(true)
//                        .hasShadowBg(false)
                .asCustom(mEditPop)
                .show();
    }

    private void postReply(int groupPosition, int childPosition, String comment) {
        int pid;
        int puid;
        if (childPosition == -1) {
            ComParentComment parentComment = mCommentList.get(groupPosition);
            pid = parentComment.getId();
            puid = parentComment.getUid();
        } else {
            ChildComment item = mCommentList.get(groupPosition).getSubList().getSubData().get(childPosition);
            pid = item.getPid();
            puid = item.getUid();
        }


        //pid为一级评论，puid0
        JsonCallback<BaseEntity> callback = new JsonCallback<BaseEntity>() {
            @Override
            public void onSuccess(BaseEntity data) {
                hideLoading();
//                ivSendMsg.setEnabled(true);
                commentCount++;
                tv_comment_count.setText(String.format("全部%d条评论", commentCount));
                ChildComment childComment = new ChildComment();
                childComment.setText(comment);
                childComment.setComment_date((int) (System.currentTimeMillis() / 1000));
                childComment.setPid(mCommentList.get(groupPosition).getId());
                childComment.setPuid(mCommentList.get(groupPosition).getUid());
                UserInfo userInfo = new UserInfo();
                userInfo.setNickname(AppUtils.getUserInfo().getUser().getNickname());
                userInfo.setThumb(AppUtils.getUserInfo().getUser().getThumb());
                childComment.setUserInfo(userInfo);
                if (childPosition == -1) {
                    if (mCommentList.get(groupPosition).getSubList() == null ||CollectionUtils.isEmpty(mCommentList.get(groupPosition).getSubList().getSubData() )) {
                        List<ChildComment> childComments = new ArrayList<>();
                        childComments.add(childComment);
                        mCommentList.get(groupPosition).setSubList(new ComParentComment.SubListBean(0,childComments));
//                        mCommentList.get(groupPosition).getSubList().setSubData(childComments);
                        communityCommentAdapter.notifyChildInserted(groupPosition, 0);
                    } else {
                        mCommentList.get(groupPosition).getSubList().getSubData().add( childComment);
                        communityCommentAdapter.notifyChildInserted(groupPosition, 0);
                    }
                } else {
                    mCommentList.get(groupPosition).getSubList().getSubData().add(childPosition, childComment);
                    communityCommentAdapter.notifyChildInserted(groupPosition, childPosition);
                }

                ToastUtils.showLong(data.getMessage());
                tvComment.setText("");
                mEditPop.dismiss();
            }

            @Override
            public void onError(Response<BaseEntity> response) {
                super.onError(response);
//                ivSendMsg.setEnabled(true);
                ToastUtils.showLong("发送出错，请重发");
            }

            @Override
            public void onStart(Request<BaseEntity, ? extends Request> request) {
                super.onStart(request);
            }
        };
        HttpUtils.postVideoCommend(AppUtils.getUid(), pid, puid, comment, mVideoId, callback);
    }

    private void getSubComment(int pid, int offset, int groupPosition, int childPosition) {
        HttpUtils.getVideoSubComment(mVideoId, offset, pid, new JsonCallback<BaseEntity<CommunitySubCommentRsp>>() {
            @Override
            protected void onSuccess(BaseEntity<CommunitySubCommentRsp> data) {
                int totalSub = mCommentList.get(groupPosition).getSubList().getCountNums();
                List<ChildComment> subData = mCommentList.get(groupPosition).getSubList().getSubData();
                int hasLoaded = subData.size() - 1;
                if (totalSub == hasLoaded + data.getData().getData().size()) {
                    mCommentList.get(groupPosition).setState(2);
                } else {
                    mCommentList.get(groupPosition).setState(1);
                }

                subData.addAll(childPosition, data.getData().getData());
                //子评论总数
                communityCommentAdapter.notifyChildRangeChanged(groupPosition, childPosition, data.getData().getData().size());
            }
        });
    }

    private List<ChildComment> filterList(List<ChildComment> subCommentItems) {
        List<ChildComment> temp = new ArrayList<>();
        temp.add(subCommentItems.get(0));
        temp.add(subCommentItems.get(1));
        temp.add(subCommentItems.get(2));
        temp.add(new ChildComment());
        return temp;
    }

    private void doPostComment() {
        if (TextUtils.isEmpty(mEditPop.getComment())) {
            ToastUtils.showShort("请输入评论");
        } else {
            if (AppUtils.isLogin()) {
                postComment(mEditPop.getComment());
            } else {
                ARouter.getInstance().build(ARConstants.PATH_LOGIN)
                        .navigation();
            }
        }
    }


    private void initData() {
        getComment(0);
    }

    private void getComment(int offset) {
        //默认是like_nums表示按照热度排序， comment_date表示按照时间的升序排序
        HttpUtils.getVideoComment(AppUtils.getUid(), mVideoId, offset, "comment_date", new JsonCallback<BaseEntity<CommunityCommentRsp>>() {
            @Override
            protected void onSuccess(BaseEntity<CommunityCommentRsp> data) {
                if (data.getData().getCountNums() < PAGE_SIZE) {
                    parentHasMore = false;
                }
                setCommentData(data);
            }

        });
    }

    private void setCommentData(BaseEntity<CommunityCommentRsp> data) {
        List<ComParentComment> data1 = data.getData().getData();
        int size = data1.size();
        if (data1.isEmpty()) {
            //空数据
        } else {
            for (int i = 0; i < size; i++) {
                int subsize = data1.get(i).getSubList().getCountNums();
                if (subsize > 3) {
                    data1.get(i).getSubList().getSubData().add(new ChildComment());
                }
            }
//            mCommentList.clear();

            mCommentList.addAll(data1);
            communityCommentAdapter.notifyDataSetChanged();
        }

    }

    private void showCommentPopup(CharSequence text) {
        mEditPop = new CustomEditTextBottomPopup(mContext, text, "");
        //点击发送按钮
//                输入文字回传
        mEditPop.setCallback(context -> {
            if (tvComment != null) {
                tvComment.setText(context);
            }
        });
        mEditPop.setOnClickListener(v1 ->
        {
            if (AntiShakeUtils.isInvalidClick(v1))
                return;
            doPostComment();
        });

        new XPopup.Builder(mContext)
                .autoOpenSoftInput(true)
//                        .hasShadowBg(false)
                .asCustom(mEditPop)
                .show();
    }



    private void postComment(String comment) {
        //pid为0一级评论，puid0
        JsonCallback<BaseEntity> callback = new JsonCallback<BaseEntity>() {
            @Override
            public void onSuccess(BaseEntity data) {
//                ivSendMsg.setEnabled(true);
                commentCount++;
                tv_comment_count.setText(String.format("全部%d条评论", commentCount));
                ComParentComment parentComment = new ComParentComment();
                parentComment.setText(comment);
                parentComment.setComment_date((int) (System.currentTimeMillis() / 1000));
                UserInfo userInfo = new UserInfo();
                userInfo.setNickname(AppUtils.getUserInfo().getUser().getNickname());
                userInfo.setThumb(AppUtils.getUserInfo().getUser().getThumb());
                parentComment.setUserInfo(userInfo);
                mCommentList.add(0, parentComment);
                communityCommentAdapter.notifyGroupInserted(0);
                ToastUtils.showLong(data.getMessage());
                tvComment.setText("");
                mEditPop.dismiss();
            }

            @Override
            public void onError(Response<BaseEntity> response) {
                super.onError(response);
//                ivSendMsg.setEnabled(true);
                ToastUtils.showLong("发送失败，请重发");
            }

            @Override
            public void onStart(Request<BaseEntity, ? extends Request> request) {
                super.onStart(request);

            }
        };
        HttpUtils.postVideoCommend(AppUtils.getUid(), 0, 0, comment, mVideoId, callback);
    }


    private void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
        loadingView.start();
    }

    private void hideLoading() {
        loadingView.stop();
        loadingView.setVisibility(View.GONE);
    }

    //完全可见执行
    @Override
    protected void onShow() {
        super.onShow();
    }

    //完全消失执行
    @Override
    protected void onDismiss() {

    }

    @Override
    protected int getMaxHeight() {
//        return 400;
        return (int) (XPopupUtils.getWindowHeight(getContext()) * .85f);
    }
}