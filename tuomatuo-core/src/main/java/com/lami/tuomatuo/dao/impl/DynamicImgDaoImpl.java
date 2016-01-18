package com.lami.tuomatuo.dao.impl;

import com.lami.tuomatuo.base.BaseDaoMysqlImpl;
import com.lami.tuomatuo.dao.DynamicCommentDaoInterface;
import com.lami.tuomatuo.dao.DynamicImgDaoInterface;
import com.lami.tuomatuo.model.DynamicComment;
import com.lami.tuomatuo.model.DynamicImg;
import org.springframework.stereotype.Repository;

/**
 * Created by xujiankang on 2016/1/18.
 */
@Repository("dynamicImgDaoInterface")
public class DynamicImgDaoImpl  extends BaseDaoMysqlImpl<DynamicImg, Long> implements DynamicImgDaoInterface {
    public DynamicImgDaoImpl(){
        super(DynamicImg.class);
    }
}