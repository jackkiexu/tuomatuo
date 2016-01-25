package com.lami.tuomatuo.controller;

import com.lami.tuomatuo.model.User;
import com.lami.tuomatuo.model.base.Result;
import com.lami.tuomatuo.model.po.BaseParam;
import com.lami.tuomatuo.service.UserService;
import com.lami.tuomatuo.utils.StringUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by xjk on 10/21/15.
 */
public abstract class BaseController {

    @Autowired
    private UserService userService;

    protected static final Logger logger = Logger.getLogger(BaseController.class);
    protected abstract boolean checkAuth();

    protected  Result execute(BaseParam baseParam){
        if (!checkAuth()) return new Result(Result.SUCCESS);
        if(!StringUtil.isMobile(baseParam.getMobile()) || StringUtil.isEmpty(baseParam.getSign())) return new Result(Result.PARAMCHECKERROR);
        User user = userService.getUserByMobile(baseParam.getMobile());
        if (!user.getSign().equals(baseParam.getSign())){
            return new Result(Result.ACCOUNT_ILLEGAL);
        }
        return new Result(Result.SUCCESS);
    }
}
