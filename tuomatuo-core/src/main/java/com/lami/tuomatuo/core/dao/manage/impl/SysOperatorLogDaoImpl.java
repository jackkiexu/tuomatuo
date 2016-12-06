package com.lami.tuomatuo.core.dao.manage.impl;


import com.lami.tuomatuo.core.base.MySqlBaseDao;
import com.lami.tuomatuo.core.dao.manage.SysOperatorLogDaoInterface;
import com.lami.tuomatuo.core.model.manage.SysOperatorLog;
import org.springframework.stereotype.Repository;

@Repository("sysOperatorLogDaoInterface")
public class SysOperatorLogDaoImpl extends MySqlBaseDao<SysOperatorLog, Integer> implements SysOperatorLogDaoInterface {
  public SysOperatorLogDaoImpl() {
    super(SysOperatorLog.class);
  }
}