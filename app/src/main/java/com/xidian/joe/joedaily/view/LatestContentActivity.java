package com.xidian.joe.joedaily.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.xidian.joe.joedaily.R;
import com.xidian.joe.joedaily.utils.HttpUtils;
import com.xidian.joe.joedaily.bean.Content;
import com.xidian.joe.joedaily.bean.Story;
import com.xidian.joe.joedaily.presenter.LatestContentPresenter;

/**
 * Created by Administrator on 2016/8/13.
 */
public class LatestContentActivity extends BaseActivity implements ContentView {

    private AppBarLayout mAppBarLayout;
    private WebView mWebView;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private ImageView mImageView;
    private Toolbar mToolbar;
    private Story mStory;
    private LatestContentPresenter mPresenter;
    private Content mContent;
    private FloatingActionButton mFloatingActionButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_content_layout);

        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.news_content_floating_button);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.news_content_app_bar);
        mWebView = (WebView) findViewById(R.id.news_content_web_view);
//        mWebView.setBackgroundColor(Color.BLACK);

        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.news_content_collapsing_toolbar);
        mImageView = (ImageView) findViewById(R.id.news_content_image_view);
        mToolbar = (Toolbar)findViewById(R.id.news_content_toolbar);

        mPresenter = new LatestContentPresenter(this,this);
        mStory = (Story) getIntent().getSerializableExtra(MainActivity.INTENT_TAG);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mCollapsingToolbarLayout.setTitle(mStory.getTitle());
        initWebView();
        mPresenter.getJson(mStory.getId());

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String report = mStory.getTitle() + " \n   此文档标题来自JoeTsai的知乎小报";
                shareIntent.putExtra(Intent.EXTRA_TEXT,report);
                startActivity(shareIntent);
            }
        });
    }

    // 待查询功能设置信息
    private void initWebView() {
        WebSettings webSettings =mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //优先使用缓存
        webSettings.setAppCacheEnabled(true);//设置启动缓存
        webSettings.setDomStorageEnabled(true); // 开启Dom Storage API 功能
        webSettings.setDatabaseEnabled(true); // 开启Database storage 功能

//        webSettings.setUseWideViewPort(true); //将图片调整到适合webView的大小
//        webSettings.setLoadWithOverviewMode(true);  //缩放至屏幕大小
//        webSettings.setDefaultTextEncodingName("utf-8") ;
//        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
//        webSettings.setSupportZoom(false);// 设置是否支持缩放，这里为false，默认为true。
//        webSettings.setBuiltInZoomControls(true);// 设置是否显示缩放工具，默认为false。

        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }

    @Override
    public void showContent(String responseString) {
        Gson gson = new Gson();
        mContent = gson.fromJson(responseString,Content.class);

        if(mContent.getImage() == null){
            mImageView.setImageResource(R.drawable.default_img);
        }else {
            HttpUtils.getImageViewObject(mQueue, mContent.getImage(), mImageView);
        }
        String css;
        if(!mIsLight){
            css = "<link rel=\"stylesheet\" href=\"file:///android_asset/css/news_day.css\" type=\"text/css\">";  // WebView调用assets目录下的本地网页和图片等资源
        }else {
            css = "<link rel=\"stylesheet\" href=\"file:///android_asset/css/news_night.css\" type=\"text/css\">";  // WebView调用assets目录下的本地网页和图片等资源
        }
        String html = "<html><head>" + css + "</head><body>" + mContent.getBody() + "</body></html>";
        html = html.replace("<div class=\"img-place-holder\">", "");
        //baseUrl("x-data://base")指定了你的data参数中数据是以什么地址为基准的
        // data中的数据可能会有超链接或者是image元素，而很多网站的地址都是用的相对路径，
        // 如果没有baseUrl，webview将访问不到这些资源。
        mWebView.loadDataWithBaseURL("x-data://base", html, "text/html", "UTF-8", null);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_mode:
                initBackground();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initBackground() {
//        mWebView.setBackgroundColor((Color.parseColor("#474546")));
    }
}
