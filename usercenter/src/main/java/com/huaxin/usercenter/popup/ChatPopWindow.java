package com.huaxin.usercenter.popup;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.AppUtils;
import com.huaxin.library.entity.ConfigEntity;
import com.huaxin.library.utils.Constant;
import com.huaxin.usercenter.R;
import com.lxj.xpopup.core.CenterPopupView;


public class ChatPopWindow extends CenterPopupView {
    private Context mContext;

    public ChatPopWindow(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.pop_chat_group;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        ConfigEntity configEntity = com.huaxin.library.utils.AppUtils.getConfig();
        findViewById(R.id.btn_cancel).setOnClickListener(v ->
                {
                    dismiss();
                }
        );
        findViewById(R.id.tv_potato).setOnClickListener(v ->
                {
                    openApp(Constant.POTATO_PACKAGE_NAME, configEntity.getFollow_potato_link());
                }
        );
        findViewById(R.id.tv_github).setOnClickListener(v ->
                {
                    openWeb(configEntity.getFollow_github_link());
                }
        );
        findViewById(R.id.tv_weibo).setOnClickListener(v ->
                {
                    openWeb(configEntity.getFollow_weibo_link());
                }
        );
        findViewById(R.id.tv_telegram).setOnClickListener(v ->
                {
                    openApp(Constant.POTATO_PACKAGE_NAME, configEntity.getFollow_telegram_link());
                }
        );


    }

    private void openWeb(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        mContext.startActivity(intent);
    }

    private void openApp(String appname, String url) {
        if (AppUtils.isAppInstalled(appname)) {
            AppUtils.launchApp(appname);
        } else {
            openWeb(Constant.TELEGRAM_URL);
        }
    }


    protected void onShow() {
        super.onShow();
    }
}
