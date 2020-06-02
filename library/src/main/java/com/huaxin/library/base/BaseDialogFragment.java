package com.huaxin.library.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.fragment.app.DialogFragment;

/**
 * 弹窗基类
 */
public abstract class BaseDialogFragment extends DialogFragment {
    private View mRootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(getLayoutId(), container, false);
        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        loadData();
    }

    /***
     * 请求获取数据
     */
    protected void loadData() {

    }

    /**
     * 初始化视图
     */
    protected abstract void initView();

    /**
     * 获取布局资源ID
     *
     * @return
     */
    protected abstract @LayoutRes
    int getLayoutId();

    /**
     * 获取风格
     *
     * @return
     */
    protected abstract @StyleRes
    int getDialogStyle();

    protected abstract boolean canCancel();

    protected abstract void setWindowAttributes(Window window);

    protected <T extends View> T findViewById(int id) {
        if (mRootView != null) {
            return mRootView.findViewById(id);
        }
        return null;
    }

}
