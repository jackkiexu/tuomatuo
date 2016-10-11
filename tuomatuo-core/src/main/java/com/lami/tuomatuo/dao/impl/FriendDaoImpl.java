package com.lami.tuomatuo.dao.impl;

import com.lami.tuomatuo.base.MySqlBaseDao;
import com.lami.tuomatuo.dao.FriendDaoInterface;
import com.lami.tuomatuo.model.Friend;
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