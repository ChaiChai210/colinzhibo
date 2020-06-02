package com.huaxin.library.intercepter;

import android.os.Bundle;
import android.util.Log;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.callback.NavigationCallback;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.LogUtils;
import com.huaxin.library.utils.ARConstants;


public class LoginNavigationCallbackImpl  implements NavigationCallback{
    @Override //找到了
    public void onFound(Postcard postcard) {

    }

    @Override //找不到了
    public void onLost(Postcard postcard) {

    }

    @Override    //跳转成功了
    public void onArrival(Postcard postcard) {

    }

    @Override
    public void onInterrupt(Postcard postcard) {
        String path = postcard.getPath();
        LogUtils.v(path);
        Bundle bundle = postcard.getExtras();
        // 拦截了
        ARouter.getInstance().build(ARConstants.PATH_LOGIN)
                .with(bundle)
                .withString("path", path)
                .navigation();
    }
}
