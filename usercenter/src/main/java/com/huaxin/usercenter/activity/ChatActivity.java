package com.huaxin.usercenter.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.huaxin.library.utils.Constant;
import com.huaxin.usercenter.R;
import com.huaxin.usercenter.fragment.ChatFragment;
import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatInfo;

public class ChatActivity extends IMActivity {
    private static final String TAG = ChatActivity.class.getSimpleName();

    private ChatFragment mChatFragment;
    private ChatInfo mChatInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_act_chat);

        chat(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
//        DemoLog.i(TAG, "onNewIntent");
        super.onNewIntent(intent);
        chat(intent);
    }

    @Override
    protected void onResume() {
//        DemoLog.i(TAG, "onResume");
        super.onResume();
    }

    private void chat(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            finish();
        } else {
            mChatInfo = (ChatInfo) bundle.getSerializable(Constant.CHAT_INFO);
            if (mChatInfo == null) {
                finish();
                return;
            }
            mChatFragment = new ChatFragment();
            mChatFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.empty_view,mChatFragment).commitAllowingStateLoss();
        }
    }


}
