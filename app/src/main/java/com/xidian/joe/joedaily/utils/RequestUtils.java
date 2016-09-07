package com.xidian.joe.joedaily.utils;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Administrator on 2016/9/5.
 */
public class RequestUtils {
    private volatile static RequestUtils sRequestUtils = null;
    private Context mContext;
    private RequestQueue mRequestQueue;

    private RequestUtils(Context context) {
        mContext = context;
        mRequestQueue = Volley.newRequestQueue(mContext);
    }

    public static RequestUtils getInstance(Context context) {
        if (sRequestUtils == null) {
            synchronized (RequestUtils.class) {
                if (sRequestUtils == null) {
                    sRequestUtils = new RequestUtils(context);
                }
            }
        }
        return sRequestUtils;
    }

    public  RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

}
