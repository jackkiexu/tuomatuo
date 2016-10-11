package com.lami.tuomatuo.model.crawler;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by xjk on 2016/3/24.
 */
@Entity
@Table(name = "crawler_chanyouji_dynamic")
public class ChanYoujiDynamic implements java.io.Serializable {

    private static final long serialVersionUID = -661516726681365472L;

    private Long id;
    private Long chanYouId;
    private String dynaWebURL;
    private String dynaCoverImgURL;
    private Long seeSum;
    private Long msgSum;
    private Long loveSum;
    private Long forwardSum;
    private String dynaTitle;
    private String dynamicMeta;
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

    public Long getChanYouId() {
        return chanYouId;
    }

    public void setChanYouId(Long chanYouId) {
        this.chanYouId = chanYouId;
    }

    public String getDynaWebURL() {
        return dynaWebURL;
    }

    public void setDynaWebURL(String dynaWebURL) {
        this.dynaWebURL = dynaWebURL;
    }

    public String getDynaCoverImgURL() {
        return dynaCoverImgURL;
    }

    public void setDynaCoverImgURL(String dynaCoverImgURL) {
        this.dynaCoverImgURL = dynaCoverImgURL;
    }

    public Long getSeeSum() {
        return seeSum;
    }

    public void setSeeSum(Long seeSum) {
        this.seeSum = seeSum;
    }

    public Long getMsgSum() {
        return msgSum;
    }

    public void setMsgSum(Long msgSum) {
        this.msgSum = msgSum;
    }

    public Long getLoveSum() {
        return loveSum;
    }

    public void setLoveSum(Long loveSum) {
        this.loveSum = loveSum;
    }

    public Long getForwardSum() {
        return forwardSum;
    }

    public void setForwardSum(Long forwardSum) {
        this.forwardSum = forwardSum;
    }

    public String getDynaTitle() {
        return dynaTitle;
    }

    public void setDynaTitle(String dynaTitle) {
        this.dynaTitle = dynaTitle;
    }

    public String getDynamicMeta() {
        return dynamicMeta;
    }

    public void setDynamicMeta(String dynamicMeta) {
        this.dynamicMeta = dynamicMeta;
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
        return "ChanYoujiDynamic{" +
                "id=" + id +
                ", chanYouId=" + chanYouId +
                ", dynaWebURL='" + dynaWebURL + '\'' +
                ", dynaCoverImgURL='" + dynaCoverImgURL + '\'' +
                ", seeSum=" + seeSum +
                ", msgSum=" + msgSum +
                ", loveSum=" + loveSum +
                ", forwardSum=" + forwardSum +
                ", dynaTitle='" + dynaTitle + '\'' +
                ", dynamicMeta='" + dynamicMeta + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
