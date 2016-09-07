package com.xidian.joe.joedaily.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.xidian.joe.joedaily.R;
import com.xidian.joe.joedaily.utils.ToolbarUtils;
import com.xidian.joe.joedaily.adapter.BaseAdapter;
import com.xidian.joe.joedaily.adapter.DrawerAdapter;
import com.xidian.joe.joedaily.adapter.NewsAdapter;
import com.xidian.joe.joedaily.adapter.ThemeAdapter;
import com.xidian.joe.joedaily.bean.LatestNews;
import com.xidian.joe.joedaily.bean.Story;
import com.xidian.joe.joedaily.bean.Theme;
import com.xidian.joe.joedaily.bean.ThemeStory;
import com.xidian.joe.joedaily.bean.TopStory;
import com.xidian.joe.joedaily.presenter.UpdatePresenter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements MainView, SwipeRefreshLayout.OnRefreshListener {
    public static final String INTENT_TAG = "com.xidian.joe.joedaily.story";

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private RecyclerView mNewsRecycleView;
    private RecyclerView mDrawerRecycleView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private LinearLayout mDrawerLinearLayout;
    private LinearLayout mContentLinearLayout;
    private TextView mTextView;
    private TextView mHelloTextView;
    private MenuItem mMenuItem;

    private UpdatePresenter mUpdatePresenter;
    private List<Theme> mThemeMenuItems;


    private NewsAdapter mNewsAdapter;
    private DrawerAdapter mDrawerAdapter;
    private ThemeAdapter mThemeAdapter;

    private long mStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);
        //以用来查询当前应用的Heap Size阈值，这个方法会返回一个整数，表明你的应用的Heap Size阈值是多少Mb(megabates)。
//        ActivityManager manager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
//        System.out.println("Max heap size is " + manager.getMemoryClass());
        mToolbar = (Toolbar) findViewById(R.id.main_activity_content_toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_activity_drawer_layout);
        mNewsRecycleView = (RecyclerView) findViewById(R.id.main_activity_content_recycler_view);
        mHelloTextView =(TextView) findViewById(R.id.main_activity_drawer_say_hello);
        mDrawerRecycleView = (RecyclerView) findViewById(R.id.main_activity_drawer_recycler_view);
        mDrawerLinearLayout = (LinearLayout) findViewById(R.id.main_activity_drawer_linear_layout);
        mContentLinearLayout =(LinearLayout) findViewById(R.id.main_activity_content_linear_layout);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.main_activity_content_swipe_refresh);
        mTextView = (TextView) findViewById(R.id.main_activity_drawer_text_view);

        mNewsAdapter = new NewsAdapter(MainActivity.this);
        mThemeAdapter = new ThemeAdapter(MainActivity.this);

        initBackground();

        mUpdatePresenter = new UpdatePresenter(this, this);
        mUpdatePresenter.loadFirst();

        mThemeMenuItems = new ArrayList<>();
        ToolbarUtils.initToolbar(this, mToolbar, "首页");
        initRecycleView();
        initDrawerLayout();
        initSwipeRefresh();

        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawers();
                mUpdatePresenter.toHomePage();
                mToolbar.setTitle(getResources().getString(R.string.menu_layout_firstPage));
            }
        });
    }

    public  boolean getLightMode(){
        return mIsLight;
    }

    private void initBackground() {
        mToolbar.setBackgroundColor(getResources().getColor(mIsLight ?      //toolbar 颜色
                android.R.color.holo_blue_dark : R.color.dark_toolbar));
        mDrawerLinearLayout.setBackgroundColor(getResources().getColor(mIsLight ?
                android.R.color.holo_blue_dark : R.color.dark_drawer));
        mDrawerRecycleView.setBackgroundColor(getResources().getColor(mIsLight ?
                R.color.light_menu_listview_background : R.color.dark_menu_listview_background));
        mTextView.setTextColor((getResources().getColor(mIsLight ?
                R.color.light_menu_header_tv : R.color.dark_menu_header_tv)));
        mHelloTextView.setTextColor((getResources().getColor(mIsLight ?
                R.color.light_menu_header_tv : R.color.dark_menu_header_tv)));
        mTextView.setBackgroundColor(getResources().getColor(mIsLight ?
                R.color.light_menu_listview_background : R.color.dark_menu_listview_background));
        mNewsAdapter.changeLightMode();
        mThemeAdapter.changeLightMode();
    }

    private void initSwipeRefresh() {
//        实现刷新时动态条的颜色；
        mSwipeRefreshLayout.setColorSchemeColors(android.R.color.white,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
//        处理刷新操作?
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    private void initDrawerLayout() {
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
    }
    // onPostCreate方法是指onCreate方法彻底执行完毕的回调，onPostResume类似，这两个方法官方说法是一般不会重写，
    // 现在知道的做法也就只有在使用ActionBarDrawerToggle的使用在onPostCreate?要在屏幕旋转时同步下状�??.

    /**
     * Synchronize the state of the drawer indicator/affordance with the linked DrawerLayout.
     * <p>
     * This should be called from your <code>Activity</code>'s
     * { link Activity#onPostCreate(android.os.Bundle) onPostCreate} method to synchronize after
     * the DrawerLayout's instance state has been restored, and any other time when the state
     * may have diverged in such a way that the ActionBarDrawerToggle was not notified.
     * (For example, if you stop forwarding appropriate drawer events for a period of time.)</p>
     */
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mActionBarDrawerToggle.syncState();
    }

    /**
     * This method should always be called by your <code>Activity</code>'s
     * {link Activity#onConfigurationChanged(android.content.res.Configuration)
     * onConfigurationChanged}
     * method.
     **/
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * This method should be called by your <code>Activity</code>'s
     * {link Activity#onOptionsItemSelected(android.view.MenuItem) onOptionsItemSelected} method.
     * If it returns true, your <code>onOptionsItemSelected</code> method should return true and
     * skip further processing.
     **/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mActionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_mode:
                initBackground();
                break;
            case R.id.action_setting:
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("确定删除缓存？")
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                    //删除缓存
                                        mQueue.getCache().clear();
                                        Toast.makeText(MainActivity.this, "缓存删除完毕", Toast.LENGTH_SHORT).show();
                                    }
                                }).setNegativeButton("取消", null).create()
                        .show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initRecycleView() {
        initNewsRecycleView();
        initDrawerRecycleView();
    }

    private void initDrawerRecycleView() {
        mDrawerRecycleView.setLayoutManager(new LinearLayoutManager(MainActivity.this,
                LinearLayoutManager.VERTICAL, false));
        mDrawerRecycleView.setItemAnimator(new DefaultItemAnimator());
    }

    private void initNewsRecycleView() {

        final LinearLayoutManager manager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
        mNewsRecycleView.setLayoutManager(manager);
        mNewsRecycleView.setItemAnimator(new DefaultItemAnimator()); //负责添加、删除数据时的动画效�??

        mThemeAdapter.setRecyclerViewItemListener(new BaseAdapter.onRecyclerViewItemListener() {
            @Override
            public void onClick(View view, Story story, int pos) {
                Toast.makeText(MainActivity.this, story.getDate(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, LatestContentActivity.class);
                intent.putExtra(INTENT_TAG, story);
                startActivity(intent);
            }
        });

        mNewsAdapter.setViewPagerItemClickListenerListener(new NewsAdapter.onViewPagerItemClickListener() {
            @Override
            public void onPageItemClick(View view, TopStory story) {
                Story storyItem = new Story();
                storyItem.setId(story.getId());
                storyItem.setTitle(story.getTitle());
                List<String> images = new ArrayList<>();
                images.add(story.getImage());
                //处理已阅读的效果
                mPreferenceUtils.saveClickItem(String.valueOf(story.getId()), true);

                Intent intent = new Intent(MainActivity.this, LatestContentActivity.class);
                intent.putExtra(INTENT_TAG, storyItem);
                startActivity(intent);
            }
        });

        mNewsAdapter.setRecyclerViewItemListenerListener(new BaseAdapter.onRecyclerViewItemListener() {
            @Override
            public void onClick(View view, Story story, int pos) {
                mPreferenceUtils.saveClickItem(String.valueOf(story.getId()), true);
                Intent intent = new Intent(MainActivity.this, LatestContentActivity.class);
                intent.putExtra(INTENT_TAG, story);
                startActivity(intent);
                mNewsAdapter.notifyItemRangeChanged(pos, 1); //
            }
        });

        mNewsRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int visibleItemCount = manager.getChildCount();
                    int totalItemCount = manager.getItemCount();
                    int first = manager.findFirstVisibleItemPosition();
                    if (first + visibleItemCount >= totalItemCount) {
                        mUpdatePresenter.loadingMore();
                    }
                }
            }
        });

        mUpdatePresenter.loadTheme();
    }

    @Override
    public void setRefreshing(boolean isRefresh) {
        mSwipeRefreshLayout.setRefreshing(isRefresh);
    }

    @Override
    public void setNewsAdapterList(LatestNews latestNews) {
        mNewsRecycleView.setAdapter(mNewsAdapter);
        mNewsAdapter.setStoriesList(latestNews.getStories());
        mNewsAdapter.setTopStoriesList(latestNews.getTop_stories());
    }

    @Override
    public void addToNewsAdapter(List<Story> storyList) {
        mNewsAdapter.addList(storyList);
    }

    @Override
    public void setThemeAdapterList(ThemeStory themeStory) {
        mThemeAdapter.setListStory(themeStory);
        mNewsRecycleView.setAdapter(mThemeAdapter);
    }

    public void setDrawerAdapter(final List<Theme> menuItems) {
        mDrawerAdapter = new DrawerAdapter(MainActivity.this, menuItems);
        mDrawerAdapter.setOnClickListener(new DrawerAdapter.onClickListener() {
            @Override
            public void onClick(View view, int position) {
                mDrawerLayout.closeDrawers();
                mToolbar.setTitle(menuItems.get(position).getName());
                mUpdatePresenter.onClick(menuItems.get(position).getId());
            }
        });
        mDrawerRecycleView.setAdapter(mDrawerAdapter);

    }

    @Override
    public void onRefresh() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(true);
            mUpdatePresenter.loadFirst();
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.closeDrawers();
        } else {
            long endTime = System.currentTimeMillis();
            if (endTime - mStartTime > 2000) {
                Toast toast = Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();
                mStartTime = endTime;
            } else {
                finish();
            }
        }

    }

}
