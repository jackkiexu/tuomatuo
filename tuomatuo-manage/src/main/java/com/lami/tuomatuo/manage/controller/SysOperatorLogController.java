package com.lami.tuomatuo.manage.controller;

import com.lami.tuomatuo.core.model.base.Result;
import com.lami.tuomatuo.core.model.manage.SysOperatorLog;
import com.lami.tuomatuo.core.service.manage.SysOperatorLogService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("sysOperatorLog")
public class SysOperatorLogController extends BaseAction<SysOperatorLog,Integer>{
	private static final Logger logger = Logger.getLogger(SysOperatorLogController.class);
	
	private SysOperatorLogService sysOperatorLogService;

	@Resource(name="sysOperatorLogService") 
	public void setSysOperatorLogService(SysOperatorLogService sysOperatorLogService) {
		this.sysOperatorLogService = sysOperatorLogService;
		super.setBaseService(sysOperatorLogService, "sysOperatorLog");
	}
	
	@Override
	public Result validate(SysOperatorLog form, HttpServletRequest request) {
		return new Result(Result.SUCCESS);
	}
	
	@Override
	public SysOperatorLog getNewObject(){return new SysOperatorLog();}
}