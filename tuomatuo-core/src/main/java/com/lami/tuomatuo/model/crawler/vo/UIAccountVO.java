package com.lami.tuomatuo.model.crawler.vo;

import lombok.Data;

/**
 * Created by xujiankang on 2016/3/21.
 */
@Data
public class UIAccountVO {
    private Long id;
    private String avatarURL;
    private String name;
    private String signature;
    private String age;
    private String qq;
    private String email;
    private String net;
    private String sina;
    private String weiChat;

    public UIAccountVO(){}
    public UIAccountVO(Long id) {
        this.id = id;
    }
}
