package com.xidian.joe.joedaily.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/8/12.
 * 最新消息
 */

/**格式：
//{"images":["http:\/\/pic3.zhimg.com\/dee0d5838e799be3663ac0417ab41ce2.jpg"],
//        "type":0,"id":8683213,"ga_prefix":"081415",
//        "title":"即便是感冒这么常见的疾病，大家仍然会有种种误解"}
 */

public class Story implements Serializable{
    private int type;
    private int id;
    private String title;
    private List<String> images;
    private boolean multipic;
    String date;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isMultipic() {
        return multipic;
    }

    public void setMultipic(boolean multipic) {
        this.multipic = multipic;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
