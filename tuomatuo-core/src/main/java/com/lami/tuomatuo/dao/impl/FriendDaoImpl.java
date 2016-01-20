package com.lami.tuomatuo.dao.impl;

import com.lami.tuomatuo.base.BaseDaoMysqlImpl;
import com.lami.tuomatuo.dao.FriendDaoInterface;
import com.lami.tuomatuo.dao.UserDaoInterface;
import com.lami.tuomatuo.model.Friend;
import com.lami.tuomatuo.model.User;
import org.springframework.stereotype.Repository;

/**
 * Created by xujiankang on 2016/1/20.
 */
@Repository("friendDaoInterface")
public class FriendDaoImpl  extends BaseDaoMysqlImpl<Friend, Long> implements FriendDaoInterface {
    public FriendDaoImpl(){
        super(Friend.class);
    }
}