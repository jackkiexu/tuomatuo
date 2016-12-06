package com.lami.tuomatuo.core.dao.manage.impl;


import com.lami.tuomatuo.core.base.MySqlBaseDao;
import com.lami.tuomatuo.core.dao.manage.UserDaoInterface;
import com.lami.tuomatuo.core.model.manage.User;
import org.springframework.stereotype.Repository;

@Repository("manageUserDaoInterface")
public class UserDaoImpl extends MySqlBaseDao<User, Integer> implements UserDaoInterface {
  public UserDaoImpl() {
    super(User.class);
  }
}