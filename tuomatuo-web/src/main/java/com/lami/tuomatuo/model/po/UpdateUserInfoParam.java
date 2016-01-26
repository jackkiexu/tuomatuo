package com.lami.tuomatuo.model.po;

import lombok.Data;

/**
 * Created by xujiankang on 2016/1/26.
 */
@Data
public class UpdateUserInfoParam extends BaseParam {
    private String imgUrl;
    private String nick;
    private Integer age;
    private Integer sex;
}
