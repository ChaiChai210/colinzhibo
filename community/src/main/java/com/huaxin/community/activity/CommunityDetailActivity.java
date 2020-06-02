package com.huaxin.community.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.example.librarybase.base.BaseActivity;
import com.example.librarybase.base.Event;
import com.example.librarybase.base.EventBusHelper;
import com.gyf.immersionbar.ImmersionBar;
import com.huaxin.community.R;
import com.huaxin.community.R2;
import com.huaxin.library.adapter.CommunityCommentAdapter;
import com.huaxin.library.adapter.CommunityImageAdapter;
import com.huaxin.library.custom.CustomEditTextBottomPopup;
import com.huaxin.library.custom.DYLoadingView;
import com.huaxin.library.custom.SuperPlayerView;
import com.huaxin.library.entity.ChildComment;
import com.huaxin.library.entity.ComParentComment;
import com.huaxin.library.entity.CommunityCommentRsp;
import com.huaxin.library.entity.CommunityRecommend;
import com.huaxin.library.entity.CommunitySubCommentRsp;
import com.huaxin.library.entity.LikeRsp;
import com.huaxin.library.entity.LoginEntity;
import com.huaxin.library.entity.UserInfo;
import com.huaxin.library.http.BaseEntity;
import com.huaxin.library.http.HttpUtils;
import com.huaxin.library.http.JsonCallback;
import com.huaxin.library.utils.ARConstants;
import com.huaxin.library.utils.AntiShakeUtils;
import com.huaxin.library.utils.AppUtils;
import com.huaxin.library.utils.EventCode;
import com.huaxin.library.utils.IconStausUtils;
import com.huaxin.library.utils.ImageUtils;
import com.huaxin.library.utils.UrlConstants;
import com.kproduce.roundcorners.RoundLinearLayout;
import com.lxj.xpopup.XPopup;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.tencent.liteav.demo.play.SuperPlayerConst;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Route(path = ARConstants.PATH_COMMUNTIY_DETAIL)
public class CommunityDetailActivity extends BaseActivity implements SuperPlayerView.OnSuperPlayerViewCallback, SuperPlayerView.OnPlayEvent {

    @BindView(R2.id.iv_avatar)
    ImageView ivAvatar;
    @BindView(R2.id.tv_nickname)
    TextView tvNickname;
    @BindView(R2.id.tv_location)
    TextView tvLocation;
    @BindView(R2.id.rl_person)
    RelativeLayout rlPerson;
    @BindView(R2.id.tv_community_title)
    TextView tvCommunityTitle;
    @BindView(R2.id.iv_video)
    ImageView ivVideo;
    @BindView(R2.id.iv_play)
    ImageView ivPlay;
    @BindView(R2.id.con_video)
    FrameLayout conVideo;
    @BindView(R2.id.rv_gallery)
    RecyclerView rvGallery;
    @BindView(R2.id.tv_content)
    TextView tvContent;
    @BindView(R2.id.fl_video)
    FrameLayout frameLayout;
    @BindView(R2.id.tv_label)
    TextView tvLabel;
    @BindView(R2.id.ll_label)
    LinearLayout llLabel;
    @BindView(R2.id.iv_share)
    ImageView ivShare;
    @BindView(R2.id.tv_comment)
    TextView tvComment;
    @BindView(R2.id.tv_like)
    TextView tvLike;
    @BindView(R2.id.rl_action)
    RelativeLayout rlAction;
    @BindView(R2.id.ll_detail)
    RoundLinearLayout llDetail;
    @BindView(R2.id.cb_watch_condition)
    CheckBox cbWatchCondition;
    @BindView(R2.id.rl_comment_header)
    RelativeLayout rlCommentHeader;
    @BindView(R2.id.rv_comment)
    RecyclerView rvComment;
    @BindView(R2.id.srl_refresh_comment)
    SmartRefreshLayout srlRefreshComment;
    @BindView(R2.id.tv_comment_content)
    TextView etComment;
    @BindView(R2.id.iv_send_msg)
    ImageView ivSendMsg;
    @BindView(R2.id.rl_comment_container)
    RoundLinearLayout rl_comment_container;
    //    @BindView(R2.id.player)
//    SuperPlayerView mSuperVideoView;
    @BindView(R2.id.container)
    ConstraintLayout container;
    @BindView(R2.id.loading_view)
    DYLoadingView loadingView;
    private int detailId;
    //    外面传进来的位置
    private int pos;
    private CommunityRecommend.DataBean detailData;
    private static int PAGE_SIZE = 10;
    private int pageNum = 1;
    private boolean parentHasMore = true;
    private boolean childHasMore = true;


    private SuperPlayerView mSuperVideoView;
    private CommunityCommentAdapter communityCommentAdapter;
    private List<ComParentComment> mCommentList = new ArrayList<>();

    private CustomEditTextBottomPopup mEditPop;

    @Override
    protected void loadData() {
        detailId = getIntent().getIntExtra("id", -1);
        pos = getIntent().getIntExtra("pos", -1);
        getData(this);
        getComment(0);
    }

    private void getComment(int offset) {
        //默认是like_nums表示按照热度排序， comment_date表示按照时间的升序排序
        HttpUtils.getCommunityComment(AppUtils.getUid(), detailId, offset, "comment_date", new JsonCallback<BaseEntity<CommunityCommentRsp>>() {
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


    private void getData(Context mContext) {
        HashMap params = new HashMap<>();
        params.put("id", detailId);
        params.put("mobile_code", AppUtils.getDeviceId(mContext));
        HttpUtils.postData(UrlConstants.GET_COMMNUNITY_DETAIL, params, new JsonCallback<BaseEntity<CommunityRecommend.DataBean>>() {
            @Override
            public void onSuccess(BaseEntity<CommunityRecommend.DataBean> data) {
                hideLoading();
                detailData = data.getData();
                setData(data.getData());
            }

            @Override
            public void onStart(Request<BaseEntity<CommunityRecommend.DataBean>, ? extends Request> request) {
                super.onStart(request);
                showLoading();
            }
        });
    }

    private void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
        loadingView.start();
    }

    private void hideLoading() {
        loadingView.stop();
        loadingView.setVisibility(View.GONE);
    }

    private void setData(CommunityRecommend.DataBean data) {
        detailData = data;
//        头部
        tvNickname.setText(data.getNickname());
        ImageUtils.displayAvatar(this, data.getThumb(), ivAvatar);
//        Glide.with(this).load(data.getThumb()).placeholder(R.drawable.ic_avatar).circleCrop().into(ivAvatar);
        tvLocation.setText(data.getCity());
        tvLocation.setVisibility(TextUtils.isEmpty(data.getCity()) ? View.GONE : View.VISIBLE);
        tvCommunityTitle.setText(data.getTitle());

        String type = data.getType();
        if (type.equals("1")) {
            tvContent.setVisibility(View.VISIBLE);
            tvContent.setText(data.getContent());
        } else {
            tvContent.setVisibility(View.GONE);
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
                rvGallery.setVisibility(View.GONE);
            } else {
                rvGallery.setVisibility(View.VISIBLE);
                rvGallery.setHasFixedSize(true);


                GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
                rvGallery.setLayoutManager(layoutManager);
                CommunityImageAdapter nestedAdapter = new CommunityImageAdapter(imageList);
                rvGallery.setAdapter(nestedAdapter);
            }

        } else {
            rvGallery.setVisibility(View.GONE);
        }
        if (type.equals("3")) {
            conVideo.setVisibility(View.VISIBLE);
            ImageUtils.displayImage(this, data.getCover(), R.drawable.ic_placeholder, ivVideo);
            ivPlay.setOnClickListener(v -> {
                ivVideo.setVisibility(View.GONE);
                ivPlay.setVisibility(View.GONE);
                frameLayout.addView(mSuperVideoView, ivVideo.getLayoutParams());
                mSuperVideoView.play(data.getContent(), data.getTitle());
            });
//            duration.setText(data.get);
        } else {
            conVideo.setVisibility(View.GONE);
        }
        tvLabel.setText(data.getCateName());

        tvComment.setText(String.valueOf(data.getComment_count()));
        IconStausUtils.doLike(tvLike, data.isIs_like());
        tvLike.setText(String.valueOf(data.getLikes_count()));
    }


    @Override
    protected int getContentViewId() {
        return R.layout.activity_community_detail;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        setTitle("帖子详情");
        mSuperVideoView = new SuperPlayerView(this);
        mSuperVideoView.setPlayerViewCallback(this);
//        mSuperVideoView.setBackVisibility(View.GONE);
        mSuperVideoView.setOnPlayEvent(this);

        rvComment.setLayoutManager(new LinearLayoutManager(this));
        rvComment.setNestedScrollingEnabled(true);

        communityCommentAdapter = new CommunityCommentAdapter(this, mCommentList);
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
                showCommentPopup(groupPosition, childPosition);
            }

        });

        rvComment.setAdapter(communityCommentAdapter);
        srlRefreshComment.setEnableRefresh(false);
        srlRefreshComment.setOnLoadMoreListener(refreshLayout -> {
            if (parentHasMore) {
                refreshLayout.finishLoadMore();
                getComment(mCommentList.size());
            } else {
                refreshLayout.finishLoadMoreWithNoMoreData();
                ToastUtils.showShort("没有更多数据");
            }
        });
//        cbWatchCondition.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if (isChecked) {
//                //不让下拉加载更多
//                hasMore = false;
//                filterCommenList();
//            } else {
//                hasMore = true;
//                commentAdapter = new CommunityCommentAdapter(commentList);
//                commentAdapter.notifyDataSetChanged();
//                rvComment.setAdapter(commentAdapter);
//            }
//        });
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
        HttpUtils.postData(UrlConstants.ARTICLE_LIKE, params, new JsonCallback<BaseEntity<LikeRsp>>() {
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
        if (AppUtils.isLogin()) {
            String hint;
            if (childPosition == -1) {
                hint = mCommentList.get(groupPosition).getUserInfo().getNickname();
            } else {
                hint = mCommentList.get(groupPosition).getSubList().getSubData().get(childPosition)
                        .getUserInfo().getNickname();
            }
            mEditPop = new CustomEditTextBottomPopup(this, etComment.getText(), hint);
            //点击发送按钮
//                输入文字回传
            mEditPop.setCallback(context -> {
                if (etComment != null) {
                    etComment.setText(context);
                }
            });
            mEditPop.setOnClickListener(v1 ->
            {
                if (AntiShakeUtils.isInvalidClick(v1))
                    return;
                postReply(groupPosition, childPosition, mEditPop.getComment());
            });


            new XPopup.Builder(this)
                    .autoOpenSoftInput(true)
//                        .hasShadowBg(false)
                    .asCustom(mEditPop)
                    .show();
        } else {
            ARouter.getInstance().build(ARConstants.PATH_LOGIN)
                    .navigation();
        }

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


        //pid为0一级评论，puid0
        JsonCallback<BaseEntity> callback = new JsonCallback<BaseEntity>() {
            @Override
            public void onSuccess(BaseEntity data) {
                hideLoading();
//                ivSendMsg.setEnabled(true);
                detailData.setComment_count(detailData.getComment_count() + 1);
                tvComment.setText(String.valueOf(detailData.getComment_count()));
                ChildComment childComment = new ChildComment();
                childComment.setText(comment);
                childComment.setComment_date((int) (System.currentTimeMillis() / 1000));
                UserInfo userInfo = new UserInfo();
                userInfo.setNickname(detailData.getNickname());
                userInfo.setThumb(detailData.getThumb());
                childComment.setPid(mCommentList.get(groupPosition).getId());
                childComment.setPuid(mCommentList.get(groupPosition).getUid());
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
                etComment.setText("");
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
                showLoading();
            }
        };
        HttpUtils.postCommend(AppUtils.getUid(), pid, puid, comment, detailId, callback);
    }


    private List<ChildComment> filterList(List<ChildComment> subCommentItems) {
        List<ChildComment> temp = new ArrayList<>();
        temp.add(subCommentItems.get(0));
        temp.add(subCommentItems.get(1));
        temp.add(subCommentItems.get(2));
        temp.add(new ChildComment());
        return temp;
    }

    private void getSubComment(int pid, int offset, int groupPosition, int childPosition) {
        HttpUtils.getCommunitySubComment(detailId, offset, pid, new JsonCallback<BaseEntity<CommunitySubCommentRsp>>() {
            @Override
            protected void onSuccess(BaseEntity<CommunitySubCommentRsp> data) {
//                子评论数量,判断是否有更多
                int totalSub = mCommentList.get(groupPosition).getSubList().getSubData().size();
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

//    private void filterCommenList() {
//        List<CommentList> temp = new ArrayList<>();
//        for (CommentList item : commentList) {
//            if (item.getUid() == 2) {
//                temp.add(item);
//            }
//        }
////        commentList.clear();
////        commentList.addAll(temp);
//        commentAdapter = new CommunityCommentAdapter(temp);
//        commentAdapter.notifyDataSetChanged();
//        rvComment.setAdapter(commentAdapter);
//    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        ImmersionBar.with(this).keyboardEnable(true).init();
    }

    @OnClick({R2.id.rl_person, R2.id.iv_video, R2.id.iv_share, R2.id.tv_comment_content, R2.id.tv_like, R2.id.iv_send_msg})
    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.iv_share) {
            AppUtils.copyToClipboard(this, "后台接口配置");
            ToastUtils.showShort("已复制到粘贴板");
        } else if (id == R.id.tv_like) {
            doLike();
        } else if (id == R.id.tv_comment_content) {
            showCommentPopup(etComment.getText());
        }

    }

    private void showCommentPopup(CharSequence text) {
        mEditPop = new CustomEditTextBottomPopup(this, text, "");
        //点击发送按钮
//                输入文字回传
        mEditPop.setCallback(context -> {
            if (etComment != null) {
                etComment.setText(context);
            }
        });
        mEditPop.setOnClickListener(v ->
        {
            if (AntiShakeUtils.isInvalidClick(v))
                return;
            doPostComment();
        });
        new XPopup.Builder(this)
                .autoOpenSoftInput(true)
//                        .hasShadowBg(false)
                .asCustom(mEditPop)
                .show();
    }

    private void doPostComment() {
        if (TextUtils.isEmpty(mEditPop.getComment())) {
            ToastUtils.showShort("没有评论内容");
        } else {
//                ivSendMsg.setEnabled(false);
            if (AppUtils.isLogin()) {
                postComment(mEditPop.getComment());
            } else {
                ARouter.getInstance().build(ARConstants.PATH_LOGIN)
                        .navigation();
            }
        }
    }

    private void doLike() {
        if (detailData == null) {
            return;
        }
        if (detailData.isIs_like()) {
            HttpUtils.postDisLike(detailData.getId(), AppUtils.getDeviceId(this), 2, 2,
                    new JsonCallback<BaseEntity>() {
                        @Override
                        protected void onSuccess(BaseEntity bean) {
                            detailData.setIs_like(false);
                            IconStausUtils.doLike(tvLike, false);
                            detailData.setLikes_count(detailData.getLikes_count() - 1);
                            tvLike.setText(String.format("%d", detailData.getLikes_count()));
                            EventBusHelper.post(new Event(EventCode.DISLIKE, pos));
                        }
                    });

        } else {
            HttpUtils.postLike(detailData.getId(), AppUtils.getDeviceId(this), 2, 2,
                    new JsonCallback<BaseEntity>() {
                        @Override
                        protected void onSuccess(BaseEntity bean) {
                            detailData.setIs_like(true);
                            IconStausUtils.doLike(tvLike, true);
                            detailData.setLikes_count(detailData.getLikes_count() + 1);
                            tvLike.setText(String.format("%d", detailData.getLikes_count()));
                            EventBusHelper.post(new Event(EventCode.LIKE, pos));
                        }
                    });
        }
    }

    private void postComment(String comment) {
        //pid为0一级评论，puid0
        JsonCallback<BaseEntity> callback = new JsonCallback<BaseEntity>() {
            @Override
            public void onSuccess(BaseEntity data) {
                hideLoading();
//                ivSendMsg.setEnabled(true);
                detailData.setComment_count(detailData.getComment_count() + 1);
                tvComment.setText(String.valueOf(detailData.getComment_count()));
                ComParentComment parentComment = new ComParentComment();
                parentComment.setText(comment);
                parentComment.setComment_date((int) (System.currentTimeMillis() / 1000));
                LoginEntity userInfo1 = AppUtils.getUserInfo();
                UserInfo userInfo = new UserInfo();
                userInfo.setNickname(AppUtils.getUserInfo().getUser().getNickname());
                userInfo.setThumb(AppUtils.getUserInfo().getUser().getThumb());
                parentComment.setUserInfo(userInfo);
                mCommentList.add(0, parentComment);
                communityCommentAdapter.notifyGroupInserted(0);
                ToastUtils.showLong(data.getMessage());
                etComment.setText("");
                mEditPop.dismiss();
            }

            @Override
            public void onError(Response<BaseEntity> response) {
                super.onError(response);
//                ivSendMsg.setEnabled(true);
                hideLoading();
                ToastUtils.showLong("发送失败，请重发");
            }

            @Override
            public void onStart(Request<BaseEntity, ? extends Request> request) {
                super.onStart(request);
                showLoading();

            }
        };
        HttpUtils.postCommend(AppUtils.getUid(), 0, 0, comment, detailId, callback);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);

    }

    @Override
    public void onStartFullScreenPlay() {
        removeParentView();
//        container.removeAllViews();
        ViewGroup viewGroup = (ViewGroup) getWindow().getDecorView();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.
                FLAG_KEEP_SCREEN_ON);
        viewGroup.addView(mSuperVideoView);

    }

    @Override
    public void onStopFullScreenPlay() {
        removeParentView();
        if (frameLayout != null) {
            frameLayout.addView(mSuperVideoView, 0);
        }
        Window windowBack = getWindow();
        //设置当前窗体为全屏显示
        int flagBack = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
        windowBack.clearFlags(flagBack);
        ImmersionBar.with(this).fitsSystemWindows(true).init();
//        llBottom.setVisibility(View.VISIBLE);
    }

    private void removeParentView() {
        ViewGroup parent = (ViewGroup) mSuperVideoView.getParent();
        if (parent != null) {
            parent.removeView(mSuperVideoView);
        }
    }

    @Override
    public void onClickFloatCloseBtn() {

    }

    @Override
    public void onClickSmallReturnBtn() {
        finish();
    }

    @Override
    public void onStartFloatWindowPlay() {

    }

    @Override
    protected void onDestroy() {
        mSuperVideoView.release();
        mSuperVideoView.resetPlayer();
        super.onDestroy();

    }

    @Override
    public void onReadyPlay() {

    }

    @Override
    public void onFirstFrame() {
//        HashMap params = new HashMap<>();
//        params.put("id", mVideoId);
//        HttpUtils.postData(UrlConstants.PLAY_STATISTICS, params, new JsonCallback<BaseEntity>() {
//            @Override
//            protected void onSuccess(BaseEntity data) {
//                videoDetailEntity.setPlay_count(videoDetailEntity.getPlay_count() + 1);
//            }
//        });
    }

    @Override
    public void onPlayerPause() {
    }

    @Override
    public void onFinish() {

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSuperVideoView.getPlayState() == SuperPlayerConst.PLAYSTATE_PLAYING) {
            mSuperVideoView.onResume();
        }
        if (mSuperVideoView.getPlayMode() == SuperPlayerConst.PLAYMODE_FULLSCREEN) {
            //隐藏虚拟按键，并且全屏
            View decorView = getWindow().getDecorView();
            if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
                decorView.setSystemUiVisibility(View.GONE);
            } else if (Build.VERSION.SDK_INT >= 19) {
                int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
                decorView.setSystemUiVisibility(uiOptions);
            }
        }
    }
}
