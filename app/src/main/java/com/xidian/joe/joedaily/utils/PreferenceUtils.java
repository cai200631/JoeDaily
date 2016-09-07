package com.xidian.joe.joedaily.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2016/8/12.
 */
public class PreferenceUtils {
    private static final String IS_LIGHT = "com.xidian.joe.joedaily.Light";
    private static final String PRE_FILE = "preference.xml";
    private volatile static PreferenceUtils sPreferenceUtils = null;  //避免指令重排

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    private PreferenceUtils(Context context){
        mSharedPreferences = context.getSharedPreferences(PRE_FILE,Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    public static PreferenceUtils getInstance(Context context){
        if(sPreferenceUtils == null){
            synchronized (PreferenceUtils.class){
                if(sPreferenceUtils == null){
                    sPreferenceUtils = new PreferenceUtils(context);
                }
            }
        }
        return sPreferenceUtils;
    }

    public void saveClickItem(String key, boolean value){
        mEditor.putBoolean(key,value);
        mEditor.apply();
    }

    public boolean isClickItem(String key){
        return mSharedPreferences.getBoolean(key,false);
    }

    public void saveLightState( boolean value){
        mEditor.putBoolean(IS_LIGHT,value);
        mEditor.apply();
    }

    public boolean isLight(){
        return mSharedPreferences.getBoolean(IS_LIGHT,true); //true为日间模式，false为夜间模式
    }

    public void saveStringToSP(String key, String value){
        mEditor.putString(key,value);
        mEditor.apply();
    }

    public String getStringFromSP(String key){
        return mSharedPreferences.getString(key,null);
    }



}
