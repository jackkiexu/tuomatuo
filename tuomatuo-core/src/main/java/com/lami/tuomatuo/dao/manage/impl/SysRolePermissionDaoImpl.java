package com.lami.tuomatuo.dao.manage.impl;


import com.lami.tuomatuo.base.MySqlBaseDao;
import com.lami.tuomatuo.dao.manage.SysRolePermissionDaoInterface;
import com.lami.tuomatuo.model.manage.SysRolePermission;
import org.springframework.stereotype.Repository;

@Repository("sysRolePermissionDaoInterface")
public class SysRolePermissionDaoImpl extends MySqlBaseDao<SysRolePermission, Integer> implements SysRolePermissionDaoInterface {
  public SysRolePermissionDaoImpl() {
    super(SysRolePermission.class);
  }
}