package com.lami.tuomatuo.core.model.manage;

import javax.persistence.*;

@Entity
@Table(name="Sys_Operator_Sp") 
public class SysOperatorSp implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;//管理员角色序号
	private Integer spId;//商户序号
	private Integer operatorId;//管理员序号
	
	public SysOperatorSp() {}
	
	/**
	 * 管理员角色序号
	 **/
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Integer getId() {
		return this.id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	/**
	 * 商户序号
	 **/
	public Integer getSpId() {
		return this.spId;
	}
	public void setSpId(Integer spId) {
		this.spId = spId;
	}
	
	/**
	 * 管理员序号
	 **/
	public Integer getOperatorId() {
		return this.operatorId;
	}
	public void setOperatorId(Integer operatorId) {
		this.operatorId = operatorId;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SysOperatorSp [")
				.append("id=").append(id).append(",")
				.append("spId=").append(spId).append(",")
				.append("operatorId=").append(operatorId)
				.append("]");
		return builder.toString();
	}

}