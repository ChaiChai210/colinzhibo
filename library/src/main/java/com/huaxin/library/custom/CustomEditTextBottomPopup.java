package com.huaxin.library.custom;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.StringUtils;
import com.huaxin.library.R;
import com.lxj.xpopup.core.BottomPopupView;

public class CustomEditTextBottomPopup extends BottomPopupView implements TextWatcher, View.OnClickListener {

    private EditText mContent;
    private CommentCallback mListener;
    private ImageButton mIvSend;
    private OnClickListener mOnClickListener;
    //传过来的文字
    private CharSequence text;
    private String hint;

    public CustomEditTextBottomPopup(@NonNull Context context, CharSequence text, String hint) {
        super(context);
        this.text = text;
        this.hint = hint;
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
        mContent.addTextChangedListener(this);
        mContent.setText(text);
        mContent.setSelection(text.length());
        if (!StringUtils.isEmpty(hint)) {
            mContent.setHint("回复 @" + hint + ":");
        }
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

    public void setCallback(CommentCallback listener) {
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

    public interface CommentCallback {
        void onTextChange(String context);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        if (l != null)
            mOnClickListener = l;
    }

    public String getComment() {
        EditText et = findViewById(R.id.et_comment_content);
        return et.getText().toString();
    }
}
