package com.xidian.joe.joedaily.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.xidian.joe.joedaily.R;
import com.xidian.joe.joedaily.support.BitmapCache;

import org.json.JSONObject;

/**
 * Created by Administrator on 2016/8/11.
 */
public class HttpUtils {

    public static void getJsonObject(RequestQueue queue, String url, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener){
        JsonObjectRequest request = new UTFJsonObjectRequest(url,null,listener,errorListener);
        queue.add(request);
    }

    public static void getImageObject(RequestQueue queue, String url, Response.Listener<Bitmap> listener,Response.ErrorListener errorListener){
        ImageRequest imageRequest = new ImageRequest(url,listener,0,0, Bitmap.Config.ARGB_8888,errorListener);
        queue.add(imageRequest);
    }

    public static void getStringObject(RequestQueue queue, String url, Response.Listener<String> listener, Response.ErrorListener errorListener){
        StringRequest request = new UTFStringRequest(url,listener,errorListener);
        queue.add(request);
    }

    public static void getImageViewObject(RequestQueue queue, String url, ImageView imageView){
        ImageLoader imageLoader = new ImageLoader(queue,new BitmapCache());
        ImageLoader.ImageListener imageListener = ImageLoader.getImageListener(imageView, R.drawable.default_img,R.drawable.default_img);
        imageLoader.get(url,imageListener);
    }

    public static boolean isNetworkConnected(Context context){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if(info != null){
            return info.isAvailable();
        }
        return false;
    }

}
