package com.lami.tuomatuo.web.controller.dict;

import com.lami.tuomatuo.core.model.base.Result;
import com.lami.tuomatuo.web.model.po.dict.AddWordParam;
import com.lami.tuomatuo.core.service.dict.DictWordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by xjk on 2016/8/23.
 */
@Controller
@RequestMapping(value = "/dict/word")
public class DictWordController extends DictBaseController {

    @Autowired
    private DictWordService dictWordService;

    @Override
    protected boolean checkAuth() {
        return false;
    }

    /**
     * 添加单词
     * @param httpServletRequest
     */
    @RequestMapping(value = "/addWordParam.form")
    @ResponseBody
    public Result getDynamic(HttpServletRequest httpServletRequest, @RequestBody AddWordParam param){
        Result result = execute(param);
        if (Result.SUCCESS != result.getStatus()) return result;
        dictWordService.addWordToUnit(param.getWord(), param.getUnitId());
        return new Result(Result.SUCCESS).setValue("");
    }
}
