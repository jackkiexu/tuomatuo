package com.lami.tuomatuo.controller.app1;

import com.lami.tuomatuo.controller.BaseController;
import com.lami.tuomatuo.model.User;
import com.lami.tuomatuo.model.UserProperty;
import com.lami.tuomatuo.model.po.LoginParam;
import com.lami.tuomatuo.service.UserPropertyService;
import com.lami.tuomatuo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by xujiankang on 2016/1/18.
 */
@Controller
@RequestMapping(value = "/app1/me")
public class MeController extends BaseController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserPropertyService userPropertyService;

    @Override
    protected boolean checkAuth() {
        return false;
    }

    @RequestMapping(value = "/getInfo.form")
    public void getInfo(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, @RequestBody LoginParam loginParam){
        User user = userService.get(loginParam.getUserId());
        UserProperty userProperty = userPropertyService.getUserProperty(loginParam.getUserId());
    }

}