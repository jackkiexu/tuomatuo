package com.lami.tuomatuo.dao.manage.impl;


import com.lami.tuomatuo.base.MySqlBaseDao;
import com.lami.tuomatuo.dao.manage.SysRoleDaoInterface;
import com.lami.tuomatuo.model.manage.SysRole;
import org.springframework.stereotype.Repository;

@Repository("sysRoleDaoInterface")
public class SysRoleDaoImpl extends MySqlBaseDao<SysRole, Integer> implements SysRoleDaoInterface {
  public SysRoleDaoImpl() {
    super(SysRole.class);
  }
}