package com.lami.tuomatuo.model.po;

import lombok.Data;

import java.util.List;

/**
 * Created by xjk on 2016/1/26.
 */
@Data
public class AddFriend extends BaseParam {
    private Long friendId; // 添加人的 userId
    private List<Long> friendList; // 添加的 userId
}
