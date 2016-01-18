package com.lami.tuomatuo.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by xujiankang on 2016/1/18.
 */
@Entity
@Table(name = "userposition")
public class UserPosition implements java.io.Serializable {

    private static final long serialVersionUID = -661516726683325252L;
    private Long id;
    private Long userId;
    private String longitude; // 经度
    private String latitude; // 纬度
    private String geohash;
    private Date createTime;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getGeohash() {
        return geohash;
    }

    public void setGeohash(String geohash) {
        this.geohash = geohash;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "UserPosition{" +
                "id=" + id +
                ", userId=" + userId +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", geohash='" + geohash + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
