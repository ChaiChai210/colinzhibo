package com.huaxin.usercenter.popup;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.huaxin.usercenter.R;
import com.lxj.xpopup.core.CenterPopupView;


public class ClearCachePopWindow extends CenterPopupView {
    private onConfirm listener;

    public ClearCachePopWindow(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.pop_clear_cache;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        findViewById(R.id.tv_cancel).setOnClickListener(v ->
                dismiss()
        );
        findViewById(R.id.tv_confirm).setOnClickListener(v -> {
            dismiss();
            if (listener != null) {
                listener.onConfirm(v);
            }
        });
    }

    public void setOnConfirm(onConfirm listener) {
        this.listener = listener;
    }

    public interface onConfirm {
        void onConfirm(View view);
    }

    protected void onShow() {
        super.onShow();
    }
}
