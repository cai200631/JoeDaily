package com.xidian.joe.joedaily.view;

import com.xidian.joe.joedaily.bean.LatestNews;
import com.xidian.joe.joedaily.bean.Story;
import com.xidian.joe.joedaily.bean.Theme;
import com.xidian.joe.joedaily.bean.ThemeStory;

import java.util.List;

/**
 * Created by Administrator on 2016/8/12.
 */
public interface MainView {
    void setRefreshing(boolean isRefresh);

    void setNewsAdapterList(LatestNews latestNews);

    void addToNewsAdapter(List<Story> storyList);

    void setThemeAdapterList(ThemeStory themeStory);

    void setDrawerAdapter(List<Theme> menuItems);

}
