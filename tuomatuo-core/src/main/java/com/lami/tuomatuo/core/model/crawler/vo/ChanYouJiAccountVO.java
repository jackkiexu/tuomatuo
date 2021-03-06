package com.lami.tuomatuo.core.model.crawler.vo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by xjk on 2016/3/22.
 */
@Data
public class ChanYouJiAccountVO {

    private Long id;
    private String name;
    private String avatarURL;
    private String sina;
    private Date createTime = new Date();
    private Date updateTime  = new Date();

    public ChanYouJiAccountVO() {
    }
    public ChanYouJiAccountVO(Long id) {
        this.id = id;
    }
}
