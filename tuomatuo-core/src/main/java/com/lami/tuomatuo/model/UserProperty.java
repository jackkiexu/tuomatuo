package com.lami.tuomatuo.model;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by xujiankang on 2016/1/18.
 */
@Entity
@Table(name = "userproperty")
public class UserProperty implements java.io.Serializable {

    private Long id;
    private Long userId;
    private Integer sex;
    private Integer age;
    private Long popular;
    private Long fansSum;
    private Long dynamicSum;
    private Long followSum;
    private Long dynamicSeeTotal;
    private Long loveTotal;

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

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Long getPopular() {
        return popular;
    }

    public void setPopular(Long popular) {
        this.popular = popular;
    }

    public Long getFansSum() {
        return fansSum;
    }

    public void setFansSum(Long fansSum) {
        this.fansSum = fansSum;
    }

    public Long getDynamicSum() {
        return dynamicSum;
    }

    public void setDynamicSum(Long dynamicSum) {
        this.dynamicSum = dynamicSum;
    }

    public Long getFollowSum() {
        return followSum;
    }

    public void setFollowSum(Long followSum) {
        this.followSum = followSum;
    }

    public Long getDynamicSeeTotal() {
        return dynamicSeeTotal;
    }

    public void setDynamicSeeTotal(Long dynamicSeeTotal) {
        this.dynamicSeeTotal = dynamicSeeTotal;
    }

    public Long getLoveTotal() {
        return loveTotal;
    }

    public void setLoveTotal(Long loveTotal) {
        this.loveTotal = loveTotal;
    }

    @Override
    public String toString() {
        return "UserProperty{" +
                "id=" + id +
                ", userId=" + userId +
                ", sex=" + sex +
                ", age=" + age +
                ", popular=" + popular +
                ", fansSum=" + fansSum +
                ", dynamicSum=" + dynamicSum +
                ", followSum=" + followSum +
                ", dynamicSeeTotal=" + dynamicSeeTotal +
                ", loveTotal=" + loveTotal +
                '}';
    }
}
