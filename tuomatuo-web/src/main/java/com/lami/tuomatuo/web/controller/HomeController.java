package com.lami.tuomatuo.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public void isLive(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){

    }
}
