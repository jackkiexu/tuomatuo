package com.lami.tuomatuo.controller.app1;

import com.lami.tuomatuo.controller.BaseController;
import com.lami.tuomatuo.model.Friend;
import com.lami.tuomatuo.model.po.GetFriendParam;
import com.lami.tuomatuo.model.po.GetNearbyFriends;
import com.lami.tuomatuo.model.po.LoginParam;
import com.lami.tuomatuo.model.vo.FriendVo;
import com.lami.tuomatuo.model.vo.UserInfoVo;
import com.lami.tuomatuo.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by xujiankang on 2016/1/18.
 */
@Controller
@RequestMapping(value = "/app1/friend")
public class FriendController  extends BaseController {

    @Autowired
    private FriendService friendService;

    @Override
    protected boolean checkAuth() {
        return false;
    }

    /**
     * 获取用户个人的朋友信息
     * @param httpServletRequest
     * @param httpServletResponse
     * @param getFriendParam
     * @return
     */
    @RequestMapping(value = "/getFriends.form")
    @ResponseBody
    public List<FriendVo> getFriends(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, @RequestBody GetFriendParam getFriendParam){
        return friendService.getFriendsByUserId(getFriendParam.getUserId());
    }

    /**
     * 获取推荐系统推荐给用户的朋友信息
     * @param httpServletRequest
     * @param httpServletResponse
     * @param getFriendParam
     * @return
     */
    @RequestMapping(value = "/getRecommendedFriends.form")
    @ResponseBody
    public List<FriendVo> getRecommendedFriends(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, @RequestBody GetFriendParam getFriendParam){
        return friendService.getFriendsByUserId(getFriendParam.getUserId());
    }

    /**
     * @param httpServletRequest
     * @param httpServletResponse
     * @param getFriendParam
     * @return
     */
    @RequestMapping(value = "/getNearbyFriends.form")
    @ResponseBody
    public List<FriendVo> getNearbyFriends(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, @RequestBody GetNearbyFriends getNearbyFriends){
        return friendService.getFriendsByUserId(getNearbyFriends.getUserId());
    }
}