package com.lami.tuomatuo.dao.manage.impl;


import com.lami.tuomatuo.base.BaseDaoMysqlImpl;
import com.lami.tuomatuo.dao.manage.SysRolePermissionDaoInterface;
import com.lami.tuomatuo.model.manage.SysRolePermission;
import org.springframework.stereotype.Repository;

@Repository("sysRolePermissionDaoInterface")
public class SysRolePermissionDaoImpl extends BaseDaoMysqlImpl<SysRolePermission, Integer> implements SysRolePermissionDaoInterface {
  public SysRolePermissionDaoImpl() {
    super(SysRolePermission.class);
  }
}