package com.huaxin.usercenter.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huaxin.library.R2;
import com.example.librarybase.base.BaseFragment;
import com.huaxin.library.http.BaseEntity;
import com.huaxin.library.http.HttpUtils;
import com.huaxin.library.http.JsonCallback;
import com.huaxin.library.utils.UrlConstants;
import com.huaxin.usercenter.R;
import com.huaxin.usercenter.adapter.SystemMsgAdapter;
import com.huaxin.usercenter.entity.SystemMsgBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

public class SystemMsgFrg extends BaseFragment {
    @BindView(R2.id.rv_content)
    RecyclerView rvContent;

    private SystemMsgAdapter systemMsgAdapter;
    private List<SystemMsgBean> mData = new ArrayList<>();

    @Override
    protected void loadData() {
        getData();
    }

    private void getData() {
        HashMap params = new HashMap<>();
        params.put("type", 1);
        HttpUtils.postData(UrlConstants.SYSTEM_MSG, params, new JsonCallback<BaseEntity<List<SystemMsgBean>>>() {
            @Override
            protected void onSuccess(BaseEntity<List<SystemMsgBean>> data) {
                systemMsgAdapter.replaceData(data.getData());
            }
        });
    }


    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        rvContent.setLayoutManager(new LinearLayoutManager(mContext));
        systemMsgAdapter = new SystemMsgAdapter(mData);
        rvContent.setAdapter(systemMsgAdapter);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_system_msg;
    }
}
