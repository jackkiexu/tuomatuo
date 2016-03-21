package com.lami.tuomatuo.crawler.controller.ui;

import com.lami.tuomatuo.crawler.controller.BaseController;
import com.lami.tuomatuo.service.crawler.UIAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by xujiankang on 2016/3/21.
 */
@Controller
@RequestMapping(value = "/ui")
public class UIAccountController extends BaseController {

    @Autowired
    private UIAccountService uiAccountService;

    @RequestMapping(value = "/crawlerAccountInfo.form")
    public void isLive(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        logger.info("getParam:"+getParam(httpServletRequest)+", getBody:"+getBody(httpServletRequest));

        for(;;){

        }
    }
}
