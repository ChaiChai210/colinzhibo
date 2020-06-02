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
import com.huaxin.library.utils.AppUtils;
import com.huaxin.library.utils.UrlConstants;
import com.huaxin.usercenter.R;
import com.huaxin.usercenter.adapter.FollowAdapter;
import com.huaxin.usercenter.entity.FollowBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

public class UserFansFrg extends BaseFragment {
    @BindView(R2.id.rv_content)
    RecyclerView rvContent;
    private List<FollowBean> mData = new ArrayList<>();
    private FollowAdapter adapter;


    @Override
    protected void loadData() {
        HashMap params = new HashMap<>();
        params.put("uid", AppUtils.getUid());
        HttpUtils.postData(UrlConstants.PERSON_FANS, params, new JsonCallback<BaseEntity<List<FollowBean>>>() {
            @Override
            protected void onSuccess(BaseEntity<List<FollowBean>> data) {
                adapter.replaceData(data.getData());
            }
        });
    }


    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        rvContent.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new FollowAdapter(mData);
        rvContent.setAdapter(adapter);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_follow;
    }
}
