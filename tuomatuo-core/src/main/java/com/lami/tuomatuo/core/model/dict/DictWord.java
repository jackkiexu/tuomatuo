package com.lami.tuomatuo.core.model.dict;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by xjk on 2016/8/23.
 */
@Data
@Entity
@Table(name = "dict_word")
public class DictWord implements java.io.Serializable {
    private static final long serialVersionUID = -66116726683365252L;
    private Long id;
    private String word;
    private Long unit;
    private String explain;
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
}



