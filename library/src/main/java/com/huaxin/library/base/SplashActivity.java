package com.huaxin.library.base;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.callback.NavCallback;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.example.librarybase.base.BaseActivity;
import com.fm.openinstall.OpenInstall;
import com.fm.openinstall.listener.AppInstallAdapter;
import com.fm.openinstall.listener.AppWakeUpAdapter;
import com.fm.openinstall.model.AppData;
import com.huaxin.library.R;
import com.huaxin.library.R2;
import com.huaxin.library.entity.BannerItem;
import com.huaxin.library.entity.ConfigEntity;
import com.huaxin.library.http.BaseEntity;
import com.huaxin.library.http.HttpUtils;
import com.huaxin.library.http.JsonCallback;
import com.huaxin.library.utils.ARConstants;
import com.huaxin.library.utils.AppUtils;
import com.huaxin.library.utils.Constant;
import com.huaxin.library.utils.ImageUtils;
import com.huaxin.library.utils.UrlConstants;
import com.lzy.okgo.OkGo;
import com.qfxl.view.RoundProgressBar;
import com.tencent.mmkv.MMKV;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends BaseActivity {
    @BindView(R2.id.iv_splash)
    ImageView ivSplash;
    @BindView(R2.id.rpb_gg)
    RoundProgressBar rpbGg;
    @BindView(R2.id.pb_loading)
    ProgressBar pb_loading;

    private boolean requestSuccess;
    AppWakeUpAdapter wakeUpAdapter = new AppWakeUpAdapter() {
        @Override
        public void onWakeUp(AppData appData) {
            //获取渠道数据
            String channelCode = appData.getChannel();
            //获取绑定数据
            String bindData = appData.getData();
            Log.d("OpenInstall", "getWakeUp : wakeupData = " + appData.toString());
        }
    };

    @Override
    protected void loadData() {
        MMKV.initialize(this);
        MMKV kv = MMKV.defaultMMKV();
        OkGo.<BaseEntity<ConfigEntity>>get(UrlConstants.SYSTEM_CONGIG).params(null).tag(this).execute(new JsonCallback<BaseEntity<ConfigEntity>>() {
            @Override
            protected void onSuccess(BaseEntity<ConfigEntity> data) {
//                requestSuccess = true;
                ConfigEntity data1 = data.getData();
                kv.encode(Constant.SP_SQ_IMG, data1.getSq_img_server());
                kv.encode(Constant.SP_SQ_VIDEO, data1.getSq_video_server());
                kv.encode(Constant.CONFIG, JSON.toJSONString(data1));
                kv.encode(Constant.SP_IMAGE_PREFIX, data1.getImage_server());
//                long end = (System.currentTimeMillis() - start)/1000;
            }
        });
        OpenInstall.getWakeUp(getIntent(), wakeUpAdapter);
        OpenInstall.getInstall(new AppInstallAdapter() {


            @Override
            public void onInstall(AppData appData) {
                //获取渠道数据
                String channelCode = appData.getChannel();
                //获取自定义数据
                String bindData = appData.getData();
                if (!TextUtils.isEmpty(bindData)) {
                    JSONObject object = JSON.parseObject(bindData);
//                    mPublicityId = object.getString("id");
                }
                Log.d("OpenInstall", "getInstall : installData = " + appData.toString());
            }
        });

        if (SPUtils.getInstance().getBoolean(Constant.SP_FIRST_INSTALL, true)) {
            startActivity(new Intent(SplashActivity.this, GuideActivity.class));
        } else {
            getAd();
        }
    }

    private void getAd() {
        HttpUtils.getBanner(2, new JsonCallback<BaseEntity<List<BannerItem>>>() {
            @Override
            protected void onSuccess(BaseEntity<List<BannerItem>> data) {
                pb_loading.setVisibility(View.GONE);
                rpbGg.setVisibility(View.VISIBLE);
                rpbGg.start();
                if (!data.getData().isEmpty()) {
                    ImageUtils.displayImage(SplashActivity.this, data.getData().get(0).getMedia(),
                            R.drawable.ic_placeholder, ivSplash);
                    ivSplash.setOnClickListener(v -> {
                        if (AppUtils.isUrl(data.getData().get(0).getUrl())) {
                            Uri uri = Uri.parse(data.getData().get(0).getUrl());
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        } else {
                            ToastUtils.showShort("网页链接不正确");
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(this);
        wakeUpAdapter = null;
    }

    @Override
    protected int getContentViewId() {
        return com.huaxin.library.R.layout.activity_splash;
    }

    @Override
    public boolean showTitleView() {
        return false;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        rpbGg.setEnabled(false);
        rpbGg.setOnClickListener(v -> {
            gotoMain();
        });
        rpbGg.setProgressChangeListener(new RoundProgressBar.ProgressChangeListener() {
            @Override
            public void onFinish() {

            }

            @Override
            public void onProgressChanged(int progress) {
                int text = 10 - progress * 10 / 100;
                rpbGg.setCenterText(text + "");
                if (0 < text && text < 6) {
                    rpbGg.setCenterText(text + "(略过)");
                    rpbGg.setEnabled(true);
                } else if (text == 0) {
                    rpbGg.setCenterText("关闭");
                    rpbGg.setEnabled(true);
                }
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    private void gotoMain() {
        ARouter.getInstance().build(ARConstants.MAIN_ACTIVITY).navigation(this, new NavCallback() {
            @Override
            public void onArrival(Postcard postcard) {
                SplashActivity.this.finish();
            }
        });
//        if (requestSuccess) {
//            ARouter.getInstance().build(ARConstants.MAIN_ACTIVITY).navigation(this, new NavCallback() {
//                @Override
//                public void onArrival(Postcard postcard) {
//                    SplashActivity.this.finish();
//                }
//            });
//
//        }

    }
}
