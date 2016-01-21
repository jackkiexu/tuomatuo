package com.lami.tuomatuo.service;

import com.lami.tuomatuo.base.BaseService;
import com.lami.tuomatuo.dao.DynamicCommentDaoInterface;
import com.lami.tuomatuo.dao.DynamicImgDaoInterface;
import com.lami.tuomatuo.model.DynamicComment;
import com.lami.tuomatuo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xujiankang on 2016/1/18.
 */
@Service("dynamicCommentService")
public class DynamicCommentService extends BaseService<DynamicComment, Long> {

    @Autowired
    private DynamicCommentDaoInterface dynamicCommentDaoInterface;

    public Long getUserDynamicCommentCount(Long dyId){
        List<Object> paramaters = new ArrayList<Object>();
        paramaters.add(dyId);
        String sql = "select count(*) from dynamiccomment where dyId = ?";
        return dynamicCommentDaoInterface.getLong(sql, paramaters);
    }

}
