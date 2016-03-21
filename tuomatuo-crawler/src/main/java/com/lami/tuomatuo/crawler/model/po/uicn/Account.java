package com.lami.tuomatuo.crawler.model.po.uicn;

import lombok.Data;

/**
 * Created by xujiankang on 2016/3/18.
 */
@Data
public class Account {
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

    public Account(){}
    public Account(Long id) {
        this.id = id;
    }
}
