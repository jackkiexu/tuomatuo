package com.lami.tuomatuo.service.manage;

import com.lami.tuomatuo.base.BaseService;
import com.lami.tuomatuo.dao.manage.SysRolePermissionDaoInterface;
import com.lami.tuomatuo.model.manage.SysRolePermission;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service("sysRolePermissionService")
public class SysRolePermissionService extends BaseService<SysRolePermission, Integer>
{
  private SysRolePermissionDaoInterface sysRolePermissionDaoInterface;

  @Resource(name="sysRolePermissionDaoInterface")
  public void setSysRolePermissionDaoInterface(SysRolePermissionDaoInterface sysRolePermissionDaoInterface)
  {
    this.sysRolePermissionDaoInterface =sysRolePermissionDaoInterface;
    this.baseDao = sysRolePermissionDaoInterface;
  }
}