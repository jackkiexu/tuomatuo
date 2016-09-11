package com.lami.dcm.lbdatasource.model;

import lombok.Data;

/**
 * Created by xjk on 9/10/16.
 */
@Data
public class Address {
    private Integer id;
    private Integer userId;
    private String cityId;

    public Address(String cityId) {
        this.cityId = cityId;
    }
}
