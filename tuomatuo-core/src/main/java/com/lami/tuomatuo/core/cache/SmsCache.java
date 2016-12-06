package com.lami.tuomatuo.core.cache;

import com.lami.tuomatuo.core.model.base.Result;

/**
 * Created by xjk on 2016/1/25.
 */
public interface SmsCache {

    /** 增加向这个 手机号码发送 验证码的个数
     * @param mobile
     * @return
     */
    Result increaseGetCodeCountGroupByIP(String IP);

    /** 增加向这个 手机号码发送 验证码的个数
     * @param mobile
     * @return
     */
    Result increaseGetCodeCountGroupByMobile(String mobile);

}
