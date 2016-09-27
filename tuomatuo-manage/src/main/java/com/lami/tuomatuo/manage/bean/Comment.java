package com.lami.tuomatuo.manage.bean;

import com.lami.tuomatuo.utils.GsonUtils;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

/**
 * 参数分类信息
 * @author Administrator
 *
 */
public class Comment {
	@Value(value="${messageSendStatus}")
	private String messageSendStatus;
	@Value(value="${messageSendStatus_success}")
	private String messageSendStatus_success;
	@Value(value="${messageSendStatus_unknow}")
	private String messageSendStatus_unknow;
	@Value(value="${messageSendStatus_failure}")
	private String messageSendStatus_failure;
	
	@Value(value="${messageReceiveStatus}")
	private String messageReceiveStatus;
	@Value(value="${messageReceiveStatus_success}")
	private String messageReceiveStatus_success;
	@Value(value="${messageReceiveStatus_unknow}")
	private String messageReceiveStatus_unknow;
	@Value(value="${messageReceiveStatus_failure}")
	private String messageReceiveStatus_failure;
	
	@Value(value="${callStatus}")
	private String callStatus;
	@Value(value="${callStatus_success}")
	private String callStatus_success;
	@Value(value="${callStatus_unknow}")
	private String callStatus_unknow;
	@Value(value="${callStatus_failure}")
	private String callStatus_failure;
	
	
	@Value(value="${chargeStatus}")
	private String chargeStatus;
	@Value(value="${chargeStatus_success}")
	private String chargeStatus_success;
	@Value(value="${chargeStatus_unknow}")
	private String chargeStatus_unknow;
	@Value(value="${chargeStatus_failure}")
	private String chargeStatus_failure;
	
	
	@Value(value="${isDeleteStatus}")
	private String isDeleteStatus;
	@Value(value="${isDeleteStatus_false}")
	private String isDeleteStatus_false;
	@Value(value="${isDeleteStatus_true}")
	private String isDeleteStatus_true;
	
	@Value(value="${payAccountType}")
	private String payAccountType;
	@Value(value="${payAccountType_alipay}")
	private String payAccountType_alipay;
	
	@Value(value="${numberOperator}")
	private String numberOperator;
	
	@Value(value="${areas}")
	private String areas;
	@Value(value="${areas_sh}")
	private String areas_sh;
	
	@Value(value="${userStatus}")
	private String userStatus;
	@Value(value="${userStatus_default}")
	private String userStatus_default;
	@Value(value="${userStatus_active}")
	private String userStatus_active;
	@Value(value="${userStatus_black}")
	private String userStatus_black;
	@Value(value="${userStatus_cancel}")
	private String userStatus_cancel;
	
	@Value(value="${service_type}")
	private String serviceType;
	@Value(value="${service_status}")
	private String serviceStatus;
	
	@Value(value="${userNumber_updateStatus1}")
	private String userNumber_updateStatus1;
	@Value(value="${userNumber_updateStatus2}")
	private String userNumber_updateStatus2;
	@Value(value="${userNumber_updateStatus3}")
	private String userNumber_updateStatus3;
	@Value(value="${userNumber_updateStatus4}")
	private String userNumber_updateStatus4;
	@Value(value="${image_path}")
	private String image_path;
	
	
	
	public Map<String,String> getUserStatus() {
		return GsonUtils.getMapGson(this.userStatus);
	}
	public String getUserStatus_default() {
		return userStatus_default;
	}
	public String getUserStatus_active() {
		return userStatus_active;
	}
	public String getUserStatus_black() {
		return userStatus_black;
	}
	public String getUserStatus_cancel() {
		return userStatus_cancel;
	}
	public Map<String,String> getMessageSendStatus() {
		return GsonUtils.getMapGson(this.messageSendStatus);
	}
	public String getMessageSendStatus_success() {
		return messageSendStatus_success;
	}
	public String getMessageSendStatus_unknow() {
		return messageSendStatus_unknow;
	}
	public String getMessageSendStatus_failure() {
		return messageSendStatus_failure;
	}
	public Map<String,String> getMessageReceiveStatus() {
		return GsonUtils.getMapGson(this.messageReceiveStatus);
	}
	public Integer getMessageReceiveStatus_success() {
		return Integer.parseInt(messageReceiveStatus_success);
	}
	public Integer getMessageReceiveStatus_unknow() {
		return Integer.parseInt(messageReceiveStatus_unknow);
	}
	public Integer getMessageReceiveStatus_failure() {
		return Integer.parseInt(messageReceiveStatus_failure);
	}
	public Map<String,String> getCallStatus() {
		return GsonUtils.getMapGson(this.callStatus);
	}
	public String getCallStatus_success() {
		return callStatus_success;
	}
	public String getCallStatus_unknow() {
		return callStatus_unknow;
	}
	public String getCallStatus_failure() {
		return callStatus_failure;
	}
	public Map<String,String> getChargeStatus() {
		return GsonUtils.getMapGson(this.chargeStatus);
	}
	public String getChargeStatus_success() {
		return chargeStatus_success;
	}
	public String getChargeStatus_unknow() {
		return chargeStatus_unknow;
	}
	public String getChargeStatus_failure() {
		return chargeStatus_failure;
	}
	public Map<String,String> getIsDeleteStatus() {
		return GsonUtils.getMapGson(this.isDeleteStatus);
	}
	public String getIsDeleteStatus_false() {
		return isDeleteStatus_false;
	}
	public String getIsDeleteStatus_true() {
		return isDeleteStatus_true;
	}
	public Map<String,String> getPayAccountType() {
		return GsonUtils.getMapGson(this.payAccountType);
	}
	public String getPayAccountType_alipay() {
		return payAccountType_alipay;
	}
	public Map<String,String> getNumberOperator() {
		return GsonUtils.getMapGson(this.numberOperator);
	}
	public Map<String,String> getAreas() {
		return GsonUtils.getMapGson(this.areas);
	}
	public String getAreas_sh() {
		return areas_sh;
	}
	
	public Map<String, String> getServiceType() {
		return GsonUtils.getMapGson(this.serviceType);
	}
	public Map<String, String> getServiceStatus() {
		return GsonUtils.getMapGson(this.serviceStatus);
	}
	public String getUserNumber_updateStatus1() {
		return userNumber_updateStatus1;
	}
	public String getUserNumber_updateStatus2() {
		return userNumber_updateStatus2;
	}
	public String getUserNumber_updateStatus3() {
		return userNumber_updateStatus3;
	}
	public String getUserNumber_updateStatus4() {
		return userNumber_updateStatus4;
	}
	public String getImage_path() {
		return image_path;
	}
	public static void main(String[] args){
		String s = "{1:正常,0:未知,-1:失败}";
		Comment comment = new Comment();
		comment.chargeStatus = s;
		Map<String,String> map = comment.getChargeStatus();
		for(String key:map.keySet()){
			System.out.println(map.get(key));
		}
	}

}
