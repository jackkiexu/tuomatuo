package com.lami.tuomatuo.manage.controller;

import com.lami.tuomatuo.manage.bean.ConstantMessage;
import com.lami.tuomatuo.manage.bean.ConstantModel;
import com.lami.tuomatuo.manage.bean.Menu;
import com.lami.tuomatuo.manage.helper.ImgCodeHelper;
import com.lami.tuomatuo.model.base.Result;
import com.lami.tuomatuo.model.manage.SysOperator;
import com.lami.tuomatuo.model.manage.SysOperatorLog;
import com.lami.tuomatuo.model.manage.SysPermission;
import com.lami.tuomatuo.service.UserService;
import com.lami.tuomatuo.service.manage.*;
import com.lami.tuomatuo.utils.DateUtils;
import com.lami.tuomatuo.utils.StringUtil;
import com.lami.tuomatuo.utils.constant.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
@RequestMapping("")
public class IndexController {
	private final static Logger logger = LoggerFactory.getLogger(IndexController.class);
	@Autowired
	private ConstantModel cmodel;
	@Autowired
	private ConstantMessage cmessage;
	@Autowired
	private SysRolePermissionService sysRolePermissionService;
	@Autowired
	private SysPermissionService sysPermissionService;
	@Autowired
	private SysOperatorService sysOperatorService;
	@Autowired
	private SysOperatorRoleService sysOperatorRoleService;
	@Autowired
	private UserService userService;
	@Autowired
	private SysOperatorLogService sysOperatorLogService;
	

	@RequestMapping("login.do")
	public String login(HttpServletRequest request,HttpServletResponse response) throws Exception{
		request.setAttribute("sessionId", request.getSession().getId());
		return ("login");
	}
	@RequestMapping("logout.do")
	public String logout(HttpServletRequest request,HttpServletResponse response) throws Exception{
		HttpSession session = request.getSession();
		session.setAttribute("admin",null);
		
		session=request.getSession(true);
		/**修复会话标识未更新漏洞 结束*/
		response.addHeader("Cache-Control", "no-store");
		response.addHeader("Pragma", "no-cache");
		response.addDateHeader("Expires", 30);
		/**浏览器端不缓冲页面*/
		return ("login.do");
	}
	@RequestMapping("vn.do")
	public void vn(HttpServletRequest request,HttpServletResponse response) throws Exception{
		response.addHeader("Cache-Control", "no-store");
		response.addHeader("Pragma", "no-cache");
		response.addDateHeader("Expires", 30);
		/**浏览器端不缓冲页面*/
		
		HttpSession session = request.getSession();
		ServletOutputStream os = response.getOutputStream();
		ImgCodeHelper.getAdminImg(session, os);
		os.close();
	}
	@RequestMapping("checkLogin.do")
	@ResponseBody
	public Map<String, Object> checkLogin(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Map<String, Object> result = new HashMap<String, Object>();
		HttpSession session = request.getSession();
		/**防止跨站点请求伪造*/
		String clientSessionId = request.getParameter("ssid");
		String serverSessionId = request.getSession().getId();
        if (!serverSessionId.equalsIgnoreCase(clientSessionId)) {
        	result.put("result", false);
			result.put("errorInfo", "session校验错误！");
			logger.error(result.toString());
			return result;
        }
        /**防止跨站点请求伪造结束*/
		
		String login = request.getParameter("login");
		String password = request.getParameter("password");
		String code = request.getParameter("code");
		if(!ImgCodeHelper.checkAdminImgCode(session, code)){
			result.put("result", false);
			result.put("errorInfo", "验证码错误");
			logger.error(result.toString());
			return result;
		}
		
		
		SysOperator t = new SysOperator();
		t.setLogin(login);
		SysOperator operator = sysOperatorService.searchOne(t);
		if(operator==null){
			result.put("result", false);
			result.put("errorInfo", "帐号错误");
			logger.error(result.toString());
			return result;
		}
		if(!operator.getPassword().equals(password)){
			result.put("result", false);
			result.put("errorInfo", "密码错误");
			logger.error(result.toString());
			return result;
		}
		if(operator.getStatus()!=null&&operator.getStatus().intValue()!=SysOperator.STATUS_SUCCESS){
			result.put("result", false);
			result.put("errorInfo", "帐号禁止使用");
			logger.error(result.toString());
			return result;
		}
		//当登陆帐号需要ip验证时判断
		if(operator!=null&&operator.getCheckIp()!=null&&operator.getCheckIp().intValue()==SysOperator.CHECKIP_1){
			String operator_ip = getIp(request);
			if(operator_ip==null||operator.getIp().indexOf(operator_ip)<0){
				result.put("result", false);
				result.put("errorInfo", "访问地址受限，联系管理员");
				logger.error(result.toString());
				return result;
			}
		}
		
		result.put("result", true);
		result.put("errorInfo", "通过验证");
		session.setAttribute("admin", operator);
		
		operator.setLastLoginTime(new Date());
		sysOperatorService.update(operator);
		WebUtils.setSessionAttribute(request, "username", t.getName());
		logger.info(result.toString());
		return result;
	}
	@RequestMapping("index.do")
	public String index(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Map<String, Object> result = new HashMap<String, Object>();
		HttpSession session = request.getSession();
		SysOperator operator = (SysOperator)session.getAttribute("admin");
		if(operator==null){
			result.put("result", false);
			result.put("errorInfo", "session为空");
			logger.error(result.toString());
			return ("login.do");
		}
		//获取全局的数据
		Map<String, Object> map = new HashMap<String, Object>();

		logger.info("" + map);
		request.setAttribute("map", map);
		//超级管理员处理模式
		if(operator!=null&&operator.getLogin().equals("root")){
			List<SysPermission> plist = sysPermissionService.getAll();
			request.getSession().setAttribute("permissionList", plist);
			
			List<Menu> list = getMenuList(plist);
			request.setAttribute("menu", list);
			return ("index");
		}
		
		String queryStr = "select a.* from sys_permission a,sys_role_permission b,sys_operator_role c where c.operatorId=? and c.roleId=b.roleId and b.permissionId=a.id";
		List<Object> param  = new ArrayList<Object>();
		param.add(operator.getId());
		List<SysPermission> plist = sysPermissionService.search(queryStr, param);
		session.setAttribute("permissionList", plist);
		
		List<Menu> list = getMenuList(plist);				
		request.setAttribute("menu", list);
		return ("index");
	}

	@SuppressWarnings("unused")
	@RequestMapping(value="upSystemAnnounce.do", method=RequestMethod.POST)
	@ResponseBody
	public Result upSystemAnnounce(HttpServletRequest request,HttpServletResponse response){
		String system_announce_category = request.getParameter("system_announce_category");
		String system_announce_theme = request.getParameter("system_announce_theme");
		String system_announce_content = request.getParameter("system_announce_content");
		String system_announce_receive_category = request.getParameter("system_announce_receive_category");
		String system_announce_receive_ids = request.getParameter("system_announce_receive_ids");
		String system_announce_push_start_time = request.getParameter("system_announce_push_start_time");
		String system_announce_push_end_time = request.getParameter("system_announce_push_end_time");
		if(system_announce_category == null ||
				system_announce_theme == null ||
				system_announce_content == null ||
				system_announce_receive_category == null ||
				system_announce_push_start_time == null ||
				system_announce_push_end_time == null){
			return new Result(Result.PARAMCHECKERROR);
		}
		SysOperator operator = (SysOperator)request.getSession().getAttribute("admin");
		if(operator.getId() != Constant.TRUE_INT)return new Result(Result.PERMISSIONS_DENY);
		SysOperatorLog sysOperatorLog = new SysOperatorLog();
		sysOperatorLog.setActive(system_announce_category+"|"+system_announce_theme+"|"+system_announce_content+"|"+
				system_announce_receive_category + "|" + system_announce_receive_ids + "|" + system_announce_push_start_time + "|"+
				system_announce_push_end_time);
		sysOperatorLog.setCreateTime(new Date());
		if(operator!=null)sysOperatorLog.setOperatorId(operator.getId());
		sysOperatorLogService.save(sysOperatorLog);

		SystemAnnounce systemAnnounce = new SystemAnnounce(Integer.parseInt(system_announce_category), system_announce_theme, system_announce_content);
		Date pushTime = DateUtils.parser(system_announce_push_start_time, "yyyy-MM-dd HH:mm:ss");
		Date endTime = DateUtils.parser(system_announce_push_end_time, "yyyy-MM-dd HH:mm:ss");
		if(pushTime == null || endTime == null)new Result(Result.PARAMCHECKERROR);
		Integer category = Integer.parseInt(system_announce_receive_category);
		String[] ids = null;
		if(category == Constant.NOTIFY_SCOPE_PART){
			ids = system_announce_receive_ids.split(",");
		}
		try {
			Set<Integer> list = new HashSet<Integer>();
			if(ids != null){
				for(String id : ids){
					if(StringUtil.isNumeric(id)){
						list.add(Integer.parseInt(id));
					}
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return new Result(Result.SUCCESS);
	}

	private List<Menu> getMenuList(List<SysPermission> pList){
		
		List<Menu> list = new ArrayList<Menu>();
		
		if(pList==null||pList.size()<=0)
			return list;
		
		Map<Integer,Menu> map = new HashMap<Integer,Menu>();
		for(int i=0;i<pList.size();i++){
			SysPermission sp = pList.get(i);
			if(sp!=null&&sp.getMenu()!=null&&sp.getMenu().equals("-1")){
				Menu menu = new Menu();
				menu.setId(sp.getId());
				menu.setName(sp.getName());
				TreeMap<String,String> map1 = new TreeMap<String,String>();
				menu.setMenuList(map1);
				map.put(sp.getId(), menu);
			}
		}
		for(int i=0;i<pList.size();i++){
			SysPermission sp = pList.get(i);
			if(sp!=null&&sp.getPath()!=null
					&&sp.getMethods()!=null
					&&sp.getMenu()!=null
					&&sp.getMenu().equals("1")
					&&sp.getParentId()!=null
					&&sp.getParentId().intValue()>0){
				String purl = "/"+sp.getPath()+"/"+sp.getMethods()+".do";
				//urlSet.add(purl);
				Menu m = map.get(sp.getParentId());
				if(m==null){
					SysPermission sps = sysPermissionService.get(sp.getParentId());
					if(sps!=null){
						Menu menu = new Menu();
						menu.setId(sps.getId());
						menu.setName(sps.getName());
						TreeMap<String,String> map1 = new TreeMap<String,String>();
						menu.setMenuList(map1);
						map.put(sps.getId(), menu);
					}
				}
				
				if(m!=null){
					TreeMap<String,String> tm = m.getMenuList();
					tm.put(sp.getName(), purl);
					map.put(sp.getParentId(), m);
				}
			}
		}
		
		for(Integer key:map.keySet()){
			list.add(map.get(key));
		}
		return list;
	}
	
	
		 
    public String getIp(HttpServletRequest request){
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
 
    }
}
