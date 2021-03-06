package com.lami.tuomatuo.web.controller.dict;

import com.lami.tuomatuo.core.model.base.Result;
import com.lami.tuomatuo.core.model.dict.DictUser;
import com.lami.tuomatuo.web.model.po.dict.DictUserParam;
import com.lami.tuomatuo.core.model.vo.DictReviewUnitVo;
import com.lami.tuomatuo.core.service.dict.DictUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by xjk on 2016/8/23.
 */
@Controller
@RequestMapping(value = "/dict/user")
public class DictUserController extends DictBaseController {

    @Autowired
    private DictUnitService dictUnitService;

    @Override
    protected boolean checkAuth() {
        return true;
    }

    /**
     * 账户登录
     * @param httpServletRequest
     */
    @RequestMapping(value = "/login.form")
    @ResponseBody
    public Result login(HttpServletRequest httpServletRequest, @RequestBody DictUserParam dictUserParam){
        /** 1. 检验账户信息
         *  2. 获取所有 unit 的信息
         */
        Result result = execute(dictUserParam);
        if (Result.SUCCESS != result.getStatus()) return result;
        DictUser dictUser = (DictUser)result.getValue();

        List<DictReviewUnitVo> dictReviewUnitVoList = dictUnitService.getAllDictReviewUnitVo(dictUser.getId());
        return new Result(Result.SUCCESS).setValue(dictReviewUnitVoList);
    }
}