package com.lami.tuomatuo.dao.manage.impl;


import com.lami.tuomatuo.base.BaseDaoMysqlImpl;
import com.lami.tuomatuo.dao.manage.SysOperatorLogDaoInterface;
import com.lami.tuomatuo.model.manage.SysOperatorLog;
import org.springframework.stereotype.Repository;

@Repository("sysOperatorLogDaoInterface")
public class SysOperatorLogDaoImpl extends BaseDaoMysqlImpl<SysOperatorLog, Integer> implements SysOperatorLogDaoInterface {
  public SysOperatorLogDaoImpl() {
    super(SysOperatorLog.class);
  }
}