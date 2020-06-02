package com.huaxin.library.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.huaxin.library.R;


public class TitleLayout extends LinearLayout {

    //    private ImageView imageView;
    private TextView mTitle;

    private String title;

    public TitleLayout(Context context) {
        this(context, null);
    }

    public TitleLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.TitleLayout);
        title = typedArray.getString(R.styleable.TitleLayout_title);
        typedArray.recycle();
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.video_layout_title, this);
        mTitle = findViewById(R.id.tv_title);
        mTitle.setText(title);
    }

}
