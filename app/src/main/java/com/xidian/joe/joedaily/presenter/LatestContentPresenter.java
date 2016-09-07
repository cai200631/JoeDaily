package com.xidian.joe.joedaily.presenter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.xidian.joe.joedaily.database.WebCacheDbHelper;
import com.xidian.joe.joedaily.utils.HttpUtils;
import com.xidian.joe.joedaily.support.Constants;
import com.xidian.joe.joedaily.utils.RequestUtils;
import com.xidian.joe.joedaily.view.ContentView;

/**
 * Created by Administrator on 2016/8/13.
 */
public class LatestContentPresenter {
    ContentView mContentView;
    Context mContext;
    RequestQueue mRequestQueue;
    WebCacheDbHelper mWebCacheDbHelper;
    RequestUtils mRequestUtils;

    public LatestContentPresenter(ContentView contentView, Context context) {
        mContentView = contentView;
        mContext = context;
        mRequestUtils = RequestUtils.getInstance(context.getApplicationContext());
        mRequestQueue = mRequestUtils.getRequestQueue();
        mWebCacheDbHelper = new WebCacheDbHelper(mContext, 1);
    }

    public void getJson(final int id) {
        if (HttpUtils.isNetworkConnected(mContext)) {
            HttpUtils.getStringObject(mRequestQueue, Constants.NEWS_CONTENT + id, new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    SQLiteDatabase db = mWebCacheDbHelper.getWritableDatabase();
                    s = s.replaceAll("'", "''");
                    db.execSQL("replace into webCache(newsId,json) values(" + id + ",'" + s + "')");
                    db.close();
                    mContentView.showContent(s);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                }
            });
        }else{
            SQLiteDatabase db = mWebCacheDbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("select * from webCache where newsId = " + id,null);
            if(cursor.moveToFirst()){
                String s = cursor.getString(cursor.getColumnIndex("json"));
                mContentView.showContent(s);
            }
            cursor.close();
            db.close();
        }
    }
}
