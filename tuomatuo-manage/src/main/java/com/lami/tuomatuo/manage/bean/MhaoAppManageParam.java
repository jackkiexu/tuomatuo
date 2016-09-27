package com.lami.tuomatuo.manage.bean;

import java.util.Map;

/**
 * 针对app接口的管理平台监控参数
 * @author Alex
 * 2015-03-12
 */
public class MhaoAppManageParam {

	private String user_count; // 用户总数
	private String user_count_today; // 今日新增用户数
	private String today_order_count; // 今日新增订单数
	private String user_ok_count; // 有效用户数
	private String user_distinct_count; // 有效 distinct 用户数
	private String user_multiple_mobile; // 当用户占用多个号码
	private String mobile_multiple_user; // 单号码被多个用户占用
	private String active_number_but_used; // 使用但空闲的号码
	private String number_pool_free_count; // 免费号码数
	private String number_pool_pay_count; // 收费号码数
	private String number_pool_manager_count; // 管理员手工投放
	private String number_pool_sms_count; // 短信密号数量
	private String didi_number_pool_count; // 滴滴业务号码池
	private String didiZhuanche_number_pool_count; // 滴滴专车号码池
	private String zhenai_number_pool_count; // 珍爱网号码数
	private String message_monitor_pool_count; // 近一天短信量监控
	private String call_monitor_pool_count; // 近一天通话量监控
	private String message_key_word_pool_count; // 近一天关键字监控
//	private String 
	
	public MhaoAppManageParam(Map<String, Object> map) {
		this.setUser_count(String.valueOf(map.get("user_count")));
		this.setUser_count_today(String.valueOf(map.get("user_count_today")));
		this.setToday_order_count(String.valueOf(map.get("today_order_count")));
		this.setUser_ok_count(String.valueOf(map.get("user")));
		this.setUser_distinct_count(String.valueOf(map.get("user_distinct")));
		this.setUser_multiple_mobile(String.valueOf(map.get("user_multiple_mobile")));
		this.setMobile_multiple_user(String.valueOf(map.get("mobile_multiple_user")));
		this.setActive_number_but_used(String.valueOf(map.get("active_number_but_used")));
		this.setNumber_pool_free_count(String.valueOf(map.get("number_pool_free_count")));
		this.setNumber_pool_pay_count(String.valueOf(map.get("number_pool_pay_count")));
		this.setNumber_pool_manager_count(String.valueOf(map.get("number_pool_manager_count")));
		this.setNumber_pool_sms_count(String.valueOf(map.get("number_pool_sms_count")));
		this.setDidi_number_pool_count(String.valueOf(map.get("didi_number_pool_count")));
		this.setDidiZhuanche_number_pool_count(String.valueOf(map.get("didiZhuanche_number_pool_count")));
		this.setZhenai_number_pool_count(String.valueOf(map.get("zhenai_number_pool_count")));
		this.setMessage_monitor_pool_count(String.valueOf(map.get("message_monitor_pool_count")));
		this.setCall_monitor_pool_count(String.valueOf(map.get("call_monitor_pool_count")));
		this.setMessage_key_word_pool_count(String.valueOf(map.get("message_key_word_pool_count")));
		
	}

	public String getUser_count() {
		return user_count;
	}

	public void setUser_count(String user_count) {
		this.user_count = user_count;
	}

	public String getUser_count_today() {
		return user_count_today;
	}

	public void setUser_count_today(String user_count_today) {
		this.user_count_today = user_count_today;
	}

	public String getToday_order_count() {
		return today_order_count;
	}

	public void setToday_order_count(String today_order_count) {
		this.today_order_count = today_order_count;
	}

	public String getUser_ok_count() {
		return user_ok_count;
	}

	public void setUser_ok_count(String user_ok_count) {
		this.user_ok_count = user_ok_count;
	}

	public String getUser_distinct_count() {
		return user_distinct_count;
	}

	public void setUser_distinct_count(String user_distinct_count) {
		this.user_distinct_count = user_distinct_count;
	}

	public String getUser_multiple_mobile() {
		return user_multiple_mobile;
	}

	public void setUser_multiple_mobile(String user_multiple_mobile) {
		this.user_multiple_mobile = user_multiple_mobile;
	}

	public String getMobile_multiple_user() {
		return mobile_multiple_user;
	}

	public void setMobile_multiple_user(String mobile_multiple_user) {
		this.mobile_multiple_user = mobile_multiple_user;
	}

	public String getActive_number_but_used() {
		return active_number_but_used;
	}

	public void setActive_number_but_used(String active_number_but_used) {
		this.active_number_but_used = active_number_but_used;
	}

	public String getNumber_pool_free_count() {
		return number_pool_free_count;
	}

	public void setNumber_pool_free_count(String number_pool_free_count) {
		this.number_pool_free_count = number_pool_free_count;
	}

	public String getNumber_pool_pay_count() {
		return number_pool_pay_count;
	}

	public void setNumber_pool_pay_count(String number_pool_pay_count) {
		this.number_pool_pay_count = number_pool_pay_count;
	}

	public String getNumber_pool_manager_count() {
		return number_pool_manager_count;
	}

	public void setNumber_pool_manager_count(String number_pool_manager_count) {
		this.number_pool_manager_count = number_pool_manager_count;
	}

	public String getNumber_pool_sms_count() {
		return number_pool_sms_count;
	}

	public void setNumber_pool_sms_count(String number_pool_sms_count) {
		this.number_pool_sms_count = number_pool_sms_count;
	}

	public String getDidi_number_pool_count() {
		return didi_number_pool_count;
	}

	public void setDidi_number_pool_count(String didi_number_pool_count) {
		this.didi_number_pool_count = didi_number_pool_count;
	}

	public String getDidiZhuanche_number_pool_count() {
		return didiZhuanche_number_pool_count;
	}

	public void setDidiZhuanche_number_pool_count(
			String didiZhuanche_number_pool_count) {
		this.didiZhuanche_number_pool_count = didiZhuanche_number_pool_count;
	}

	public String getZhenai_number_pool_count() {
		return zhenai_number_pool_count;
	}

	public void setZhenai_number_pool_count(String zhenai_number_pool_count) {
		this.zhenai_number_pool_count = zhenai_number_pool_count;
	}
	public String getMessage_monitor_pool_count() {
		return message_monitor_pool_count;
	}

	public void setMessage_monitor_pool_count(String message_monitor_pool_count) {
		this.message_monitor_pool_count = message_monitor_pool_count;
	}

	public String getCall_monitor_pool_count() {
		return call_monitor_pool_count;
	}

	public void setCall_monitor_pool_count(String call_monitor_pool_count) {
		this.call_monitor_pool_count = call_monitor_pool_count;
	}

	public String getMessage_key_word_pool_count() {
		return message_key_word_pool_count;
	}

	public void setMessage_key_word_pool_count(String message_key_word_pool_count) {
		this.message_key_word_pool_count = message_key_word_pool_count;
	}

	@Override
	public String toString() {
		return "MhaoAppManageParam [user_count=" + user_count
				+ ", user_count_today=" + user_count_today
				+ ", today_order_count=" + today_order_count
				+ ", user_ok_count=" + user_ok_count + ", user_distinct_count="
				+ user_distinct_count + ", user_multiple_mobile="
				+ user_multiple_mobile + ", mobile_multiple_user="
				+ mobile_multiple_user + ", active_number_but_used="
				+ active_number_but_used + ", number_pool_free_count="
				+ number_pool_free_count + ", number_pool_pay_count="
				+ number_pool_pay_count + ", number_pool_manager_count="
				+ number_pool_manager_count + ", number_pool_sms_count="
				+ number_pool_sms_count + ", didi_number_pool_count="
				+ didi_number_pool_count + ", didiZhuanche_number_pool_count="
				+ didiZhuanche_number_pool_count
				+ ", zhenai_number_pool_count=" + zhenai_number_pool_count
				+ ", message_monitor_pool_count=" + message_monitor_pool_count
				+ ", call_monitor_pool_count=" + call_monitor_pool_count
				+ ", message_key_word_pool_count="
				+ message_key_word_pool_count + "]";
	}

	
	
}
