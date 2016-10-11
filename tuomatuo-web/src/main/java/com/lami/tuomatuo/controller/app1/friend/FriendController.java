package com.lami.tuomatuo.controller.app1.friend;

import com.lami.tuomatuo.controller.BaseController;
import com.lami.tuomatuo.model.Friend;
import com.lami.tuomatuo.model.User;
import com.lami.tuomatuo.model.base.Result;
import com.lami.tuomatuo.model.po.AddFriend;
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
 * Created by xjk on 2016/1/18.
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
     * 根据关键字查询朋友
     * @param httpServletRequest
     * @param httpServletResponse
     * @param getFriendParam
     * @return
     */
    @RequestMapping(value = "/getSearchFriends.form")
    @ResponseBody
    public List<FriendVo> getRecommendedFriends(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, @RequestBody GetFriendParam getFriendParam){
        return friendService.searchFriend(getFriendParam.getContent(), getFriendParam.getOffset(), getFriendParam.getPageSize());
    }

    /** 添加朋友
     * @param httpServletRequest
     * @param httpServletResponse
     * @param getFriendParam
     * @return
     */
    @RequestMapping(value = "/followFriend.form")
    @ResponseBody
    public Result followFriend(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, @RequestBody AddFriend addFriend){
        Result result = execute(addFriend);
        if (Result.SUCCESS != result.getStatus()) return result;
        User user = (User)result.getValue();
        return new Result(Result.SUCCESS).setValue(friendService.followFriend(user.getId(), addFriend.getFriendList()));
    }

    /** 删除朋友
     * @param httpServletRequest
     * @param httpServletResponse
     * @param getFriendParam
     * @return
     */
    @RequestMapping(value = "/unfollowFriend.form")
    @ResponseBody
    public Result unfollowFriend(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, @RequestBody AddFriend addFriend){
        Result result = execute(addFriend);
        if (Result.SUCCESS != result.getStatus()) return result;
        User user = (User)result.getValue();
        friendService.unFollowFriend(user.getId(), addFriend.getFriendId());
        return new Result(Result.SUCCESS);
    }
}