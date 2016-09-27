package com.lami.tuomatuo.manage.bean;

import org.springframework.beans.factory.annotation.Value;

/**
 * 提示信息
 * @author Administrator
 *
 */
public class ConstantMessage {

	@Value(value="${parameter_error}")
	public String parameter_error;
	@Value(value="${no_login}")
	public String no_login;
	@Value(value="${no_permission}")
	public String no_permission;
	@Value(value="${no_user}")
	public String no_user;
	@Value(value="${error_password}")
	public String error_password;
	@Value(value="${error_user}")
	public String error_user;
}
