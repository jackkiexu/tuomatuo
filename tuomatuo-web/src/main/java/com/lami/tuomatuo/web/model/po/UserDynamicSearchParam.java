package com.lami.tuomatuo.web.model.po;

import lombok.Data;

/**
 * Created by xjk on 2016/1/26.
 */
@Data
public class UserDynamicSearchParam extends BaseParam {
    String content; // 用户输入的关键词
}
