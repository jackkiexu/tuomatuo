package com.lami.tuomatuo.search.entity;

import lombok.Data;
import org.apache.solr.client.solrj.beans.Field;

import java.util.Date;

/**
 * Created by xjk on 2016/4/12.
 */
@Data
public class Item {
    @Field
    private String id;
    @Field
    private String avatarURL;
    @Field
    private Integer sex;
    @Field
    private String address;
    @Field
    private String affilliation;
    @Field
    private Date createTime;
    @Field
    private Date updateTime;
    @Field
    private String name;
}
