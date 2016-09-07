package com.xidian.joe.joedaily.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.xidian.joe.joedaily.R;
import com.xidian.joe.joedaily.utils.PreferenceUtils;
import com.xidian.joe.joedaily.utils.RequestUtils;

/**
 * Created by Administrator on 2016/8/23.
 */
public class BaseActivity extends AppCompatActivity {
    RequestQueue mQueue;
    PreferenceUtils mPreferenceUtils;
    RequestUtils mRequestUtils;
    boolean mIsLight;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mQueue = Volley.newRequestQueue(this);
        mPreferenceUtils = PreferenceUtils.getInstance(this);
        mRequestUtils = RequestUtils.getInstance(this.getApplicationContext());
        mQueue = mRequestUtils.getRequestQueue();
        mIsLight = mPreferenceUtils.isLight();  //默认返回true
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
//        menu.getItem(0).setTitle(mIsLight ? "日间模式" : "夜间模式"); //默认toolbar显示夜间模式
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_mode:
                mIsLight = !mIsLight;
                mPreferenceUtils.saveLightState(mIsLight);
                break;
            case R.id.action_setting:
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
