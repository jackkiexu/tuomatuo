package com.lami.tuomatuo.core.dao.manage.impl;


import com.lami.tuomatuo.core.base.MySqlBaseDao;
import com.lami.tuomatuo.core.dao.manage.SysOperatorRoleDaoInterface;
import com.lami.tuomatuo.core.model.manage.SysOperatorRole;
import org.springframework.stereotype.Repository;

@Repository("sysOperatorRoleDaoInterface")
public class SysOperatorRoleDaoImpl extends MySqlBaseDao<SysOperatorRole, Integer> implements SysOperatorRoleDaoInterface {
  public SysOperatorRoleDaoImpl() {
    super(SysOperatorRole.class);
  }
}