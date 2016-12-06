package com.lami.tuomatuo.web.model.po.crawler;

import com.lami.tuomatuo.web.model.po.BaseParam;
import lombok.Data;

/**
 * Created by xjk on 2016/3/21.
 */
@Data
public class CrawlerUIAccountParam extends BaseParam {
    private Long minId;
    private Long maxId;
}
