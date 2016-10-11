package com.lami.tuomatuo.controller.app1.dynamic;

import com.lami.tuomatuo.controller.BaseController;
import com.lami.tuomatuo.model.DynamicComment;
import com.lami.tuomatuo.model.User;
import com.lami.tuomatuo.model.base.Result;
import com.lami.tuomatuo.model.po.DynamicAddCommentParam;
import com.lami.tuomatuo.model.po.GetDynamicCommentParam;
import com.lami.tuomatuo.service.DynamicCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by xjk on 2016/1/26.
 */
@Controller
@RequestMapping(value = "/app1/dynamicComment")
public class UserDynamicCommentController extends BaseController {

    @Autowired
    private DynamicCommentService dynamicCommentService;

    @Override
    protected boolean checkAuth() {
        return true;
    }

    /**
     * 用户查询动态对应的评论
     * @param httpServletRequest
     * @param httpServletResponse
     * @param getUserDynamicParam
     */
    @RequestMapping(value = "/getComment.form")
    @ResponseBody
    public Result getDynamic(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, @RequestBody GetDynamicCommentParam getDynamicCommentParam){
        Result result = execute(getDynamicCommentParam);
        if (Result.SUCCESS != result.getStatus()) return result;
        List<DynamicComment> dynamicCommentList = dynamicCommentService.getUserDynamicCommentById(getDynamicCommentParam.getDynamicCommentId(), getDynamicCommentParam.getOffset(), getDynamicCommentParam.getPageSize());
        return new Result(Result.SUCCESS).setValue(dynamicCommentList);
    }

    /**
     * 用户对动态增加评论
     * @param httpServletRequest
     * @param httpServletResponse
     * @param getUserDynamicParam
     */
    @RequestMapping(value = "/addComment.form")
    @ResponseBody
    public Result addComment(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, @RequestBody DynamicAddCommentParam dynamicAddCommentParam){
        Result result = execute(dynamicAddCommentParam);
        if (Result.SUCCESS != result.getStatus()) return result;

        User user = (User)result.getValue();
        dynamicCommentService.addUserDynamicComment(user.getId(), dynamicAddCommentParam.getDyId(), dynamicAddCommentParam.getContent());
        return new Result(Result.SUCCESS);
    }
}
