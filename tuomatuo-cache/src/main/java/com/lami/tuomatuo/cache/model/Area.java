package com.lami.tuomatuo.cache.model;

import lombok.Data;

/**
 * Created by xjk on 2016/12/7.
 */
@Data
public class Area {
    private Integer id;
    private Integer parentCode;
    private String name;
    private Integer code;
    private String pinyin;
    private Integer type;
}
