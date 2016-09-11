package com.lami.dcm.lbdatasource.model;

import lombok.Data;

/**
 * Created by xjk on 9/10/16.
 */
@Data
public class User {
    private Integer id;
    private String name;

    public User(String name) {
        this.name = name;
    }
}
