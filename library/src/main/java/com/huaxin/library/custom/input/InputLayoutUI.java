package com.huaxin.library.custom.input;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.huaxin.library.R;

abstract class InputLayoutUI extends LinearLayout implements IInputLayout {

    //    protected static final int CAPTURE = 1;
//    protected static final int AUDIO_RECORD = 2;
//    protected static final int VIDEO_RECORD = 3;
//    protected static final int SEND_PHOTO = 4;
//    protected static final int SEND_FILE = 5;
    private static String TAG = InputLayoutUI.class.getSimpleName();


    /**
     * 消息发送按钮
     */
    protected ImageView mSendTextButton;


    /**
     * 文本输入框
     */
    protected EditText mTextInput;

    protected Activity mActivity;

    public InputLayoutUI(Context context) {
        super(context);
        initViews();
    }

    public InputLayoutUI(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public InputLayoutUI(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    private void initViews() {
        mActivity = (Activity) getContext();
        inflate(mActivity, R.layout.library_chat_input, this);
//        mShortcutArea = findViewById(R.id.shortcut_area);
        mSendTextButton = findViewById(R.id.send_btn);
        mTextInput = findViewById(R.id.chat_message_input);

        // 子类实现所有的事件处理
        init();
    }


    protected abstract void init();


    @Override
    public EditText getInputText() {
        return mTextInput;
    }


}
