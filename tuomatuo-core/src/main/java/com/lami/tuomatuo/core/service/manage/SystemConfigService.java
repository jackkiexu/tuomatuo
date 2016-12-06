package com.lami.tuomatuo.core.service.manage;

import com.lami.tuomatuo.core.base.BaseService;
import com.lami.tuomatuo.core.dao.manage.SystemConfigDaoInterface;
import com.lami.tuomatuo.core.model.manage.SystemConfig;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service("systemConfigService")
public class SystemConfigService extends BaseService<SystemConfig, Integer>
{
  private SystemConfigDaoInterface systemConfigDaoInterface;

  @Resource(name="systemConfigDaoInterface")
  public void setSystemConfigDaoInterface(SystemConfigDaoInterface systemConfigDaoInterface)
  {
    this.systemConfigDaoInterface =systemConfigDaoInterface;
    this.baseDao = systemConfigDaoInterface;
  }
}