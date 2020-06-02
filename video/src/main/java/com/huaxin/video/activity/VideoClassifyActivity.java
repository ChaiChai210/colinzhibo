package com.huaxin.video.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.ToastUtils;
import com.example.librarybase.base.BaseActivity;
import com.huaxin.library.decoration.GridLayoutItemDecoration;
import com.huaxin.library.http.BaseEntity;
import com.huaxin.library.http.HttpUtils;
import com.huaxin.library.http.JsonCallback;
import com.huaxin.library.utils.ARConstants;
import com.huaxin.video.R;
import com.huaxin.video.R2;
import com.huaxin.video.adapter.VideoClassifyAdapter;
import com.huaxin.video.adapter.VideoTypeChooseAdapter;
import com.huaxin.video.entity.VideoClassifyDetailEntity;
import com.huaxin.video.entity.VideoTypeEntity;
import com.huaxin.video.views.layoutmanager.CenterLayoutManager;
import com.orhanobut.logger.Logger;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.yqritc.recyclerviewflexibledivider.VerticalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

@Route(path = ARConstants.PATH_VIDEO_ALL_TYPE)
public class VideoClassifyActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {

    @BindView(R2.id.rb_complex)
    RadioButton rbComplex;
    @BindView(R2.id.rb_most_play)
    RadioButton rbMostPlay;
    @BindView(R2.id.rb_latest)
    RadioButton rbLatest;
    @BindView(R2.id.rb_most_like)
    RadioButton rbMostLike;
    @BindView(R2.id.rg_more)
    RadioGroup rgMore;
    @BindView(R2.id.rcv_video_type)
    RecyclerView rcvVideoType;
    @BindView(R2.id.rv_video)
    RecyclerView rvVideo;
    @BindView(R2.id.srl_common)
    SmartRefreshLayout srlCommon;

    private int last_page = 1;
    private int pageNum = 1;
    private int order_by = 1;
    private int mClassId = -1;
    private CenterLayoutManager centerLayoutManager;
    private VideoTypeChooseAdapter mChooseAdapter;
    private List<VideoTypeEntity> mTypeData = new ArrayList<>();

    private StaggeredGridLayoutManager mStaggereManager;
    private VideoClassifyAdapter classifyAdapter;
    private List<VideoClassifyDetailEntity.DataBean> mData = new ArrayList<>();

    @Override
    protected void loadData() {
        getVideoType();
        getClassifyVideo();
    }

    private void getClassifyVideo() {
        Log.e("chia", pageNum + "order_by=" + order_by + "mClassId = " + +mClassId);
        HttpUtils.getVideoByType(pageNum, order_by, mClassId, new JsonCallback<BaseEntity<VideoClassifyDetailEntity>>() {
            @Override
            protected void onSuccess(BaseEntity<VideoClassifyDetailEntity> data) {
                last_page = data.getData().getLast_page();
                mData = data.getData().getData();
                if (pageNum == 1) {
                    classifyAdapter.replaceData(mData);
                } else {
                    classifyAdapter.addData(mData);
                }
            }

        });
    }

    private void getVideoType() {
        HttpUtils.getVideoType(0, new JsonCallback<BaseEntity<List<VideoTypeEntity>>>() {
            @Override
            protected void onSuccess(BaseEntity<List<VideoTypeEntity>> data) {
                mChooseAdapter.replaceData(data.getData());
//                mTypeData.addAll(data.getData());
//                mChooseAdapter.notifyDataSetChanged();
                centerLayoutManager.smoothScrollToPosition(rcvVideoType, new RecyclerView.State(), mChooseAdapter.getChecked());
            }

        });
    }

    @Override
    protected int getContentViewId() {
        return R.layout.video_classify;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        setTitle(R.string.title_all_type);
        mClassId = getIntent().getIntExtra("type", -1);

        rgMore.setOnCheckedChangeListener(this);
//

        centerLayoutManager = new CenterLayoutManager(this, RecyclerView.HORIZONTAL, false);
        rcvVideoType.setLayoutManager(centerLayoutManager);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, RecyclerView.HORIZONTAL);
        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.video_item_divider));
        rcvVideoType.addItemDecoration(new VerticalDividerItemDecoration.Builder(this)
                .color(getResources().getColor(android.R.color.transparent))
                .sizeResId(R.dimen.divider)
                .build());
        mChooseAdapter = new VideoTypeChooseAdapter(mClassId, mTypeData);
        mChooseAdapter.setOnItemClickListener((adapter, view, position) -> {
            centerLayoutManager.smoothScrollToPosition(rcvVideoType, new RecyclerView.State(), position);
            VideoTypeEntity videoTypeEntity = (VideoTypeEntity) adapter.getItem(position);
            mClassId = videoTypeEntity.getId();
            mChooseAdapter.notifyDataSetChanged();
//            pageNum = 1;
//            srlCommon.resetNoMoreData();
//            getClassifyVideo();
            refresh();

        });
        rcvVideoType.setAdapter(mChooseAdapter);

        mStaggereManager = new StaggeredGridLayoutManager(2, RecyclerView.VERTICAL);
        rvVideo.setLayoutManager(mStaggereManager);
        rvVideo.addItemDecoration(new GridLayoutItemDecoration(this, R.drawable.video_item_divider));
        classifyAdapter = new VideoClassifyAdapter(mData);
//        classifyAdapter.setOnItemClickListener(this);
        rvVideo.setAdapter(classifyAdapter);

        srlCommon.setOnRefreshListener(refreshLayout -> {
            pageNum = 1;
            getClassifyVideo();
            refreshLayout.finishRefresh();
        });

        srlCommon.setOnLoadMoreListener(refreshLayout -> {
            Logger.e("page", pageNum);
            if (pageNum < last_page) {
                refreshLayout.finishLoadMore();
                pageNum++;
                getClassifyVideo();
            } else {
                refreshLayout.finishLoadMoreWithNoMoreData();
                ToastUtils.showShort("没有更多数据");
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.rb_complex) {
            order_by = 1;
        } else if (checkedId == R.id.rb_most_play) {
            order_by = 2;
        } else if (checkedId == R.id.rb_latest) {
            order_by = 3;
        } else if (checkedId == R.id.rb_most_like) {
            order_by = 4;
        }
//        pageNum = 1;
//        getClassifyVideo();
        refresh();
    }

    private void refresh()
    {
        srlCommon.autoRefresh();
    }


}
