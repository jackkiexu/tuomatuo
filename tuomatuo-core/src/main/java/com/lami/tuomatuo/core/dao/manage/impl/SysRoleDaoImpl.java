package com.lami.tuomatuo.core.dao.manage.impl;


import com.lami.tuomatuo.core.base.MySqlBaseDao;
import com.lami.tuomatuo.core.dao.manage.SysRoleDaoInterface;
import com.lami.tuomatuo.core.model.manage.SysRole;
import org.springframework.stereotype.Repository;

@Repository("sysRoleDaoInterface")
public class SysRoleDaoImpl extends MySqlBaseDao<SysRole, Integer> implements SysRoleDaoInterface {
  public SysRoleDaoImpl() {
    super(SysRole.class);
  }
}