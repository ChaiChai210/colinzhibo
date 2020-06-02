package com.huaxin.usercenter.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.huaxin.library.adapter.BannerAdapter;
import com.example.librarybase.base.BaseFragment;
import com.huaxin.library.custom.UserCenterItem;
import com.huaxin.library.db.MyDatabase;
import com.huaxin.library.db.WatchDao;
import com.huaxin.library.entity.BannerItem;
import com.huaxin.library.http.BaseEntity;
import com.huaxin.library.http.HttpUtils;
import com.huaxin.library.http.JsonCallback;
import com.huaxin.library.intercepter.LoginNavigationCallbackImpl;
import com.huaxin.library.utils.ARConstants;
import com.huaxin.library.utils.AppUtils;
import com.huaxin.library.utils.Constant;
import com.huaxin.library.utils.RouteUtils;
import com.huaxin.library.utils.UrlConstants;
import com.huaxin.usercenter.R;
import com.huaxin.usercenter.R2;
import com.huaxin.usercenter.custom.UserHeadView;
import com.huaxin.usercenter.entity.UserBean;
import com.huaxin.usercenter.popup.ChatPopWindow;
import com.lxj.xpopup.XPopup;
import com.xuexiang.xui.widget.banner.recycler.BannerLayout;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouteUtils.Mine_Fragment_Main)
public class MainUserFragment extends BaseFragment implements AppBarLayout.OnOffsetChangedListener {
    @BindView(R2.id.tv_nickname)
    TextView tvNickname;
    @BindView(R2.id.tv_id)
    TextView tvId;
    @BindView(R2.id.tv_location)
    TextView tvLocation;
    @BindView(R2.id.ll_user_info)
    LinearLayout llUserInfo;
    @BindView(R2.id.tv_watch_count)
    TextView tvWatchCount;
    @BindView(R2.id.ll_today_watch_count)
    LinearLayout llTodayWatchCount;
    @BindView(R2.id.tv_follow_count)
    TextView tvFollowCount;
    @BindView(R2.id.ll_follow)
    LinearLayout llFollow;
    @BindView(R2.id.tv_fans_count)
    TextView tvFansCount;
    @BindView(R2.id.ll_fans)
    LinearLayout llFans;
    @BindView(R2.id.head)
    UserHeadView head;
    @BindView(R2.id.tv_user_title)
    TextView tvUserTitle;
    @BindView(R2.id.toolbar_layout)
    CollapsingToolbarLayout toolbarLayout;
    @BindView(R2.id.app_bar)
    AppBarLayout appBar;
    @BindView(R2.id.iv_promotion)
    ImageView ivPromotion;
    @BindView(R2.id.ll_promotion)
    LinearLayout llPromotion;
    @BindView(R2.id.iv_feedback)
    ImageView ivFeedback;
    @BindView(R2.id.ll_my_purse)
    LinearLayout llMyPurse;
    @BindView(R2.id.iv_anchor_verify)
    ImageView ivAnchorVerify;
    @BindView(R2.id.ll_anchor_verify)
    LinearLayout llAnchorVerify;
    @BindView(R2.id.ll_chat)
    LinearLayout llChat;
    @BindView(R2.id.uci_post)
    UserCenterItem uciPost;
    @BindView(R2.id.uci_record)
    UserCenterItem uciRecord;
    @BindView(R2.id.uci_collect)
    UserCenterItem uciCollect;
    @BindView(R2.id.iv_setting)
    ImageView ivSetting;
    @BindView(R2.id.iv_user_msg)
    ImageView ivUserMsg;
    @BindView(R2.id.bl_horizontal)
    BannerLayout blHorizontal;
    @BindView(R2.id.uci_download)
    UserCenterItem uciDownload;

    private WatchDao watchDao;
    private BannerAdapter bannerAdapter;

    @Override
    protected void loadData() {
        getBanner();
    }

    private void getWatchRecord() {
        MyDatabase db = MyDatabase.getInstance(mContext);
        watchDao = db.watchDao();
        int size = watchDao.getWatchCount();
        uciRecord.setContent("目前有" + size + "个记录");
    }


    private void getBanner() {
        HttpUtils.getBanner(4, new JsonCallback<BaseEntity<List<BannerItem>>>() {
            @Override
            protected void onSuccess(BaseEntity<List<BannerItem>> data) {
                bannerAdapter = new BannerAdapter(data.getData());
                blHorizontal.setAdapter(bannerAdapter);
            }
        });
    }

    private void getUserInfo() {
        HashMap params = new HashMap<>();
        params.put("uid", AppUtils.getUid());
        params.put("mobile_code", AppUtils.getDeviceId(mContext));
        HttpUtils.postData(UrlConstants.PROFILE, params, new JsonCallback<BaseEntity<UserBean>>() {
            @Override
            protected void onSuccess(BaseEntity<UserBean> data) {
                setUser(data.getData());
            }
        });
    }

    private void setUser(UserBean data) {
        tvFollowCount.setText(String.format("%d", data.getFollow_count()));
        tvFansCount.setText(String.format("%d", data.getFans_count()));
        tvNickname.setText(data.getNickname());
        head.setHeadUrl(data.getThumb());
        head.setGender(data.getSex());
        head.setLever(data.getUser_verify());
        tvId.setText(String.format("ID:%d", data.getId()));
        tvLocation.setText(data.getAddress());
        uciPost.setContent("目前有" + data.getArticle_count() + "个帖子");
        uciCollect.setContent("目前有" + data.getCollection_count() + "个收藏");
        tvUserTitle.setText(data.getNickname());
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        appBar.addOnOffsetChangedListener(this);
        tvUserTitle.setText("未登录");
    }

    @Override
    public void onResume() {
        super.onResume();
        getWatchRecord();
        getDownLoad();
        if (AppUtils.isLogin()) {
            getUserInfo();
        }
    }

    private void getDownLoad() {
        int size = MyDatabase.getInstance(mContext).downloadDao().getWatchCount();
        uciDownload.setContent("目前有" + size + "个记录");
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_user_center;
    }

    @OnClick({R2.id.iv_setting, R2.id.iv_user_msg, R2.id.ll_promotion, R2.id.tv_nickname,
            R2.id.ll_follow, R2.id.ll_fans, R2.id.ll_chat, R2.id.ll_my_purse, R2.id.uci_post,
            R2.id.uci_collect, R2.id.uci_record,R2.id.uci_download
            , R2.id.ll_anchor_verify})
    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.iv_setting) {
            ARouter.getInstance().build(ARConstants.PATH_SETTING).navigation();
        } else if (id == R.id.tv_nickname) {
            ARouter.getInstance().build(ARConstants.EDIT_PROFILE).navigation(mContext, new LoginNavigationCallbackImpl());
        } else if (id == R.id.iv_user_msg) {
            ARouter.getInstance().build(ARConstants.PATH_USER_MSG).navigation(mContext, new LoginNavigationCallbackImpl());
        } else if (id == R.id.ll_promotion) {
            ARouter.getInstance().build(ARConstants.PATH_MY_PROMOTION).navigation(mContext, new LoginNavigationCallbackImpl());
        } else if (id == R.id.ll_my_purse) {
            ARouter.getInstance().build(ARConstants.PATH_MyWalletActivity).navigation(mContext, new LoginNavigationCallbackImpl());
        } else if (id == R.id.ll_fans) {
            ARouter.getInstance().build(ARConstants.PATH_USER_FOLLOW).withInt(Constant.KEY_USER_ID, 1).navigation();
        } else if (id == R.id.ll_follow) {
            ARouter.getInstance().build(ARConstants.PATH_USER_FOLLOW).withInt(Constant.KEY_USER_ID, 0).navigation();
        } else if (id == R.id.uci_post) {
            ARouter.getInstance().build(ARConstants.PATH_MY_POST).navigation(mContext, new LoginNavigationCallbackImpl());
        } else if (id == R.id.uci_record) {
            ARouter.getInstance().build(ARConstants.WATCH_RECORD).navigation();
        } else if (id == R.id.uci_download) {
            ARouter.getInstance().build(ARConstants.MY_DOWNLOAD).navigation();
        } else if (id == R.id.ll_chat) {
            ChatPopWindow customPopup = new ChatPopWindow(mContext);
            new XPopup.Builder(mContext)
                    .autoOpenSoftInput(true)
                    .asCustom(customPopup)
                    .show();


        } else if (id == R.id.uci_collect) {
            ARouter.getInstance().build(ARConstants.PATH_USER_VIDEO).navigation(mContext, new LoginNavigationCallbackImpl());
        } else if (id == R.id.ll_anchor_verify) {

        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        //垂直方向偏移量
        float percent = (float) Math.abs(verticalOffset) / (float) appBarLayout.getTotalScrollRange();
        tvUserTitle.setAlpha(percent);
    }

}
