package com.lami.tuomatuo.model.crawler;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by xjk on 2016/3/22.
 */
@Entity
@Table(name = "crawler_chanyouji_account")
public class ChanYouJiAccount implements java.io.Serializable {

    private static final long serialVersionUID = -661516726683365472L;

    private Long id;
    private String name;
    private String avatarURL;
    private String sina;
    private Date createTime;
    private Date updateTime;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }

    public String getSina() {
        return sina;
    }

    public void setSina(String sina) {
        this.sina = sina;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "ChanYouJiAccount{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", avatarURL='" + avatarURL + '\'' +
                ", sina='" + sina + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
