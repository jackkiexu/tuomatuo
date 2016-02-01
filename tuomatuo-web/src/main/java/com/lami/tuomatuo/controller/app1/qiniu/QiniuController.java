package com.lami.tuomatuo.controller.app1.qiniu;

import com.lami.tuomatuo.controller.BaseController;
import com.lami.tuomatuo.model.User;
import com.lami.tuomatuo.model.UserDynamic;
import com.lami.tuomatuo.model.UserProperty;
import com.lami.tuomatuo.model.po.LoginParam;
import com.lami.tuomatuo.model.vo.UserDynamicVo;
import com.lami.tuomatuo.model.vo.UserInfoVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by xujiankang on 2016/2/1.
 */
@Controller
@RequestMapping(value = "/app1/qiniu")
public class QiniuController extends BaseController {

    @Override
    protected boolean checkAuth() {
        return false;
    }

    /**
     * 七牛那边图片上传的 callBack 处理
     * @param httpServletRequest
     * @param httpServletResponse
     * @return
     */
    @RequestMapping(value = "/callbackUrl.form")
    @ResponseBody
    public void getInfo(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        logger.info("getParam: " + getParam(httpServletRequest));
        logger.info("getBody: " + getBody(httpServletRequest));

    }
}
