package com.lami.tuomatuo.service.manage;

import com.lami.tuomatuo.base.BaseService;
import com.lami.tuomatuo.dao.manage.SysOperatorRoleDaoInterface;
import com.lami.tuomatuo.model.manage.SysOperatorRole;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service("sysOperatorRoleService")
public class SysOperatorRoleService extends BaseService<SysOperatorRole, Integer>
{
  private SysOperatorRoleDaoInterface sysOperatorRoleDaoInterface;

  @Resource(name="sysOperatorRoleDaoInterface")
  public void setSysOperatorRoleDaoInterface(SysOperatorRoleDaoInterface sysOperatorRoleDaoInterface)
  {
    this.sysOperatorRoleDaoInterface =sysOperatorRoleDaoInterface;
    this.baseDao = sysOperatorRoleDaoInterface;
  }
}