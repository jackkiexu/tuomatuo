package com.lami.tuomatuo.model.crawler;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by xujiankang on 2016/3/25.
 */
@Entity
@Table(name = "crawler_hupu_account")
public class TopitDynamic implements java.io.Serializable {

    private static final long serialVersionUID = -662516826683365472L;

    private Long id;
    private Long tipAId; // id for TopitAccount
    private String dynamicURL;
    private String coverImgURL;
    private String title;
    private List<String> catalog; // list for imgs URL
    private Date createTime;
    private Date updateTime;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTipAId() {
        return tipAId;
    }

    public void setTipAId(Long tipAId) {
        this.tipAId = tipAId;
    }

    public String getDynamicURL() {
        return dynamicURL;
    }

    public void setDynamicURL(String dynamicURL) {
        this.dynamicURL = dynamicURL;
    }

    public String getCoverImgURL() {
        return coverImgURL;
    }

    public void setCoverImgURL(String coverImgURL) {
        this.coverImgURL = coverImgURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getCatalog() {
        return catalog;
    }

    public void setCatalog(List<String> catalog) {
        this.catalog = catalog;
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
        return "TopitDynamic{" +
                "id=" + id +
                ", tipAId=" + tipAId +
                ", dynamicURL='" + dynamicURL + '\'' +
                ", coverImgURL='" + coverImgURL + '\'' +
                ", title='" + title + '\'' +
                ", catalog=" + catalog +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
