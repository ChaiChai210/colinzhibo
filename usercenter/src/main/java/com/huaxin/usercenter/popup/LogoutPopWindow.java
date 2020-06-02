package com.huaxin.usercenter.popup;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.SPUtils;
import com.huaxin.library.base.SplashActivity;
import com.huaxin.library.utils.Constant;
import com.huaxin.usercenter.R;
import com.lxj.xpopup.core.CenterPopupView;
import com.tencent.mmkv.MMKV;


public class LogoutPopWindow extends CenterPopupView {
    private Context mContext;
    MMKV kv;

    public LogoutPopWindow(@NonNull Context context) {
        super(context);
        mContext = context;

    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.pop_logout;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        kv = MMKV.defaultMMKV();
        findViewById(R.id.tv_cancel).setOnClickListener(v ->
                {
                    dismiss();
                    logout();
                }
        );
        findViewById(R.id.tv_confirm).setOnClickListener(v -> dismiss());
    }

    //退出登录
    private void logout() {
        kv.encode(Constant.IS_LOGIN, false);
        kv.encode(Constant.IM_LOGIN, false);
        Intent intent = new Intent(mContext, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    protected void onShow() {
        super.onShow();
    }
}
