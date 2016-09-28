package com.lami.tuomatuo.dao.manage.impl;


import com.lami.tuomatuo.base.MySqlBaseDao;
import com.lami.tuomatuo.dao.manage.UserDaoInterface;
import com.lami.tuomatuo.model.manage.User;
import org.springframework.stereotype.Repository;

@Repository("manageUserDaoInterface")
public class UserDaoImpl extends MySqlBaseDao<User, Integer> implements UserDaoInterface {
  public UserDaoImpl() {
    super(User.class);
  }
}