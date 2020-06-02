package com.huaxin.usercenter.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.example.librarybase.base.BaseActivity;
import com.huaxin.library.custom.SettingView;
import com.huaxin.library.intercepter.LoginNavigationCallbackImpl;
import com.huaxin.library.utils.ARConstants;
import com.huaxin.usercenter.R;
import com.huaxin.usercenter.R2;
import com.huaxin.usercenter.popup.ClearCachePopWindow;
import com.huaxin.usercenter.popup.LogoutPopWindow;
import com.huaxin.usercenter.utils.DataCleanManager;
import com.lxj.xpopup.XPopup;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Route(path = ARConstants.PATH_SETTING, priority = 6)
public class SettingActivity extends BaseActivity implements ClearCachePopWindow.onConfirm {
    @BindView(R2.id.sv_community_convention)
    SettingView svCommunityConvention;
    @BindView(R2.id.sv_privacy)
    SettingView svPrivacy;
    @BindView(R2.id.sv_service_and_privacy)
    SettingView svServiceAndPrivacy;
    @BindView(R2.id.sv_anchor_agreement)
    SettingView svAnchorAgreement;
    @BindView(R2.id.sv_feedback)
    SettingView svFeedback;
    @BindView(R2.id.tv_cache)
    TextView tvCache;
    @BindView(R2.id.cache_layout)
    RelativeLayout cacheLayout;
    @BindView(R2.id.tv_version)
    TextView tvVersion;
    @BindView(R2.id.rl_check_update)
    RelativeLayout rlCheckUpdate;
    @BindView(R2.id.btn_logout)
    Button btnLogout;
    @BindView(R2.id.set_activity)
    LinearLayout setActivity;

    @Override
    protected void loadData() {

    }


    @Override
    protected int getContentViewId() {
        return R.layout.user_act_setting;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        setTitle("设置");
        tvCache.setText(DataCleanManager.getTotalCacheSize(SettingActivity.this));
        tvVersion.setText(AppUtils.getAppVersionName() + "日期：" + "2010-5-29");

        if (com.huaxin.library.utils.AppUtils.isLogin()) {
            btnLogout.setVisibility(View.VISIBLE);
        } else {
            btnLogout.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R2.id.sv_community_convention, R2.id.sv_privacy, R2.id.sv_service_and_privacy, R2.id.sv_anchor_agreement,
            R2.id.sv_feedback, R2.id.cache_layout, R2.id.rl_check_update, R2.id.btn_logout})
    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.sv_community_convention) {
            gotoWeb("社区公约", "http://web.huatdd.com/#/CommunityConvention");
//        } else if (id == R.id.sv_privacy) {
//            gotoWeb("服务和隐私政策","http://web.huatdd.com/#/ServicesAndPrivacyPolicy");
        } else if (id == R.id.sv_service_and_privacy) {
            gotoWeb("服务和隐私政策", "http://web.huatdd.com/#/ServicesAndPrivacyPolicy");
        } else if (id == R.id.sv_anchor_agreement) {
            gotoWeb("主播协议", "http://web.huatdd.com/#/AnchorAgreement");
        } else if (id == R.id.sv_feedback) {
            ARouter.getInstance().build(ARConstants.PATH_FEEDBACK).navigation(this, new LoginNavigationCallbackImpl());
        } else if (id == R.id.cache_layout) {
            ClearCachePopWindow customPopup = new ClearCachePopWindow(this);
            customPopup.setOnConfirm(this);
            new XPopup.Builder(this)
                    .autoOpenSoftInput(true)
                    .asCustom(customPopup)
                    .show();
        } else if (id == R.id.rl_check_update) {

        } else if (id == R.id.btn_logout) {
            LogoutPopWindow customPopup = new LogoutPopWindow(this);
            new XPopup.Builder(this)
                    .autoOpenSoftInput(true)
                    .asCustom(customPopup)
                    .show();
        }
    }

    private void gotoWeb(String title, String url) {
        Bundle bunle = new Bundle();
        bunle.putString("title", title);
        bunle.putString("url", url);
        ARouter.getInstance().build(ARConstants.ConventionActivity).with(bunle).navigation();
    }

    @Override
    public void onConfirm(View view) {
        DataCleanManager.clearAllCache(this);
        ToastUtils.showShort("缓存清理完毕");
        tvCache.setText("0M");
    }
}
