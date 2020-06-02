package com.huaxin.library.custom.chat;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.huaxin.library.custom.input.InputLayout;
import com.tencent.qcloud.tim.uikit.component.NoticeLayout;
import com.tencent.qcloud.tim.uikit.component.TitleBarLayout;
import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatInfo;
import com.tencent.qcloud.tim.uikit.modules.chat.layout.message.MessageLayout;
import com.tencent.qcloud.tim.uikit.modules.message.MessageInfo;

public abstract class ChatLayoutUI extends LinearLayout implements IChatLayout {

    protected NoticeLayout mGroupApplyLayout;
    protected View mRecordingGroup;
    protected ImageView mRecordingIcon;
    protected TextView mRecordingTips;
    private TitleBarLayout mTitleBar;
    private MessageLayout mMessageLayout;
    private InputLayout mInputLayout;
    private NoticeLayout mNoticeLayout;
    private ChatInfo mChatInfo;

    public ChatLayoutUI(Context context) {
        super(context);
        initViews();
    }

    public ChatLayoutUI(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public ChatLayoutUI(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    private void initViews() {
        inflate(getContext(), com.tencent.qcloud.tim.uikit.R.layout.chat_layout, this);

        mTitleBar = findViewById(com.tencent.qcloud.tim.uikit.R.id.chat_title_bar);
        mMessageLayout = findViewById(com.tencent.qcloud.tim.uikit.R.id.chat_message_layout);
        mInputLayout = findViewById(com.tencent.qcloud.tim.uikit.R.id.chat_input_layout);
        mRecordingGroup = findViewById(com.tencent.qcloud.tim.uikit.R.id.voice_recording_view);
        mRecordingIcon = findViewById(com.tencent.qcloud.tim.uikit.R.id.recording_icon);
        mRecordingTips = findViewById(com.tencent.qcloud.tim.uikit.R.id.recording_tips);
        mGroupApplyLayout = findViewById(com.tencent.qcloud.tim.uikit.R.id.chat_group_apply_layout);
        mNoticeLayout = findViewById(com.tencent.qcloud.tim.uikit.R.id.chat_notice_layout);

        init();
    }

    protected void init() {

    }

    @Override
    public InputLayout getInputLayout() {
        return mInputLayout;
    }

    @Override
    public MessageLayout getMessageLayout() {
        return mMessageLayout;
    }

    @Override
    public NoticeLayout getNoticeLayout() {
        return mNoticeLayout;
    }

    @Override
    public ChatInfo getChatInfo() {
        return mChatInfo;
    }

    @Override
    public void setChatInfo(ChatInfo chatInfo) {
        mChatInfo = chatInfo;
        if (chatInfo == null) {
            return;
        }
        String chatTitle = chatInfo.getChatName();
        getTitleBar().setTitle(chatTitle, TitleBarLayout.POSITION.MIDDLE);
    }

    @Override
    public void exitChat() {

    }

    @Override
    public void initDefault() {

    }

    @Override
    public void loadMessages() {

    }

    @Override
    public void sendMessage(MessageInfo msg, boolean retry) {

    }

    @Override
    public TitleBarLayout getTitleBar() {
        return mTitleBar;
    }

    @Override
    public void setParentLayout(Object parent) {

    }
}
