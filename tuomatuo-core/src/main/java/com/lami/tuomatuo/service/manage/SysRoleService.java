package com.lami.tuomatuo.service.manage;


import com.lami.tuomatuo.base.BaseService;
import com.lami.tuomatuo.dao.manage.SysRoleDaoInterface;
import com.lami.tuomatuo.model.manage.SysRole;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service("sysRoleService")
public class SysRoleService extends BaseService<SysRole, Integer>
{
  private SysRoleDaoInterface sysRoleDaoInterface;

  @Resource(name="sysRoleDaoInterface")
  public void setSysRoleDaoInterface(SysRoleDaoInterface sysRoleDaoInterface)
  {
    this.sysRoleDaoInterface =sysRoleDaoInterface;
    this.baseDao = sysRoleDaoInterface;
  }
}