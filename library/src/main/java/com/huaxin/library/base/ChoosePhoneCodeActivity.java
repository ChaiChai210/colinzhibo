package com.huaxin.library.base;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.colin.indexview.IndexStickyView;
import com.colin.indexview.adapter.IndexHeaderFooterAdapter;
import com.colin.indexview.adapter.IndexStickyViewAdapter;
import com.colin.indexview.listener.OnItemClickListener;
import com.example.librarybase.base.BaseActivity;
import com.example.librarybase.base.Event;
import com.example.librarybase.base.EventBusHelper;
import com.google.gson.reflect.TypeToken;
import com.huaxin.library.R;
import com.huaxin.library.R2;
import com.huaxin.library.custom.IndexStickyViewDecoration;
import com.huaxin.library.entity.CountryEntity;
import com.huaxin.library.utils.ARConstants;
import com.huaxin.library.utils.EventCode;
import com.huaxin.library.utils.JsonUtil;
import com.huaxin.library.utils.ResourceUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

@Route(path = ARConstants.CHOOSE_COUNTRY_CODE)
public class ChoosePhoneCodeActivity extends BaseActivity implements OnItemClickListener<CountryEntity> {
    @BindView(R2.id.indexStickyView)
    IndexStickyView mIndexStickyView;
    @BindView(R2.id.et_search)
    EditText etSearch;
    @BindView(R2.id.iv_delete1)
    ImageView ivDelete1;
    private List<CountryEntity> mCountryList = new ArrayList<>();
    private CityAdapter mAdapter;

    @Override
    protected void loadData() {

    }

    @Override
    protected int getContentViewId() {
        return R.layout.act_choose_country_code;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        setTitle("国家或地区");
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            //这里写事件，返回为true，即为搜索键的事件
            doSearch(v.getText().toString());
            return true;
        });
        ivDelete1.setOnClickListener(v -> {
                    etSearch.setText("");
                    mAdapter = new CityAdapter(mCountryList);
                    mIndexStickyView.setAdapter(mAdapter);
                    addHeader();
                    KeyboardUtils.hideSoftInput(ChoosePhoneCodeActivity.this);
                }
        );

        mIndexStickyView.addItemDecoration(new IndexStickyViewDecoration(this));
        mCountryList = initCitys();
        mAdapter = new CityAdapter(mCountryList);
        mAdapter.setOnItemClickListener(this);
        mIndexStickyView.setAdapter(mAdapter);
        addHeader();

    }

    private void addHeader() {
        IndexHeaderFooterAdapter<CountryEntity> hotCityHeaderAdapter = new IndexHeaderFooterAdapter<CountryEntity>(
                "热", "热门城市", initHotCitys()
        ) {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {

                View view = LayoutInflater.from(ChoosePhoneCodeActivity.this).inflate(R.layout.indexsticky_item_country, parent, false);
                return new CityViewHolder(view);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, CountryEntity itemData) {
                CityViewHolder cityViewHolder = (CityViewHolder) holder;
                cityViewHolder.code.setText("+" + itemData.getPhoneCode());
                cityViewHolder.country.setText(itemData.getCountryName());
            }
        };
        hotCityHeaderAdapter.setOnItemClickListener(this);
        mIndexStickyView.addIndexHeaderAdapter(hotCityHeaderAdapter);
    }

    private void doSearch(String textSearch) {
        if (TextUtils.isEmpty(textSearch)) {
            ToastUtils.showShort("请输入搜索内容");
        } else {
            etSearch.setText(textSearch);
            getSearchResult(textSearch);
        }
    }

    private void getSearchResult(String textSearch) {
        List<CountryEntity> result = new ArrayList<>();
        for (CountryEntity item : mCountryList) {
            if (item.getCountryName().contains(textSearch)) {
                result.add(item);
            }
        }
//        mCountryList.clear();
//        mCountryList.addAll(result);
        mAdapter = new CityAdapter(result);
        mAdapter.setOnItemClickListener(this);
        mIndexStickyView.setAdapter(mAdapter);
    }

    List<CountryEntity> initHotCitys() {
        List<CountryEntity> list = new ArrayList<>();
        list.add(new CountryEntity("中国", "86"));
        list.add(new CountryEntity("中国香港", "852"));
        list.add(new CountryEntity("中国澳门", "853"));
        list.add(new CountryEntity("中国台湾", "886"));
        list.add(new CountryEntity("美国", "1"));
        list.add(new CountryEntity("澳大利亚", "61"));
        list.add(new CountryEntity("马来西亚", "60"));
        list.add(new CountryEntity("菲律宾", "63"));
        list.add(new CountryEntity("泰国", "66"));
        return list;
    }

    private List<CountryEntity> initCitys() {
        return JsonUtil.fromJson(ResourceUtils.readStringFromAssert("CountryCode.json"), new TypeToken<List<CountryEntity>>() {
        }.getType());
        // 初始化数据
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }


    class CityAdapter extends IndexStickyViewAdapter<CountryEntity> {
        public CityAdapter(List<CountryEntity> originalList) {
            super(originalList);
        }

        @Override
        public RecyclerView.ViewHolder onCreateIndexViewHolder(ViewGroup parent) {

            View view = LayoutInflater.from(ChoosePhoneCodeActivity.this).inflate(R.layout.indexsticky_item_index, parent, false);
            return new IndexViewHolder(view);
        }

        @Override
        public RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent) {

            View view = LayoutInflater.from(ChoosePhoneCodeActivity.this).inflate(R.layout.indexsticky_item_country, parent, false);
            return new CityViewHolder(view);
        }

        @Override
        public void onBindIndexViewHolder(RecyclerView.ViewHolder holder, int position, String indexName) {

            IndexViewHolder indexViewHolder = (IndexViewHolder) holder;
            indexViewHolder.mTextView.setText(indexName);
        }

        @Override
        public void onBindContentViewHolder(RecyclerView.ViewHolder holder, int position, CountryEntity itemData) {

            CityViewHolder cityViewHolder = (CityViewHolder) holder;
            cityViewHolder.code.setText(String.format("+%s", itemData.getPhoneCode()));
            cityViewHolder.country.setText(itemData.getCountryName());
        }
    }

    class IndexViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;

        public IndexViewHolder(View itemView) {

            super(itemView);
            mTextView = itemView.findViewById(R.id.tv_index);
        }
    }

    class CityViewHolder extends RecyclerView.ViewHolder {
        TextView code;
        TextView country;

        public CityViewHolder(View itemView) {

            super(itemView);
            code = itemView.findViewById(R.id.tv_country_code);
            country = itemView.findViewById(R.id.tv_country_name);

        }
    }

    @Override
    public void onItemClick(View childView, int position, CountryEntity item) {
        EventBusHelper.post(new Event(EventCode.CHOOSE_COUNTRY_CODE, item));
        finish();
    }
}
