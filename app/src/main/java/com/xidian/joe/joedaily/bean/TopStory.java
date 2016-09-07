package com.xidian.joe.joedaily.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/8/12.
 * 热门消息
 * 格式：
 * [{"image":"http:\/\/pic2.zhimg.com\/cb048a1b018c665d28a7aee9e8581df9.jpg",
 * "type":0,"id":8663219,"ga_prefix":"081411",
 * "title":"分享两个简单小创意，大人和孩子都可以画得很开心"}
 */
public class TopStory implements Serializable {
    private String image; //重要！重要！！
    private int type;
    private int id; //重要！重要！！
    private String ga_prefix;
    private String title;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGa_prefix() {
        return ga_prefix;
    }

    public void setGa_prefix(String ga_prefix) {
        this.ga_prefix = ga_prefix;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
