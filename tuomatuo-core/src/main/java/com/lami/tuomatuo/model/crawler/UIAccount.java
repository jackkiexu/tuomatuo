package com.lami.tuomatuo.model.crawler;

import javax.persistence.*;

/**
 * Created by xjk on 2016/3/21.
 */
@Entity
@Table(name = "crawler_ui_account")
public class UIAccount implements java.io.Serializable {
    private static final long serialVersionUID = -661516726683365452L;
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

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNet() {
        return net;
    }

    public void setNet(String net) {
        this.net = net;
    }

    public String getSina() {
        return sina;
    }

    public void setSina(String sina) {
        this.sina = sina;
    }

    public String getWeiChat() {
        return weiChat;
    }

    public void setWeiChat(String weiChat) {
        this.weiChat = weiChat;
    }

    @Override
    public String toString() {
        return "UIAccount{" +
                "id=" + id +
                ", avatarURL='" + avatarURL + '\'' +
                ", name='" + name + '\'' +
                ", signature='" + signature + '\'' +
                ", age='" + age + '\'' +
                ", qq='" + qq + '\'' +
                ", email='" + email + '\'' +
                ", net='" + net + '\'' +
                ", sina='" + sina + '\'' +
                ", weiChat='" + weiChat + '\'' +
                '}';
    }
}
