package com.lami.tuomatuo.model.po.dict;

import com.lami.tuomatuo.model.po.BaseParam;
import lombok.Data;

/**
 * Created by xujiankang on 2016/9/5.
 */
@Data
public class AddWordParam extends BaseParam {
    private String word;
    private Long unitId;
}
