package com.lami.tuomatuo.core.service.manage;

import com.lami.tuomatuo.core.base.BaseService;
import com.lami.tuomatuo.core.dao.manage.SysOperatorDaoInterface;
import com.lami.tuomatuo.core.model.manage.SysOperator;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service("sysOperatorService")
public class SysOperatorService extends BaseService<SysOperator, Integer>
{
  private SysOperatorDaoInterface sysOperatorDaoInterface;

  @Resource(name="sysOperatorDaoInterface")
  public void setSysOperatorDaoInterface(SysOperatorDaoInterface sysOperatorDaoInterface)
  {
    this.sysOperatorDaoInterface =sysOperatorDaoInterface;
    this.baseDao = sysOperatorDaoInterface;
  }
}