package com.lami.tuomatuo.service;

import com.lami.tuomatuo.base.BaseService;
import com.lami.tuomatuo.dao.DynamicCommentDaoInterface;
import com.lami.tuomatuo.dao.DynamicImgDaoInterface;
import com.lami.tuomatuo.model.DynamicComment;
import com.lami.tuomatuo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by xujiankang on 2016/1/18.
 */
@Service("dynamicCommentService")
public class DynamicCommentService extends BaseService<DynamicComment, Integer> {

    @Autowired
    private DynamicCommentDaoInterface dynamicCommentDaoInterface;

}
