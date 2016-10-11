package com.lami.tuomatuo.controller.crawler.ui;

import com.lami.tuomatuo.controller.BaseController;
import com.lami.tuomatuo.model.UserDynamic;
import com.lami.tuomatuo.model.base.Result;
import com.lami.tuomatuo.model.po.GetUserDynamicParam;
import com.lami.tuomatuo.model.po.crawler.CrawlerUIAccountParam;
import com.lami.tuomatuo.service.crawler.UIAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by xjk on 2016/3/21.
 */
@Controller
@RequestMapping(value = "/crawler/ui")
public class UIAccountController extends BaseController {

    @Autowired
    private UIAccountService uiAccountService;

    @Override
    protected boolean checkAuth() {
        return false;
    }

    /**
     * 用户查询自己的动态信息
     * @param httpServletRequest
     * @param httpServletResponse
     */
    @RequestMapping(value = "/initUIAccount.form")
    @ResponseBody
    public Result getDynamic(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, @RequestBody CrawlerUIAccountParam crawlerUIAccountParam){
        Result result = execute(crawlerUIAccountParam);
        if (Result.SUCCESS != result.getStatus()) return result;
        uiAccountService.crawlerUIAccount(crawlerUIAccountParam.getMinId(), crawlerUIAccountParam.getMaxId());
        return new Result(Result.SUCCESS).setValue("");
    }

}
