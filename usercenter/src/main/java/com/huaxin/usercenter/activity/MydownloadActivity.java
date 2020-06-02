package com.huaxin.usercenter.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.util.FileUtil;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.FileUtils;
import com.example.librarybase.base.BaseActivity;
import com.huaxin.library.db.Download;
import com.huaxin.library.db.DownloadDao;
import com.huaxin.library.db.MyDatabase;
import com.huaxin.library.db.WatchRecord;
import com.huaxin.library.utils.ARConstants;
import com.huaxin.library.utils.okdownload.DownloadManager;
import com.huaxin.library.utils.okdownload.QueueRecyclerAdapter;
import com.huaxin.usercenter.R;
import com.huaxin.usercenter.R2;
import com.huaxin.usercenter.adapter.DownloadAdapter;
import com.huaxin.usercenter.entity.DownloadListEntity;
import com.xuexiang.xui.widget.statelayout.MultipleStatusView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Route(path = ARConstants.MY_DOWNLOAD)
public class MydownloadActivity extends BaseActivity {
    @BindView(R2.id.iv_back)
    ImageView ivBack;
    @BindView(R2.id.tv_edit)
    TextView tvEdit;
    @BindView(R2.id.tv_choose_all)
    TextView tvChooseAll;
    @BindView(R2.id.tv_delete)
    TextView tvDelete;
    @BindView(R2.id.ll_operation)
    LinearLayout llOperation;

    @BindView(R2.id.multiple_status_view)
    MultipleStatusView multipleStatusView;
    RecyclerView recyclerView;
    private DownloadManager mDownloadManager;
    QueueRecyclerAdapter queueRecyclerAdapter;
    private List<DownloadListEntity> mData = new ArrayList<>();
    private DownloadAdapter downloadAdapter;
    private DownloadDao downloadDao;
    List<Download> downloadList;
    private boolean allChoose;

    private boolean isEdit;
    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
//        ImmersionBar.with(this).keyboardEnable(true).init();
    }
    @Override
    public boolean showTitleView() {
        return false;
    }

    @Override
    protected void loadData() {

    }


    @Override
    protected int getContentViewId() {
        return R.layout.user_act_my_download;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        setTitle("我的下载");
        mDownloadManager = DownloadManager.getInstance();
        downloadDao = MyDatabase.getInstance(this).downloadDao();
        downloadList = downloadDao.getDownloadList();
        if (downloadList.isEmpty()) {
            multipleStatusView.showEmpty();
        }
//        DownloadListEntity item1 = new DownloadListEntity();
//        item1.setName("已缓存");
//        item1.setData(mDownloadManager.getFinishedDownload());
//        DownloadListEntity item2 = new DownloadListEntity();
//        item2.setName("缓存中");
//        item2.setData(mDownloadManager.getUnFinishedDownload());
//        mData.clear();
//        mData.add(item1);
//        mData.add(item2);

        recyclerView = (RecyclerView) multipleStatusView.getContentView();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        queueRecyclerAdapter = new QueueRecyclerAdapter(this, downloadList, mDownloadManager);
//        downloadAdapter = new DownloadAdapter(this, mData);
        recyclerView.setAdapter(queueRecyclerAdapter);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R2.id.iv_back, R2.id.tv_edit, R2.id.tv_choose_all, R2.id.tv_delete})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_back) {
            finish();
        } else if (id == R.id.tv_edit) {
            isEdit = !isEdit;
            if (isEdit) {
                tvEdit.setText("完成");
                llOperation.setVisibility(View.VISIBLE);
                queueRecyclerAdapter.setEdited(true);
            } else {
                tvEdit.setText("编辑");
                llOperation.setVisibility(View.GONE);
                queueRecyclerAdapter.setEdited(false);
            }
        } else if (id == R.id.tv_choose_all) {
            setAllChoose();
        } else if (id == R.id.tv_delete) {
            deleteChoose();
        }
    }
    private void setAllChoose() {
        int size = downloadList.size();
        allChoose = !allChoose;
        if (allChoose) {
            tvChooseAll.setText("取消全选");
        } else {
            tvChooseAll.setText("全选");
        }
        for (int i = 0; i < size; i++) {
            downloadList.get(i).setChecked(allChoose);
        }
        queueRecyclerAdapter.notifyDataSetChanged();
    }

    private void deleteChoose() {
        List<Integer> temp = new ArrayList<>();
        List<Download> downloads = new ArrayList<>();
        int size = downloadList.size();
        for (int i = 0; i < size; i++) {
            if (downloadList.get(i).isChecked()) {
                temp.add(downloadList.get(i).getId());
                downloads.add(downloadList.get(i));
            }

        }
        //删除数据库
        mDownloadManager.deleteFiles(downloads);
        downloadDao.deleteAll(temp);
        downloadList = downloadDao.getDownloadList();
        queueRecyclerAdapter = new QueueRecyclerAdapter(this, downloadList, mDownloadManager);
//        downloadAdapter = new DownloadAdapter(this, mData);
        recyclerView.setAdapter(queueRecyclerAdapter);
    }
}
