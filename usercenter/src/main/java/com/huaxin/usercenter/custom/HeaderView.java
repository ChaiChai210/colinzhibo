package com.huaxin.usercenter.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huaxin.usercenter.R;


public class HeaderView extends LinearLayout {
    private Context mContext;
    private TextView title;
    private ImageView left;
    private ImageView right;

    private String text_title;
    private int color;

    public HeaderView(Context context) {
        this(context, null);
    }

    public HeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.HeaderView);
        text_title = typedArray.getString(R.styleable.HeaderView_hv_title);
        color = typedArray.getColor(R.styleable.HeaderView_hv_color, Color.parseColor("#00000000"));
        typedArray.recycle();
        //
        init();
    }

    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_header_view, this);
        title = view.findViewById(R.id.tv_title);
        title.setText(text_title);
        title.setTextColor(color);

        left = view.findViewById(R.id.iv_left_line);
        right = view.findViewById(R.id.iv_right_line);
        left.setBackgroundColor(color);
        right.setBackgroundColor(color);

    }


}
