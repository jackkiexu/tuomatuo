package com.lami.tuomatuo.manage.controller;

import com.lami.tuomatuo.core.model.base.Result;
import com.lami.tuomatuo.core.model.manage.SysOperator;
import com.lami.tuomatuo.core.service.manage.SysOperatorService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("sysOperator")
public class SysOperatorController extends BaseAction<SysOperator,Integer>{
	private static final Logger logger = Logger.getLogger(SysOperatorController.class);
	
	private SysOperatorService sysOperatorService;

	@Resource(name="sysOperatorService") 
	public void setSysOperatorService(SysOperatorService sysOperatorService) {
		this.sysOperatorService = sysOperatorService;
		super.setBaseService(sysOperatorService, "sysOperator");
	}
	
	@Override
	public Result validate(SysOperator form, HttpServletRequest request) {
		
		return new Result(Result.SUCCESS);
	}
	
	@Override
	public SysOperator getNewObject(){return new SysOperator();}
	
	protected Map<String, Object> getSaveParam(HttpServletRequest request){
		Map<String, Object> param = getParam(request);
		param.put("createTime", new Date());
		param.put("updateTime", new Date());
		return param;
	}
	
	@RequestMapping(value = "/updatePassword.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> updatePassword(HttpServletRequest request) {
		Map<String, Object> result = new HashMap<String, Object>();
		HttpSession session = request.getSession();
		SysOperator operator = (SysOperator)session.getAttribute("admin");
		if(operator==null){
			result.put("result", false);
			result.put("msg", "session为空");
			logger.error(result.toString());
			return result;
		}
		String passwordOld = request.getParameter("passwordOld");
		String passwordNew = request.getParameter("passwordNew");
		
		SysOperator so = sysOperatorService.get(operator.getId());
		if(!so.getPassword().equals(passwordOld)){
			result.put("result", false);
			result.put("msg", "原密码输入错误！");
			logger.error(result.toString());
			return result;
		}
		so.setPassword(passwordNew);
		so.setUpdateTime(new Date());
		sysOperatorService.update(so);
		result.put("result", true);
		result.put("msg", "密码修改成功！");
		return result;
	}
	
	@RequestMapping(value = "/lock.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> lock(HttpServletRequest request) {
		Map<String, Object> result = new HashMap<String, Object>();
		String id = request.getParameter("id");
		if(id==null||"".equals(id)){
			result.put("result", false);
			result.put("msg","参数错误");
			return result;
		}
		SysOperator so = sysOperatorService.get(Integer.parseInt(id));
		if(so==null){
			result.put("result", false);
			result.put("msg","参数错误");
			return result;
		}
		so.setStatus(SysOperator.STATUS_STOP);
		sysOperatorService.update(so);
		
		result.put("result", true);
		result.put("msg","操作成功");
		return result;
	}
	@RequestMapping(value = "/unlock.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> unlock(HttpServletRequest request) {
		Map<String, Object> result = new HashMap<String, Object>();
		String id = request.getParameter("id");
		if(id==null||"".equals(id)){
			result.put("result", false);
			result.put("msg","参数错误");
			return result;
		}
		SysOperator so = sysOperatorService.get(Integer.parseInt(id));
		if(so==null){
			result.put("result", false);
			result.put("msg","参数错误");
			return result;
		}
		so.setStatus(SysOperator.STATUS_SUCCESS);
		sysOperatorService.update(so);
		
		result.put("result", true);
		result.put("msg","操作成功");
		return result;
	}
}
