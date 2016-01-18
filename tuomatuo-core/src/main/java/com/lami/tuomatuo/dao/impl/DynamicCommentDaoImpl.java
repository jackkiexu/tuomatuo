package com.lami.tuomatuo.dao.impl;

import com.lami.tuomatuo.base.BaseDaoMysqlImpl;
import com.lami.tuomatuo.dao.DynamicCommentDaoInterface;
import com.lami.tuomatuo.dao.UserDaoInterface;
import com.lami.tuomatuo.model.DynamicComment;
import com.lami.tuomatuo.model.User;
import org.springframework.stereotype.Repository;

/**
 * Created by xujiankang on 2016/1/18.
 */
@Repository("dynamicCommentDaoInterface")
public class DynamicCommentDaoImpl extends BaseDaoMysqlImpl<DynamicComment, Long> implements DynamicCommentDaoInterface {
    public DynamicCommentDaoImpl(){
        super(DynamicComment.class);
    }
}