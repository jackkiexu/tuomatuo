package com.lami.tuomatuo.model.crawler.vo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by xujiankang on 2016/3/22.
 */
@Data
public class ChanYouJiAccountVO {

    private Long id;
    private String name;
    private String avatarURL;
    private String sina;

    public ChanYouJiAccountVO() {
    }
    public ChanYouJiAccountVO(Long id) {
        this.id = id;
    }
}
