package com.huaxin.usercenter.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.example.librarybase.base.BaseActivity;
import com.huaxin.library.custom.LinesEditView;
import com.huaxin.library.http.BaseEntity;
import com.huaxin.library.http.HttpUtils;
import com.huaxin.library.http.JsonCallback;
import com.huaxin.library.utils.ARConstants;
import com.huaxin.library.utils.AppUtils;
import com.huaxin.library.utils.Constant;
import com.huaxin.library.utils.UrlConstants;
import com.huaxin.usercenter.R;
import com.huaxin.usercenter.R2;
import com.huaxin.usercenter.entity.FeedbackType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Route(path = ARConstants.PATH_FEEDBACK)
public class FeedBackActivity extends BaseActivity {
    @BindView(R2.id.sp_type)
    Spinner spType;
    @BindView(R2.id.et_title)
    EditText etTitle;
    @BindView(R2.id.et_input)
    LinesEditView etInput;
    @BindView(R2.id.btn_submit)
    Button btnSubmit;

    private List<FeedbackType> feedbackTypeList = new ArrayList<>();
    private List<String> typeList = new ArrayList<>();

    private int typeId = -1;

    private String title;
    private String content;

    @Override
    protected void loadData() {
        getData();
    }

    private void getData() {
        HashMap params = new HashMap<>();
        HttpUtils.postData(UrlConstants.PERSON_FEEDBACK, params, new JsonCallback<BaseEntity<List<FeedbackType>>>() {
            @Override
            protected void onSuccess(BaseEntity<List<FeedbackType>> data) {
                feedbackTypeList.clear();
                feedbackTypeList.addAll(data.getData());
                addData(data.getData());
            }
        });
    }

    private void addData(List<FeedbackType> data) {
        if (data == null || data.isEmpty()) {
            return;
        }
        for (int i = 0; i < data.size(); i++) {
            typeList.add(data.get(i).getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(FeedBackActivity.this, android.R.layout.simple_spinner_item, typeList);
        spType.setAdapter(adapter);
    }


    @Override
    protected int getContentViewId() {
        return R.layout.act_feedback;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        setTitle("帮助与反馈");
        spType.setSelection(0, true);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);     //设置下拉列表框的下拉选项样式
        spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typeId = feedbackTypeList.get(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick(R2.id.btn_submit)
    public void onViewClicked() {
        submitFeedback();
    }

    private void submitFeedback() {
        title = Objects.requireNonNull(etTitle.getText()).toString().trim();
        content = Objects.requireNonNull(etInput.getContentText());
        if (TextUtils.isEmpty(title)) {
            ToastUtils.showShort("标题不能为空");
            return;
        }
        if (TextUtils.isEmpty(content)) {
            ToastUtils.showShort("请输入描述");
            return;
        }
        HashMap params = new HashMap<>();
        params.put("type", typeId);
        params.put("uid", AppUtils.getUid());
        params.put("title", title);
        params.put("describe", content);
        HttpUtils.postData(UrlConstants.PERSON_ADD_FEEDBACK, params, new JsonCallback<BaseEntity>() {
            @Override
            protected void onSuccess(BaseEntity data) {
                ToastUtils.showShort("提交成功");
                finish();
            }

        });
    }
}
