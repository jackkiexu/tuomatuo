package com.lami.tuomatuo.core.dao.manage.impl;


import com.lami.tuomatuo.core.base.MySqlBaseDao;
import com.lami.tuomatuo.core.dao.manage.SystemConfigDaoInterface;
import com.lami.tuomatuo.core.model.manage.SystemConfig;
import org.springframework.stereotype.Repository;

@Repository("systemConfigDaoInterface")
public class SystemConfigDaoImpl extends MySqlBaseDao<SystemConfig, Integer> implements SystemConfigDaoInterface {
  public SystemConfigDaoImpl() {
    super(SystemConfig.class);
  }
}