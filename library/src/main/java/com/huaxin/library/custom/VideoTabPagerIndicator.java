package com.huaxin.library.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.blankj.utilcode.util.ConvertUtils;
import com.huaxin.library.R;

import net.lucode.hackware.magicindicator.FragmentContainerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.model.PositionData;

import java.util.List;

public class VideoTabPagerIndicator extends View implements IPagerIndicator {
    private final Bitmap mBitmap;
    private final int mHeight;
    private final int mWidth;
    private Drawable mDrawable;
    private RectF mRect = new RectF();
    private List<PositionData> mPositionDataList;
    private int mVerticalPadding;
    private int mHorizontalPadding;
    private Interpolator mStartInterpolator = new LinearInterpolator();
    private Interpolator mEndInterpolator = new LinearInterpolator();
    private Rect mCopyRect = new Rect();
    private final Paint mPaint;

    public VideoTabPagerIndicator(Context context) {
        super(context);
        mDrawable = context.getResources().getDrawable(R.mipmap.ic_video_tab_line);
        mHeight = mDrawable.getIntrinsicHeight();
        mWidth = mDrawable.getIntrinsicWidth();
        mBitmap = ConvertUtils.drawable2Bitmap(mDrawable);
        mPaint = new Paint();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        int width = (int) (mRect.right - mRect.left);
        int left = (int) (mRect.left + (width - mWidth) / 2);
        canvas.drawBitmap(mBitmap, left, (int) mRect.bottom + mHeight, mPaint);
        /*mRect.roundOut(mCopyRect);
        mDrawable.setBounds(mCopyRect);
        mDrawable.draw(canvas);*/
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mPositionDataList == null || mPositionDataList.isEmpty()) {
            return;
        }

        // 计算锚点位置
        PositionData current = FragmentContainerHelper.getImitativePositionData(mPositionDataList, position);
        PositionData next = FragmentContainerHelper.getImitativePositionData(mPositionDataList, position + 1);
        mRect.left = current.mContentLeft - mHorizontalPadding + (next.mContentLeft - current.mContentLeft) * mEndInterpolator.getInterpolation(positionOffset);
        mRect.top = current.mContentTop - mVerticalPadding;
        mRect.right = current.mContentRight + mHorizontalPadding + (next.mContentRight - current.mContentRight) * mStartInterpolator.getInterpolation(positionOffset);
        mRect.bottom = current.mContentBottom + mVerticalPadding;
        invalidate();
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPositionDataProvide(List<PositionData> dataList) {
        mPositionDataList = dataList;
    }


    public int getVerticalPadding() {
        return mVerticalPadding;
    }

    public void setVerticalPadding(int verticalPadding) {
        mVerticalPadding = verticalPadding;
    }

    public int getHorizontalPadding() {
        return mHorizontalPadding;
    }

    public void setHorizontalPadding(int horizontalPadding) {
        mHorizontalPadding = horizontalPadding;
    }

    public Interpolator getStartInterpolator() {
        return mStartInterpolator;
    }

    public void setStartInterpolator(Interpolator startInterpolator) {
        mStartInterpolator = startInterpolator;
        if (mStartInterpolator == null) {
            mStartInterpolator = new LinearInterpolator();
        }
    }

    public Interpolator getEndInterpolator() {
        return mEndInterpolator;
    }

    public void setEndInterpolator(Interpolator endInterpolator) {
        mEndInterpolator = endInterpolator;
        if (mEndInterpolator == null) {
            mEndInterpolator = new LinearInterpolator();
        }
    }
}
