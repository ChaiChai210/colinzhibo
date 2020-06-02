package com.huaxin.library.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huaxin.library.R;

public class SettingView extends RelativeLayout {
    private Context mContext;
    private TextView left_title;

    private String text_title;

    public SettingView(Context context) {

        this(context, null);
    }

    public SettingView(Context context, AttributeSet attrs) {

        this(context, attrs, 0);
    }

    public SettingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.SettingView);
        text_title = typedArray.getString(R.styleable.SettingView_sv_title);
        typedArray.recycle();
        //
        init();
    }

    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_setting, this);
        left_title = view.findViewById(R.id.tv_left);
        left_title.setText(text_title);
    }

}
