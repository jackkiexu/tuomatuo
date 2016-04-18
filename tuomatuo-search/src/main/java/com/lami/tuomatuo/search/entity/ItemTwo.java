package com.lami.tuomatuo.search.entity;

import lombok.Data;
import org.apache.solr.client.solrj.beans.Field;

import java.util.Date;

/**
 * Created by xujiankang on 2016/4/12.
 */
@Data
public class ItemTwo {
    @Field
    private Long id;
    @Field
    private String subject;
    @Field
    private String content;
    @Field("category_id")
    private long categoryId;
    @Field("category_name")
    private String categoryName;
    @Field("last_update_time")
    private Date lastUpdateTime;
}
