package com.lami.tuomatuo.model.po;

import lombok.Data;

/**
 * Created by xjk on 2016/1/26.
 */
@Data
public class DynamicAddCommentParam  extends BaseParam {
    private Long dyId; // 用户动态的 id
    private String content; // 用户动态的评论内容
}
