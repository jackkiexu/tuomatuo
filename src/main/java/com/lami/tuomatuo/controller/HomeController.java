package com.lami.tuomatuo.controller;

import com.lami.tuomatuo.mapper.write.UserMapper;
import com.lami.tuomatuo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by xjk on 10/21/15.
 */
@Controller
public class HomeController extends BaseController {

    @Autowired
    private UserMapper userMapper;

    @Override
    protected boolean checkAuth() {
        return false;
    }

    @RequestMapping(value = "/isLive.form")
    public void isLive(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        try {
            httpServletResponse.getOutputStream().write("OK".getBytes("UTF-8"));
            User user = userMapper.getUserById(9);
            logger.info(user);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
    }
}
