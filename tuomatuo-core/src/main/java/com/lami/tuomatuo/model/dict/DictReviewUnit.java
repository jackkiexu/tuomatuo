package com.lami.tuomatuo.model.dict;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by xujiankang on 2016/8/23.
 */
@Data
@Entity
@Table(name = "dict_review_unit")
public class DictReviewUnit implements java.io.Serializable {
    private static final long serialVersionUID = -66151672668365252L;
    private Long id;
    private Long userId; //用户 ID
    private String uticket; // 进行单元测试的唯一标识
    private String currentWord; // 当前复习到的词
    private String wordAll; // 所有的单词
    private Long unit; // 进行复习的单元
    private Integer reviewSum; // 将要复习的单词数
    private Integer hasReviewSum; //已经复习的单词书
    private Integer reviewOver; // 是否复习完
    private Integer isDelete; // 是否删除
    private Date createTime;
    private Date endTime; // 最久时间->超时删除

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}