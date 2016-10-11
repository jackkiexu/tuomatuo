package com.lami.tuomatuo.model.po;

import lombok.Data;

/**
 * Created by xjk on 2016/1/19.
 */
@Data
public class GetUserDynamicParam extends BaseParam {
    private String longitude; // 经度
    private String latitude; // 纬度
}
