package com.lami.tuomatuo.controller.app1.login;

import com.lami.sms.pipe.yupian.SendResult;
import com.lami.sms.pipe.yupian.YuPian;
import com.lami.tuomatuo.cache.SmsCache;
import com.lami.tuomatuo.cache.UserCache;
import com.lami.tuomatuo.controller.BaseController;
import com.lami.tuomatuo.model.User;
import com.lami.tuomatuo.model.base.Result;
import com.lami.tuomatuo.model.enums.SmsType;
import com.lami.tuomatuo.model.enums.UserStatus;
import com.lami.tuomatuo.model.po.GetSmsParam;
import com.lami.tuomatuo.model.po.LoginParam;
import com.lami.tuomatuo.model.po.VerifyCodeParam;
import com.lami.tuomatuo.service.SmsService;
import com.lami.tuomatuo.service.UserService;
import com.lami.tuomatuo.utils.GsonUtils;
import com.lami.tuomatuo.utils.HttpHelper;
import com.lami.tuomatuo.utils.RandomUtils;
import com.lami.tuomatuo.utils.StringUtil;
import com.lami.tuomatuo.utils.constant.Constant;
import com.lami.tuomatuo.utils.help.InitHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Created by xujiankang on 2016/1/18.
 */
@Controller
@RequestMapping(value = "/app1/login")
public class AppLoginController extends BaseController{

    @Autowired
    private UserCache userCache;
    @Autowired
    private SmsCache smsCache;
    @Autowired
    private UserService userService;
    @Autowired
    private SmsService smsService;

    @Override
    protected boolean checkAuth() {
        return false;
    }

    @RequestMapping(value = "/getSms.form")
    public Result getSms(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, @RequestBody GetSmsParam getSmsParam){
        /** 1. 检验传来的参数
         *  2. 防止 DDOS 检测 mobile 和 IP
         *  3. 获取验证码表 (发给用户的验证码 10 分钟不变化)
         *  4. 发送验证码 更新用户表中的 code
         */
        if(!StringUtil.isMobile(getSmsParam.getMobile())) return new Result(Result.PARAMCHECKERROR);
        String IP = HttpHelper.getIpAddr(httpServletRequest);
        logger.info("getSmsParam:"+getSmsParam+", IP:"+IP);
        Result ipLimitResult = smsCache.increaseGetCodeCountGroupByIP(IP);
        Result mobileLimitResult = smsCache.increaseGetCodeCountGroupByMobile(getSmsParam.getMobile());
        if(ipLimitResult.getStatus() != Result.SUCCESS || mobileLimitResult.getStatus() != Result.SUCCESS){ // 若限额超标, 则直接提示系统错误, 且不发送短信
            logger.info("ipLimit :"+ ipLimitResult + ", mobileLimit:"+mobileLimitResult+", IP:"+IP+", mobile:"+getSmsParam.getMobile());
            return new Result(Result.LIMIT_GET_CODE);
        }
        User user = userService.getUserByMobile(getSmsParam.getMobile());
        if (user == null){
            user = InitHelper.initUser(getSmsParam.getMobile());
            user = userService.save(user);
        }
        String code = RandomUtils.randomString(4, true);
        String smsContent = "【托马托】您的验证码是"+code;
        // TODO 将发送的信息的状态进行存储
        String json = YuPian.sendSms(Constant.YUNPIAN_SIGN, smsContent, getSmsParam.getMobile());

        SendResult sendResult = new SendResult();
        try {
            sendResult = (SendResult)GsonUtils.getObjectFromJson(json, SendResult.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("YuPian.sendSms mobile:"+getSmsParam.getMobile() +", content:"+smsContent +", result:"+json+", sendResult:"+sendResult);

        smsService.saveSms(getSmsParam.getMobile(), smsContent, SmsType.YUNPIAN.getId(), user.getId());

        user.setCode(code);
        user.setSendCodeTime(new Date());
        userService.update(user);
        return new Result();
    }

    @RequestMapping(value = "/verifyCode.form")
    public Result verifyCode(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, @RequestBody VerifyCodeParam verifyCodeParam){
        /** 1. 校验参数
         *  2.校验验证码
         */
        if(!StringUtil.isMobile(verifyCodeParam.getMobile())) return new Result(Result.PARAMCHECKERROR);
        if (!StringUtil.isNumeric(verifyCodeParam.getCode()) || verifyCodeParam.getCode().length() != 4)  return new Result(Result.PARAMCHECKERROR);

        User user = userService.getUserByMobile(verifyCodeParam.getMobile());
        if(user==null) return new Result(Result.PARAMCHECKERROR, "用户不存在");

        int nowCount = userCache.userVerifyCodeFailCount(user.getId(), Constant.ZERO);
        if(nowCount > 4){
            String IP = HttpHelper.getIpAddr(httpServletRequest);
            logger.info("Exception 用户("+IP+"暴力破解账号, verifyCodeParam:"+verifyCodeParam);
            return new Result(Result.USER_ERROR_CODE_LIMIT);
        }

        if (!user.getCode().equals(verifyCodeParam.getCode())){
            userCache.userVerifyCodeFailCount(user.getId(), Constant.TRUE);
            return new Result(Result.PARAMCHECKERROR, "验证码有误");
        }

        if(nowCount != 0) userCache.userVerifyCodeFailCount(user.getId(), -nowCount);

        if (user.getStatus() == UserStatus.BLACK.getId()) return new Result(Result.USER_STATUS_BLACK);
        if (user.getStatus() == UserStatus.INIT.getId()){
            user.setStatus(UserStatus.OK.getId());
            user.setLastLoginTime(new Date());
            user.setSign(StringUtil.getSign());
            userService.update(user);
        }
        return new Result(Result.SUCCESS).setValue(user);
    }
}
