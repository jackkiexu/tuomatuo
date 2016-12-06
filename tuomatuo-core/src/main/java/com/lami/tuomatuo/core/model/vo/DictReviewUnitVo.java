package com.lami.tuomatuo.core.model.vo;

import com.lami.tuomatuo.core.model.dict.DictReviewUnit;
import lombok.Data;

import java.util.Date;

/**
 * Created by xjk on 2016/8/23.
 */
@Data
public class DictReviewUnitVo {

    private Long unitId; // 单元 id
    private String name; // 单元名称
    private String wordSum;  // 单元单词数
    private Integer type; // 四六级, 托福, 雅思, gmat,
    private Date createTime;

    private DictReviewUnit dictReviewUnit; // 登录人对应的 reviewUnit (endTime未超时)
}
