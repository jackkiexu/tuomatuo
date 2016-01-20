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

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

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
				'}';
	}
}