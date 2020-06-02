package com.huaxin.main.dialog;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;

import com.huaxin.main.R;
import com.lxj.xpopup.core.BottomPopupView;

public class CustomEditTextBottomPopup extends BottomPopupView implements TextWatcher, View.OnClickListener {

    private EditText mContent;
    private CommentListener mListener;
    private ImageButton mIvSend;
    private View.OnClickListener mOnClickListener;

    public CustomEditTextBottomPopup(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.input_bottom_view;
    }

    @Override
    protected void initPopupContent() {
        super.initPopupContent();
        mIvSend = getPopupImplView().findViewById(R.id.iv_input_send);
        mIvSend.setOnClickListener(this);
        mContent = getPopupImplView().findViewById(R.id.et_comment_content);
        mContent.setVisibility(VISIBLE);
        mContent.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (mListener != null) {
            mListener.onTextChange(mContent.getText().toString().trim());
        }
    }

    public void addCommentListener(CommentListener listener) {
        mListener = listener;
    }

    public void clearText() {
        if (mContent != null) {
            mContent.setText("");
        }
    }

    @Override
    public void onClick(View v) {
        mOnClickListener.onClick(v);
    }

    public interface CommentListener {
        void onTextChange(String context);
    }

    @Override
    public void setOnClickListener(View.OnClickListener l) {
        if (l != null)
            mOnClickListener = l;
    }
}
