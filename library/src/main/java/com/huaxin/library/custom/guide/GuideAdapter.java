package com.huaxin.library.custom.guide;

import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

public class GuideAdapter extends PagerAdapter {
    private ArrayList<View> views;

    public GuideAdapter(ArrayList<View> views) {
        this.views = views;
    }

    public int getCount() {
        return this.views.size();
    }

    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(this.views.get(position));
    }

    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(this.views.get(position));
        return this.views.get(position);
    }
}
