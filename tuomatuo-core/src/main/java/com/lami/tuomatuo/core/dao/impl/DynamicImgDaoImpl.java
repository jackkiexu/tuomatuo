package com.lami.tuomatuo.core.dao.impl;

import com.lami.tuomatuo.core.base.MySqlBaseDao;
import com.lami.tuomatuo.core.dao.DynamicImgDaoInterface;
import com.lami.tuomatuo.core.model.DynamicImg;
import org.springframework.stereotype.Repository;

/**
 * Created by xjk on 2016/1/18.
 */
@Repository("dynamicImgDaoInterface")
public class DynamicImgDaoImpl  extends MySqlBaseDao<DynamicImg, Long> implements DynamicImgDaoInterface {
    public DynamicImgDaoImpl(){
        super(DynamicImg.class);
    }
}