package com.lami.tuomatuo.manage.controller;

import com.lami.tuomatuo.core.model.base.Result;
import com.lami.tuomatuo.core.model.manage.SysPermission;
import com.lami.tuomatuo.core.model.manage.SysRolePermission;
import com.lami.tuomatuo.core.service.manage.SysPermissionService;
import com.lami.tuomatuo.core.service.manage.SysRolePermissionService;
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
@RequestMapping("sysRolePermission")
public class SysRolePermissionController extends BaseAction<SysRolePermission,Integer>{
	private static final Logger logger = Logger.getLogger(SysRolePermissionController.class);
	
	private SysRolePermissionService sysRolePermissionService;

	@Autowired
	private SysPermissionService sysPermissionService;
	
	@Resource(name="sysRolePermissionService") 
	public void setSysRolePermissionService(SysRolePermissionService sysRolePermissionService) {
		this.sysRolePermissionService = sysRolePermissionService;
		super.setBaseService(sysRolePermissionService, "sysRolePermission");
	}
	
	@Override
	public Result validate(SysRolePermission form, HttpServletRequest request) {
		return new Result(Result.SUCCESS);
	}
	
	@Override
	public SysRolePermission getNewObject(){return new SysRolePermission();}
	
	@RequestMapping(value = "/preSetRolePermission.do")
	@ResponseBody
	public Map<String, Object> preSetRolePermission(HttpServletRequest request){
		Map<String, Object> result = new HashMap<String, Object>();
		//get role id
		String roleId = request.getParameter("roleId");
		if(roleId==null||"".equals(roleId)){
			result.put("result", false);
			result.put("msg","参数错误");
			return result;
		}
		//list selected permission
		String queryStr = "select a.* from sys_permission a,sys_role_permission b where b.roleId=? and b.permissionId=a.id";
		List<Object> param  = new ArrayList<Object>();
		param.add(roleId);

		List<SysPermission> selectedList = sysPermissionService.search(queryStr, param);
		result.put("selectedList", selectedList);
		
		//list all permission
		List<SysPermission> selectList = sysPermissionService.getAll();
		//remove selected permission
		Iterator<SysPermission> it = selectList.iterator();
		List<SysPermission> delList = new ArrayList<SysPermission>();
		while(it.hasNext()){
			SysPermission sp = it.next();
			for(int i=0;i<selectedList.size();i++){
				SysPermission tmp = selectedList.get(i);
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
	@RequestMapping(value = "/setRolePermission.do")
	@ResponseBody
	public Map<String, Object> setRolePermission(HttpServletRequest request){
		Map<String, Object> result = new HashMap<String, Object>();
		//get role id
		String roleId = request.getParameter("roleId");
		if(roleId==null||"".equals(roleId)){
			result.put("result", false);
			result.put("msg","参数错误");
			return result;
		}
		//list selected permission
		String selectedId = request.getParameter("ids");
		logger.info(selectedId);
		//remove role id bind permission ids;
		ArrayList<Object> p = new ArrayList<Object>();
		p.add(roleId);
		int res = sysRolePermissionService.execute("delete from sys_role_permission where roleId=?", p);
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
					SysRolePermission t = new SysRolePermission();
					t.setRoleId(Integer.parseInt(roleId));
					t.setPermissionId(Integer.parseInt(ids[i]));
					sysRolePermissionService.save(t);
				}
			}
		}
		result.put("result", true);
		result.put("msg","操作成功");
		return result;
	}
}