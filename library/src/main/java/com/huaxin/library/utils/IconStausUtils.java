package com.huaxin.library.utils;

import android.graphics.drawable.Drawable;
import android.widget.TextView;

import com.huaxin.library.R;


/**
 * author : Administrator
 * time   : 2019/01/18
 * desc   :
 * version: 1.0
 */

public class IconStausUtils {

    //点赞
    public static void doLike(TextView view, boolean like) {
        int res;
        if (like) {
            res = R.drawable.icon_liked;
        } else {
            res = R.drawable.icon_like;

        }
        view.setCompoundDrawablesWithIntrinsicBounds(view.getContext().getResources().getDrawable(res), null, null, null);
    }

    public static void setLike(TextView view, boolean like) {
        int res;
        if (!like) {
            res = R.drawable.ic_like_checked;
        } else {
            res = R.drawable.ic_like_normal;

        }
        Drawable icon = view.getContext().getResources().getDrawable(res);

//drawable的左边到textview左边缘+padding的距离，drawable的上边离textview上边缘+padding的距离
//drawable的右边边离textview左边缘+padding的距离，drawable的下边离textview上边缘+padding的距离
        icon.setBounds(0, 0, 25, 25);
        view.setCompoundDrawablesWithIntrinsicBounds(null, icon, null, null);
    }

}
