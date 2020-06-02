package com.huaxin.community.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.PhoneUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.example.librarybase.base.BaseActivity;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.huaxin.library.adapter.CommunityAdapter;
import com.huaxin.library.entity.CommunityRecommend;
import com.huaxin.library.R2;
import com.huaxin.library.adapter.HistorySearchAdapter;
import com.huaxin.library.adapter.HotSearchAdapter;
import com.huaxin.library.entity.HotsSarchTag;
import com.huaxin.library.http.BaseEntity;
import com.huaxin.library.http.HttpUtils;
import com.huaxin.library.http.JsonCallback;
import com.huaxin.library.utils.ARConstants;
import com.huaxin.library.utils.AppUtils;
import com.huaxin.library.utils.Constant;
import com.huaxin.library.utils.UrlConstants;
import com.kproduce.roundcorners.RoundRelativeLayout;
import com.orhanobut.logger.Logger;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.xuexiang.xui.widget.statelayout.MultipleStatusView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Route(path = ARConstants.PATH_COMMUNITY_SEARCH)
public class CommunitySearchActivity extends BaseActivity {
    @BindView(R2.id.iv_search)
    ImageView ivSearch;
    @BindView(R2.id.et_search)
    EditText etSearch;
    @BindView(R2.id.iv_delete1)
    ImageView ivDelete1;
    @BindView(R2.id.ll_search)
    RoundRelativeLayout llSearch;
    @BindView(R2.id.tv_cancel)
    TextView tvCancel;
    @BindView(R2.id.delete_iv)
    ImageView deleteIv;
    @BindView(R2.id.rl_history)
    RelativeLayout rlHistory;
    @BindView(R2.id.rv_history)
    RecyclerView rvHistory;
    @BindView(R2.id.rv_hot_search)
    RecyclerView rvHotSearch;
    @BindView(R2.id.ll_search_con)
    LinearLayout llSearchCon;
    @BindView(R2.id.rv_search_result)
    RecyclerView rvSearchResult;
    @BindView(R2.id.srl_common)
    SmartRefreshLayout srlCommon;
    @BindView(R2.id.multiple_status_view)
    MultipleStatusView mMultipleStatusView;

    private HotSearchAdapter hotSearchAdapter;
    private HistorySearchAdapter historySearchAdapter;
    private List<HotsSarchTag> hotSearchBeans = new ArrayList<>();
    private List<String> historySearchList = new ArrayList<>();


    private CommunityAdapter communityRecommentAdapter;
    private List<CommunityRecommend.DataBean> mData = new ArrayList<>();
    private static int PAGE_SIZE = 10;
    private int last_page = 1;
    private int pageNum = 1;

    @Override
    protected void loadData() {
        getSearchTag();
    }



    @Override
    protected int getContentViewId() {
        return com.huaxin.library.R.layout.activity_search;
    }



    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        Set<String> search_history = SPUtils.getInstance().getStringSet(Constant.SP_COMMUNITY_HISTORY_SEARCH);
        if(search_history.isEmpty()){
            rlHistory.setVisibility(View.GONE);
            rvHistory.setVisibility(View.GONE);
        }else {
            rlHistory.setVisibility(View.VISIBLE);
            rvHistory.setVisibility(View.VISIBLE);
        }
        historySearchList.addAll(search_history);
        historySearchAdapter = new HistorySearchAdapter(historySearchList);
        historySearchAdapter.setOnItemClickListener((adapter, view, position) -> {
            doSearch(historySearchList.get(position));
        });
        rvHistory.setLayoutManager(getLayoutManager());
        rvHistory.setAdapter(historySearchAdapter);

        rvHotSearch.setLayoutManager(getLayoutManager());
        hotSearchAdapter = new HotSearchAdapter(hotSearchBeans);
        hotSearchAdapter.setOnItemClickListener((adapter, view, position) -> {
            doSearch(hotSearchBeans.get(position).getName());
        });
        rvHotSearch.setAdapter(hotSearchAdapter);


        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            //这里写事件，返回为true，即为搜索键的事件
            doSearch(v.getText().toString());
            return true;
        });

        srlCommon.setEnableRefresh(false);
        srlCommon.setOnLoadMoreListener(refreshLayout -> {
            if (pageNum < last_page) {
                refreshLayout.finishLoadMore();
                pageNum++;
                getSearchResult(etSearch.getText().toString().trim());
            } else {
                refreshLayout.finishLoadMoreWithNoMoreData();
                ToastUtils.showShort("没有更多数据");
            }
        });
        rvSearchResult.setLayoutManager(new LinearLayoutManager(this));
        communityRecommentAdapter = new CommunityAdapter(mData);
//        communityRecommentAdapter.setCollectListener(this);
        rvSearchResult.setAdapter(communityRecommentAdapter);
    }

    private void doSearch(String textSearch) {
        if (TextUtils.isEmpty(textSearch)) {
            ToastUtils.showShort("请输入搜索内容");
        } else {
            mMultipleStatusView.setVisibility(View.VISIBLE);
            llSearchCon.setVisibility(View.GONE);
            etSearch.setText(textSearch);
            etSearch.setSelection(textSearch.length());
            if (!historySearchList.contains(textSearch)) {
                if (historySearchList.size() == 10) {
                    historySearchList.remove(historySearchList.size() - 1);
                }
                historySearchList.add(0, textSearch);
                historySearchAdapter.notifyItemInserted(0);
            }

            Set<String> searchSet = new HashSet<>(historySearchList);
            SPUtils.getInstance().put(Constant.SP_COMMUNITY_HISTORY_SEARCH, searchSet);

            getSearchResult(textSearch);
        }
    }

    /**
     * 获取搜索结果
     *
     * @param
     */
    private void getSearchResult(String textSearch) {
        HashMap params = new HashMap<>();
        params.put("page_size", PAGE_SIZE);
        params.put("page", pageNum);
        params.put("mobile_code", AppUtils.getDeviceId(this));
        params.put("search_text", textSearch);
        HttpUtils.postData(UrlConstants.GET_COMMNUNITY_RECOMMEND, params, new JsonCallback<BaseEntity<CommunityRecommend>>() {
            @Override
            public void onSuccess(BaseEntity<CommunityRecommend> data) {
                if(data.getData().getData().isEmpty()){
                    mMultipleStatusView.showEmpty();
                }
                last_page = data.getData().getLast_page();
                mData = data.getData().getData();

                if (pageNum == 1) {
                    communityRecommentAdapter.replaceData(mData);
                } else {
                    communityRecommentAdapter.addData(mData);
                }
            }
        });
    }

    private void getSearchTag() {
        HttpUtils.getData(UrlConstants.COMMUNITY_SEARCH_TAG, new JsonCallback<BaseEntity<List<HotsSarchTag>>>() {
            @Override
            public void onSuccess(BaseEntity<List<HotsSarchTag>> data) {
                Logger.d(data.getData());
                hotSearchBeans.addAll(data.getData());
                hotSearchAdapter.notifyDataSetChanged();
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

    @OnClick({R2.id.iv_delete1, R2.id.tv_cancel, R2.id.delete_iv})
    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == com.huaxin.library.R.id.iv_delete1) {
            etSearch.setText("");
            llSearchCon.setVisibility(View.VISIBLE);
            mMultipleStatusView.setVisibility(View.GONE);
        } else if (id == com.huaxin.library.R.id.tv_cancel) {
            finish();
        } else if (id == com.huaxin.library.R.id.delete_iv) {
            SPUtils.getInstance().remove(Constant.SP_COMMUNITY_HISTORY_SEARCH);
            rlHistory.setVisibility(View.GONE);
            rvHistory.setVisibility(View.GONE);
        }
    }
}
