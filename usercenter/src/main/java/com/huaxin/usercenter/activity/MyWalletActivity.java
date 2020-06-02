package com.huaxin.usercenter.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.ToastUtils;
import com.example.librarybase.base.BaseActivity;
import com.huaxin.library.http.BaseEntity;
import com.huaxin.library.http.HttpUtils;
import com.huaxin.library.http.JsonCallback;
import com.huaxin.library.utils.ARConstants;
import com.huaxin.library.utils.AppUtils;
import com.huaxin.library.utils.UrlConstants;
import com.huaxin.usercenter.R;
import com.huaxin.usercenter.R2;
import com.huaxin.usercenter.adapter.ChargerAdapter;
import com.huaxin.usercenter.adapter.PayTypeAdapter;
import com.huaxin.usercenter.entity.PayBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

@Route(path = ARConstants.PATH_MyWalletActivity)
public class MyWalletActivity extends BaseActivity {
    @BindView(R2.id.tv_diamond_count)
    TextView tvDiamondCount;
    @BindView(R2.id.rv_pay_type)
    RecyclerView rvPayType;
    @BindView(R2.id.gv_charge)
    GridView gvCharge;
    @BindView(R2.id.btn_submit)
    ImageButton btnSubmit;
    @BindView(R2.id.tv_money)
    TextView tvMoney;
    private PayTypeAdapter payTypeAdapter;
    private List<PayBean.PayListBean> payTypeList = new ArrayList<>();
    private PayBean.PayListBean payListBean;
    private List<PayBean.PayListBean.ChargeRulesBean> payRuleList = new ArrayList<>();
    private PayBean.PayListBean.ChargeRulesBean chargeRuleListBean;

    private ChargerAdapter chargerAdapter;

    @Override
    protected void loadData() {
        getData();
    }

    private void getData() {
        HashMap params = new HashMap<>();
        params.put("uid", AppUtils.getUid());
        HttpUtils.postData(UrlConstants.PAY_LIST, params, new JsonCallback<BaseEntity<PayBean>>() {
            @Override
            protected void onSuccess(BaseEntity<PayBean> data) {
                if(data.getData().getPay_list().isEmpty()){
                 return;
                }
                payTypeList.clear();
                payTypeList.addAll(data.getData().getPay_list());
                setPayType(0);
            }
        });
    }


    private void getChargeRule(int pos) {
        payRuleList.clear();
        payRuleList.addAll(payTypeList.get(pos).getCharge_rules());
        if(!payRuleList.isEmpty()){
            chargerAdapter.notifyDataSetChanged();
            tvMoney.setText(payRuleList.get(pos).getMoney());
        }else {
            //后台不能为空数据的
            chargerAdapter.notifyDataSetChanged();
            tvMoney.setText(0);
        }

    }

    private void setPayType(int position) {
        payTypeAdapter.setClickPos(position);
        payListBean = payTypeList.get(position);
        getChargeRule(position);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.layout_my_wallet;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        setTitle("我的钱包");
        payTypeAdapter = new PayTypeAdapter(payTypeList);
        payTypeAdapter.setOnItemClickListener((adapter, view, position) -> {
            setPayType(position);
        });
        rvPayType.setLayoutManager(new LinearLayoutManager(this));
        rvPayType.setAdapter(payTypeAdapter);
        rvPayType.setNestedScrollingEnabled(false);


//        rechargeAdapter = new RechargeAdapter(this,clickPos,payRuleList) ;
        chargerAdapter = new ChargerAdapter(this, R.layout.item_recharge, payRuleList);
        gvCharge.setAdapter(chargerAdapter);
        gvCharge.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gvCharge.setOnItemClickListener((parent, view, position, id) -> {
            chargerAdapter.setPosition(position);
            chargeRuleListBean = payRuleList.get(position);
            tvMoney.setText(chargeRuleListBean.getMoney());
        });
    }

    public void submitMoney(View view) {
        if (payListBean == null) {
            ToastUtils.showShort("支付方式不存在");
            return;
        }

        String url = UrlConstants.PAY + "?" + "coin_id=" + payListBean.getId() + "&uid=" + AppUtils.getUid() + "&pay_id=" + payListBean.getId();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        this.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
