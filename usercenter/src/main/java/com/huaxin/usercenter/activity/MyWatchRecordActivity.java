package com.huaxin.usercenter.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.example.librarybase.base.BaseActivity;
import com.huaxin.library.db.MyDatabase;
import com.huaxin.library.db.WatchDao;
import com.huaxin.library.db.WatchRecord;
import com.huaxin.library.utils.ARConstants;
import com.huaxin.usercenter.R;
import com.huaxin.usercenter.R2;
import com.huaxin.usercenter.adapter.WatchRecordAdapter;
import com.huaxin.usercenter.entity.WatchListEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Route(path = ARConstants.WATCH_RECORD)
public class MyWatchRecordActivity extends BaseActivity {
    @BindView(R2.id.iv_back)
    ImageView ivBack;
    @BindView(R2.id.tv_edit)
    TextView tvEdit;
    @BindView(R2.id.rl_header)
    RelativeLayout rlHeader;
    @BindView(R2.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R2.id.tv_choose_all)
    TextView tvChooseAll;
    @BindView(R2.id.tv_delete)
    TextView tvDelete;
    @BindView(R2.id.ll_operation)
    LinearLayout llOperation;

    private WatchRecordAdapter watchRecordAdapter;
    private List<WatchListEntity> watchListEntities = new ArrayList<>();
    private List<WatchRecord> today = new ArrayList<>();
    private List<WatchRecord> sevenDays = new ArrayList<>();
    private List<WatchRecord> rest = new ArrayList<>();

    //    private WatchRecordRepository watchRecordRepository;
    private WatchDao watchDao;

    private static final int DAY_TIME = 24 * 3600 * 1000;
    //    private static final int DAY_TIME = 1 * 3600 * 100;
    private static final int SEVEN_DAY = 7 * 24 * 3600 * 1000;

    private boolean allChoose;

    private boolean isEdit;

    @Override
    protected void loadData() {

    }

    @Override
    public boolean showTitleView() {
        return false;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.act_watch_record;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        MyDatabase db = MyDatabase.getInstance(this);
        watchDao = db.watchDao();
        getData();
        watchRecordAdapter = new WatchRecordAdapter(this, watchListEntities);
        recyclerView.setAdapter(watchRecordAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    private void getData() {
        watchListEntities.clear();
        long now = System.currentTimeMillis();
        today = watchDao.getWatchList(now - DAY_TIME, now);
        sevenDays = watchDao.getWatchList(now - SEVEN_DAY, now - DAY_TIME);
        rest = watchDao.getWatchList(now - SEVEN_DAY);
        if (!today.isEmpty()) {
            WatchListEntity item1 = new WatchListEntity();
            item1.setName("今日");
            item1.setData(today);
            watchListEntities.add(item1);
        }
        if (!sevenDays.isEmpty()) {
            WatchListEntity item2 = new WatchListEntity();
            item2.setName("七日");
            item2.setData(sevenDays);
            watchListEntities.add(item2);
        }
        if (!rest.isEmpty()) {
            WatchListEntity item3 = new WatchListEntity();
            item3.setName("更早");
            item3.setData(rest);
            watchListEntities.add(item3);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                watchRecordAdapter.setEdited(true);
//                EventBusHelper.post(new Event(EventCode.CODE_EDIT, -1));
            } else {
                tvEdit.setText("编辑");
                llOperation.setVisibility(View.GONE);
                watchRecordAdapter.setEdited(false);
//                EventBusHelper.post(new Event(EventCode.CODE_FINISH_EDIT, -1));
            }
        } else if (id == R.id.tv_choose_all) {
            setAllChoose();
        } else if (id == R.id.tv_delete) {
            deleteChoose();
        }
    }

    private void deleteChoose() {
        List<Long> temp = new ArrayList<>();
        int size = watchListEntities.size();
        for (int i = 0; i < size; i++) {
            int sizeChild = watchListEntities.get(i).getData().size();
            List<WatchRecord> records = watchListEntities.get(i).getData();
            for (int j = 0; j < sizeChild; j++)
                if (records.get(j).isChecked()) {
                    temp.add(records.get(j).getId());
                }

        }
        //删除数据库
        watchDao.deleteAll(temp);
        getData();
        watchRecordAdapter.notifyDataChanged();
    }


    private void setAllChoose() {
        int size = watchListEntities.size();
        allChoose = !allChoose;
        if (allChoose) {
            tvChooseAll.setText("取消全选");
        } else {
            tvChooseAll.setText("全选");
        }
        for (int i = 0; i < size; i++) {
            int sizeChild = watchListEntities.get(i).getData().size();
            for (int j = 0; j < sizeChild; j++) {
                watchListEntities.get(i).getData().get(j).setChecked(allChoose);
            }
        }
        watchRecordAdapter.notifyDataChanged();
    }
}
