package com.lami.tuomatuo.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by xjk on 10/21/15.
 */
@Controller
public class HomeController extends BaseController {


    @Override
    protected boolean checkAuth() {
        return false;
    }

    @RequestMapping(value = "/isLive.form")
    @ResponseBody
    public String isLive(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        return "OK";
    }
}
