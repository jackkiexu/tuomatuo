package com.lami.tuomatuo.core.dao.manage.impl;

import com.lami.tuomatuo.core.base.MySqlBaseDao;
import com.lami.tuomatuo.core.dao.manage.SysOperatorSpDaoInterface;
import com.lami.tuomatuo.core.model.manage.SysOperatorSp;
import org.springframework.stereotype.Repository;

@Repository("sysOperatorSpDaoInterface")
public class SysOperatorSpDaoImpl extends MySqlBaseDao<SysOperatorSp, Integer> implements SysOperatorSpDaoInterface {
  public SysOperatorSpDaoImpl() {
    super(SysOperatorSp.class);
  }
}