package com.lami.tuomatuo.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;


@Entity
@Table(name = "user")
public class User implements java.io.Serializable {

	private static final long serialVersionUID = -6615167266833265252L;
	private Long id;
	private String name;
	private String mobile;
	private String mail;
	private Integer accountType; // 0, 默认创建的用户, 1 微信登录创建的用户, 2 QQ登录创建的用户
	private Integer status;
	private Date createTime;
	private Date updateTime;
	private Date lastLoginTime;
	private Long thirdAccountId; // 用户第三发账户的id (这个id可能存在于多张表中)
	private Date lastSynMemTime; // 上次同步到内存的时间
	private String code; // 验证码
	private Date sendCodeTime; // 验证码发送时间
	private String sign; // 用户签名, 每次登陆都会改变


	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public Integer getAccountType() {
		return accountType;
	}

	public void setAccountType(Integer accountType) {
		this.accountType = accountType;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public Long getThirdAccountId() {
		return thirdAccountId;
	}

	public void setThirdAccountId(Long thirdAccountId) {
		this.thirdAccountId = thirdAccountId;
	}

	public Date getLastSynMemTime() {
		return lastSynMemTime;
	}

	public void setLastSynMemTime(Date lastSynMemTime) {
		this.lastSynMemTime = lastSynMemTime;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Date getSendCodeTime() {
		return sendCodeTime;
	}

	public void setSendCodeTime(Date sendCodeTime) {
		this.sendCodeTime = sendCodeTime;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	@Override
	public String toString() {
		return "User{" +
				"id=" + id +
				", name='" + name + '\'' +
				", mobile='" + mobile + '\'' +
				", mail='" + mail + '\'' +
				", accountType=" + accountType +
				", status=" + status +
				", createTime=" + createTime +
				", updateTime=" + updateTime +
				", lastLoginTime=" + lastLoginTime +
				", thirdAccountId=" + thirdAccountId +
				", lastSynMemTime=" + lastSynMemTime +
				", code='" + code + '\'' +
				", sendCodeTime=" + sendCodeTime +
				", sign='" + sign + '\'' +
				'}';
	}
}