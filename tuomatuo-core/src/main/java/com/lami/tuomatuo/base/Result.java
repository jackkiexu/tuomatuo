package com.lami.tuomatuo.base;



public class Result {
	
	/**
	 * 操作成功
	 */
	public final static int SUCCESS = 0;

	/**
	 * 未登录
	 */
	public final static int RESULT_NOLOGIN = 1;

	/**
	 * 没有访问权限
	 */
	public final static int NOPERMISSION = 2;

	/**
	 * 参数验证错误
	 */
	public final static int PARAMCHECKERROR = 3;

	/**
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

	int status = -1;
	String msg; //信息
	Object value; //对象

	public Result(){}

	public Result(int status){
		this.setStatus(status);
	}
	
	public Result(int status,String msg){
		this.setStatus(status);
		this.msg = msg;
	}

	public int getStatus() {
		return status;
	}

	public Result setStatus(int status) {
		this.status = status;
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
			default:msg="未知错误！";
		}
		return this;
	}
	
	public String getMsg() {
		return msg;
	}
	public Result setMsg(String msg) {
		this.msg = msg;
		return this;
	}

	public <E extends Object> E getValue() {
		return (E) value;
	}
	public <E extends Object> Result setValue(E value) {
		this.value = value;
		return this;
	}
	@Override
	public String toString() {
		return "Result [status=" + status + ", msg=" + msg + ", value=" + value + "]";
	}
}
