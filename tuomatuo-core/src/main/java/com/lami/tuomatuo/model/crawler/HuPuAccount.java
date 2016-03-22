package com.lami.tuomatuo.model.crawler;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by xujiankang on 2016/3/22.
 */

@Entity
@Table(name = "crawler_hupu_account")
public class HuPuAccount  implements java.io.Serializable {

    private static final long serialVersionUID = -661516726683365472L;

    private Long id;
    private String name;
    private String avatarURL;
    private Integer sex; // 1: 男 2: 女
    private String address; // 所在地
    private String affiliation; // NBA球队
    private Date createTime; // 加入时间
    private Date updateTime; // 更新时间

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

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
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
        return "HuPuAccount{" +
                "id=" + id +
                ", avatarURL='" + avatarURL + '\'' +
                ", sex=" + sex +
                ", address='" + address + '\'' +
                ", affiliation='" + affiliation + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
