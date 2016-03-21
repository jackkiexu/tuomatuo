package com.lami.tuomatuo.crawler.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by xujiankang on 2016/3/21.
 */
@Controller
public class HomeController extends BaseController {

    @RequestMapping(value = "/isLive.form")
    public void isLive(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        logger.info("getParam:"+getParam(httpServletRequest)+", getBody:"+getBody(httpServletRequest));
    }
}
