package com.huaxin.library.custom;

import android.os.CountDownTimer;
import android.widget.TextView;

import com.huaxin.library.R;

public class LoginCountDownTimer extends CountDownTimer {
    private TextView mTextView;

    public LoginCountDownTimer(TextView textView, long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        this.mTextView = textView;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        mTextView.setClickable(false); //设置不可点击
        mTextView.setText(millisUntilFinished / 1000 + "s");  //设置倒计时时间

    }

    @Override
    public void onFinish() {
        mTextView.setText(R.string.send_sms_code);
        mTextView.setClickable(true);//重新获得点击

    }


}
