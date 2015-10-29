package com.lami.tuomatuo.controller;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by xjk on 10/21/15.
 */
public abstract class BaseController {

    protected static final Logger logger = Logger.getLogger(BaseController.class);

    protected abstract boolean checkAuth();

    protected  boolean execute(HttpServletRequest request, HttpServletResponse response){
        if (checkAuth()){
            // TODO some check auth
        }
        return false;
    }
}
