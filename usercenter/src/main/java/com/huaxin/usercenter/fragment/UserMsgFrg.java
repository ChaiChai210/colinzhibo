package com.huaxin.usercenter.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.librarybase.base.BaseFragment;
import com.huaxin.library.custom.chat.ConversationLayout;
import com.huaxin.library.utils.Constant;
import com.huaxin.usercenter.R;
import com.huaxin.usercenter.R2;
import com.huaxin.usercenter.activity.ChatActivity;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.mmkv.MMKV;
import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatInfo;
import com.tencent.qcloud.tim.uikit.modules.conversation.base.ConversationInfo;

import butterknife.BindView;

public class UserMsgFrg extends BaseFragment {

    @BindView(R2.id.conversation_layout)
    ConversationLayout conversationLayout;
    @BindView(R2.id.ll_no_data)
    LinearLayout llNoData;

    @Override
    protected void loadData() {

    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        // 从布局文件中获取会话列表面板
        if( MMKV.defaultMMKV().decodeBool(Constant.IM_LOGIN)){
            conversationLayout.setVisibility(View.VISIBLE);
            llNoData.setVisibility(View.GONE);
            // 会话列表面板的默认UI和交互初始化
            conversationLayout.initDefault();
            // 通过API设置ConversataonLayout各种属性的样例，开发者可以打开注释，体验效果
//        ConversationLayoutHelper.customizeConversation(mConversationLayout);
            conversationLayout.getConversationList().setOnItemClickListener((view1, position, conversationInfo) -> {
                //此处为demo的实现逻辑，更根据会话类型跳转到相关界面，开发者可根据自己的应用场景灵活实现
                startChatActivity(conversationInfo);
            });
        }else {
            llNoData.setVisibility(View.VISIBLE);
            conversationLayout.setVisibility(View.GONE);
        }


    }

    private void startChatActivity(ConversationInfo conversationInfo) {
        ChatInfo chatInfo = new ChatInfo();
        chatInfo.setType(conversationInfo.isGroup() ? TIMConversationType.Group : TIMConversationType.C2C);
        chatInfo.setId(conversationInfo.getId());
        chatInfo.setChatName(conversationInfo.getTitle());
        Intent intent = new Intent(mContext, ChatActivity.class);
        intent.putExtra(Constant.CHAT_INFO, chatInfo);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.conversation_fragment1;
    }
}
