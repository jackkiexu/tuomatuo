package com.lami.tuomatuo.dao.manage.impl;


import com.lami.tuomatuo.base.MySqlBaseDao;
import com.lami.tuomatuo.dao.manage.SysOperatorRoleDaoInterface;
import com.lami.tuomatuo.model.manage.SysOperatorRole;
import org.springframework.stereotype.Repository;

@Repository("sysOperatorRoleDaoInterface")
public class SysOperatorRoleDaoImpl extends MySqlBaseDao<SysOperatorRole, Integer> implements SysOperatorRoleDaoInterface {
  public SysOperatorRoleDaoImpl() {
    super(SysOperatorRole.class);
  }
}