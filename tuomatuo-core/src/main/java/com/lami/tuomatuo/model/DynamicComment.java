package com.lami.tuomatuo.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by xujiankang on 2016/1/18.
 */
@Entity
@Table(name = "dynamiccomment")
public class DynamicComment implements java.io.Serializable {
    private Long id;
    private Long dyId;
    private Long userId;
    private String content;
    private Date createTime;
    private Date updateTime;
    private Long approve;
    private Long oppose;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDyId() {
        return dyId;
    }

    public void setDyId(Long dyId) {
        this.dyId = dyId;
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

    public Long getApprove() {
        return approve;
    }

    public void setApprove(Long approve) {
        this.approve = approve;
    }

    public Long getOppose() {
        return oppose;
    }

    public void setOppose(Long oppose) {
        this.oppose = oppose;
    }

    @Override
    public String toString() {
        return "DynamicComment{" +
                "id=" + id +
                ", dyId=" + dyId +
                ", userId=" + userId +
                ", content='" + content + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", approve=" + approve +
                ", oppose=" + oppose +
                '}';
    }
}
