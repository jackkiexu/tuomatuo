package com.lami.tuomatuo.service.manage;

import com.lami.tuomatuo.base.BaseService;
import com.lami.tuomatuo.dao.manage.SysPermissionDaoInterface;
import com.lami.tuomatuo.model.manage.SysPermission;
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