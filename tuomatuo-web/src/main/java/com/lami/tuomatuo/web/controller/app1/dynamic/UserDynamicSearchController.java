package com.lami.tuomatuo.web.controller.app1.dynamic;

import com.lami.tuomatuo.web.controller.BaseController;
import com.lami.tuomatuo.web.model.po.UserDynamicSearchParam;
import com.lami.tuomatuo.core.model.UserDynamic;
import com.lami.tuomatuo.core.model.base.Result;
import com.lami.tuomatuo.core.service.UserDynamicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xjk on 2016/1/26.
 */
@Controller
@RequestMapping(value = "/app1/dynamicSearch")
public class UserDynamicSearchController extends BaseController {

    @Autowired
    private UserDynamicService userDynamicService;

    @Override
    protected boolean checkAuth() {
        return true;
    }

    /**
     * 用户对动态的查询
     * @param httpServletRequest
     * @param httpServletResponse
     * @param getUserDynamicParam
     */
    @RequestMapping(value = "/searchVersonOne.form")
    @ResponseBody
    public Result searchVersonOne(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, @RequestBody UserDynamicSearchParam userDynamicSearchParam){
        Result result = execute(userDynamicSearchParam);
        if (Result.SUCCESS != result.getStatus()) return result;

        List<UserDynamic> userDynamicList = new ArrayList<UserDynamic>();
        String[] keys = userDynamicSearchParam.getContent().split(" ");
        for (String key : keys){
            List<UserDynamic> userDynamicListTemp = userDynamicService.searchDynamic(key, userDynamicSearchParam.getOffset(), userDynamicSearchParam.getPageSize());
            userDynamicList.addAll(userDynamicListTemp);
        }
        return new Result(Result.SUCCESS).setValue(userDynamicService.getUserDynamicVoByUserDynamic(userDynamicList));
    }
}
