package com.lami.tuomatuo.core.dao.impl;

import com.lami.tuomatuo.core.base.MySqlBaseDao;
import com.lami.tuomatuo.core.dao.FriendDaoInterface;
import com.lami.tuomatuo.core.model.Friend;
import org.springframework.stereotype.Repository;

/**
 * Created by xjk on 2016/1/20.
 */
@Repository("friendDaoInterface")
public class FriendDaoImpl  extends MySqlBaseDao<Friend, Long> implements FriendDaoInterface {
    public FriendDaoImpl(){
        super(Friend.class);
    }
}