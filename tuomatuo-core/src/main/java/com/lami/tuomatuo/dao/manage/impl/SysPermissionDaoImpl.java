package com.lami.tuomatuo.dao.manage.impl;


import com.lami.tuomatuo.base.BaseDaoMysqlImpl;
import com.lami.tuomatuo.dao.manage.SysPermissionDaoInterface;
import com.lami.tuomatuo.model.manage.SysPermission;
import org.springframework.stereotype.Repository;

@Repository("sysPermissionDaoInterface")
public class SysPermissionDaoImpl extends BaseDaoMysqlImpl<SysPermission, Integer> implements SysPermissionDaoInterface {
  public SysPermissionDaoImpl() {
    super(SysPermission.class);
  }
}