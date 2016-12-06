package com.lami.tuomatuo.web.controller.app1.qiniu;

import com.lami.tuomatuo.web.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by xjk on 2016/2/1.
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
