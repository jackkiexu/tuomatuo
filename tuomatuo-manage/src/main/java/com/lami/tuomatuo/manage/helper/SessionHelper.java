package com.lami.tuomatuo.manage.helper;


import com.lami.tuomatuo.model.User;
import com.lami.tuomatuo.utils.constant.Constant;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionHelper {
	
	public User getUserSession(HttpServletRequest request){
		HttpSession session=request.getSession();
		if(session!=null){
			return (User)session.getAttribute(Constant.SESSION_USER);
		}
		return null;
	}
	public void setUserSession(HttpServletRequest request,User user){
		HttpSession session=request.getSession(true);
		if(user!=null){
			session.setAttribute(Constant.SESSION_USER,user);
		}else{
			session.removeAttribute(Constant.SESSION_USER);
		}
	}

}
