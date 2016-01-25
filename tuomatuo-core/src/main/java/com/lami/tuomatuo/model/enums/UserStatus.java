package com.lami.tuomatuo.model.enums;

/**
 * Created by xujiankang on 2016/1/25.
 */
public enum UserStatus {
    INIT(0, "初始化"),
    OK(1, "OK"),
    BLACK(2, "拉黑");

    private int id;
    private String name;

    private UserStatus(int id, String name) {
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
