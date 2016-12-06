package com.lami.tuomatuo.core.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by xjk on 2016/1/18.
 */
@Entity
@Table(name = "qq_account")
public class QQAccount implements java.io.Serializable {

    private static final long serialVersionUID = -661516726683325252L;
    private Long id;
    private String qqId;
    private String qqImgUrl;
    private Date createTime;
    private Date updateTime;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQqId() {
        return qqId;
    }

    public void setQqId(String qqId) {
        this.qqId = qqId;
    }

    public String getQqImgUrl() {
        return qqImgUrl;
    }

    public void setQqImgUrl(String qqImgUrl) {
        this.qqImgUrl = qqImgUrl;
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
        return "QQAccount{" +
                "id=" + id +
                ", qqId='" + qqId + '\'' +
                ", qqImgUrl='" + qqImgUrl + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
