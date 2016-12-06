package com.lami.tuomatuo.web.model.po.dict;

import com.lami.tuomatuo.web.model.po.BaseParam;
import lombok.Data;

/**
 * Created by xjk on 2016/8/25.
 */
@Data
public class ReviewUnitWordparam extends BaseParam {
    private Long unitId;
    private String word;
    private String uTicket;
}
