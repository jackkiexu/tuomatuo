package com.lami.tuomatuo.servlet;

import com.lami.tuomatuo.model.po.web.ContentHolder;
import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * Created by xjk on 2016/8/25.
 */

public class InitServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(InitServlet.class);

    @Override
    public void init(ServletConfig config) throws ServletException {
        try{
            logger.info("init start.");
            super.init(config);
            ContentHolder.context = (WebApplicationContext) getServletConfig().getServletContext().getAttribute("org.springframework.web.servlet.FrameworkServlet.CONTEXT.springmvc");
            logger.info("init OK.");
        }catch(Exception e){
            logger.info(e.getMessage());
        }
    }
}