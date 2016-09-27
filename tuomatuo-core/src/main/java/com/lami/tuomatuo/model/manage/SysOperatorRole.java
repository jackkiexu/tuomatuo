package com.lami.tuomatuo.model.manage;

import javax.persistence.*;

@Entity
@Table(name="Sys_Operator_Role") 
public class SysOperatorRole implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;//管理员角色序号
	private Integer roleId;//角色序号
	private Integer operatorId;//管理员序号
	
	public SysOperatorRole() {}
	
	/**管理员角色序号**/
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Integer getId() {
		return this.id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	/**角色序号**/
	public Integer getRoleId() {
		return this.roleId;
	}
	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}
	
	/**管理员序号**/
	public Integer getOperatorId() {
		return this.operatorId;
	}
	public void setOperatorId(Integer operatorId) {
		this.operatorId = operatorId;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SysOperatorRole [")
				.append("id=").append(id).append(",")
				.append("roleId=").append(roleId).append(",")
				.append("operatorId=").append(operatorId)
				.append("]");
		return builder.toString();
	}

}