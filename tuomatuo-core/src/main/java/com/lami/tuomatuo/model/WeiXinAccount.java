package com.lami.tuomatuo.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by xujiankang on 2016/1/18.
 */
@Entity
@Table(name = "wexinaccount")
public class WeiXinAccount implements java.io.Serializable {

    private static final long serialVersionUID = -661516766833265252L;
    private Long id;
    private String weiXinId;
    private String weiXinImgUrl;
    private Date createTime;
    private Date updateTime;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWeiXinId() {
        return weiXinId;
    }

    public void setWeiXinId(String weiXinId) {
        this.weiXinId = weiXinId;
    }

    public String getWeiXinImgUrl() {
        return weiXinImgUrl;
    }

    public void setWeiXinImgUrl(String weiXinImgUrl) {
        this.weiXinImgUrl = weiXinImgUrl;
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
        return "WeiXinAccount{" +
                "id=" + id +
                ", weiXinId='" + weiXinId + '\'' +
                ", weiXinImgUrl='" + weiXinImgUrl + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
