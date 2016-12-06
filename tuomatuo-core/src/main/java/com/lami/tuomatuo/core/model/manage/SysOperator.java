package com.lami.tuomatuo.core.model.manage;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="Sys_Operator") 
public class SysOperator implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private String telephone;//联系电话
	private String login;//登录帐号
	private Date createTime;//创建时间
	private String name;//姓名
	private Date lastLoginTime;//最后登录时间
	private String password;//登录密码
	private Integer id;//管理员序号
	private Date updateTime;//更新时间
	private Integer status;//帐号状态
	private Integer checkIp;
	private String ip;
	/** 无需判断ip **/
	public static final Integer CHECKIP_0=0;
	/** 需判断ip **/
	public static final Integer CHECKIP_1=1;
	public static final Integer STATUS_SUCCESS=0;
	public static final Integer STATUS_STOP=1;
	
	
	
	public Integer getCheckIp() {
		return checkIp;
	}

	public void setCheckIp(Integer checkIp) {
		this.checkIp = checkIp;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	/**帐号状态**/
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public SysOperator() {}
	
	/**联系电话**/
	public String getTelephone() {
		return this.telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	
	/**登录帐号**/
	public String getLogin() {
		return this.login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	
	/**创建时间**/
	public Date getCreateTime() {
		return this.createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	/**姓名**/
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	/**最后登录时间**/
	public Date getLastLoginTime() {
		return this.lastLoginTime;
	}
	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}
	
	/**登录密码**/
	public String getPassword() {
		return this.password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	/**管理员序号**/
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Integer getId() {
		return this.id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	/**更新时间**/
	public Date getUpdateTime() {
		return this.updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	public String toString() {
		return "SysOperator [telephone=" + telephone + ", login=" + login
				+ ", createTime=" + createTime + ", name=" + name
				+ ", lastLoginTime=" + lastLoginTime + ", password=" + password
				+ ", id=" + id + ", updateTime=" + updateTime + ", status="
				+ status + ", checkIp=" + checkIp + ", ip=" + ip + "]";
	}
	

}