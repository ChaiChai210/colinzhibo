package com.huaxin.video.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.ToastUtils;
import com.donkingliang.labels.LabelsView;
import com.example.librarybase.base.BaseActivity;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.huaxin.library.adapter.CircleLabelAdapter;
import com.huaxin.library.entity.LabelClassify;
import com.huaxin.library.entity.LabelDetail;
import com.huaxin.library.http.BaseEntity;
import com.huaxin.library.http.HttpUtils;
import com.huaxin.library.http.JsonCallback;
import com.huaxin.library.utils.ARConstants;
import com.huaxin.library.utils.Constant;
import com.huaxin.library.utils.UrlConstants;
import com.huaxin.video.R;
import com.huaxin.video.R2;
import com.huaxin.video.adapter.VideoChooseLabelAdapter;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

@Route(path = ARConstants.VIDEO_CHOOSE_LABEL)
public class VideoChooseLabelActivity extends BaseActivity implements CircleLabelAdapter.OnLabelClick {
    @BindView(R2.id.iv_back)
    ImageView ivBack;
    @BindView(R2.id.rv_choose_label)
    RecyclerView rvChooseLabel;
    @BindView(R2.id.rv_label)
    RecyclerView rvLabel;
    @BindView(R2.id.labels)
    LabelsView labels;

    private List<LabelClassify> mLabels = new ArrayList<>();
    private CircleLabelAdapter labelAdapter;

    private ArrayList<LabelDetail> mChooseTypes = new ArrayList<>();
    private VideoChooseLabelAdapter chooseLabelAdapter;

    @Override
    protected void loadData() {
        getCircles();
    }

    private void getCircles() {
        HashMap params = new HashMap<>();
        HttpUtils.postData(UrlConstants.COMMUNITY_CIRCLES, params, new JsonCallback<BaseEntity<List<LabelClassify>>>() {
            @Override
            public void onSuccess(BaseEntity<List<LabelClassify>> data) {
                mLabels.addAll(data.getData());
                labelAdapter.notifyDataSetChanged();
                if (!data.getData().isEmpty()) {
                    List<LabelDetail> circleList = mLabels.get(0).getLabelList();
                    labels.setLabels(circleList, (label, position, data1) -> data1.getName());
                }

            }
        });
    }


    @Override
    public void onLabelClick(int pos) {
        if (mLabels.get(pos).getLabelList() != null) {
            List<LabelDetail> circleList = mLabels.get(pos).getLabelList();
            labels.setLabels(circleList, (label, position, data1) -> data1.getName());
        }


    }


    @Override
    protected int getContentViewId() {
        return R.layout.act_video_choose_label;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        setTitle("视频标签");
        ivBack.setOnClickListener(v ->
        {
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra(Constant.EXTRA_CHOOSE_LABELS, mChooseTypes);
            setResult(RESULT_OK, intent);
            finish();
        });
        //传递过来数据
        mChooseTypes = getIntent().getParcelableArrayListExtra(Constant.EXTRA_CHOSEN_LABELS);

        rvLabel.setLayoutManager(new LinearLayoutManager(this));
        labelAdapter = new CircleLabelAdapter(mLabels);
        labelAdapter.setOnLabelClick(this);
        labelAdapter.setClickPos(0);
        rvLabel.setAdapter(labelAdapter);

        rvChooseLabel.setLayoutManager(getLayoutManager());
        chooseLabelAdapter = new VideoChooseLabelAdapter(mChooseTypes);
        rvChooseLabel.setAdapter(chooseLabelAdapter);
//        labels.setOnLabelSelectChangeListener((label, data, isSelect, position) -> {
//            if (isSelect) {
//                if (mChooseTypes.size() > 9) {
//                    ToastUtils.showShort("最多添加10条");
//                    return;
//                }
//                if (mChooseTypes.contains(data)) {
//                    return;
//                }
//                mChooseTypes.add(0, ((LabelDetail) data));
//                chooseLabelAdapter.notifyItemInserted(0);
//            } else {
//
//            }
//        });
        labels.setOnLabelClickListener(new LabelsView.OnLabelClickListener() {
            @Override
            public void onLabelClick(TextView label, Object data, int position) {
                if (mChooseTypes.size() > 9) {
                    ToastUtils.showShort("最多添加10条");
                    return;
                }
                if (mChooseTypes.contains(data)) {
                    ToastUtils.showShort("已经选中");
                    return;
                }
                mChooseTypes.add(0, ((LabelDetail) data));
                chooseLabelAdapter.notifyItemInserted(0);
            }
        });
    }

    private FlexboxLayoutManager getLayoutManager() {
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setAlignItems(AlignItems.STRETCH);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        return layoutManager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    public boolean showTitleView() {
        return false;
    }
}
