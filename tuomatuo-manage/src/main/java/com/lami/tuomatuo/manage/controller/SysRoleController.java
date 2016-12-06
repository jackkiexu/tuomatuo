package com.lami.tuomatuo.manage.controller;

import com.lami.tuomatuo.core.model.base.Result;
import com.lami.tuomatuo.core.model.manage.SysRole;
import com.lami.tuomatuo.core.service.manage.SysRoleService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("sysRole")
public class SysRoleController extends BaseAction<SysRole,Integer>{
	private static final Logger logger = Logger.getLogger(SysRoleController.class);
	
	private SysRoleService sysRoleService;

	@Resource(name="sysRoleService") 
	public void setSysRoleService(SysRoleService sysRoleService) {
		this.sysRoleService = sysRoleService;
		super.setBaseService(sysRoleService, "sysRole");

	}
	
	@Override
	public Result validate(SysRole form, HttpServletRequest request) {
		return new Result(Result.SUCCESS);
	}
	
	@Override
	public SysRole getNewObject(){return new SysRole();}
}