package com.lami.tuomatuo.dao.manage.impl;


import com.lami.tuomatuo.base.BaseDaoMysqlImpl;
import com.lami.tuomatuo.dao.manage.SystemConfigDaoInterface;
import com.lami.tuomatuo.model.manage.SystemConfig;
import org.springframework.stereotype.Repository;

@Repository("systemConfigDaoInterface")
public class SystemConfigDaoImpl extends BaseDaoMysqlImpl<SystemConfig, Integer> implements SystemConfigDaoInterface {
  public SystemConfigDaoImpl() {
    super(SystemConfig.class);
  }
}