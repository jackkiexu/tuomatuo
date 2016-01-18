package com.lami.tuomatuo.model;

import javax.persistence.*;

/**
 * Created by xujiankang on 2016/1/18.
 */
@Entity
@Table(name = "userdynamic")
public class UserDynamic implements java.io.Serializable {

    private Long id;
    private Long userId;
    private Integer type;
    private Long love;
    private Integer fromType;
    private String longitude; // 经度
    private String latitude; // 纬度
    private Integer storagePolicy; // 存储的策略
    private Long dynamicContentId; // 用户动态内容的id

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getLove() {
        return love;
    }

    public void setLove(Long love) {
        this.love = love;
    }

    public Integer getFromType() {
        return fromType;
    }

    public void setFromType(Integer fromType) {
        this.fromType = fromType;
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

    public Integer getStoragePolicy() {
        return storagePolicy;
    }

    public void setStoragePolicy(Integer storagePolicy) {
        this.storagePolicy = storagePolicy;
    }

    public Long getDynamicContentId() {
        return dynamicContentId;
    }

    public void setDynamicContentId(Long dynamicContentId) {
        this.dynamicContentId = dynamicContentId;
    }

    @Override
    public String toString() {
        return "UserDynamic{" +
                "id=" + id +
                ", userId=" + userId +
                ", type=" + type +
                ", love=" + love +
                ", fromType=" + fromType +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", storagePolicy=" + storagePolicy +
                ", dynamicContentId=" + dynamicContentId +
                '}';
    }
}
