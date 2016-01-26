package com.lami.tuomatuo.model.po;

import lombok.Data;

/**
 * Created by xujiankang on 2016/1/19.
 */
@Data
public class GetFriendParam extends BaseParam {
    private Long userId;
    private String content;
}
