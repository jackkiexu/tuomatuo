package com.lami.tuomatuo.web.model.po;

import lombok.Data;

/**
 * Created by xjk on 2016/1/19.
 */
@Data
public class GetFriendParam extends BaseParam {
    private Long userId;
    private String content;
}
