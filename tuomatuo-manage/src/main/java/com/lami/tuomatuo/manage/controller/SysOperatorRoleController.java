package com.lami.tuomatuo.manage.controller;

import com.lami.tuomatuo.model.base.Result;
import com.lami.tuomatuo.model.manage.SysOperatorRole;
import com.lami.tuomatuo.model.manage.SysRole;
import com.lami.tuomatuo.service.manage.SysOperatorRoleService;
import com.lami.tuomatuo.service.manage.SysRoleService;
import com.lami.tuomatuo.utils.StringUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping("sysOperatorRole")
public class SysOperatorRoleController extends BaseAction<SysOperatorRole,Integer>{
	private static final Logger logger = Logger.getLogger(SysOperatorRoleController.class);
	
	private SysOperatorRoleService sysOperatorRoleService;
	@Autowired
	private SysRoleService sysRoleService;
	
	@Resource(name="sysOperatorRoleService") 
	public void setSysOperatorRoleService(SysOperatorRoleService sysOperatorRoleService) {
		this.sysOperatorRoleService = sysOperatorRoleService;
		super.setBaseService(sysOperatorRoleService, "sysOperatorRole");
	}
	
	@Override
	public Result validate(SysOperatorRole form, HttpServletRequest request) {
		return new Result(Result.SUCCESS);
	}
	
	@Override
	public SysOperatorRole getNewObject(){return new SysOperatorRole();}
	
	@RequestMapping(value = "/preSetOperatorRole.do")
	@ResponseBody
	public Map<String, Object> preSetOperatorRole(HttpServletRequest request){
		Map<String, Object> result = new HashMap<String, Object>();
		//get role id
		String operatorId = request.getParameter("operatorId");
		if(operatorId==null||"".equals(operatorId)){
			result.put("result", false);
			result.put("msg","参数错误");
			return result;
		}
		//list selected permission
		String queryStr = "select a.* from sys_role a,sys_operator_role b where b.operatorId=? and b.roleId=a.id";
		List<Object> param  = new ArrayList<Object>();
		param.add(operatorId);

		List<SysRole> selectedList = sysRoleService.search(queryStr, param);
		result.put("selectedList", selectedList);
		
		//list all permission
		List<SysRole> selectList = sysRoleService.getAll();
		//remove selected permission
		Iterator<SysRole> it = selectList.iterator();
		List<SysRole> delList = new ArrayList<SysRole>();
		while(it.hasNext()){
			SysRole sp = it.next();
			for(int i=0;i<selectedList.size();i++){
				SysRole tmp = selectedList.get(i);
				if(tmp!=null&&tmp.getId()!=null&&
						sp!=null&&sp.getId()!=null&&
						sp.getId().intValue()==tmp.getId().intValue()){
					delList.add(sp);
				}
			}
		}
		selectList.removeAll(delList);
		result.put("selectList", selectList);
		
		
		result.put("result",true);
		return result;
	}
	@RequestMapping(value = "/setOperatorRole.do")
	@ResponseBody
	public Map<String, Object> setOperatorRole(HttpServletRequest request){
		Map<String, Object> result = new HashMap<String, Object>();
		//get role id
		String operatorId = request.getParameter("operatorId");
		if(operatorId==null||"".equals(operatorId)){
			result.put("result", false);
			result.put("msg","参数错误");
			return result;
		}
		//list selected permission
		String selectedId = request.getParameter("ids");
		logger.info(selectedId);
		//remove role id bind permission ids;
		ArrayList<Object> p = new ArrayList<Object>();
		p.add(operatorId);
		int res = sysOperatorRoleService.execute("delete from sys_operator_role where operatorId=?", p);
		if(res<0){
			result.put("result", false);
			result.put("msg","数据执行出错");
			return result;
		}
		
		if(selectedId==null||"".equals(selectedId)){
			//do nothing
		}else{
			String[] ids = selectedId.split(",");
			for(int i=0;i<ids.length;i++){
				if(ids[i]!=null&& StringUtil.isNumeric(ids[i])){
					SysOperatorRole t = new SysOperatorRole();
					t.setOperatorId(Integer.parseInt(operatorId));
					t.setRoleId(Integer.parseInt(ids[i]));
					sysOperatorRoleService.save(t);
				}
			}
		}
		result.put("result", true);
		result.put("msg","操作成功");
		return result;
	}
}