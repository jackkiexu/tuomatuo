package com.lami.tuomatuo.manage.controller;


import com.lami.tuomatuo.core.model.base.Result;
import com.lami.tuomatuo.core.model.manage.SysPermission;
import com.lami.tuomatuo.core.service.manage.SysPermissionService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("sysPermission")
public class SysPermissionController extends BaseAction<SysPermission,Integer>{
	private static final Logger logger = Logger.getLogger(SysPermissionController.class);
	
	private SysPermissionService sysPermissionService;

	@Resource(name="sysPermissionService") 
	public void setSysPermissionService(SysPermissionService sysPermissionService) {
		this.sysPermissionService = sysPermissionService;
		super.setBaseService(sysPermissionService, "sysPermission");
	}
	
	@Override
	public Result validate(SysPermission form, HttpServletRequest request) {
		return new Result(Result.SUCCESS);
	}
	
	@Override
	public SysPermission getNewObject(){return new SysPermission();}
	
	@RequestMapping(value = "/preSearch.do")
	public String preSearch(HttpServletRequest request) {
		SysPermission t = new SysPermission();
		t.setMenu("-1");//
		List<SysPermission> list = sysPermissionService.search(t);
		request.setAttribute("parentId", list);
		return (viewName + "/" + viewName + "Search");
	}
}