package com.lami.tuomatuo.web.model.po;

import lombok.Data;

/**
 * Created by xjk on 2016/1/18.
 */
@Data
public class LoginParam {
    private Long userId;
    private String name;
    private String sign;
}
