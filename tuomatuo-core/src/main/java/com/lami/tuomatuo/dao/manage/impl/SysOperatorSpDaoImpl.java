package com.lami.tuomatuo.dao.manage.impl;

import com.lami.tuomatuo.base.BaseDaoMysqlImpl;
import com.lami.tuomatuo.dao.manage.SysOperatorSpDaoInterface;
import com.lami.tuomatuo.model.manage.SysOperatorSp;
import org.springframework.stereotype.Repository;

@Repository("sysOperatorSpDaoInterface")
public class SysOperatorSpDaoImpl extends BaseDaoMysqlImpl<SysOperatorSp, Integer> implements SysOperatorSpDaoInterface {
  public SysOperatorSpDaoImpl() {
    super(SysOperatorSp.class);
  }
}