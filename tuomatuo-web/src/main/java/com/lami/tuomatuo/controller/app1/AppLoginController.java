package com.lami.tuomatuo.controller.app1;

import com.lami.tuomatuo.controller.BaseController;
import com.lami.tuomatuo.model.po.LoginParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by xujiankang on 2016/1/18.
 */
@Controller
@RequestMapping(value = "/app1/login")
public class AppLoginController extends BaseController{

    @Override
    protected boolean checkAuth() {
        return false;
    }

    @RequestMapping(value = "/login.form")
    public void login(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, @RequestBody LoginParam loginParam){
        try {
            logger.info("loginResult:"+loginParam);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    @RequestMapping(value = "/lgout.form")
    public void lgout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        try {
            logger.info("loginResult:");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

}
