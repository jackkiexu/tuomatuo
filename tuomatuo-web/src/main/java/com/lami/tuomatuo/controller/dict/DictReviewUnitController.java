package com.lami.tuomatuo.controller.dict;

import com.lami.tuomatuo.controller.BaseController;
import com.lami.tuomatuo.model.base.Result;
import com.lami.tuomatuo.model.dict.DictReviewUnit;
import com.lami.tuomatuo.model.dict.DictUser;
import com.lami.tuomatuo.model.po.crawler.CrawlerUIAccountParam;
import com.lami.tuomatuo.model.po.dict.ReviewUnitWordparam;
import com.lami.tuomatuo.model.po.dict.StartReviewUnitParam;
import com.lami.tuomatuo.service.dict.DictReviceUnitService;
import com.lami.tuomatuo.service.dict.DictUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by xujiankang on 2016/8/23.
 */
@Controller
@RequestMapping(value = "/dict/reviewUnit")
public class DictReviewUnitController extends DictBaseController {

    @Autowired
    private DictReviceUnitService dictReviceUnitService;

    @Override
    protected boolean checkAuth() {
        return true;
    }

    /**
     * 开启一个新单元的学习
     * @param httpServletRequest
     */
    @RequestMapping(value = "/startReviewUnit.form")
    @ResponseBody
    public Result startReviewUnit(HttpServletRequest httpServletRequest, @RequestBody StartReviewUnitParam param){
        Result result = execute(param);
        if (Result.SUCCESS != result.getStatus()) return result;
        DictUser dictUser = (DictUser)result.getValue();
        DictReviewUnit dictReviewUnit = dictReviceUnitService.beginReviewUnit(dictUser.getId(), param.getUnitId());
        return new Result(Result.SUCCESS).setValue(dictReviewUnit);
    }

    /**
     * 复习单元中的单词
     * @param httpServletRequest
     */
    @RequestMapping(value = "/processReviewUnit.form")
    @ResponseBody
    public Result processReviewUnit(HttpServletRequest httpServletRequest, @RequestBody ReviewUnitWordparam param){
        Result result = execute(param);
        if (Result.SUCCESS != result.getStatus()) return result;
        DictUser dictUser = (DictUser)result.getValue();
        DictReviewUnit dictReviewUnit = dictReviceUnitService.beginReviewUnit(dictUser.getId(), param.getUnitId());
        return new Result(Result.SUCCESS).setValue(dictReviewUnit);
    }
}