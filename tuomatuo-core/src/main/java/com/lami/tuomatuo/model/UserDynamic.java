package com.lami.tuomatuo.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by xjk on 2016/1/18.
 */
@Entity
@Table(name = "user_dynamic")
public class UserDynamic implements java.io.Serializable {

    private Long id;
    private Long userId;
    private Integer type;
    private Long love;
    private Integer fromType;
    private String longitude; // 经度
    private String latitude; // 纬度
    private String geoHash; // geoHash 值
    private Integer storagePolicy; // 存储的策略
    private Long dynamicCommentId; // 用户动态评论的 ID
    private Long dynamicSeeSum; // 用户的动态被查看的次数
    private Long dynamicRecommend; // 用户动态转发 推荐次数
    private Date createTime;
    private Date updateTime;
    private Long hotValue;
    private String title; // 用户动态的标题

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

    public String getGeoHash() {
        return geoHash;
    }

    public void setGeoHash(String geoHash) {
        this.geoHash = geoHash;
    }

    public Integer getStoragePolicy() {
        return storagePolicy;
    }

    public void setStoragePolicy(Integer storagePolicy) {
        this.storagePolicy = storagePolicy;
    }

    public Long getDynamicCommentId() {
        return dynamicCommentId;
    }

    public void setDynamicCommentId(Long dynamicCommentId) {
        this.dynamicCommentId = dynamicCommentId;
    }

    public Long getDynamicSeeSum() {
        return dynamicSeeSum;
    }

    public void setDynamicSeeSum(Long dynamicSeeSum) {
        this.dynamicSeeSum = dynamicSeeSum;
    }

    public Long getDynamicRecommend() {
        return dynamicRecommend;
    }

    public void setDynamicRecommend(Long dynamicRecommend) {
        this.dynamicRecommend = dynamicRecommend;
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

    public Long getHotValue() {
        return hotValue;
    }

    public void setHotValue(Long hotValue) {
        this.hotValue = hotValue;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
                ", geoHash='" + geoHash + '\'' +
                ", storagePolicy=" + storagePolicy +
                ", dynamicCommentId=" + dynamicCommentId +
                ", dynamicSeeSum=" + dynamicSeeSum +
                ", dynamicRecommend=" + dynamicRecommend +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", hotValue=" + hotValue +
                ", title='" + title + '\'' +
                '}';
    }
}
