package com.lami.tuomatuo.core.service.manage;

import com.lami.tuomatuo.core.base.BaseService;
import com.lami.tuomatuo.core.dao.manage.SysPermissionDaoInterface;
import com.lami.tuomatuo.core.model.manage.SysPermission;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service("sysPermissionService")
public class SysPermissionService extends BaseService<SysPermission, Integer>
{
  private SysPermissionDaoInterface sysPermissionDaoInterface;

  @Resource(name="sysPermissionDaoInterface")
  public void setSysPermissionDaoInterface(SysPermissionDaoInterface sysPermissionDaoInterface)
  {
    this.sysPermissionDaoInterface =sysPermissionDaoInterface;
    this.baseDao = sysPermissionDaoInterface;
  }
}