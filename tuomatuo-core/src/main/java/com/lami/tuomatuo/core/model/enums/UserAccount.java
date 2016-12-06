package com.lami.tuomatuo.core.model.enums;

/**
 * Created by xjk on 2016/1/25.
 */
public enum UserAccount {

    NORMAL(0, "普通用户"),
    QQ(2, "QQ用户"),
    WEIXIN(3, "WeiXin用户");

    private int id;
    private String name;

    private UserAccount(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
