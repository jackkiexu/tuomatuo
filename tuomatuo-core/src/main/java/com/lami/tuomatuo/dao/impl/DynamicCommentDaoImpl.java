package com.lami.tuomatuo.dao.impl;

import com.lami.tuomatuo.base.MySqlBaseDao;
import com.lami.tuomatuo.dao.DynamicCommentDaoInterface;
import com.lami.tuomatuo.model.DynamicComment;
import org.springframework.stereotype.Repository;

/**
 * Created by xjk on 2016/1/18.
 */
@Repository("dynamicCommentDaoInterface")
public class DynamicCommentDaoImpl extends MySqlBaseDao<DynamicComment, Long> implements DynamicCommentDaoInterface {
    public DynamicCommentDaoImpl(){
        super(DynamicComment.class);
    }
}