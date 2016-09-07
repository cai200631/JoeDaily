package com.xidian.joe.joedaily.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.xidian.joe.joedaily.utils.PreferenceUtils;
import com.xidian.joe.joedaily.bean.Story;
import com.xidian.joe.joedaily.bean.ThemeStory;
import com.xidian.joe.joedaily.utils.RequestUtils;

/**
 * Created by Administrator on 2016/8/12.
 */
public abstract class BaseAdapter extends RecyclerView.Adapter<BaseAdapter.BaseViewHolder> {
     static final int ITEM_TYPE_HEADER = 0;
     static final int ITEM_TYPE_ITEM = 1;

     Context mContext;
     LayoutInflater mInflater;
     ThemeStory mThemeStory;
     PreferenceUtils mPreferenceUtils;
     RequestQueue mQueue;
    RequestUtils mRequestUtils;
    boolean mIsLight;


    public onRecyclerViewItemListener mRecyclerViewItemListener;

    public interface onRecyclerViewItemListener {
        void onClick(View view, Story story, int pos);
    }

    public BaseAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mPreferenceUtils = PreferenceUtils.getInstance(mContext);
        mRequestUtils = RequestUtils.getInstance(mContext.getApplicationContext());
        mQueue = mRequestUtils.getRequestQueue();
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? ITEM_TYPE_HEADER : ITEM_TYPE_ITEM;
    }

    public class BaseViewHolder extends RecyclerView.ViewHolder {
        public BaseViewHolder(View itemView) {
            super(itemView);
        }
    }

}
