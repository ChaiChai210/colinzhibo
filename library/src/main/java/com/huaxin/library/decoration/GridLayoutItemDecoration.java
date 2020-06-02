package com.huaxin.library.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class GridLayoutItemDecoration extends RecyclerView.ItemDecoration {

    private final int mWidth;
    private final int mHeight;
    private Drawable mDriver;
    private RecyclerView.LayoutManager mLayoutManager;
    private int mTagPos = 0;

    // 网上绝大部分用的系统的一个属性 叫做 android.R.attrs.listDriver ，这个也可以，需要在清单文件中配置
    public GridLayoutItemDecoration(Context context, int drawableRescourseId) {
        // 解析获取 Drawable
        mDriver = ContextCompat.getDrawable(context, drawableRescourseId);
        mWidth = mDriver.getIntrinsicWidth();
        mHeight = mDriver.getIntrinsicHeight();
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        // 留出分割线的位置  每个item控件的下边和右边
        if (mLayoutManager == null)
            mLayoutManager = parent.getLayoutManager();
        int childPosition = parent.getChildAdapterPosition(view);
        if (mLayoutManager instanceof GridLayoutManager) {
            if (((GridLayoutManager) mLayoutManager).getSpanSizeLookup().getSpanSize(childPosition) == 1) {
                outRect.top = mHeight / 2;
                outRect.bottom = mHeight / 2;
                if (((GridLayoutManager) mLayoutManager).getSpanSizeLookup().getSpanIndex(childPosition, ((GridLayoutManager) mLayoutManager).getSpanCount()) == 0) {
                    outRect.left = mWidth;
                    outRect.right = mWidth / 2;

                } else if (((GridLayoutManager) mLayoutManager).getSpanSizeLookup().getSpanIndex(childPosition, ((GridLayoutManager) mLayoutManager).getSpanCount()) == ((GridLayoutManager) mLayoutManager).getSpanCount() - 1) {
                    outRect.left = mWidth / 2;
                    outRect.right = mWidth;
                } else {
                    outRect.right = mWidth / 2;
                    outRect.left = mWidth / 2;
                }
            }
        } else if (mLayoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager.LayoutParams params =
                    (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
            /**
             * 根据params.getSpanIndex()来判断左右边确定分割线
             * 第一列设置左边距为space，右边距为space/2  （第二列反之）
             */
            int spanCount = ((StaggeredGridLayoutManager) mLayoutManager).getSpanCount();
            if (params.getSpanIndex() % spanCount == 0) {
                outRect.right = mWidth / 2;
            } else if (params.getSpanIndex() % spanCount == spanCount - 1) {
                outRect.left = mWidth / 2;
            } else {
                outRect.left = mWidth / 2;
                outRect.right = mWidth / 2;
            }
            if(childPosition<spanCount){
                outRect.bottom = mHeight / 2;
            }else{
                outRect.top = mHeight / 2;
                outRect.bottom = mHeight / 2;
            }
        }


    }

    // 绘制分割线
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        // 绘制分割线

        // 绘制水平方向
        // drawHorizontal(c, parent);
        // 绘制垂直方向
        //  drawVertical(c, parent);
    }


    /**
     * 绘制垂直方向的分割线
     *
     * @param c
     * @param parent
     */
    private void drawVertical(Canvas c, RecyclerView parent) {
        if(mLayoutManager instanceof GridLayoutManager){
            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                if (((GridLayoutManager)mLayoutManager).getSpanSizeLookup().getSpanSize(i) == 1) {
                    View childView = parent.getChildAt(i);
                    RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) childView.getLayoutParams();
                    int top = childView.getTop() - params.topMargin;
                    int bottom = childView.getBottom() + params.bottomMargin;
                    int column = i % ((GridLayoutManager)mLayoutManager).getSpanCount(); // 列数
                    int right = 0;
                    int left = 0;
                    if (column == 0) {
                        left = childView.getRight() + params.rightMargin;
                        right = left + mDriver.getIntrinsicWidth();
                        //计算水平分割线的位置
                        mDriver.setBounds(left, top, right, bottom);
                        mDriver.draw(c);
                        left = childView.getLeft() - params.leftMargin - mDriver.getIntrinsicWidth() / 2;
                        right = childView.getLeft() - params.leftMargin;
                        mDriver.setBounds(left, top, right, bottom);
                        mDriver.draw(c);
                    } else {
                        right = childView.getLeft() - params.leftMargin;
                        left = right - mDriver.getIntrinsicWidth();
                        //计算水平分割线的位置
                        mDriver.setBounds(left, top, right, bottom);
                        mDriver.draw(c);
                        left = childView.getRight() + params.rightMargin;
                        right = left + mDriver.getIntrinsicWidth() / 2;
                        mDriver.setBounds(left, top, right, bottom);
                        mDriver.draw(c);
                    }

                }

            }
        }
      
    }


    /**
     * 绘制水平方向的分割线
     *
     * @param c
     * @param parent
     */
    private void drawHorizontal(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) childView.getLayoutParams();

            int left = childView.getLeft() - params.leftMargin;
            int right = childView.getRight() + mDriver.getIntrinsicWidth() + params.rightMargin;
            int top = childView.getBottom() + params.bottomMargin;
            int bottom = top + mDriver.getIntrinsicHeight();

            //计算水平分割线的位置
            mDriver.setBounds(left, top, right, bottom);
            mDriver.draw(c);
        }
    }
}
