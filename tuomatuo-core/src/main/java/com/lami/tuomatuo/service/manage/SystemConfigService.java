package com.lami.tuomatuo.service.manage;

import com.lami.tuomatuo.base.BaseService;
import com.lami.tuomatuo.dao.manage.SystemConfigDaoInterface;
import com.lami.tuomatuo.model.manage.SystemConfig;
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