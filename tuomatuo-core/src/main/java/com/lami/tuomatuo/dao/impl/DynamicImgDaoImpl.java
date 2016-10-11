package com.lami.tuomatuo.dao.impl;

import com.lami.tuomatuo.base.MySqlBaseDao;
import com.lami.tuomatuo.dao.DynamicImgDaoInterface;
import com.lami.tuomatuo.model.DynamicImg;
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