package com.huaxin.library.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.huaxin.library.R;
import com.kproduce.roundcorners.RoundRelativeLayout;


public class VerifyCodeView extends RoundRelativeLayout {
    private ImageView captcha;
    private EditText verify_code;
    private ImageButton btn_publish;

    public VerifyCodeView(Context context) {
        this(context, null);
    }

    public VerifyCodeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerifyCodeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View root = View.inflate(context, R.layout.layout_verify_code, this);
        captcha = root.findViewById(R.id.iv_captcha);
        verify_code = root.findViewById(R.id.et_verify_code);
        btn_publish = root.findViewById(R.id.ib_publish);
    }

    public void setCaptcha(String url) {
        Glide.with(getContext()).load(url).into(captcha);
    }

    public void setPublishListen(View.OnClickListener listen) {

        btn_publish.setOnClickListener(listen);
    }

    public String getVerify_code() {
        return verify_code.getText().toString().trim();
    }

    public ImageView getCaptcha() {
        return captcha;
    }
}
