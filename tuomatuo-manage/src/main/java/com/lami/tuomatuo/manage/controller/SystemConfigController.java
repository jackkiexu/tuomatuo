package com.lami.tuomatuo.manage.controller;

import com.lami.tuomatuo.model.base.Result;
import com.lami.tuomatuo.model.manage.SystemConfig;
import com.lami.tuomatuo.service.manage.SystemConfigService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("systemConfig")
public class SystemConfigController extends BaseAction<SystemConfig,Integer>{
	private static final Logger logger = Logger.getLogger(SystemConfigController.class);
	
	private SystemConfigService systemConfigService;

	@Resource(name="systemConfigService") 
	public void setSystemConfigService(SystemConfigService systemConfigService) {
		this.systemConfigService = systemConfigService;
		super.setBaseService(systemConfigService, "systemConfig");
	}
	
	@Override
	public Result validate(SystemConfig form, HttpServletRequest request) {
		return new Result(Result.SUCCESS);
	}
	
	@Override
	public SystemConfig getNewObject(){return new SystemConfig();}
}