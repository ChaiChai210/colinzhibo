package com.huaxin.usercenter.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.ToastUtils;
import com.example.lib.QRCodeUtil.QRCodeUtil;
import com.example.librarybase.base.BaseActivity;
import com.gyf.immersionbar.ImmersionBar;
import com.huaxin.library.entity.ConfigEntity;
import com.huaxin.library.entity.LoginEntity;
import com.huaxin.library.utils.ARConstants;
import com.huaxin.library.utils.AppUtils;
import com.huaxin.library.utils.ImageUtils;
import com.huaxin.usercenter.R;
import com.huaxin.usercenter.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

@Route(path = ARConstants.PATH_MY_PROMOTION)
public class MyPromotionActivity extends BaseActivity {


    @BindView(R2.id.iv_finish)
    ImageView ivFinish;
    @BindView(R2.id.tv_scan)
    TextView tvScan;
    @BindView(R2.id.iv_qrcode)
    ImageView ivQrcode;
    @BindView(R2.id.tv_promotion_code_text)
    TextView tvPromotionCodeText;
    @BindView(R2.id.tv_promotion_code)
    TextView tvPromotionCode;
    @BindView(R2.id.tv_website_tip)
    TextView tvWebsiteTip;
    @BindView(R2.id.tv_website)
    TextView tvWebsite;
    @BindView(R2.id.btn_save)
    Button btnSave;
    @BindView(R2.id.btn_copy)
    Button btnCopy;
    @BindView(R2.id.container)
    View mContainer;

    private ConfigEntity config;
    private String downLoadUrl;

    @Override
    protected void loadData() {
        getData();
    }

    private void getData() {

    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        ImmersionBar.with(this).fullScreen(true).init();
    }

    @Override
    public boolean showTitleView() {
        return false;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.act_my_promotion;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        ivFinish.setOnClickListener(v -> finish());
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        LoginEntity entity = AppUtils.getUserInfo();
        String inviteCode = entity.getUser().getInvitation_code();
        tvPromotionCode.setText(inviteCode);
//download.yuyingbaobei.com/index.html/?promo_code=dbie2x1
        config = AppUtils.getConfig();
        downLoadUrl = config.getApp_download() + "/?promo_code=" + inviteCode;
        tvWebsite.setText(config.getMain_url());
        ivQrcode.post(() -> {
            int width = ivQrcode.getWidth();
            ivQrcode.setImageBitmap(QRCodeUtil.createQRCodeBitmap(downLoadUrl, width, bitmap, 0.2f));
        });

        btnCopy.setOnClickListener(v ->
                {
                    AppUtils.copyToClipboard(MyPromotionActivity.this, downLoadUrl);
                    ToastUtils.showShort("已复制到粘贴板");
                }
               );
        btnSave.setOnClickListener(v -> {
            boolean success = ImageUtils.saveImageToGallery(MyPromotionActivity.this, createBitmap(mContainer));
            if (success) {
                ToastUtils.showShort("保存成功，请到图片库分享");
            }
        });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    private Bitmap createBitmap(View view) {
        view.buildDrawingCache();
        return view.getDrawingCache();
    }


}
