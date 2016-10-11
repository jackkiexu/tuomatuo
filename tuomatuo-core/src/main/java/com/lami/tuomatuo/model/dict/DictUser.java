package com.lami.tuomatuo.model.dict;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by xjk on 2016/8/23.
 */
@Data
@Entity
@Table(name = "dict_user")
public class DictUser implements java.io.Serializable {

    private static final long serialVersionUID = -661516726683325252L;
    private Long id;
    private String mobile; // 默认账号
    private String passwd;  // 默认密码
    private Integer status; // 状态 默认 0
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