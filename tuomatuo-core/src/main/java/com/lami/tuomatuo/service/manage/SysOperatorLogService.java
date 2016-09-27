package com.lami.tuomatuo.service.manage;


import com.lami.tuomatuo.base.BaseService;
import com.lami.tuomatuo.dao.manage.SysOperatorLogDaoInterface;
import com.lami.tuomatuo.model.manage.SysOperatorLog;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service("sysOperatorLogService")
public class SysOperatorLogService extends BaseService<SysOperatorLog, Integer>
{
  private SysOperatorLogDaoInterface sysOperatorLogDaoInterface;

  @Resource(name="sysOperatorLogDaoInterface")
  public void setSysOperatorLogDaoInterface(SysOperatorLogDaoInterface sysOperatorLogDaoInterface)
  {
    this.sysOperatorLogDaoInterface =sysOperatorLogDaoInterface;
    this.baseDao = sysOperatorLogDaoInterface;
  }
}