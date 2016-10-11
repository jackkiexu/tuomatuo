package com.lami.tuomatuo.model.crawler.vo;

import lombok.Data;

import java.util.Date;

/**
 * Created by xjk on 2016/3/22.
 */
@Data
public class HuPuAccountVO {
    private Long id;
    private String name;
    private String avatarURL;
    private Integer sex;
    private String address = ""; // 所在地
    private String affiliation = ""; // NBA球队
    private Date createTime = new Date(); // 加入时间
    private Date updateTime = new Date();

    public HuPuAccountVO() {}
    public HuPuAccountVO(Long id) {
        this.id = id;
    }
}
