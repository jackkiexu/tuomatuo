package com.lami.tuomatuo.core.service.manage;


import com.lami.tuomatuo.core.base.BaseService;
import com.lami.tuomatuo.core.dao.manage.SysOperatorSpDaoInterface;
import com.lami.tuomatuo.core.model.manage.SysOperatorSp;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service("sysOperatorSpService")
public class SysOperatorSpService extends BaseService<SysOperatorSp, Integer>
{
  private SysOperatorSpDaoInterface sysOperatorSpDaoInterface;

  @Resource(name="sysOperatorSpDaoInterface")
  public void setSysOperatorSpDaoInterface(SysOperatorSpDaoInterface sysOperatorSpDaoInterface)
  {
    this.sysOperatorSpDaoInterface =sysOperatorSpDaoInterface;
    this.baseDao = sysOperatorSpDaoInterface;
  }
}