package com.lami.tuomatuo.manage.bean;

import com.lami.tuomatuo.utils.GsonUtils;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

/**
 * 主要配置常量信息
 * @author Administrator
 *
 */
public class Config {
	@Value(value="${homeDir}")
	private String homeDir;
	@Value(value="${myDomainUrl}")
	private String myDomainUrl;
	@Value(value="${alipaySpId}")
	private String alipaySpId;
	@Value(value="${alipaySpKey}")
	private String alipaySpKey;
	@Value(value="${alipayAccount}")
	private String alipayAccount;
	@Value(value="${alipayNotifyUrl}")
	private String alipayNotifyUrl;
	@Value(value="${alipayReturnUrl}")
	private String alipayReturnUrl;
	@Value(value="${alipayLogPath}")
	private String alipayLogPath;
	
	
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
	
	
	public String getHomeDir() {
		return homeDir;
	}
	public String getMyDomainUrl() {
		return myDomainUrl;
	}
	public String getAlipaySpId() {
		return alipaySpId;
	}
	public String getAlipaySpKey() {
		return alipaySpKey;
	}
	public String getAlipayAccount() {
		return alipayAccount;
	}
	public String getAlipayNotifyUrl() {
		return alipayNotifyUrl;
	}
	public String getAlipayReturnUrl() {
		return alipayReturnUrl;
	}
	public String getAlipayLogPath() {
		return alipayLogPath;
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
	public String getMessageReceiveStatus_success() {
		return messageReceiveStatus_success;
	}
	public String getMessageReceiveStatus_unknow() {
		return messageReceiveStatus_unknow;
	}
	public String getMessageReceiveStatus_failure() {
		return messageReceiveStatus_failure;
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
	public String getNumberOperator() {
		return numberOperator;
	}
	public Map<String,String> getAreas() {
		return GsonUtils.getMapGson(this.areas);
	}
	public String getAreas_sh() {
		return areas_sh;
	}
	
		
	
		
}
