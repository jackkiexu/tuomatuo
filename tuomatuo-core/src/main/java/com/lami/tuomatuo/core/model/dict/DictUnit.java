package com.lami.tuomatuo.core.model.dict;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by xjk on 2016/8/23.
 */
@Data
@Entity
@Table(name = "dict_unit")
public class DictUnit implements java.io.Serializable {

    private static final long serialVersionUID = -66516726683325252L;
    private Long id;
    private String name; // 单元名称
    private String wordSum;  // 单元单词数
    private Integer type; // 四六级, 托福, 雅思, gmat,
    private Date createTime;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}