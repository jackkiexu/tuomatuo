package com.lami.tuomatuo.core.model.enums;

/**
 * Created by xjk on 2016/1/25.
 */
public enum SmsType {
    YUNPIAN (0, "云片");

    private int id;
    private String name;

    private SmsType(int id, String name) {
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
