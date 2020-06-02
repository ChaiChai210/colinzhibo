package com.huaxin.usercenter.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.librarybase.base.BaseFragment;
import com.example.librarybase.base.BindEventBus;
import com.example.librarybase.base.Event;
import com.example.librarybase.base.EventBusHelper;
import com.huaxin.library.http.BaseEntity;
import com.huaxin.library.http.HttpUtils;
import com.huaxin.library.http.JsonCallback;
import com.huaxin.library.utils.AppUtils;
import com.huaxin.library.utils.Constant;
import com.huaxin.library.utils.EventCode;
import com.huaxin.library.utils.UrlConstants;
import com.huaxin.usercenter.R;
import com.huaxin.usercenter.R2;
import com.huaxin.usercenter.adapter.LongVideoAdapter;
import com.huaxin.usercenter.entity.LongVideoBean;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

@BindEventBus
public class MyVideoFrg extends BaseFragment {
    @BindView(R2.id.rv_content)
    RecyclerView rvContent;
    @BindView(R2.id.tv_choose_all)
    TextView tvChooseAll;
    @BindView(R2.id.tv_delete)
    TextView tvDelete;
    @BindView(R2.id.ll_operation)
    LinearLayout llOperation;

    private LongVideoAdapter videoAdapter;
    private List<LongVideoBean> mData = new ArrayList<>();
    //0是长视频，1是短视频
    private int type = 1;

    private boolean allChoose;


    public static MyVideoFrg newInstance(int pos) {
        Bundle args = new Bundle();
        args.putInt(Constant.KEY_VIDOE_POS, pos);
        MyVideoFrg fragment = new MyVideoFrg();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void loadData() {
        type = getArguments().getInt(Constant.KEY_VIDOE_POS);
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        HashMap params = new HashMap<>();
        params.put("type", type);
        params.put("uid", AppUtils.getUid());
        params.put("mobile_code", AppUtils.getDeviceId(mContext));
        HttpUtils.postData(UrlConstants.MY_COLLECT_VIDEO, params, new JsonCallback<BaseEntity<List<LongVideoBean>>>() {
            @Override
            protected void onSuccess(BaseEntity<List<LongVideoBean>> data) {
                videoAdapter.replaceData(data.getData());
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleEvent(Event<Integer> event) {
        // do something
        if (event.getCode() == EventCode.CODE_EDIT) {
            llOperation.setVisibility(View.VISIBLE);
            videoAdapter.setEdited(true);
        } else if (event.getCode() == EventCode.CODE_FINISH_EDIT) {
            llOperation.setVisibility(View.GONE);
            videoAdapter.setEdited(false);
        } else if (event.getCode() == EventCode.CHOOSE_ALL) {
            if (type == event.getData()) {
                setAllChoose();
            }

        } else if (event.getCode() == EventCode.DELETE) {
            if (type == event.getData()) {
                deleteChoose();
            }
        }
    }

    private void setAllChoose() {
        int size = mData.size();
        allChoose = !allChoose;
        if (allChoose) {
            tvChooseAll.setText("取消全选");
        } else {
            tvChooseAll.setText("全选");
        }
        for (int i = 0; i < size; i++) {
            mData.get(i).setChecked(allChoose);
        }
        videoAdapter.notifyDataSetChanged();
    }

    private void deleteChoose() {
        int size = mData.size();
        List<LongVideoBean> temp = new ArrayList<>();
        StringBuilder deleteChoose = new StringBuilder();
        for (int i = 0; i < size; i++) {
            if (mData.get(i).isChecked()) {
                temp.add(mData.get(i));
                deleteChoose.append(mData.get(i).getId()).append(",");
            }
        }
        HashMap params = new HashMap<>();
        params.put("str_id", deleteChoose);
        params.put("uid", AppUtils.getUid());
        params.put("mobile_code", AppUtils.getDeviceId(mContext));
        HttpUtils.postData(UrlConstants.DELETE_COLLECT_VIDEO, params, new JsonCallback<BaseEntity<List<LongVideoBean>>>() {
            @Override
            protected void onSuccess(BaseEntity<List<LongVideoBean>> data) {
                mData.removeAll(temp);
                videoAdapter.notifyDataSetChanged();
                EventBusHelper.postSticky(new Event(EventCode.REFRESH_VIDEO, ""));
            }
        });

    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {

        rvContent.setLayoutManager(new LinearLayoutManager(mContext));
        videoAdapter = new LongVideoAdapter(mData);
        rvContent.setAdapter(videoAdapter);

    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_collect_video;
    }

    @OnClick({R2.id.tv_choose_all, R2.id.tv_delete})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_choose_all) {
            setAllChoose();
        } else if (id == R.id.tv_delete) {
            deleteChoose();
        }
    }


}
