package com.lami.tuomatuo.core.service;

import com.lami.tuomatuo.core.base.BaseService;
import com.lami.tuomatuo.core.dao.DynamicImgDaoInterface;
import com.lami.tuomatuo.core.model.DynamicImg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by xjk on 2016/1/18.
 */
@Service("dynamicImgService")
public class DynamicImgService extends BaseService<DynamicImg, Long> {

    @Autowired
    private DynamicImgDaoInterface dynamicImgDaoInterface;

}