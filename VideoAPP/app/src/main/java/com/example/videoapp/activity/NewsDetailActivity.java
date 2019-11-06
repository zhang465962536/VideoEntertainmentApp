package com.example.videoapp.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.videoapp.R;

import butterknife.Bind;
import butterknife.ButterKnife;
/*
* 新闻详情页界面逻辑  使用webview显示 */
public class NewsDetailActivity extends Activity implements View.OnClickListener {

    @Bind(R.id.iv_title_back)
    ImageView ivTitleBack;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.webview)
    WebView webview;
    @Bind(R.id.pb_webview_loading)
    ProgressBar pb_webview_loading;
    @Bind(R.id.iv_title_share)
    ImageView ivTitleShare;

    private String url;
    private WebSettings webSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        ButterKnife.bind(this);

        ivTitleShare.setVisibility(View.VISIBLE);
        ivTitleBack.setOnClickListener(this);
        ivTitleShare.setOnClickListener(this);
        getData();
    }

    //获取数据
    private void getData() {
        url = getIntent().getStringExtra("url");
        //设置支持javaScript
        webSettings = webview.getSettings();
        //设置支持javaScript
        webSettings.setJavaScriptEnabled(true);
        //设置双击变大变小
        webSettings.setUseWideViewPort(true);
        //增加缩放按钮
        webSettings.setBuiltInZoomControls(true);
        //设置文字大小
//        webSettings.setTextSize(WebSettings.TextSize.NORMAL);
        webSettings.setTextZoom(100);
        //不让从当前网页跳转到系统的浏览器中
        webview.setWebViewClient(new WebViewClient() {
            //当加载页面完成的时候回调
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pb_webview_loading.setVisibility(View.GONE);
            }
        });
        webview.loadUrl(url);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_title_back:
                finish();
                break;

            case R.id.iv_title_share:
                break;
        }
    }
}
