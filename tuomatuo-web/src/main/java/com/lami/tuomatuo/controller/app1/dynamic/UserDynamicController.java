package com.lami.tuomatuo.controller.app1.dynamic;

import com.lami.tuomatuo.controller.BaseController;
import com.lami.tuomatuo.model.UserDynamic;
import com.lami.tuomatuo.model.base.Result;
import com.lami.tuomatuo.model.po.GetFriendParam;
import com.lami.tuomatuo.model.po.GetUserDynamicParam;
import com.lami.tuomatuo.service.UserDynamicService;
import com.lami.tuomatuo.utils.GeoHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by xjk on 2016/1/18.
 */
@Controller
@RequestMapping(value = "/app1/dynamic")
public class UserDynamicController extends BaseController {

    @Autowired
    private UserDynamicService userDynamicService;

    @Override
    protected boolean checkAuth() {
        return true;
    }

    /**
     * 用户查询自己的动态信息
     * @param httpServletRequest
     * @param httpServletResponse
     * @param getUserDynamicParam
     */
    @RequestMapping(value = "/getoOwnDynamic.form")
    @ResponseBody
    public Result getDynamic(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, @RequestBody GetUserDynamicParam getUserDynamicParam){
        Result result = execute(getUserDynamicParam);
        if (Result.SUCCESS != result.getStatus()) return result;

        List<UserDynamic> userDynamicList = userDynamicService.getUserDynamic(getUserDynamicParam.getOffset(), getUserDynamicParam.getPageSize());
        return new Result(Result.SUCCESS).setValue(userDynamicList);
    }

    /**
     * 用户查询当前最热的动态
     * @param httpServletRequest
     * @param httpServletResponse
     * @param getUserDynamicParam
     */
    @RequestMapping(value = "/getHotDynamic.form")
    @ResponseBody
    public Result getHotDynamic(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, @RequestBody GetUserDynamicParam getUserDynamicParam){
        Result result = execute(getUserDynamicParam);
        if (Result.SUCCESS != result.getStatus()) return result;

        List<UserDynamic> userDynamicList = userDynamicService.getHotDynamic(getUserDynamicParam.getOffset(), getUserDynamicParam.getPageSize());
        return new Result(Result.SUCCESS).setValue(userDynamicService.getUserDynamicVoByUserDynamic(userDynamicList));
    }

    /**
     * 用户查询 与自己相近的用户的动态信息
     * @param httpServletRequest
     * @param httpServletResponse
     * @param getUserDynamicParam
     */
    @RequestMapping(value = "/getNearbyDynamic.form")
    @ResponseBody
    public Result getNearbyDynamic(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, @RequestBody GetUserDynamicParam getUserDynamicParam){
        Result result = execute(getUserDynamicParam);
        if (Result.SUCCESS != result.getStatus()) return result;
        Double latitude = null;
        Double longitude = null;
        try {
            latitude = Double.parseDouble(getUserDynamicParam.getLatitude());
            longitude = Double.parseDouble(getUserDynamicParam.getLongitude());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            new Result(Result.PARAMCHECKERROR);
        }

        String geoHash = GeoHash.geInstance().encode(latitude, longitude);
        List<UserDynamic> userDynamicList = userDynamicService.getUserNearbyDynamic(geoHash, getUserDynamicParam.getOffset(), getUserDynamicParam.getPageSize());
        return new Result(Result.SUCCESS).setValue(userDynamicService.getUserDynamicVoByUserDynamic(userDynamicList));
    }
}