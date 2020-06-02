package com.huaxin.library.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.LayoutRes;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.example.librarybase.base.BaseActivity;
import com.orhanobut.logger.Logger;

import butterknife.ButterKnife;

/**
 * 模块视图基类
 */
public abstract class BaseViewHolder implements LifecycleObserver{
    protected View mContentView;
    protected ViewGroup mParentView;
    protected String mTag;
    protected Context mContext;

    public BaseViewHolder(Context context, ViewGroup viewGroup) {
        this(context, viewGroup, new Object[0]);

    }

    protected @LayoutRes
    abstract int getLayoutId();

    public BaseViewHolder(Context context, ViewGroup viewGroup, Object... args) {
        mTag = getClass().getSimpleName();
        processArguments(args);
        mContext = context;
        mParentView = viewGroup;
        mContentView = LayoutInflater.from(context).inflate(getLayoutId(), mParentView, false);
        ButterKnife.bind(this,mContentView);
        init();

    }

    public View getContentView() {
        return mContentView;
    }

    protected <T extends View> T findViewById(int res) {
        return mContentView.findViewById(res);
    }

    /***
     * 初始化
     */
    protected abstract void init();

    /**
     * 获取可变传参
     *
     * @param args 可变传参
     */
    protected void processArguments(Object[] args) {

    }

    public void addToParent() {
        if (mParentView != null && mContentView != null) {
            mParentView.addView(mContentView);
        }
    }

    public void removeFromParent() {
        ViewParent parent = mContentView.getParent();
        if (parent != null) {
            ((ViewGroup) parent).removeView(mContentView);
        }
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreate() {
        Logger.d(mTag, "onCreate");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        Logger.d(mTag, "onStart");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        Logger.d(mTag, "onResume");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        Logger.d(mTag, "onPause");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        Logger.d(mTag, "onStop");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        Logger.d(mTag, "onDestroy");
    }

    /**
     * 订阅Activity的生命周期
     */
    public void subscribeActivityLifeCycle() {
        if (mContext instanceof BaseActivity) {
            ((BaseActivity) mContext).getLifecycle().addObserver(this);
        }
    }

    /**
     * 取消订阅Activity的生命周期
     */
    public void unSubscribeActivityLifeCycle() {
        if (mContext instanceof BaseActivity) {
            ((BaseActivity) mContext).getLifecycle().removeObserver(this);
        }
    }
    public void subscribeFragmentLifeCycle(Lifecycle lifecycle){
        if(lifecycle!=null){
            lifecycle.addObserver(this);
        }
    }
    public void  unSubscribeFragmentLifeCycle(Lifecycle lifecycle){
        if(lifecycle!=null){
            lifecycle.addObserver(this);
        }
    }


}
