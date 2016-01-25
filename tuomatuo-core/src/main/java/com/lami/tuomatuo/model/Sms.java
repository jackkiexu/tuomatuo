package com.lami.tuomatuo.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by xujiankang on 2016/1/25.
 */
@Entity
@Table(name = "sms")
public class Sms implements java.io.Serializable {
    private Integer id;
    private Long userId;
    private String content;
    private Integer type;
    private Date createTime;
    private  Date updateTime;
    private String mobile;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override
    public String toString() {
        return "Sms{" +
                "id=" + id +
                ", userId=" + userId +
                ", content='" + content + '\'' +
                ", type=" + type +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", mobile='" + mobile + '\'' +
                '}';
    }
}
