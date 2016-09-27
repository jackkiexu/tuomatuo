package com.lami.tuomatuo.model.manage;

import javax.persistence.*;

@Entity
@Table(name="System_Config") 
public class SystemConfig implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	private Integer id;//系统参数序号
	private String name;//参数名称
	private String details;//参数说明
	private String value;//参数值
	
	public SystemConfig() {}
	
	/**
	 * 系统参数序号
	 * **/
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Integer getId() {
		return this.id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	/**
	 * 参数名称
	 * **/
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * 参数说明
	 * **/
	public String getDetails() {
		return this.details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	
	/**
	 * 参数值
	 * **/
	public String getValue() {
		return this.value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SystemConfig [")
				.append("id=").append(id).append(",")
				.append("name=").append(name).append(",")
				.append("details=").append(details).append(",")
				.append("value=").append(value)
				.append("]");
		return builder.toString();
	}

}