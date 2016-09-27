package com.lami.tuomatuo.service.manage;


import com.lami.tuomatuo.base.BaseService;
import com.lami.tuomatuo.dao.manage.SysOperatorSpDaoInterface;
import com.lami.tuomatuo.model.manage.SysOperatorSp;
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