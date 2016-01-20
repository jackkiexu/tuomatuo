package com.lami.tuomatuo.controller.app1;

import com.lami.tuomatuo.controller.BaseController;
import com.lami.tuomatuo.model.User;
import com.lami.tuomatuo.model.UserDynamic;
import com.lami.tuomatuo.model.UserProperty;
import com.lami.tuomatuo.model.po.LoginParam;
import com.lami.tuomatuo.model.po.UploadUserPosition;
import com.lami.tuomatuo.model.vo.UserDynamicVo;
import com.lami.tuomatuo.model.vo.UserInfoVo;
import com.lami.tuomatuo.model.vo.UserPositionVo;
import com.lami.tuomatuo.service.UserDynamicService;
import com.lami.tuomatuo.service.UserPositionService;
import com.lami.tuomatuo.service.UserPropertyService;
import com.lami.tuomatuo.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/** http://www.wubiao.info/401
 * Created by xujiankang on 2016/1/18.
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

    @Override
    protected boolean checkAuth() {
        return false;
    }

    /**
     * get user informations by userId
     *
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
        List<UserDynamicVo> userDynamicVoList = new ArrayList<UserDynamicVo>();
        for(UserDynamic userDynamic : userDynamicList){
            userDynamicVoList.add(new UserDynamicVo(userDynamic));
        }
        UserInfoVo userInfoVo = new UserInfoVo(user, userProperty);
        userInfoVo.setUserDynamicVoList(userDynamicVoList);
        return userInfoVo;
    }

    /**
     * 用户上传自己地理位置的信息
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
     * 用户上传自己地理位置的信息 -> redis
     * @param httpServletRequest
     * @param httpServletResponse
     * @param uploadUserPosition
     */
    @RequestMapping(value="/uploadUserPositionToMem.form")
    public void uploadUserPositionToMem(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, @RequestBody UploadUserPosition uploadUserPosition){
        UserPositionVo userPositionVo = new UserPositionVo(uploadUserPosition.getUserId(), uploadUserPosition.getLongitude(), uploadUserPosition.getLatitude());
        userPositionService.uploadUserPositionToMem(userPositionVo);
    }
}