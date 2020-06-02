package com.huaxin.library.utils;


import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.launcher.ARouter;

public class FragmentUtils {
    public static Fragment getCommunityFragment() {
        Fragment fragment = (Fragment) ARouter.getInstance().build(RouteUtils.Community_Fragment_Main).navigation();
        return fragment;
    }

    public static Fragment getVideoFragment() {
        Fragment fragment = (Fragment) ARouter.getInstance().build(RouteUtils.Video_Fragment_Main).navigation();
        return fragment;
    }


    public static Fragment getMineFragment() {
        Fragment fragment = (Fragment) ARouter.getInstance().build(RouteUtils.Mine_Fragment_Main).navigation();
        return fragment;
    }

}
