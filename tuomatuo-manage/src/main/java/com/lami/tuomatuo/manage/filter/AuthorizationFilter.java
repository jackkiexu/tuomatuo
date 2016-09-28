package com.lami.tuomatuo.manage.filter;


import com.lami.tuomatuo.manage.helper.ContentHolder;
import com.lami.tuomatuo.model.manage.SysOperator;
import com.lami.tuomatuo.model.manage.SysOperatorLog;
import com.lami.tuomatuo.model.manage.SysPermission;
import com.lami.tuomatuo.service.manage.SysOperatorLogService;
import com.lami.tuomatuo.utils.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.util.WebUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AuthorizationFilter implements Filter {
	
	private final static Logger logger = org.apache.log4j.Logger.getLogger(AuthorizationFilter.class);
	static ExecutorService pool = Executors.newSingleThreadExecutor(); 
	private String loginUrl;
	private List<String> ignoreURIs = new ArrayList<String>();
	static ObjectMapper mapper = new ObjectMapper();
	static {
		
	}
	class LogThread extends Thread {
		SysOperatorLog t;
		public LogThread(SysOperatorLog log){
			this.t = log;
		}
	    @Override
	    public void run() {
	    	try{
	    		SysOperatorLogService ols = (SysOperatorLogService) ContentHolder.context.getBean("sysOperatorLogService");
	    		ols.save(t);
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}
	    }
	}
	public void init(FilterConfig config) throws ServletException {
		if(null == loginUrl) {
			loginUrl = "/login.do";
		}
		//某些URL前缀不予处理（例如 /login.do）
		String ignores = config.getInitParameter("ignore");
		if(ignores != null)
		for(String ig : StringUtils.split(ignores, ','))
			ignoreURIs.add(ig.trim());
	}
	@SuppressWarnings("unchecked")
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {		
		HttpServletResponse response = (HttpServletResponse)res;
		HttpServletRequest request = (HttpServletRequest)req;
		
		String path = request.getContextPath();
		String req_uri = request.getRequestURI();
		Object username = WebUtils.getSessionAttribute(request, "operator");
		logger.info("path:" + path + ", req_uri:" + req_uri + ", username:" + username);
		try {
			//过滤URL前缀
			for(String ignoreURI : ignoreURIs){
				if(req_uri.indexOf(ignoreURI)>=0){
					logger.info(req_uri+"|"+ignoreURI+" pass!");
					chain.doFilter(request, response);
					return ;
				}
			}
			//判断是否有权限
			HttpSession session = request.getSession();
			
			List<SysPermission> plist = (List<SysPermission>)session.getAttribute("permissionList");
			logger.info(plist);
			if(plist!=null&&plist.size()>0){
				for(int i=0;i<plist.size();i++){
					SysPermission sp = plist.get(i);
					if(sp!=null&&sp.getPath()!=null&&sp.getMethods()!=null){
						String purl = "/"+sp.getPath()+"/"+sp.getMethods()+".do";
						logger.info(purl);
						if(req_uri.indexOf(purl)>=0){
							logger.info(request.getRemoteAddr()+":"+req_uri+" pass!");
							try{
								if(!sp.getMethods().toLowerCase().equals("presearch")){
									SysOperator operator = (SysOperator)session.getAttribute("admin");
									
									SysOperatorLog t = new SysOperatorLog();
									t.setOperatorId(operator.getId());
									t.setIsDelete(0);
									t.setCreateTime(new Date());
									String active = operator.getLogin()+":"+sp.getName()+","+sp.getPath()+","+sp.getMethods();
									if(!sp.getMethods().toLowerCase().startsWith("search")){
										active=active+";"+getParam(request);
									}
									t.setActive(StringUtil.trim(active, 500));
									LogThread lt = new LogThread(t);
									lt.setDaemon(true);
									pool.execute(lt);
								}
							}catch(Exception e){
								e.printStackTrace();
							}
							chain.doFilter(request, response);
							logger.info("chain.doFilter(request, response)");
							return ;
						}
					}
				}
			}

			logger.info("username:"+username +", SysPermissionList is null");
			if(StringUtils.isEmpty(username.toString()) && (request.getHeader("x-requested-with")!=null&&request.getHeader("x-requested-with").toLowerCase().indexOf("xmlhttprequest")>=0)){
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("result", false);
				result.put("msg", "登录超时，请重新登录");
				result.put("value", "nologin");
				response.setContentType("application/json;charset=UTF-8");
				response.setCharacterEncoding("UTF-8");
				PrintWriter out = response.getWriter();
				out.write(mapper.writeValueAsString(result));
				out.flush();
				out.close();
				return;
			}else{
				response.sendRedirect(path+loginUrl);
			}
		} catch (Exception ex) {
			response.sendRedirect(path+loginUrl);
		}
	}
	private String getParam(HttpServletRequest request){
		Map<String, Object> map = request.getParameterMap();
		Map<String, Object> param = new HashMap<String,Object>();
		Map.Entry entry;
		String key = "";
		String value = "";
		if (map != null && map.size() > 0) {
			Iterator entries = map.entrySet().iterator();
			while (entries.hasNext()) {
				entry = (Map.Entry) entries.next();
				key = (String) entry.getKey();
				Object valueObj = entry.getValue();
				if (null == valueObj) {
					value = "";
				} else if (valueObj instanceof String[]) {
					String[] values = (String[]) valueObj;
					for (int j = 0; j < values.length; j++) {
						value = values[j] + ",";
					}
					value = value.substring(0, value.length() - 1);
				} else {
					value = valueObj.toString();
				}
				param.put(key, value);
			}
		}
		return param.toString();
	}
	
	public void destroy() {
	}

}
