package com.lami.tuomatuo.web.model.po.dict;

import com.lami.tuomatuo.web.model.po.BaseParam;
import lombok.Data;

/**
 * Created by xjk on 2016/9/5.
 */
@Data
public class AddWordParam extends BaseParam {
    private String word;
    private Long unitId;
}
