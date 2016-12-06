package com.lami.tuomatuo.core.dao.manage.impl;


import com.lami.tuomatuo.core.base.MySqlBaseDao;
import com.lami.tuomatuo.core.dao.manage.SysRolePermissionDaoInterface;
import com.lami.tuomatuo.core.model.manage.SysRolePermission;
import org.springframework.stereotype.Repository;

@Repository("sysRolePermissionDaoInterface")
public class SysRolePermissionDaoImpl extends MySqlBaseDao<SysRolePermission, Integer> implements SysRolePermissionDaoInterface {
  public SysRolePermissionDaoImpl() {
    super(SysRolePermission.class);
  }
}