package com.huaxin.library.base;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.example.librarybase.base.BaseActivity;
import com.example.librarybase.base.Event;
import com.fm.openinstall.OpenInstall;
import com.gyf.immersionbar.ImmersionBar;
import com.huaxin.library.R;
import com.huaxin.library.R2;
import com.huaxin.library.custom.LoginCountDownTimer;
import com.huaxin.library.entity.CountryEntity;
import com.huaxin.library.entity.LoginEntity;
import com.huaxin.library.http.BaseEntity;
import com.huaxin.library.http.HttpUtils;
import com.huaxin.library.http.JsonCallback;
import com.example.librarybase.base.BindEventBus;
import com.huaxin.library.utils.ARConstants;
import com.huaxin.library.utils.Constant;
import com.huaxin.library.utils.EventCode;
import com.huaxin.library.utils.GenerateTestUserSig;
import com.huaxin.library.utils.UrlConstants;
import com.tencent.mmkv.MMKV;
import com.tencent.qcloud.tim.uikit.TUIKit;
import com.tencent.qcloud.tim.uikit.base.IUIKitCallBack;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
@BindEventBus
@Route(path = ARConstants.PATH_LOGIN)
public class LoginActivity extends BaseActivity {
    @BindView(R2.id.iv_back)
    ImageButton ivBack;
    @BindView(R2.id.et_number)
    AppCompatEditText etNumber;
    @BindView(R2.id.tv_country_code)
    TextView mCountryCode;
    @BindView(R2.id.et_code)
    AppCompatEditText etCode;
    @BindView(R2.id.btn_get_code)
    Button btnGetCode;
    @BindView(R2.id.btn_login)
    AppCompatButton btnLogin;

    private String phoneNum;
    private String verify_code;
    public String path;
    private String countryCode = "+86";

    MMKV kv;
    @Override
    protected void loadData() {
         kv = MMKV.defaultMMKV();
        path = getIntent().getStringExtra("path");
    }


    @Override
    protected int getContentViewId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        
        ivBack.setOnClickListener(v -> finish());
        phoneNum = Objects.requireNonNull(etNumber.getText()).toString().trim();
        verify_code = Objects.requireNonNull(etCode.getText()).toString().trim();
        mCountryCode.setText(countryCode);
        mCountryCode.setOnClickListener(v -> ARouter.getInstance().build(ARConstants.CHOOSE_COUNTRY_CODE).navigation());
    }

    public void login(View v) {
        phoneNum =etNumber.getText().toString().trim();
        verify_code = Objects.requireNonNull(etCode.getText()).toString().trim();
        if (TextUtils.isEmpty(phoneNum)) {
            ToastUtils.showLong(R.string.hint_number_error);
        } else if (TextUtils.isEmpty(verify_code)) {
            ToastUtils.showLong(R.string.hint_code_error);
        } else {
            doLogin();
        }

    }


    private void doLogin() {
        phoneNum = Objects.requireNonNull(etNumber.getText()).toString().trim();
        verify_code = Objects.requireNonNull(etCode.getText()).toString().trim();

        HttpUtils.login(this, countryCode, phoneNum, verify_code, new JsonCallback<BaseEntity<LoginEntity>>() {
            @Override
            protected void onSuccess(BaseEntity<LoginEntity> data) {
                OpenInstall.reportRegister();
                LoginEntity.UserBean user = data.getData().getUser();
//                String userid = "dd";
                String userSig = GenerateTestUserSig.genTestUserSig(String.valueOf(user.getId()));
                TUIKit.login(String.valueOf(user.getId()), userSig, new IUIKitCallBack() {
                    @Override
                    public void onError(String module, final int code, final String desc) {
                        runOnUiThread(() -> ToastUtil.toastLongMessage("登录失败, errCode = " + code + ", errInfo = " + desc));
                    }

                    @Override
                    public void onSuccess(Object data) {
                        kv.encode(Constant.IM_LOGIN, true);
                    }
                });
                
                kv.encode(Constant.IS_LOGIN, true);
                Log.e("uid", data.getData().getUser().getId() + "");
                kv.encode(Constant.UID, data.getData().getUser().getId());
                kv.encode(Constant.USER_INFO, JSON.toJSONString(data.getData()));
                ToastUtils.showShort("登录成功");
                if (!StringUtils.isEmpty(path)) {
                    ARouter.getInstance().build(path)
                            .with(getIntent().getExtras())
                            .navigation();
                }
                finish();
            }
        });

    }

    public void getCode(View v) {
        phoneNum = etNumber.getText().toString().trim();
        if (!TextUtils.isEmpty(phoneNum)) {
            LoginCountDownTimer mCountDownTimerUtils = new LoginCountDownTimer(btnGetCode, 60000, 1000);
            mCountDownTimerUtils.start();
            getVerifyCode();
        } else {
            ToastUtils.showLong(R.string.hint_number_error);
        }
    }

    private void getVerifyCode() {
        HashMap params = new HashMap<>();
        params.put("mobile", phoneNum);
        HttpUtils.postData(UrlConstants.VERIFY_CODE, params, new JsonCallback<BaseEntity>() {
            @Override
            public void onSuccess(BaseEntity data) {
//                ToastUtils.showShort("请输入验证码");
            }
        });
    }

    @Override
    public boolean showTitleView() {
        return false;
    }

    @Override
    protected void initImmersionBar() {
        ImmersionBar.with(this).fullScreen(true).init();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleEvent(Event<CountryEntity> event) {
        if (event.getCode() == EventCode.CHOOSE_COUNTRY_CODE) {
            countryCode = "+" + event.getData().getPhoneCode();
            mCountryCode.setText(countryCode);
        }

    }
}
