package com.lami.tuomatuo.controller.crawler.ui;

import com.lami.tuomatuo.controller.BaseController;
import com.lami.tuomatuo.model.base.Result;
import com.lami.tuomatuo.model.po.crawler.CrawlerUIAccountParam;
import com.lami.tuomatuo.service.crawler.ChanYouJiAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by xjk on 2016/3/24.
 */
@Controller
@RequestMapping(value = "/crawler/chanyouji")
public class ChanYouJiAccountController extends BaseController {

    @Autowired
    private ChanYouJiAccountService chanYouJiAccountService;

    @Override
    protected boolean checkAuth() {
        return false;
    }

    /**
     * 用户查询自己的动态信息
     * @param httpServletRequest
     * @param httpServletResponse
     */
    @RequestMapping(value = "/initChanYouJiAccount.form")
    @ResponseBody
    public Result initHuPuAccount(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, @RequestBody CrawlerUIAccountParam crawlerUIAccountParam){
        Result result = execute(crawlerUIAccountParam);
        if (Result.SUCCESS != result.getStatus()) return result;
        chanYouJiAccountService.crawlerChanYouJi(crawlerUIAccountParam.getMinId(), crawlerUIAccountParam.getMaxId());
        return new Result(Result.SUCCESS).setValue("");
    }


}
