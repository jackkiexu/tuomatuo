package com.lami.tuomatuo.core.dao.manage.impl;

import com.lami.tuomatuo.core.dao.manage.SysOperatorDaoInterface;
import com.lami.tuomatuo.core.base.MySqlBaseDao;
import com.lami.tuomatuo.core.model.manage.SysOperator;
import org.springframework.stereotype.Repository;

@Repository("sysOperatorDaoInterface")
public class SysOperatorDaoImpl extends MySqlBaseDao<SysOperator, Integer> implements SysOperatorDaoInterface {
  public SysOperatorDaoImpl() {
    super(SysOperator.class);
  }
}