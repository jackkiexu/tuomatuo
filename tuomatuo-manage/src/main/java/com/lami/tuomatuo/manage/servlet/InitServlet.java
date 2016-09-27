package com.lami.tuomatuo.manage.servlet;

import com.lami.tuomatuo.manage.bean.Comment;
import com.lami.tuomatuo.manage.helper.ContentHolder;
import org.springframework.web.context.WebApplicationContext;
//import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

public class InitServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	
	public void init(ServletConfig config) throws ServletException{
		super.init(config);
		ContentHolder.context = (WebApplicationContext)getServletConfig().getServletContext().getAttribute("org.springframework.web.servlet.FrameworkServlet.CONTEXT.springmvc");
		ContentHolder.comment = ContentHolder.context.getBean("comment",Comment.class);
		
	}

}
