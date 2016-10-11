package com.lami.tuomatuo.model.base;

/**
 * Created by xjk on 2016/1/25.
 */
public class Result {

    /**
     * RESULT_SUCCESS=0;
     * 操作成功
     */
    public final static int SUCCESS = 0;
    /**
     * RESULT_NOLOGIN=1;
     * 未登录
     */
    public final static int RESULT_NOLOGIN = 1;
    /**
     * RESULT_NOPERMISSION=2;
     * 没有访问权限
     */
    public final static int NOPERMISSION = 2;
    /**
     * RESULT_NOPERMISSION=3;
     * 参数验证错误
     */
    public final static int PARAMCHECKERROR = 3;
    /**
     * 	ERROR = 4;
     * 业务流程错误
     */
    public static final int ERROR = 4;
    /**
     * 未知错误
     */
    public static  final int SYSTEM_ERROR = 5;
    /** 账户认证失败
     */
    public static  final int ACCOUNT_ILLEGAL = 6;
    /**
     * 用发送内容中含有违禁词
     */
    public static final int ERROR_KEY_WORD = 7;
    /**
     * 权限不足
     */
    public static final int PERMISSIONS_DENY = 8;

    /**
     * 您的系统版本过低,请升级至最新系统
     */
    public static final int APP_VERSION_TO_LOWWER = 9;
    /**
     * 抱歉系统维护中，请稍候再试。
     */
    public static final int SYSTEM_MAINTENANCE = 10;
    /**
     * 手机号码格式不对
     */
    public static final int INCORRECT_MOBILE = 11;
    /**
     * 验证码输入多次错误,账户将冻结60分钟！
     */
    public static final int USER_ERROR_CODE_LIMIT = 12;
    /**
     * 您的账号因投诉或使用不当被停用
     */
    public static final int USER_STATUS_BLACK = 13;
    /**
     * 单个号码获取验证码的次数超标
     */
    public static final int COUNT_OUT_OF_LIMIT_GET_CODE_BY_MOBILE = 14;
    /**
     * 单个IP获取验证码的次数超标
     */
    public static final int COUNT_OUT_OF_LIMIT_GET_CODE_BY_IP = 15;
    /**
     * 获取验证码次数超标,请明天再试
     */
    public static final int LIMIT_GET_CODE = 16;


    boolean success = false;
    int status = -1;
    String msg;//信息
    Object value;//对象
    String nextUrl;

    public Result(){}
    /**
     * 该构造器通过对传入参数判断，获取返回信息。
     * @param status
     */
    public Result(int status){
        this.setStatus(status);
        if(status==SUCCESS)
            success = true;
    }

    public Result(int status,String msg){
        this.setStatus(status);
        this.msg = msg;
    }
    public Result setStatus(int status) {
        this.status = status;
        if(status==SUCCESS)
            success = true;
        switch(status){
            case SUCCESS:msg="操作成功！";break;
            case RESULT_NOLOGIN:msg="未登录";break;
            case NOPERMISSION:msg="请登录";break;
            case PARAMCHECKERROR:msg="参数验证错误";break;
            case SYSTEM_ERROR:msg="未知错误";break;
            case ACCOUNT_ILLEGAL:msg="账户认证失败";break;
            case ERROR_KEY_WORD:msg="发送内容中含有违禁词!";break;
            case PERMISSIONS_DENY:msg="权限不足";break;
            case APP_VERSION_TO_LOWWER:msg="您的系统版本过低,请升级至最新系统";break;
            case SYSTEM_MAINTENANCE:msg="抱歉系统维护中，请稍候再试。";break;
            case INCORRECT_MOBILE:msg="手机号码格式不对";break;
            case USER_ERROR_CODE_LIMIT:msg="验证码输入多次错误,账户将冻结60分钟！";break;
            case USER_STATUS_BLACK:msg="您的账号因投诉或使用不当被停用!";break;
            case COUNT_OUT_OF_LIMIT_GET_CODE_BY_MOBILE:msg="单个号码获取验证码的次数超标";break;
            case COUNT_OUT_OF_LIMIT_GET_CODE_BY_IP:msg="单个IP获取验证码的次数超标";break;
            case LIMIT_GET_CODE:msg="获取验证码次数超标,请明天再试";break;
            default:msg="未知错误！";
        }
        return this;
    }

    public String getNextUrl() {
        return nextUrl;
    }
    public Result setNextUrl(String nextUrl) {
        this.nextUrl = nextUrl;
        return this;
    }
    public int getStatus() {
        return status;
    }
    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }
    public String getMsg() {
        return msg;
    }
    public Result setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    @SuppressWarnings("unchecked")
    public <E extends Object> E getValue() {
        return (E) value;
    }
    public <E extends Object> Result setValue(E value) {
        this.value = value;
        return this;
    }
    @Override
    public String toString() {
        return "Result [status=" + status + ", msg=" + msg + ", value=" + value
                + ", nextUrl=" + nextUrl + "]";
    }
}
