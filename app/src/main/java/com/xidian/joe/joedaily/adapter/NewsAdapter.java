package com.xidian.joe.joedaily.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xidian.joe.joedaily.R;
import com.xidian.joe.joedaily.utils.HttpUtils;
import com.xidian.joe.joedaily.bean.Story;
import com.xidian.joe.joedaily.bean.TopStory;
import com.xidian.joe.joedaily.view.MainActivity;
import com.xidian.joe.joedaily.view.ScrollViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/12.
 */
public class NewsAdapter extends BaseAdapter implements View.OnClickListener, ScrollViewPager.OnItemClickListener {

    private List<TopStory> mTopStories;
    private List<Story> mStories;
    private String mLastDate;
    private onViewPagerItemClickListener mViewPagerItemClickListener;

    public interface onViewPagerItemClickListener{
        void onPageItemClick(View view, TopStory story);
    }

    public NewsAdapter(Context context) {
        super(context);
        this.mTopStories = new ArrayList<>();
        this.mStories = new ArrayList<>();
        mIsLight = mPreferenceUtils.isLight();
    }

    public void addList(List<Story> list){
        int size = mStories.size();
        mStories.addAll(size,list);
        notifyItemRangeInserted(size, list.size());
    }

    public void setRecyclerViewItemListenerListener(onRecyclerViewItemListener listener){
        mRecyclerViewItemListener = listener;
    }
    //Head栏中展示的新闻的点击事件
    public void setViewPagerItemClickListenerListener(onViewPagerItemClickListener listener){
        mViewPagerItemClickListener = listener;
    }

    public void setStoriesList(List<Story> list){
        mStories.clear();
        mStories.addAll(list);
        notifyDataSetChanged();
    }

    public void setTopStoriesList(List<TopStory> list){
        mTopStories.clear();
        mTopStories.addAll(list);      // BUG
        notifyDataSetChanged();
    }


    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if(viewType == ITEM_TYPE_HEADER){
            view = mInflater.inflate(R.layout.item_head_layout,parent,false);
            return new NewsHeadViewHolder(view);
        }else {
            view = mInflater.inflate(R.layout.item_body_layout,parent,false);
            return new NewsBodyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if(position == 0){
            NewsHeadViewHolder headHolder = (NewsHeadViewHolder) holder;
            headHolder.mScrollViewPager.setTopStories(mTopStories);
            headHolder.mScrollViewPager.setOnItemClickListener(this);
            headHolder.mScrollViewPager.setBackgroundColor(mContext.getResources().getColor(mIsLight ?
                    R.color.light_menu_listview_background : R.color.dark_menu_listview_background));
        }else {
            final NewsBodyViewHolder bodyHolder = (NewsBodyViewHolder) holder;
            // 是否已阅
            if(mPreferenceUtils.isClickItem(String.valueOf(mStories.get(position).getId()))){
                bodyHolder.mTitleTextView.setTextColor(mIsLight? mContext.getResources()
                        .getColor(R.color.clicked_textColor):mContext.getResources().getColor(R.color.textColor));
            }else {
                bodyHolder.mTitleTextView.setTextColor(mIsLight? mContext.getResources()
                        .getColor(R.color.textColor):mContext.getResources().getColor(R.color.clicked_textColor));
            }
            //显示标题
            bodyHolder.mTitleTextView.setText(mStories.get(position).getTitle());

            //显示图片
            if(mStories.get(position).getImages().get(0) == null){
                bodyHolder.mPicImageView.setVisibility(View.GONE);
            }else {

                if(mStories.get(position).getImages().get(0) ==null){
                    bodyHolder.mPicImageView.setImageResource(R.drawable.default_img);
                }else {
                    HttpUtils.getImageViewObject(mQueue,mStories.get(position).getImages().get(0),
                            bodyHolder.mPicImageView);
                }
                bodyHolder.itemView.setTag(position);
                if(position == 1){
                    bodyHolder.mTimeTextView.setVisibility(View.VISIBLE);
                    bodyHolder.mTimeTextView.setText(mContext.getResources().getText(R.string.item_news_hotpots));
                    bodyHolder.mTimeTextView.setTextColor(mContext.getResources().getColor(mIsLight?
                            android.R.color.black:android.R.color.white));
                    mLastDate = mStories.get(position).getDate();
                }else {
                    if(mLastDate.equals(mStories.get(position).getDate())){
                        bodyHolder.mTimeTextView.setVisibility(View.GONE);
                    }else {
                        bodyHolder.mTimeTextView.setVisibility(View.VISIBLE);
                        bodyHolder.mTimeTextView.setText(mStories.get(position).getDate());
                        bodyHolder.mTimeTextView.setTextColor(mContext.getResources().getColor(mIsLight?
                                android.R.color.black:android.R.color.white));
                        mLastDate = mStories.get(position).getDate();
                    }
                }

                ((LinearLayout) bodyHolder.mTitleTextView.getParent().getParent().getParent()).setBackgroundColor(mContext.getResources().getColor(mIsLight ?
                        R.color.light_menu_listview_background : R.color.dark_menu_listview_background));
                ((CardView) bodyHolder.mTitleTextView.getParent().getParent()).setCardBackgroundColor(mContext.getResources().getColor(mIsLight ?
                        R.color.light_menu_listview_background : R.color.dark_menu_listview_background));

                bodyHolder.itemView.setOnClickListener(this);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mStories.size();
    }

    @Override
    public void onClick(View v) {
        if(mRecyclerViewItemListener != null){
            int position = (int) v.getTag();
            if(position != 0){
                mRecyclerViewItemListener.onClick(v,mStories.get(position),position);
            }
        }
    }


    @Override
    public void onItemClick(View v, TopStory story) {
        if(mViewPagerItemClickListener!= null){
            mViewPagerItemClickListener.onPageItemClick(v,story);
        }
    }

    public class NewsBodyViewHolder extends BaseViewHolder{
        private TextView mTimeTextView;
        private TextView mTitleTextView;
        private ImageView mPicImageView;


        public NewsBodyViewHolder(View itemView) {
            super(itemView);
            mTimeTextView =(TextView) itemView.findViewById(R.id.item_body_time_textView);
            mTitleTextView =(TextView) itemView.findViewById(R.id.item_body_title_text_view);
            mPicImageView = (ImageView) itemView.findViewById(R.id.item_body_image_view);
        }
    }

    public class NewsHeadViewHolder extends BaseViewHolder{
        private ScrollViewPager mScrollViewPager;

        public NewsHeadViewHolder(View itemView) {
            super(itemView);
            mScrollViewPager = (ScrollViewPager) itemView.findViewById(R.id.item_head_scroll_viewPager);
        }
    }

    public void changeLightMode(){
        mIsLight = ((MainActivity) mContext).getLightMode();
        notifyDataSetChanged();
    }

}
