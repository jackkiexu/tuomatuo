package com.lami.tuomatuo.crawler.servlet;

import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by xujiankang on 2016/3/21.
 */
public class LoginServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(LoginServlet.class);

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("Login Servlet do Post");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("Login Servlet do Get");
    }
}