package com.huaxin.video.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.example.librarybase.base.BaseActivity;
import com.example.librarybase.base.Event;
import com.example.librarybase.base.EventBusHelper;
import com.gyf.immersionbar.ImmersionBar;
import com.huaxin.library.adapter.BannerAdapter;
import com.huaxin.library.adapter.CommunityCommentAdapter;
import com.huaxin.library.custom.CustomEditTextBottomPopup;
import com.huaxin.library.custom.SuperPlayerView;
import com.huaxin.library.db.WatchRecord;
import com.huaxin.library.db.WatchRecordRepository;
import com.huaxin.library.entity.BannerItem;
import com.huaxin.library.entity.ChildComment;
import com.huaxin.library.entity.ComParentComment;
import com.huaxin.library.entity.CommunityCommentRsp;
import com.huaxin.library.entity.CommunitySubCommentRsp;
import com.huaxin.library.entity.LikeRsp;
import com.huaxin.library.entity.UserInfo;
import com.huaxin.library.entity.VideoDetailEntity;
import com.huaxin.library.http.BaseEntity;
import com.huaxin.library.http.HttpUtils;
import com.huaxin.library.http.JsonCallback;
import com.huaxin.library.utils.ARConstants;
import com.huaxin.library.utils.AntiShakeUtils;
import com.huaxin.library.utils.AppUtils;
import com.huaxin.library.utils.EventCode;
import com.huaxin.library.utils.UrlConstants;
import com.huaxin.video.R;
import com.huaxin.video.R2;
import com.huaxin.video.adapter.MayLikeAdapter;
import com.huaxin.video.entity.LikeEntity;
import com.huaxin.video.entity.VideoGuessLikeEntity;
import com.kproduce.roundcorners.RoundTextView;
import com.lxj.xpopup.XPopup;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.qfxl.view.RoundProgressBar;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.tencent.liteav.demo.play.SuperPlayerConst;
import com.xuexiang.xui.widget.banner.recycler.BannerLayout;
import com.xuexiang.xui.widget.popupwindow.good.GoodView;
import com.xuexiang.xui.widget.popupwindow.good.IGoodView;
import com.xuexiang.xui.widget.statelayout.MultipleStatusView;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Route(path = ARConstants.PATH_VIDEO_PLAY)
public class VideoDetailActivity extends BaseActivity implements SuperPlayerView.OnSuperPlayerViewCallback, SuperPlayerView.OnPlayEvent {
    @BindView(R2.id.player)
    SuperPlayerView mSuperVideoView;
    @BindView(R2.id.ib_dislike)
    ImageButton ibDislike;
    @BindView(R2.id.sb_like_rate)
    SeekBar sbLikeRate;
    @BindView(R2.id.tv_like_rate)
    TextView tvLikeRate;
    @BindView(R2.id.ib_like)
    ImageButton ibLike;
    @BindView(R2.id.tv_video_title)
    TextView tvTitle;
    @BindView(R2.id.tv_time)
    TextView tvTime;
    @BindView(R2.id.tv_play_times)
    TextView tvPlayTimes;
    @BindView(R2.id.tv_comment_num)
    TextView tvCommentNum;
    @BindView(R2.id.iv_share)
    ImageButton ivShare;
    @BindView(R2.id.iv_collection)
    ImageButton ivCollection;
    @BindView(R2.id.rv_guess_like)
    RecyclerView rvGuessLike;
    @BindView(R2.id.rv_comment)
    RecyclerView rvComment;
    @BindView(R2.id.srl_container)
    SmartRefreshLayout srlContainer;
    @BindView(R2.id.tv_comment_content)
    TextView tvComment;
    @BindView(R2.id.iv_send)
    ImageButton ivSend;
    @BindView(R2.id.ll_bottom)
    LinearLayout llBottom;
    @BindView(R2.id.bl_horizontal)
    BannerLayout blHorizontal;
    @BindView(R2.id.tv_content)
    RoundTextView mContent;
    @BindView(R2.id.img_gg)
    ImageView imgGg;
    @BindView(R2.id.rpb_gg)
    RoundProgressBar rpbGg;
    @BindView(R2.id.fl_load_gg)
    FrameLayout flLoadGg;
    @BindView(R2.id.multiple_status_view)
    MultipleStatusView multipleStatusView;
    private int mVideoId;

    private VideoDetailEntity videoDetailEntity;
    private MayLikeAdapter mayLikeAdapter;
    private List<VideoGuessLikeEntity> mLikeData = new ArrayList<>();
    //评论列表
    private static int PAGE_SIZE = 10;
    private int pageNum = 1;
    private boolean parentHasMore = true;
    private boolean childHasMore = true;
    private CommunityCommentAdapter communityCommentAdapter;
    private List<ComParentComment> mCommentList = new ArrayList<>();

    private CustomEditTextBottomPopup mEditPop;

    private WatchRecordRepository watchRecordRepository;
    private BannerAdapter bannerAdapter;

    private IGoodView mGoodView;
    private int lastShowPosition;

    @Override
    public boolean showTitleView() {
        return false;
    }

    @Override
    protected void loadData() {
        mVideoId = getIntent().getIntExtra("id", -1);
        getAd();
        getData();
        watchRecordRepository = new WatchRecordRepository(this);
        getBanner();
    }

    private void getAd() {
        //广告
        HttpUtils.getBanner(6, new JsonCallback<BaseEntity<List<BannerItem>>>() {
            @Override
            protected void onSuccess(BaseEntity<List<BannerItem>> data) {
                Glide.with(VideoDetailActivity.this).load(data.getData().get(0).getMedia()).placeholder(com.huaxin.library.R.mipmap.find_item_holder).into(imgGg);
            }
        });
    }

    private void getBanner() {
        HttpUtils.getBanner(5, new JsonCallback<BaseEntity<List<BannerItem>>>() {
            @Override
            protected void onSuccess(BaseEntity<List<BannerItem>> data) {
                bannerAdapter = new BannerAdapter(data.getData());
                blHorizontal.setAdapter(bannerAdapter);
            }
        });

    }

    private void getData() {
        getVideoData();
        getLikeData();
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

    @Override
    public void onReadyPlay() {
        mSuperVideoView.onResume();
    }

    @Override
    public void onFirstFrame() {
        HashMap params = new HashMap<>();
        params.put("id", mVideoId);
        HttpUtils.postData(UrlConstants.PLAY_STATISTICS, params, new JsonCallback<BaseEntity>() {
            @Override
            protected void onSuccess(BaseEntity data) {
                videoDetailEntity.setPlay_count(videoDetailEntity.getPlay_count() + 1);
                if (tvPlayTimes != null) {
                    tvPlayTimes.setText(videoDetailEntity.getPlay_count() + "");
                }

            }
        });
    }

    @Override
    public void onPlayerPause() {
        pause();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pause();
    }

    @Override
    public void onFinish() {
        insert(videoDetailEntity, mSuperVideoView.getmCurrentTimeWhenPause());
    }

    @Override
    protected void onDestroy() {
        mSuperVideoView.release();
        mSuperVideoView.resetPlayer();
        super.onDestroy();

    }


    //猜你喜欢
    private void getLikeData() {
        JsonCallback<BaseEntity<List<VideoGuessLikeEntity>>> callback = new JsonCallback<BaseEntity<List<VideoGuessLikeEntity>>>() {
            @Override
            protected void onSuccess(BaseEntity<List<VideoGuessLikeEntity>> data) {
                mayLikeAdapter.replaceData(data.getData());
            }

        };
        HttpUtils.getMaybeLike(mVideoId, callback);
    }

    private void getVideoData() {
        JsonCallback<BaseEntity<VideoDetailEntity>> callback = new JsonCallback<BaseEntity<VideoDetailEntity>>() {
            @Override
            public void onSuccess(BaseEntity<VideoDetailEntity> data) {
                videoDetailEntity = data.getData();
                multipleStatusView.setVisibility(View.VISIBLE);
                setVideoData(videoDetailEntity);
            }
        };
        HttpUtils.getVideoDetail(mVideoId, callback);
    }

    private void setVideoData(VideoDetailEntity data) {
        //空指针，因为请求还没完成就销毁了
        if (mSuperVideoView != null) {
            mSuperVideoView.preplay(data.getVideo_href(), data.getTitle());
        }

        tvTitle.setText(data.getTitle());
        mContent.setText(data.getIntro());
        Date date = TimeUtils.string2Date(data.getCreated_at());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd ");
        String time = TimeUtils.date2String(date, dateFormat);
        tvTime.setText(String.format(getString(R.string.hint_create_time), time));
        tvPlayTimes.setText(String.format("%d", data.getPlay_count()));
        setLike(data.getLike_rate());
        setCommenText(data.getComment_count());
        ivCollection.setImageResource(data.isIs_collection() ? R.mipmap.ic_dts_collect_collected : R.mipmap.ic_dts_collect_normal);
    }

    private void setCommenText(int count) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        String numComment = getResources().getString(R.string.hint_comment_num);
        tvCommentNum.setText(getNumFormatString(builder, count, numComment));
    }

    private void setLike(float like_rate) {
        int progress = (new BigDecimal(String.valueOf(like_rate)).multiply(new BigDecimal(String.valueOf(100))).intValue());
        sbLikeRate.setProgress(progress);
        tvLikeRate.setText(String.format(getResources().getString(R.string.hint_think_good), String.valueOf(progress)));
    }

    private SpannableStringBuilder getNumFormatString(SpannableStringBuilder builder, int count, String formatString) {
        builder.clear();
        String countFormat = String.valueOf(count);
        SpannableString spannedString = new SpannableString(countFormat);
        ForegroundColorSpan span = new ForegroundColorSpan(getResources().getColor(R.color.text_color));
        spannedString.setSpan(span, 0, countFormat.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        builder.append(spannedString);
        builder.append(" ");
        spannedString = new SpannableString(formatString);
        span = new ForegroundColorSpan(getResources().getColor(R.color.colorTitle));
        spannedString.setSpan(span, 0, formatString.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        builder.append(spannedString);
        return builder;
    }


    @Override
    protected int getContentViewId() {
        return R.layout.activity_video_detail;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        mGoodView = new GoodView(this);
        mSuperVideoView.setPlayerViewCallback(this);
        mSuperVideoView.setOnPlayEvent(this);
        rpbGg.setProgressChangeListener(new RoundProgressBar.ProgressChangeListener() {
            @Override
            public void onFinish() {
                if (flLoadGg != null) {
                    flLoadGg.setVisibility(View.GONE);
                }
                if (mSuperVideoView != null) {
                    mSuperVideoView.onResume();
                }

            }

            @Override
            public void onProgressChanged(int progress) {
                int text = 5 - progress * 5 / 100;
                rpbGg.setCenterText(text + "");
            }
        });
        rpbGg.start();

        mayLikeAdapter = new MayLikeAdapter(mLikeData);
        rvGuessLike.setAdapter(mayLikeAdapter);
        rvGuessLike.setLayoutManager(new LinearLayoutManager(this));
        rvGuessLike.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(Color.TRANSPARENT)
                .sizeResId(R.dimen.maybe_like_divider)
                .build());

        rvComment.setLayoutManager(new LinearLayoutManager(this));
        rvComment.setNestedScrollingEnabled(true);
        rvComment.setHasFixedSize(true);
        communityCommentAdapter = new CommunityCommentAdapter(this, mCommentList);
        communityCommentAdapter.setLikeListener((view, groupPosition, childPosition) -> {
            sendLikeRequest(groupPosition, childPosition);
        });
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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
    }

    @OnClick({R2.id.iv_share, R2.id.iv_collection, R2.id.iv_send, R2.id.ib_like, R2.id.ib_dislike})
    public void onViewClicked(View view) {
        int id = view.getId();

        if (id == R.id.iv_share) {
            AppUtils.copyToClipboard(this, "后台接口配置");
            ToastUtils.showShort("已复制到粘贴板");
        } else if (id == R.id.ib_like) {
            if (AntiShakeUtils.isInvalidClick(view))
                return;
            doLike();
        } else if (id == R.id.ib_dislike) {
            if (AntiShakeUtils.isInvalidClick(view))
                return;
            doDisLike();
        } else if (id == R.id.iv_collection) {
            if (AntiShakeUtils.isInvalidClick(view))
                return;
            doCollect();
        } else if (id == R.id.iv_send) {
            if (AntiShakeUtils.isInvalidClick(view))
                return;
            String comment = tvComment.getText().toString().trim();
            if (TextUtils.isEmpty(comment)) {
                ToastUtils.showShort("没有评论内容");
            } else {
//                ivSend.setEnabled(false);
                if (AppUtils.isLogin()) {
                    postComment(comment);
                } else {
                    ARouter.getInstance().build(ARConstants.PATH_LOGIN)
                            .navigation();
                }
            }
        }
    }

    //
    private void doDisLike() {
        HttpUtils.postDisLike(mVideoId, AppUtils.getDeviceId(this), 1,
                new JsonCallback<BaseEntity<LikeEntity>>() {
                    @Override
                    protected void onSuccess(BaseEntity<LikeEntity> bean) {
                        int progress = (int) (bean.getData().getLike_rate() * 100);
                        sbLikeRate.setProgress(progress);
                        tvLikeRate.setText(String.format(getResources().getString(R.string.hint_think_good), String.valueOf(progress)));
                        ToastUtils.showShort(bean.getMessage());
                    }
                });
    }

    private void doLike() {
        mGoodView.setImageResource(R.mipmap.ic_like_up)
                .show(ibLike);
        HttpUtils.postLike(mVideoId, AppUtils.getDeviceId(this), 2, 1,
                new JsonCallback<BaseEntity<LikeEntity>>() {
                    @Override
                    protected void onSuccess(BaseEntity<LikeEntity> bean) {
                        int progress = (int) (bean.getData().getLike_rate() * 100);
                        sbLikeRate.setProgress(progress);
                        tvLikeRate.setText(String.format(getResources().getString(R.string.hint_think_good), String.valueOf(progress)));
                        ToastUtils.showShort(bean.getMessage());
                    }
                });
    }

    private void doCollect() {
        if (videoDetailEntity.isIs_collection()) {
            HttpUtils.postDisLike(mVideoId, AppUtils.getDeviceId(this), 1, 1,
                    new JsonCallback<BaseEntity>() {
                        @Override
                        protected void onSuccess(BaseEntity bean) {
                            videoDetailEntity.setIs_collection(false);
                            ivCollection.setImageResource(R.mipmap.ic_dts_collect_normal);
                            ToastUtils.showShort("取消收藏成功");
                            EventBusHelper.post(new Event(EventCode.COLLECT_LONG_VIDEO, "dd"));
                        }
                    });

        } else {
            HttpUtils.postLike(mVideoId, AppUtils.getDeviceId(this), 1, 1,
                    new JsonCallback<BaseEntity>() {
                        @Override
                        protected void onSuccess(BaseEntity bean) {
                            ToastUtils.showShort("收藏成功");
                            videoDetailEntity.setIs_collection(true);
                            ivCollection.setImageResource(R.mipmap.ic_dts_collect_collected);
                            EventBusHelper.post(new Event(EventCode.COLLECT_LONG_VIDEO, "dd"));
                        }
                    });
        }
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
            mEditPop = new CustomEditTextBottomPopup(this, tvComment.getText(), hint);
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


        //pid为一级评论，puid0
        JsonCallback<BaseEntity> callback = new JsonCallback<BaseEntity>() {
            @Override
            public void onSuccess(BaseEntity data) {
//                hideLoading();
//                ivSendMsg.setEnabled(true);
                videoDetailEntity.setComment_count(videoDetailEntity.getComment_count() + 1);
                setCommenText(videoDetailEntity.getComment_count());
                ChildComment childComment = new ChildComment();
                childComment.setText(comment);
                childComment.setComment_date((int) (System.currentTimeMillis() / 1000));
                childComment.setPid(mCommentList.get(groupPosition).getId());
                childComment.setPuid(mCommentList.get(groupPosition).getUid());
                UserInfo userInfo = new UserInfo();
                userInfo.setNickname(AppUtils.getUserInfo().getUser().getNickname());
                userInfo.setThumb(AppUtils.getUserInfo().getUser().getThumb());
                childComment.setUserInfo(userInfo);
                //一级评论的回复
                if (childPosition == -1) {
                    if (mCommentList.get(groupPosition).getSubList() == null || CollectionUtils.isEmpty(mCommentList.get(groupPosition).getSubList().getSubData())) {
                        List<ChildComment> childComments = new ArrayList<>();
                        childComments.add(childComment);
                        mCommentList.get(groupPosition).setSubList(new ComParentComment.SubListBean(0, childComments));
//                        mCommentList.get(groupPosition).getSubList().setSubData(childComments);
                        communityCommentAdapter.notifyChildInserted(groupPosition, 0);
                    } else {
                        mCommentList.get(groupPosition).getSubList().getSubData().add(childComment);
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
//                hideLoading();
                ToastUtils.showLong("发送出错，请重发");
            }

            @Override
            public void onStart(Request<BaseEntity, ? extends Request> request) {
                super.onStart(request);
//                showLoading();
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

    private void postComment(String comment) {
        //pid为0一级评论，puid0
        JsonCallback<BaseEntity> callback = new JsonCallback<BaseEntity>() {
            @Override
            public void onSuccess(BaseEntity data) {
//                hideLoading();
//                ivSendMsg.setEnabled(true);
                videoDetailEntity.setComment_count(videoDetailEntity.getComment_count() + 1);
                setCommenText(videoDetailEntity.getComment_count());
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
//                hideLoading();
                ToastUtils.showLong("发送失败，请重发");
            }

            @Override
            public void onStart(Request<BaseEntity, ? extends Request> request) {
                super.onStart(request);
//                showLoading();

            }
        };
        HttpUtils.postVideoCommend(AppUtils.getUid(), 0, 0, comment, mVideoId, callback);
    }

    private void showCommentPopup(CharSequence text) {
        mEditPop = new CustomEditTextBottomPopup(this, text, "");
        //点击发送按钮
//                输入文字回传
        mEditPop.setCallback(context -> {
            if (tvComment != null) {
                tvComment.setText(context);
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

    @Override
    public void onStartFullScreenPlay() {
        llBottom.setVisibility(View.GONE);
    }

    @Override
    public void onStopFullScreenPlay() {
        Window windowBack = getWindow();
        //设置当前窗体为全屏显示
        int flagBack = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
        windowBack.clearFlags(flagBack);
        ImmersionBar.with(this).fitsSystemWindows(true).init();
        llBottom.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClickFloatCloseBtn() {

    }

    @Override
    public void onClickSmallReturnBtn() {
        pause();
        finish();
    }

    @Override
    public void onStartFloatWindowPlay() {

    }

    private void pause() {
        if (mSuperVideoView != null) {
            mSuperVideoView.onPause();
            if (videoDetailEntity != null) {
                if (mSuperVideoView.getmCurrentTimeWhenPause() > 0) {
                    insert(videoDetailEntity, mSuperVideoView.getmCurrentTimeWhenPause());
                }
            }
        }
    }

    private void insert(VideoDetailEntity videoDetailEntity, float currentTimeWhenPause) {
        WatchRecord user = new WatchRecord();
        user.setId(videoDetailEntity.getId());
        user.setName(videoDetailEntity.getTitle());
        user.setThumb(videoDetailEntity.getThumb());
        user.setIntro(videoDetailEntity.getIntro());
        user.setVideo_href(videoDetailEntity.getVideo_href());
        user.setPlayDuration(currentTimeWhenPause);
        user.setDuration(videoDetailEntity.getDuration());
        user.setTimestamp(System.currentTimeMillis());
        watchRecordRepository.insert(user);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        rpbGg.stop();
        flLoadGg.setVisibility(View.VISIBLE);
        rpbGg.setProgressChangeListener(new RoundProgressBar.ProgressChangeListener() {
            @Override
            public void onFinish() {
                if (flLoadGg != null) {
                    flLoadGg.setVisibility(View.GONE);
                }
                if (mSuperVideoView != null) {
                    mSuperVideoView.onResume();
                }

            }

            @Override
            public void onProgressChanged(int progress) {
                int text = 5 - progress * 5 / 100;
                rpbGg.setCenterText(text + "");
            }
        });
        rpbGg.start();
        mVideoId = intent.getIntExtra("id", -1);
        getData();
    }


}
