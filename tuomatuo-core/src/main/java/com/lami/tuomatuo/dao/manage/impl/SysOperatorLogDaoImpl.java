package com.lami.tuomatuo.dao.manage.impl;


import com.lami.tuomatuo.base.MySqlBaseDao;
import com.lami.tuomatuo.dao.manage.SysOperatorLogDaoInterface;
import com.lami.tuomatuo.model.manage.SysOperatorLog;
import org.springframework.stereotype.Repository;

@Repository("sysOperatorLogDaoInterface")
public class SysOperatorLogDaoImpl extends MySqlBaseDao<SysOperatorLog, Integer> implements SysOperatorLogDaoInterface {
  public SysOperatorLogDaoImpl() {
    super(SysOperatorLog.class);
  }
}