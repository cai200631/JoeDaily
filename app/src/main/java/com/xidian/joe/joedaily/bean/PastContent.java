package com.xidian.joe.joedaily.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/8/12.
 * 热门中的过期文章 与 LastestNews格式一致，只是不同日期对应不同内容
 * eg:  http://news-at.zhihu.com/api/4/news/before/20160801
 * 重构时考虑将二者并一；
 */
public class PastContent {
    private String date;
    private List<Story> stories;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Story> getStories() {
        return stories;
    }

    public void setStories(List<Story> stories) {
        this.stories = stories;
    }
}
