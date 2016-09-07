package com.xidian.joe.joedaily.support;

import android.app.ActivityManager;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

import java.security.MessageDigest;

/**
 * Created by Administrator on 2016/8/15.
 * 使用图片缓存技术
 */
public class BitmapCache implements ImageLoader.ImageCache {


    //LruCache非常适合用来缓存图片，它的主要算法原理是将最近使用的对象用强引用存储在LinkedHashMap中，并且把
    // 最近最少使用的对象在缓存值达到预设定值之前从内存中移除。
//从API9(Android 2.3)开始，垃圾回收器会更倾向于回收持有软引用或者弱引用的对象，这让软引用和弱引用变得不再可靠；
//同时，在Android3.0中，图片的数据会存储在本地的内存中，因而无法使用一种可预见的方式将其释放，这就有潜在的风险
//    造成应用程序的内存溢出并崩溃OOM。
    private LruCache<String,Bitmap> mBitmapLruCache;

    public BitmapCache(){
        int maxSize = 20*1024*1024;
        mBitmapLruCache= new LruCache<String,Bitmap>(maxSize){
//            the cache size is measured in the number of entries
            @Override
            protected int sizeOf(String key, Bitmap value) {
//                public final int getByteCount() {
//                    // int result permits bitmaps up to 46,340 x 46,340
//                    return getRowBytes() * getHeight();
//                }
                return value.getByteCount();
            }
        };
    }

    @Override
    public Bitmap getBitmap(String s) {
        return mBitmapLruCache.get(s);
    }

    @Override
    public void putBitmap(String s, Bitmap bitmap) {
        mBitmapLruCache.put(s,bitmap);
    }
}
