package com.lami.tuomatuo.model.crawler.vo;

import lombok.Data;

import java.util.Date;

/**
 * Created by xjk on 2016/3/25.
 */
@Data
public class TopitAccountVO {
    private Long id;
    private String name;
    private String avatarURL;
    private Date createTime;
    private Date updateTime;
}
