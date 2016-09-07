package com.xidian.joe.joedaily.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.xidian.joe.joedaily.R;
import com.xidian.joe.joedaily.utils.HttpUtils;
import com.xidian.joe.joedaily.bean.TopStory;
import com.xidian.joe.joedaily.utils.RequestUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/12.
 */
public class ScrollViewPager extends FrameLayout implements
        View.OnClickListener, ViewPager.OnPageChangeListener {
    private Context mContext;
    private LinearLayout mLinearLayout;
    private ViewPager mViewPager;
    private TextView mTitleTextView;
    private ImageView mPicImageView;
    private List<View> mNewsViews;
    private List<ImageView> mDotImageViews;
    private List<TopStory> mTopStories;
    private OnItemClickListener mOnItemClickListener;
    private RequestQueue mQueue;
    private RequestUtils mRequestUtils;
    private Handler mHandler;

    private static class MyHandler extends Handler {
        private final WeakReference<ScrollViewPager> mPagerWeakReference;

        private MyHandler(ScrollViewPager pager) {
            mPagerWeakReference = new WeakReference<>(pager);
        }

        @Override
        public void handleMessage(Message msg) {
            ScrollViewPager scrollViewPager = mPagerWeakReference.get();
            if(scrollViewPager != null){
                ViewPager viewPager = scrollViewPager.mViewPager;
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                this.sendEmptyMessageDelayed(0, 4000);
            }
        }
    }

    public interface OnItemClickListener{
        void onItemClick(View v, TopStory story);
    }


    public ScrollViewPager(Context context) {
        this(context,null);
    }

    public ScrollViewPager(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ScrollViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTopStories = new ArrayList<>();
        mContext = context;
        mNewsViews = new ArrayList<>();
        mDotImageViews = new ArrayList<>();
        mHandler = new MyHandler(this);
        mRequestUtils = RequestUtils.getInstance(context.getApplicationContext());
        mQueue = mRequestUtils.getRequestQueue();
    }

    public void setTopStories(List<TopStory> list){
        this.mTopStories = list;
        initUI();
    }

    private void initUI() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dot_layout,this,true);
        mViewPager = (ViewPager) view.findViewById(R.id.dot_view_pager);
        mLinearLayout = (LinearLayout) view.findViewById(R.id.dot_linear_layout);
        mLinearLayout.removeAllViews();
        int storyLen = mTopStories.size();
        for (int i = 0; i < storyLen; i++) {
            ImageView imgView_dot = new ImageView(mContext);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            params.leftMargin = 5;
            params.rightMargin = 5;
            mLinearLayout.addView(imgView_dot,params);
            mDotImageViews.add(imgView_dot);

            View viewPager = LayoutInflater.from(mContext).inflate(R.layout.viewpager_item_layout,null);
            final ImageView imageView = (ImageView) viewPager.findViewById(R.id.viewpager_item_imgPic);
            TextView textView = (TextView) viewPager.findViewById(R.id.viewpager_item_textView);
            if(mTopStories.get(i).getImage()==null){
                imageView.setImageResource(R.drawable.default_img);
            }else{
                HttpUtils.getImageViewObject(mQueue,mTopStories.get(i).getImage(),imageView);
            }
            textView.setText( mTopStories.get(i).getTitle());
            mNewsViews.add(viewPager);
            viewPager.setOnClickListener(this);
            if(i == 0){
                mDotImageViews.get(i).setImageResource(R.drawable.dot_focus);
            }else {
                mDotImageViews.get(i).setImageResource(R.drawable.dot_blur);
            }
        }
        mViewPager.setAdapter(new MyPagerAdapter());
        mViewPager.setCurrentItem(Integer.MAX_VALUE/2 -(Integer.MAX_VALUE/2 %storyLen));
        mViewPager.setOnPageChangeListener(this);

        startPlay();
    }

    private void startPlay() {
        mHandler.sendEmptyMessageDelayed(0,4000);
    }


    @Override
    public void onClick(View v) {
        if(mOnItemClickListener != null){
            TopStory story = mTopStories.get(mViewPager.getCurrentItem()% mTopStories.size());
            mOnItemClickListener.onItemClick(v,story);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {  //选中点为黑色
        int len = mDotImageViews.size();
        for (int i = 0; i < len; i++) {
            if(position%len == i){
                mDotImageViews.get(i).setImageResource(R.drawable.dot_focus);
            }else {
                mDotImageViews.get(i).setImageResource(R.drawable.dot_blur);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void setOnItemClickListener (OnItemClickListener listener){
        this.mOnItemClickListener = listener;
    }

    class MyPagerAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mNewsViews.get(position%mNewsViews.size()));
            return mNewsViews.get(position%mNewsViews.size());
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
