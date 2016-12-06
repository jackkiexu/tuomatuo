package com.lami.tuomatuo.web.controller.app1.me;

import com.lami.tuomatuo.core.service.*;
import com.lami.tuomatuo.web.model.po.UploadUserPosition;
import com.lami.tuomatuo.web.controller.BaseController;
import com.lami.tuomatuo.core.model.User;
import com.lami.tuomatuo.core.model.UserDynamic;
import com.lami.tuomatuo.core.model.UserProperty;
import com.lami.tuomatuo.core.model.base.Result;
import com.lami.tuomatuo.web.model.po.LoginParam;
import com.lami.tuomatuo.web.model.po.UpdateUserInfoParam;
import com.lami.tuomatuo.core.model.vo.UserDynamicVo;
import com.lami.tuomatuo.core.model.vo.UserInfoVo;
import com.lami.tuomatuo.core.model.vo.UserPositionVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/** http://www.wubiao.info/401
 * Created by xjk on 2016/1/18.
 */
@Controller
@RequestMapping(value = "/app1/me")
public class MeController extends BaseController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserDynamicService userDynamicService;
    @Autowired
    private UserPropertyService userPropertyService;
    @Autowired
    private UserPositionService userPositionService;
    @Autowired
    private MobileAccountService mobileAccountService;

    @Override
    protected boolean checkAuth() {
        return true;
    }

    /**
     * get user informations by userId
     * @param httpServletRequest
     * @param httpServletResponse
     * @param loginParam
     * @return
     */
    @RequestMapping(value = "/getInfo.form")
    @ResponseBody
    public UserInfoVo getInfo(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, @RequestBody LoginParam loginParam){
        User user = userService.get(loginParam.getUserId());
        UserProperty userProperty = userPropertyService.getUserPropertyByUserId(loginParam.getUserId());
        List<UserDynamic> userDynamicList = userDynamicService.getUserDynamicByUserId(loginParam.getUserId());
        List<UserDynamicVo> userDynamicVoList = userDynamicService.getUserDynamicVoByUserDynamic(userDynamicList);

        UserInfoVo userInfoVo = new UserInfoVo(user, userProperty);
        userInfoVo.setUserDynamicVoList(userDynamicVoList);
        return userInfoVo;
    }

    /**
     * user update self position to database
     * @param httpServletRequest
     * @param httpServletResponse
     * @param uploadUserPosition
     */
    @RequestMapping(value="/uploadUserPosition.form")
    public void updateUserPosition(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, @RequestBody UploadUserPosition uploadUserPosition){
        UserPositionVo userPositionVo = new UserPositionVo(uploadUserPosition.getUserId(), uploadUserPosition.getLongitude(), uploadUserPosition.getLatitude());
        userPositionService.uploadUserPosition(userPositionVo);
    }

    /**
     * user update self position to redis
     * @param httpServletRequest
     * @param httpServletResponse
     * @param uploadUserPosition
     */
    @RequestMapping(value="/uploadUserPositionToMem.form")
    public void uploadUserPositionToMem(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, @RequestBody UploadUserPosition uploadUserPosition){
        UserPositionVo userPositionVo = new UserPositionVo(uploadUserPosition.getUserId(), uploadUserPosition.getLongitude(), uploadUserPosition.getLatitude());
        userPositionService.uploadUserPositionToMem(userPositionVo);
    }

    /**
     * user update self properties
     * @param httpServletRequest
     * @param httpServletResponse
     * @param uploadUserPosition
     */
    @RequestMapping(value="/updateUserInfo.form")
    public Result updateUserInfo(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, @RequestBody UpdateUserInfoParam updateUserInfoParam){
        Result result = execute(updateUserInfoParam);
        if (Result.SUCCESS != result.getStatus()) return result;
        User user = (User)result.getValue();

        mobileAccountService.updateMobileAccount(user.getThirdAccountId(),updateUserInfoParam.getNick(), updateUserInfoParam.getImgUrl(), updateUserInfoParam.getAge(), updateUserInfoParam.getSex(), user.getId());
        return new Result(Result.SUCCESS);
    }

}