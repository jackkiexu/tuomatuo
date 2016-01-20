package com.lami.tuomatuo.controller.app1;

import com.lami.tuomatuo.controller.BaseController;
import com.lami.tuomatuo.model.po.GetFriendParam;
import com.lami.tuomatuo.model.po.GetUserDynamicParam;
import com.lami.tuomatuo.service.UserDynamicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by xujiankang on 2016/1/18.
 */
@Controller
@RequestMapping(value = "/app1/dynamic")
public class UserDynamicController extends BaseController {

    @Autowired
    private UserDynamicService userDynamicService;

    @Override
    protected boolean checkAuth() {
        return false;
    }

    /**
     * 用户查询自己的动态信息
     * @param httpServletRequest
     * @param httpServletResponse
     * @param getUserDynamicParam
     */
    @RequestMapping(value = "/getDynamic.form")
    @ResponseBody
    public void getDynamic(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, @RequestBody GetUserDynamicParam getUserDynamicParam){

    }

    /**
     * 用户查询当前最热的动态
     * @param httpServletRequest
     * @param httpServletResponse
     * @param getUserDynamicParam
     */
    @RequestMapping(value = "/getHotDynamic.form")
    @ResponseBody
    public void getHotDynamic(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, @RequestBody GetUserDynamicParam getUserDynamicParam){

    }

    /**
     * 用户查询 与自己相近的用户的动态信息
     * @param httpServletRequest
     * @param httpServletResponse
     * @param getUserDynamicParam
     */
    @RequestMapping(value = "/getNearbyDynamic.form")
    @ResponseBody
    public void getNearbyDynamic(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, @RequestBody GetUserDynamicParam getUserDynamicParam){

    }
}