package com.lami.tuomatuo.core.service.manage;

import com.lami.tuomatuo.core.base.BaseService;
import com.lami.tuomatuo.core.dao.manage.SysOperatorRoleDaoInterface;
import com.lami.tuomatuo.core.model.manage.SysOperatorRole;
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