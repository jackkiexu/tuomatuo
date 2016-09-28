package com.lami.tuomatuo.utils.constant;

/**
 * Created by xujiankang on 2016/1/25.
 */
public class Constant {

    public static final String YUNPIAN_SIGN = ""; // 短信发送 云片的 sign

    public static final int ZERO = 0;
    public static final int TRUE = 1;

    /** 针对单个IP获取验证码的次数 */
    public static final int  GET_CODE_LIMIT_FOR_IP = 10;

    /** 针对单个号码获取验证码的次数 */
    public static final int  GET_CODE_LIMIT_FOR_MOBILE = 10;

    public static final String SESSION_USER = "smSessionUser";

    public static final int TRUE_INT = 1;

    public static final int NOTIFY_SCOPE_UNIT = 1; // 信息作用于所有人
    public static final int NOTIFY_SCOPE_PART = 2; // 信息作用于单个(个别, 或几个)
}
