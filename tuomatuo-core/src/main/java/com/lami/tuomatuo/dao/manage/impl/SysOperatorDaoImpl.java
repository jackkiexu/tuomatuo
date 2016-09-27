package com.lami.tuomatuo.dao.manage.impl;

import com.lami.tuomatuo.base.BaseDaoMysqlImpl;
import com.lami.tuomatuo.dao.manage.SysOperatorDaoInterface;
import com.lami.tuomatuo.model.manage.SysOperator;
import org.springframework.stereotype.Repository;

@Repository("sysOperatorDaoInterface")
public class SysOperatorDaoImpl extends BaseDaoMysqlImpl<SysOperator, Integer> implements SysOperatorDaoInterface {
  public SysOperatorDaoImpl() {
    super(SysOperator.class);
  }
}