package com.huaxin.usercenter.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.blankj.utilcode.util.ResourceUtils;
import com.huaxin.library.utils.ImageUtils;
import com.huaxin.usercenter.R;


public class UserHeadView extends RelativeLayout {
    private Context mContext;
    private ImageView head;
    private ImageView gender;
    private ImageView lever;

    public UserHeadView(Context context) {
        this(context, null);
    }

    public UserHeadView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UserHeadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_user_head, this);
        head = view.findViewById(R.id.iv_avatar);
        gender = view.findViewById(R.id.iv_gender);
        lever = view.findViewById(R.id.iv_lever);
    }

    public void setGender(int sex) {
        if (sex == 1) {
            gender.setImageResource(R.drawable.ic_male);
        } else if (sex == 2) {
            gender.setImageResource(R.drawable.user_ic_female);
        }
    }

    public void setHeadUrl(String url) {
        ImageUtils.displayAvatar(mContext, url, head);
    }

    public void setLever(int leverValue) {
        int id = ResourceUtils.getDrawableIdByName("user_lever_" + leverValue);
        lever.setImageResource(id);
    }

}
