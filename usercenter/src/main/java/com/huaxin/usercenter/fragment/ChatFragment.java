package com.huaxin.usercenter.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.librarybase.base.BaseFragment;
import com.huaxin.library.utils.Constant;
import com.huaxin.usercenter.R;
import com.tencent.qcloud.tim.uikit.component.TitleBarLayout;
import com.tencent.qcloud.tim.uikit.modules.chat.ChatLayout;
import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatInfo;
import com.tencent.qcloud.tim.uikit.modules.chat.layout.message.MessageLayout;
import com.tencent.qcloud.tim.uikit.modules.message.MessageInfo;

public class ChatFragment extends BaseFragment {

    private ChatLayout mChatLayout;
    private TitleBarLayout mTitleBar;
    private ChatInfo mChatInfo;


    @Override
    protected void loadData() {
    }
    @SuppressLint("ResourceAsColor")
    @Override
    protected void initView(View view, Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        mChatInfo = (ChatInfo) bundle.getSerializable(Constant.CHAT_INFO);
        if (mChatInfo == null) {
            return;
        }
        //从布局文件中获取聊天面板组件
        mChatLayout = view.findViewById(R.id.chat_layout);

        //单聊组件的默认UI和交互初始化
        mChatLayout.initDefault();

        /*
         * 需要聊天的基本信息
         */
        mChatLayout.setChatInfo(mChatInfo);

        //获取单聊面板的标题栏
        mTitleBar = mChatLayout.getTitleBar();
        mTitleBar.setLeftIcon(R.drawable.user_back);
//        mTitleBar.getMiddleTitle().setTextColor(R.color.white);
        mTitleBar.getRightGroup().removeAllViews();
        ImageView imageView = new ImageView(mContext);
        imageView.setImageResource(R.drawable.ic_user_live);
//        TextView textView = (new TextView(getActivity()));
//        textView.setText("dfsfsdf");

        mTitleBar.getRightGroup().addView(imageView);

        //单聊面板标记栏返回按钮点击事件，这里需要开发者自行控制
        mTitleBar.setOnLeftClickListener(view1 -> getActivity().finish());
        mChatLayout.getMessageLayout().setOnItemClickListener(new MessageLayout.OnItemClickListener() {
            @Override
            public void onMessageLongClick(View view, int position, MessageInfo messageInfo) {
                //因为adapter中第一条为加载条目，位置需减1
                mChatLayout.getMessageLayout().showItemPopMenu(position - 1, messageInfo, view);
            }

            @Override
            public void onUserIconClick(View view, int position, MessageInfo messageInfo) {
                if (null == messageInfo) {
                    return;
                }
                ChatInfo info = new ChatInfo();
                info.setId(messageInfo.getFromUser());
                // TODO: 2020/2/8
//                Intent intent = new Intent(DemoApplication.instance(), FriendProfileActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.putExtra(TUIKitConstants.ProfileType.CONTENT, info);
//                DemoApplication.instance().startActivity(intent);
            }
        });
    }

    @Override
    protected int getContentViewId() {
        return R.layout.user_frg_chat;
    }
}
