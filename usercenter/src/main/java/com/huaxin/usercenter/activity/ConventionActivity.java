package com.huaxin.usercenter.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.example.librarybase.base.BaseActivity;
import com.huaxin.library.utils.ARConstants;
import com.huaxin.usercenter.R;
import com.huaxin.usercenter.R2;

import butterknife.BindView;
@Route(path = ARConstants.ConventionActivity)
public class ConventionActivity extends BaseActivity {
    @BindView(R2.id.web_progress)
    ProgressBar mProgressBar;
    @BindView(R2.id.my_web)
    WebView mWebView;
    @Override
    protected void loadData() {

    }

    @Override
    protected int getContentViewId() {
        return R.layout.act_protocol;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        mProgressBar.setMax(100);
        WebSettings settings = mWebView.getSettings();
//        mWebView.loadUrl("file:///android_asset/xieyi.html");
//        mWebView.loadUrl("https://www.baidu.com/");
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportMultipleWindows(true);

        mWebView.setWebViewClient(new WebViewClient());
        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 0) {
                    mProgressBar.setVisibility(View.VISIBLE);
                } else if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mProgressBar.setProgress(newProgress);
                }
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String title = bundle.getString("title");
            String url = bundle.getString("url");
            //设置标题
            //setTitleText(title);
            setTitle(title);
            mWebView.loadUrl(url);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //挂在后台  资源释放
        mWebView.getSettings().setJavaScriptEnabled(false);
    }

    @Override
    protected void onDestroy() {
        mWebView.setVisibility(View.GONE);
        mWebView.destroy();
        super.onDestroy();
    }
}
