package com.lami.tuomatuo.core.service;

import com.lami.tuomatuo.core.base.BaseService;
import com.lami.tuomatuo.core.dao.DynamicCommentDaoInterface;
import com.lami.tuomatuo.core.model.DynamicComment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by xjk on 2016/1/18.
 */
@Service("dynamicCommentService")
public class DynamicCommentService extends BaseService<DynamicComment, Long> {

    @Autowired
    private DynamicCommentDaoInterface dynamicCommentDaoInterface;

    public Long getUserDynamicCommentCount(Long dyId){
        List<Object> paramaters = new ArrayList<Object>();
        paramaters.add(dyId);
        String sql = "select count(*) from dynamic_comment where dyId = ?";
        return dynamicCommentDaoInterface.getLong(sql, paramaters);
    }

    public List<DynamicComment> getUserDynamicCommentById(Long dyId, Integer offset, Integer pageSize){
        List<Object> paramaters = new ArrayList<Object>();
        paramaters.add(dyId);
        paramaters.add(offset);
        paramaters.add(pageSize);
        String sql = "select * from dynamic_comment where dyId = ? order by id desc limit ?,?";
        return dynamicCommentDaoInterface.search(sql, paramaters);
    }

    public void addUserDynamicComment(Long userId, Long dyId, String content){
        DynamicComment dynamicComment = new DynamicComment();
        dynamicComment.setContent(content);
        dynamicComment.setDyId(dyId);
        dynamicComment.setUserId(userId);
        dynamicComment.setCreateTime(new Date());
        dynamicComment.setUpdateTime(new Date());
        dynamicCommentDaoInterface.save(dynamicComment);
    }

}
