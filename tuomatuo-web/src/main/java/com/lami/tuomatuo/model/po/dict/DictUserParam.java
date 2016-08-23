package com.lami.tuomatuo.model.po.dict;

import com.lami.tuomatuo.model.po.BaseParam;
import lombok.Data;

/**
 * Created by xujiankang on 2016/8/23.
 */
@Data
public class DictUserParam extends BaseParam {
    private String mobile;
    private String passwd;
}
