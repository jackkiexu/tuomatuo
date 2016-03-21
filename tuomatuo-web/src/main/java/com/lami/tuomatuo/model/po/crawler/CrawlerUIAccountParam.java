package com.lami.tuomatuo.model.po.crawler;

import com.lami.tuomatuo.model.po.BaseParam;
import lombok.Data;

/**
 * Created by xujiankang on 2016/3/21.
 */
@Data
public class CrawlerUIAccountParam extends BaseParam {
    private Long minId;
    private Long maxId;
}
