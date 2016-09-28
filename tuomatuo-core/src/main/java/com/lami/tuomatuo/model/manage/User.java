package com.lami.tuomatuo.model.manage;

import javax.persistence.*;
import java.util.Date;

/**
 * User entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "user")
public class User implements java.io.Serializable {

	// Fields
	private static final long serialVersionUID = -6615167266833265252L;
	private Integer id;
	private String loginName;
	private String password;
	private String mail;
	private String openId;
	private Integer status;
	private Integer fromType;
	private Integer guideFlag;
	private Date createTime;
	private Date updateTime;
	private Date endTime;
	private String endTimeDes;
	private Integer currentMsgCount;
	private Integer sendDelayMsgFlag;
	private Integer currentCallMinutes;
	private Date lastLoginTime;
	private String lastLoginTimeDes;
	private Date sendSecretTime;
	private Date sendCodeTime; // 发送验证码时间(用于10分钟内验证码不变化)
	private String mailSecret;
	private String code;
	private String tpOpenId; // 微信端与mhao的 uniqueid
	private Integer experienceFlag;
	private String sign;// 校验的签名
	private Integer isDirectCall; // 用户能否进行直接呼叫
	private Integer isDirectReceiveMsg; // 用户是否能够直接接收验证码
	private Integer isWorkForJustRecNormalCall; // 用户是否只接收正常的电话接听
	private Integer isWorkForBlockMobile; // 用户的黑名单是否起作用
	private String iosDeviceToken; // ios 用户的 deviceToken
	private Date   lastChangeLoginNameTime; // 上次用户改变登录号码的时间 
	
	// Constructors
	/** default constructor */
	public User() {
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return this.id;
	}

	@Transient
	public String getEndTimeDes() {
		return endTimeDes;
	}

	public Integer getSendDelayMsgFlag() {
		return sendDelayMsgFlag;
	}

	public void setSendDelayMsgFlag(Integer sendDelayMsgFlag) {
		this.sendDelayMsgFlag = sendDelayMsgFlag;
	}

	public void setEndTimeDes(String endTimeDes) {
		this.endTimeDes = endTimeDes;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLoginName() {
		return this.loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Date getSendSecretTime() {
		return sendSecretTime;
	}

	@Transient
	public String getLastLoginTimeDes() {
		return lastLoginTimeDes;
	}
	public void setLastLoginTimeDes(String lastLoginTimeDes) {
		this.lastLoginTimeDes = lastLoginTimeDes;
	}
	public void setSendSecretTime(Date sendSecretTime) {
		this.sendSecretTime = sendSecretTime;
	}
	public Date getSendCodeTime() {
		return sendCodeTime;
	}

	public void setSendCodeTime(Date sendCodeTime) {
		this.sendCodeTime = sendCodeTime;
	}

	public String getMailSecret() {
		return mailSecret;
	}

	public void setMailSecret(String mailSecret) {
		this.mailSecret = mailSecret;
	}

	public String getMail() {
		return this.mail;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Date getLastLoginTime() {
		return this.lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Integer getCurrentMsgCount() {
		return currentMsgCount;
	}

	public void setCurrentMsgCount(Integer currentMsgCount) {
		this.currentMsgCount = currentMsgCount;
	}

	public Integer getCurrentCallMinutes() {
		return currentCallMinutes;
	}

	public void setCurrentCallMinutes(Integer currentCallMinutes) {
		this.currentCallMinutes = currentCallMinutes;
	}

	public Integer getFromType() {
		return fromType;
	}

	public void setFromType(Integer fromType) {
		this.fromType = fromType;
	}

	public String getTpOpenId() {
		return tpOpenId;
	}

	public void setTpOpenId(String tpOpenId) {
		this.tpOpenId = tpOpenId;
	}

	public Integer getGuideFlag() {
		return guideFlag;
	}

	public void setGuideFlag(Integer guideFlag) {
		this.guideFlag = guideFlag;
	}

	public Integer getExperienceFlag() {
		return experienceFlag;
	}

	public void setExperienceFlag(Integer experienceFlag) {
		this.experienceFlag = experienceFlag;
	}
	
	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}
	
	public Integer getIsDirectCall() {
		return isDirectCall;
	}

	public void setIsDirectCall(Integer isDirectCall) {
		this.isDirectCall = isDirectCall;
	}
	public Integer getIsDirectReceiveMsg() {
		return isDirectReceiveMsg;
	}

	public void setIsDirectReceiveMsg(Integer isDirectReceiveMsg) {
		this.isDirectReceiveMsg = isDirectReceiveMsg;
	}
	public Integer getIsWorkForJustRecNormalCall() {
		return isWorkForJustRecNormalCall;
	}

	public void setIsWorkForJustRecNormalCall(Integer isWorkForJustRecNormalCall) {
		this.isWorkForJustRecNormalCall = isWorkForJustRecNormalCall;
	}

	public Integer getIsWorkForBlockMobile() {
		return isWorkForBlockMobile;
	}

	public void setIsWorkForBlockMobile(Integer isWorkForBlockMobile) {
		this.isWorkForBlockMobile = isWorkForBlockMobile;
	}
	public String getIosDeviceToken() {
		return iosDeviceToken;
	}
	public void setIosDeviceToken(String iosDeviceToken) {
		this.iosDeviceToken = iosDeviceToken;
	}
	public Date getLastChangeLoginNameTime() {
		return lastChangeLoginNameTime;
	}
	public void setLastChangeLoginNameTime(Date lastChangeLoginNameTime) {
		this.lastChangeLoginNameTime = lastChangeLoginNameTime;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", loginName=" + loginName + ", password="
				+ password + ", mail=" + mail + ", openId=" + openId
				+ ", status=" + status + ", fromType=" + fromType
				+ ", guideFlag=" + guideFlag + ", createTime=" + createTime
				+ ", updateTime=" + updateTime + ", endTime=" + endTime
				+ ", endTimeDes=" + endTimeDes + ", currentMsgCount="
				+ currentMsgCount + ", sendDelayMsgFlag=" + sendDelayMsgFlag
				+ ", currentCallMinutes=" + currentCallMinutes
				+ ", lastLoginTime=" + lastLoginTime + ", lastLoginTimeDes="
				+ lastLoginTimeDes + ", sendSecretTime=" + sendSecretTime
				+ ", sendCodeTime=" + sendCodeTime + ", mailSecret="
				+ mailSecret + ", code=" + code + ", tpOpenId=" + tpOpenId
				+ ", experienceFlag=" + experienceFlag + ", sign=" + sign
				+ ", isDirectCall=" + isDirectCall + ", isDirectReceiveMsg="
				+ isDirectReceiveMsg + ", isWorkForJustRecNormalCall="
				+ isWorkForJustRecNormalCall + ", isWorkForBlockMobile="
				+ isWorkForBlockMobile + ", iosDeviceToken=" + iosDeviceToken
				+ ", lastChangeLoginNameTime=" + lastChangeLoginNameTime + "]";
	}
}