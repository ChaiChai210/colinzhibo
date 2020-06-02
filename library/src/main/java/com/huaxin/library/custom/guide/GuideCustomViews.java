package com.huaxin.library.custom.guide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.huaxin.library.R;

import java.util.ArrayList;

public class GuideCustomViews extends FrameLayout implements ViewPager.OnPageChangeListener {
    private Context mContext;
    private ViewPager viewPager;
    private LinearLayout pointContent;
    private View rootView;
    private int pageSize;
    private ArrayList<View> mPageViews;
    private ArrayList<ImageView> mPointView;
    private Bitmap selectPoint;
    private Bitmap unselectPoint;
    private CallBack callBack;
    private float screenDensity;

    public GuideCustomViews(@NonNull Context context) {
        this(context, null);
    }

    public GuideCustomViews(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GuideCustomViews(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initView(context);
    }

    private void initView(Context context) {
        this.mContext = context;
        this.mPageViews = new ArrayList<>();
        this.mPointView =new ArrayList<>();
        this.rootView = LayoutInflater.from(context).inflate(R.layout.view_guide, this, true);
        this.viewPager = this.rootView.findViewById(R.id.guide_viewpager);
        this.pointContent = this.rootView.findViewById(R.id.point_content);
    }

    public void setData(int[] pageImages, int[] guidePoint, CallBack callBack) {
        this.callBack = callBack;
        LayoutParams mParams = new LayoutParams(-1, -1);
        this.pageSize = pageImages.length;

        for(int i = 0; i < this.pageSize; ++i) {
            ImageView iv = new ImageView(this.mContext);
            iv.setLayoutParams(mParams);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            iv.setImageResource(pageImages[i]);
            this.mPageViews.add(iv);
        }

        this.viewPager.setAdapter(new GuideAdapter(this.mPageViews));
        this.viewPager.addOnPageChangeListener(this);
        this.initPointViews(guidePoint);
    }

    private void initPointViews(int[] guidePoint) {
        this.selectPoint = BitmapFactory.decodeResource(this.getResources(), guidePoint[0]);
        this.unselectPoint = BitmapFactory.decodeResource(this.getResources(), guidePoint[1]);
        this.mPointView =new ArrayList<>();
        this.pointContent.removeAllViews();
        LayoutParams mParams = new LayoutParams(-2, -2);
        mParams.setMargins(this.dip2px(7), 0, 0, 0);

        for(int i = 0; i < this.pageSize; ++i) {
            ImageView iv = new ImageView(this.mContext);
            iv.setLayoutParams(mParams);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            iv.setImageBitmap(this.unselectPoint);
            this.mPointView.add(iv);
            this.pointContent.addView(iv);
        }

        this.switchHighlightPoint(0);
    }

    private void switchHighlightPoint(int index) {
        if (index >= 0 && index <= this.pageSize - 1) {
            int size = this.mPointView.size();

            for(int i = 0; i < size; ++i) {
                if (index == i) {
                    this.mPointView.get(i).setImageBitmap(this.selectPoint);
                } else {
                    this.mPointView.get(i).setImageBitmap(this.unselectPoint);
                }
            }

        }
    }

    public int dip2px(int dipValue) {
        if (dipValue == 0) {
            return 0;
        } else {
            float scale = this.getScreenDensity();
            return (int)((float)dipValue * scale + 0.5F);
        }
    }

    public float getScreenDensity() {
        if (this.screenDensity != 0.0F) {
            return this.screenDensity;
        } else {
            DisplayMetrics dm = this.mContext.getResources().getDisplayMetrics();
            this.screenDensity = dm.density;
            return this.screenDensity;
        }
    }

    public void onPageScrolled(int i, float v, int i1) {
    }

    public void onPageSelected(int i) {
        this.switchHighlightPoint(i);
        if (this.callBack != null) {
            this.callBack.callSlidingPosition(i);
            if (i == this.pageSize - 1) {
                this.mPageViews.get(this.pageSize - 1).setOnClickListener(v ->
                       GuideCustomViews.this.callBack.onClickLastListener());
                this.callBack.callSlidingLast();
            }
        }

    }

    public void onPageScrollStateChanged(int i) {
    }

    public void clear() {
        this.pageSize = 0;
        this.clearPageViews();
        this.clearPointView();
        this.clearBitmap();
    }

    private void clearPageViews() {
        if (null != this.mPageViews) {
            int size = this.mPageViews.size();

            for(int i = 0; i < size; ++i) {
                this.mPageViews.get(i).setBackgroundResource(0);
            }

            this.mPageViews.clear();
        }

        this.mPageViews = null;
    }

    private void clearPointView() {
        if (null != this.mPointView) {
            int size = this.mPointView.size();

            for(int i = 0; i < size; ++i) {
                this.mPointView.get(i).setBackgroundResource(0);
            }

            this.mPointView.clear();
        }

        this.mPointView = null;
    }

    private void clearBitmap() {
        if (!this.selectPoint.isRecycled()) {
            this.selectPoint.recycle();
            this.selectPoint = null;
        }

        if (!this.unselectPoint.isRecycled()) {
            this.unselectPoint.recycle();
            this.unselectPoint = null;
        }

    }

}
