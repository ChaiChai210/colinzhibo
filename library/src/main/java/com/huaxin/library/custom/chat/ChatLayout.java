package com.huaxin.library.custom.chat;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.tencent.qcloud.tim.uikit.modules.chat.C2CChatManagerKit;
import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatInfo;
import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatManagerKit;


public class ChatLayout extends AbsChatLayout {

//    private GroupInfo mGroupInfo;
//    private GroupChatManagerKit mGroupChatManager;
    private C2CChatManagerKit mC2CChatManager;
    private boolean isGroup = false;

    public ChatLayout(Context context) {
        super(context);
    }

    public ChatLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ChatLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setChatInfo(ChatInfo chatInfo) {
        super.setChatInfo(chatInfo);
        if (chatInfo == null) {
            return;
        }
//
//        if (chatInfo.getType() == TIMConversationType.C2C) {
//            isGroup = false;
//        } else {
//            isGroup = true;
//        }

            mC2CChatManager = C2CChatManagerKit.getInstance();
            mC2CChatManager.setCurrentChatInfo(chatInfo);
            loadChatMessages(null);
    }

    @Override
    public ChatManagerKit getChatManager() {
        return mC2CChatManager;
    }

}
