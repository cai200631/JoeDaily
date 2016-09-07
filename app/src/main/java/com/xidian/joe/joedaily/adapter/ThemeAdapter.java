package com.xidian.joe.joedaily.adapter;

import android.app.IntentService;
import android.content.Context;
import android.os.AsyncTask;
import android.os.HandlerThread;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xidian.joe.joedaily.R;
import com.xidian.joe.joedaily.utils.HttpUtils;
import com.xidian.joe.joedaily.bean.ThemeStory;
import com.xidian.joe.joedaily.view.MainActivity;

/**
 * Created by Administrator on 2016/8/13.
 * 主题新闻系列
 */

public class ThemeAdapter extends BaseAdapter implements View.OnClickListener {


    public ThemeAdapter(Context context) {
        super(context);
        mThemeStory = new ThemeStory();
    }

    //初次加载时调用
    public void setListStory(ThemeStory story){
        mThemeStory = story;
        notifyDataSetChanged();
    }

    //加载更多时调用
    public void addListStory(ThemeStory story){
        int size = story.getStories().size();
        mThemeStory.getStories().addAll(story.getStories());
    }


    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == ITEM_TYPE_HEADER){
            View view =mInflater.inflate(R.layout.theme_head_layout,parent,false);
            return new ThemeHeadViewHolder(view);
        }else {
            View view = mInflater.inflate(R.layout.theme_body_layout,parent,false);
            return new ThemeBodyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if(position == 0){  //theme页面的抬头部分
            final ThemeHeadViewHolder viewHolder = (ThemeHeadViewHolder) holder;
            viewHolder.mHeadTextView.setText(mThemeStory.getDescription());
            if(mThemeStory.getThemeBackground()== null){
                viewHolder.mHeadImageView.setImageResource(R.drawable.default_img);
            }else {
                HttpUtils.getImageViewObject(mQueue, mThemeStory.getThemeBackground(), viewHolder.mHeadImageView);
            }
        }else {  //主页面部分，cardView
            final ThemeBodyViewHolder viewHolder = (ThemeBodyViewHolder) holder;

            if(mThemeStory.getStories().get(position).getImages() == null){
                viewHolder.mBodyImageView.setVisibility(View.GONE);
            }else {
                if(mThemeStory.getStories().get(position).getImages().size() == 0) {
                    viewHolder.mBodyImageView.setVisibility(View.GONE);
                }else {
                    if(mThemeStory.getStories().get(position).getImages().get(0)== null){
                        viewHolder.mBodyImageView.setImageResource(R.drawable.default_img);
                    }else{
                        HttpUtils.getImageViewObject(mQueue,mThemeStory.getStories()
                                .get(position).getImages().get(0),viewHolder.mBodyImageView);
                    }

                    viewHolder.mBodyImageView.setVisibility(View.VISIBLE);
                }
            }
            viewHolder.itemView.setTag(position);
            viewHolder.mBodyTextView.setText(mThemeStory.getStories().get(position).getTitle());
            viewHolder.itemView.setOnClickListener(this);
            //已阅标识
            if(mPreferenceUtils.isClickItem(String.valueOf(mThemeStory.getStories().get(position).getId()))){
                viewHolder.mBodyTextView.setTextColor(mIsLight? mContext.getResources()
                        .getColor(R.color.clicked_textColor):mContext.getResources().getColor(R.color.textColor));
            }else {
                viewHolder.mBodyTextView.setTextColor(mIsLight? mContext.getResources()
                        .getColor(R.color.textColor):mContext.getResources().getColor(R.color.clicked_textColor));
            }

        }
    }

    @Override
    public int getItemCount() {
        return mThemeStory.getStories().size();
    }

    @Override   //列表项处理点击事件，通过View.onClick()进行处理，关键：获取当前点击的ViewHolder项的内容；
    public void onClick(View v) {
        if(mRecyclerViewItemListener != null){
            int position = (int) v.getTag();  //获取点击的ViewHolder项内容
            mRecyclerViewItemListener.onClick(v,mThemeStory.getStories().get(position),position);
        }
    }

    public void setRecyclerViewItemListener(onRecyclerViewItemListener listener){
        mRecyclerViewItemListener = listener;
    }

    public class ThemeHeadViewHolder extends BaseViewHolder{
        private TextView mHeadTextView;
        private ImageView mHeadImageView;

        public ThemeHeadViewHolder(View itemView) {
            super(itemView);
            mHeadImageView = (ImageView) itemView.findViewById(R.id.theme_head_image_view);
            mHeadTextView =(TextView) itemView.findViewById(R.id.theme_head_text_view);
        }
    }

    public class ThemeBodyViewHolder extends BaseViewHolder{
        private TextView mBodyTextView;
        private ImageView mBodyImageView;

        public ThemeBodyViewHolder(View itemView) {
            super(itemView);
            mBodyImageView = (ImageView) itemView.findViewById(R.id.theme_body_image_view);
            mBodyTextView =(TextView) itemView.findViewById(R.id.theme_body_text_view);
        }
    }

    public void changeLightMode(){
        mIsLight = ((MainActivity) mContext).getLightMode();
        notifyDataSetChanged();
    }

}
