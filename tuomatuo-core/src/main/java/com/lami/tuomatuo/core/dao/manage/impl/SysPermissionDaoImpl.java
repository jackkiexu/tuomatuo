package com.lami.tuomatuo.core.dao.manage.impl;


import com.lami.tuomatuo.core.base.MySqlBaseDao;
import com.lami.tuomatuo.core.model.manage.SysPermission;
import com.lami.tuomatuo.core.dao.manage.SysPermissionDaoInterface;
import org.springframework.stereotype.Repository;

@Repository("sysPermissionDaoInterface")
public class SysPermissionDaoImpl extends MySqlBaseDao<SysPermission, Integer> implements SysPermissionDaoInterface {
  public SysPermissionDaoImpl() {
    super(SysPermission.class);
  }
}