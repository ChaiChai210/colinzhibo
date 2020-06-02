package com.huaxin.library.custom.input;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.tencent.qcloud.tim.uikit.modules.message.MessageInfo;
import com.tencent.qcloud.tim.uikit.modules.message.MessageInfoUtil;
import com.tencent.qcloud.tim.uikit.utils.TUIKitLog;

/**
 * 聊天界面
 */

public class InputLayout extends InputLayoutUI implements View.OnClickListener, TextWatcher {

    private static final String TAG = InputLayout.class.getSimpleName();
    private static final int STATE_NONE_INPUT = -1;
    private static final int STATE_SOFT_INPUT = 0;
    private static final int STATE_VOICE_INPUT = 1;
    private static final int STATE_FACE_INPUT = 2;
    private static final int STATE_ACTION_INPUT = 3;
    //    private FaceFragment mFaceFragment;
    private ChatInputHandler mChatInputHandler;
    private MessageHandler mMessageHandler;
    //    private InputMoreFragment mInputMoreFragment;
    private boolean mSendEnable;
    //    private boolean mAudioCancel;
    private int mCurrentState;
    private int mLastMsgLineCount;
    //    private float mStartRecordY;
    private String mInputContent;

    public InputLayout(Context context) {
        super(context);
    }

    public InputLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public InputLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void init() {

        mSendTextButton.setOnClickListener(this);
        mTextInput.addTextChangedListener(this);
        mTextInput.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                showSoftInput();
                return false;
            }
        });
        mTextInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                return false;
            }
        });
        mTextInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return false;
            }
        });


    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mTextInput.removeTextChangedListener(this);
    }


    public void setChatInputHandler(ChatInputHandler handler) {
        this.mChatInputHandler = handler;
    }


    public void setMessageHandler(MessageHandler handler) {
        this.mMessageHandler = handler;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == com.tencent.qcloud.tim.uikit.R.id.send_btn) {
            if (mSendEnable) {
                if (mMessageHandler != null) {
                    mMessageHandler.sendMessage(MessageInfoUtil.buildTextMessage(mTextInput.getText().toString().trim()));
                }
                mTextInput.setText("");
            }
        }
    }

    private void showSoftInput() {
        TUIKitLog.i(TAG, "showSoftInput");
        mTextInput.requestFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mTextInput, 0);
        if (mChatInputHandler != null) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    mChatInputHandler.onInputAreaClick();
                }
            }, 200);
        }
    }

    public void hideSoftInput() {
        TUIKitLog.i(TAG, "hideSoftInput");
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mTextInput.getWindowToken(), 0);
        mTextInput.clearFocus();
    }





    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        mInputContent = s.toString();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (TextUtils.isEmpty(s.toString().trim())) {
            mSendEnable = false;
        } else {
            mSendEnable = true;
            if (mTextInput.getLineCount() != mLastMsgLineCount) {
                mLastMsgLineCount = mTextInput.getLineCount();
                if (mChatInputHandler != null) {
                    mChatInputHandler.onInputAreaClick();
                }
            }
//            if (!TextUtils.equals(mInputContent, mTextInput.getText().toString())) {
//                FaceManager.handlerEmojiText(mTextInput, mTextInput.getText().toString(), true);
//            }
        }
    }


    public interface MessageHandler {
        void sendMessage(MessageInfo msg);
    }

    public interface ChatInputHandler {
        void onInputAreaClick();

    }

}
