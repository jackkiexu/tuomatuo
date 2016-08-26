package com.lami.tuomatuo.model.po.dict;

import com.lami.tuomatuo.model.po.BaseParam;
import lombok.Data;

/**
 * Created by xujiankang on 2016/8/25.
 */
@Data
public class ReviewUnitWordparam extends BaseParam {
    private Long unitId;
    private String word;
    private String uTicket;
}
