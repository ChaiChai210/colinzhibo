package com.huaxin.community.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.example.librarybase.base.BaseActivity;
import com.huaxin.community.R;
import com.huaxin.community.R2;
import com.huaxin.community.adapter.CircleDetailAdapter;
import com.huaxin.community.adapter.CircleLabelAdapter;
import com.huaxin.library.entity.LabelClassify;
import com.huaxin.library.entity.LabelDetail;
import com.huaxin.library.http.BaseEntity;
import com.huaxin.library.http.HttpUtils;
import com.huaxin.library.http.JsonCallback;
import com.huaxin.library.utils.ARConstants;
import com.huaxin.library.utils.Constant;
import com.huaxin.library.utils.UrlConstants;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

@Route(path = ARConstants.PATH_CHOOSE_CIRCLE)
public class ChooseCircleActivity extends BaseActivity implements CircleLabelAdapter.OnLabelClick {
    @BindView(R2.id.rv_label)
    RecyclerView rvLabel;
    @BindView(R2.id.rv_detail)
    RecyclerView rvDetail;

    private List<LabelClassify> mLabels = new ArrayList<>();
    private List<LabelDetail> circleListBeans = new ArrayList<>();
    private CircleLabelAdapter labelAdapter;
    private CircleDetailAdapter detailAdapter;

      private void getCircles() {
        HashMap params = new HashMap<>();
        HttpUtils.postData(UrlConstants.COMMUNITY_CIRCLES, params, new JsonCallback<BaseEntity<List<LabelClassify>>>() {
            @Override
            public void onSuccess(BaseEntity<List<LabelClassify>> data) {
                Logger.d(data.getData());
                mLabels.addAll(data.getData());
                labelAdapter.notifyDataSetChanged();
                if (!data.getData().isEmpty()) {
                    circleListBeans.addAll(data.getData().get(0).getLabelList());
                }
                rvDetail.setAdapter(detailAdapter);

            }
        });
    }

    @Override
    protected void loadData() {
        getCircles();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_choose_circle;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        setTitle("选择圈子");
        rvLabel.setLayoutManager(new LinearLayoutManager(this));
        labelAdapter = new CircleLabelAdapter(mLabels);
        labelAdapter.setOnLabelClick(this);
        labelAdapter.setClickPos(0);
        rvLabel.setAdapter(labelAdapter);

        rvDetail.setLayoutManager(new LinearLayoutManager(this));
        detailAdapter = new CircleDetailAdapter(circleListBeans);
        detailAdapter.setOnItemClickListener((adapter, view, position) -> {
            Intent intent = new Intent();
            intent.putExtra(Constant.EXTRA_CIRCLR_ITEM, circleListBeans.get(position));
            setResult(RESULT_OK, intent);
            finish();
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    public void onLabelClick(int pos) {
        circleListBeans.clear();
        if (mLabels.get(pos).getLabelList() != null) {
            circleListBeans.addAll(mLabels.get(pos).getLabelList());
        }


        detailAdapter.notifyDataSetChanged();
    }
}
