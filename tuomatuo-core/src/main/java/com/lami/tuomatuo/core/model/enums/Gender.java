package com.lami.tuomatuo.core.model.enums;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by xjk on 2016/1/25.
 */
public enum Gender {

    MALE(1, "男孩"),
    FEMALE(2, "女孩"),
    UNKNOWN(3, "未知");

    private int id;
    private String name;

    private Gender(int id, String name) {
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
