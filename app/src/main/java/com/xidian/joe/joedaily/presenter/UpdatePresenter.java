package com.xidian.joe.joedaily.presenter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.xidian.joe.joedaily.database.ListCacheDbHelper;
import com.xidian.joe.joedaily.utils.HttpUtils;
import com.xidian.joe.joedaily.bean.LatestNews;
import com.xidian.joe.joedaily.bean.PastContent;
import com.xidian.joe.joedaily.bean.Story;
import com.xidian.joe.joedaily.bean.Theme;
import com.xidian.joe.joedaily.bean.ThemeStory;
import com.xidian.joe.joedaily.support.Constants;
import com.xidian.joe.joedaily.utils.PreferenceUtils;
import com.xidian.joe.joedaily.utils.RequestUtils;
import com.xidian.joe.joedaily.view.MainView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/12.
 */
public class UpdatePresenter {
    private MainView mMainView;
    private Context mContext;
    private RequestQueue mQueue;
    private RequestUtils mRequestUtils;
    private Gson mGson;

    private LatestNews mLatestNews;
    private String mDate;
    private String mLastDate;
    private ListCacheDbHelper mListCacheDbHelper;
    private PreferenceUtils mPreferenceUtils;

    private boolean isLoading;
    private boolean isLoadingMore;


    public UpdatePresenter(MainView mainView, Context context) {
        mMainView = mainView;
        mContext = context;
        mRequestUtils = RequestUtils.getInstance(context.getApplicationContext());
        mQueue = mRequestUtils.getRequestQueue();
        mGson = new Gson();
        isLoading = false;
        isLoadingMore = false;
        mListCacheDbHelper = new ListCacheDbHelper(mContext, 1);
        mPreferenceUtils = PreferenceUtils.getInstance(mContext);
    }

    // 首次加载
    public void loadFirst() {
        isLoading = true;
        if (HttpUtils.isNetworkConnected(mContext)) {
            HttpUtils.getStringObject(mQueue, Constants.NEWS, new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    SQLiteDatabase database = mListCacheDbHelper.getWritableDatabase();
                    database.execSQL("replace into listCache(date,json) values("
                            + Constants.LATEST_COLUMN + ",'" + s + "')");
                    database.close();
                    parseTodayResponseString(s);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
//                    parseTodayResponseString(new String(mQueue.getCache().get(Constants.NEWS).data));
                }
            });
        } else {
            SQLiteDatabase database = mListCacheDbHelper.getReadableDatabase();
            Cursor cursor = database.rawQuery("select * from listCache where date = " + Constants.LATEST_COLUMN, null);
            if (cursor.moveToFirst()) {
                String jsonStr = cursor.getString(cursor.getColumnIndex("json"));
                parseTodayResponseString(jsonStr);
            } else {
                isLoading = false;
            }
            cursor.close();
            database.close();
        }
    }

    private void parseTodayResponseString(String s) {
        mLatestNews = mGson.fromJson(s, LatestNews.class);  //Gson是如何进行解析的
        mDate = mLatestNews.getDate();
        mLastDate = mDate;
        for (Story story : mLatestNews.getStories()) {
            story.setDate(convertDate(mDate));
        }
        isLoading = false; // 存疑
        mMainView.setRefreshing(false);
        mMainView.setNewsAdapterList(mLatestNews);
    }

    //存疑
    public void loadingMore() {
        isLoadingMore = true;
        if (HttpUtils.isNetworkConnected(mContext)) {
            HttpUtils.getStringObject(mQueue, Constants.PAST_URL + mDate, new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    SQLiteDatabase database = mListCacheDbHelper.getWritableDatabase();
                    database.execSQL("replace into listCache(date,json) values(" + mDate + ",'" + s + "')");
                    database.close();
                    parsePastResponseString(s);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                }
            });

        } else {
            SQLiteDatabase database = mListCacheDbHelper.getReadableDatabase();
            Cursor cursor = database.rawQuery("select * from listCache where date = " + mDate, null);
            if (cursor.moveToFirst()) {
                String s = cursor.getString(cursor.getColumnIndex("json"));
                parsePastResponseString(s);
            } else {
                database.delete("listCache", "date< " + mDate, null);
                isLoadingMore = false;
                Toast.makeText(mContext, "没有更多显示内容了", Toast.LENGTH_SHORT).show();
            }
            cursor.close();
            database.close();
        }
    }

    private void parsePastResponseString(String s) {
        PastContent content = mGson.fromJson(s, PastContent.class);
        mDate = content.getDate();
        for (Story story : content.getStories()) {
            story.setDate(convertDate(mDate));
        }
        isLoadingMore = false;
        mMainView.setRefreshing(false);
        mLatestNews.getStories().addAll(content.getStories());
        mMainView.addToNewsAdapter(content.getStories());
    }

    private String convertDate(String date) {
        String result = date.substring(0, 4);
        result += "年";
        result += date.substring(4, 6);
        result += "月";
        result += date.substring(6, 8);
        return result + "日";
    }

    public void onClick(final String id) {
        if (HttpUtils.isNetworkConnected(mContext)) {
            HttpUtils.getStringObject(mQueue, Constants.THEME_CONTENT + id, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    SQLiteDatabase database = mListCacheDbHelper.getWritableDatabase();
                    database.execSQL("replace into listCache(date,json) values(" + (Constants.BASE_COLUMN +
                            Integer.parseInt(id)) + ",'" + response + "')");
                    database.close();
                    parseThemeResponseJson(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                }
            });
        } else {
            SQLiteDatabase database = mListCacheDbHelper.getReadableDatabase();
            Cursor cursor = database.rawQuery("select * from listCache where date = " + (Constants.BASE_COLUMN +
                    Integer.parseInt(id)), null);
            if (cursor.moveToFirst()) {
                String json = cursor.getString(cursor.getColumnIndex("json"));
                parseThemeResponseJson(json);
            }
            cursor.close();
            database.close();
        }
    }

    private void parseThemeResponseJson(String object) {
        ThemeStory themeStory = mGson.fromJson(object, ThemeStory.class);
        mMainView.setRefreshing(false);
        mMainView.setThemeAdapterList(themeStory);
    }

    public void loadTheme() {   //由于标题不变，可将theme信息放入sharedPreference中；
        if (HttpUtils.isNetworkConnected(mContext)) {
            HttpUtils.getJsonObject(mQueue, Constants.THEME, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject object) {
                    mPreferenceUtils.saveStringToSP(Constants.THEMES, object.toString());
                    parseThemeJson(object);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                }
            });
        } else {
            String json = mPreferenceUtils.getStringFromSP(Constants.THEMES);
            try {
                JSONObject jsonObject = new JSONObject(json);
                parseThemeJson(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void parseThemeJson(JSONObject object) {
        try {
            JSONArray itemArray = object.getJSONArray("others");
            List<Theme> menuItems = new ArrayList<>();
            for (int i = 0; i < itemArray.length(); i++) {
                Theme theme = new Theme();
                JSONObject item = itemArray.getJSONObject(i);
                theme.setId(item.getString("id"));
                theme.setName(item.getString("name"));
                menuItems.add(theme);
                mMainView.setDrawerAdapter(menuItems);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void toHomePage() {
        mMainView.setNewsAdapterList(mLatestNews);
    }
}
