package com.lami.tuomatuo.core.model.crawler.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Created by xjk on 2016/3/25.
 */
@Data
public class TopitDynamicVO {
    private Long id;
    private Long tipAId; // id for TopitAccount
    private String dynamicURL;
    private String coverImgURL;
    private String title;
    private List<String> catalog; // list for imgs URL
    private Date createTime;
    private Date updateTime;
}
