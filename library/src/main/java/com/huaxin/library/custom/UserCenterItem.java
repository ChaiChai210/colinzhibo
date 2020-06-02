package com.huaxin.library.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huaxin.library.R;

/**
 * 用户中心界面展示条目的封装
 */
public class UserCenterItem extends LinearLayout {
    private Context mContext;
    private Drawable mDrawable;
    private ImageView imageView;
    private String title;
    private String text_content;
    private TextView tv_title;
    private TextView content;

    public UserCenterItem(Context context) {
        this(context, null);
    }

    public UserCenterItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UserCenterItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.UserCenterItem);
        mDrawable = typedArray.getDrawable(R.styleable.UserCenterItem_uci_drawable);
        title = typedArray.getString(R.styleable.UserCenterItem_uci_title);
        text_content = typedArray.getString(R.styleable.UserCenterItem_uci_content);
        typedArray.recycle();
        //
        init();
    }

    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_user_center_item, this);
        imageView = view.findViewById(R.id.iv_image);
        imageView.setImageDrawable(mDrawable);
        tv_title = view.findViewById(R.id.tv_title);
        tv_title.setText(title);
        content = view.findViewById(R.id.tv_content);
        content.setText(text_content);
    }

    public void setContent(String text){
        content.setText(text);
    }

}
