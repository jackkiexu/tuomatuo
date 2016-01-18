package com.lami.tuomatuo.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by xujiankang on 2016/1/18.
 */
@Entity
@Table(name = "dynamiclove")
public class UserDynamicLove implements java.io.Serializable {
    private Long id;
    private Long dyId;
    private Long loveId; // 点赞的人
    private Date createTime;

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

    public Long getLoveId() {
        return loveId;
    }

    public void setLoveId(Long loveId) {
        this.loveId = loveId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "UserDynamicLove{" +
                "id=" + id +
                ", dyId=" + dyId +
                ", loveId=" + loveId +
                ", createTime=" + createTime +
                '}';
    }
}
