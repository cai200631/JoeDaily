package com.xidian.joe.joedaily.bean;

/**
 * Created by Administrator on 2016/8/12.
 *
 * 格式：
 * {"color":15007,"thumbnail":"http:\/\/pic3.zhimg.com\/0e71e90fd6be47630399d63c58beebfc.jpg",
 * "description":"了解自己和别人，了解彼此的欲望和局限。","id":13,"name":"日常心理学"}
 */
public class Theme {
    String id;
    String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
