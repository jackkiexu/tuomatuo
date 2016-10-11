package com.lami.tuomatuo.model.crawler.vo;

import com.lami.tuomatuo.model.crawler.UIAccount;
import lombok.Data;
import org.apache.commons.beanutils.BeanUtils;

/**
 * Created by xjk on 2016/3/21.
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

    public UIAccount convertToUIAccount(){
        UIAccount uiAccount = new UIAccount();
        uiAccount.setId(this.id);
        uiAccount.setAvatarURL(this.avatarURL);
        uiAccount.setName(this.name);
        uiAccount.setSignature(this.signature);
        uiAccount.setAge(this.age);
        uiAccount.setQq(this.qq);
        uiAccount.setEmail(this.email);
        uiAccount.setNet(this.net);
        uiAccount.setSina(this.sina);
        uiAccount.setWeiChat(this.weiChat);

        return uiAccount;
    }
}
