package com.lami.tuomatuo.model.manage;

import javax.persistence.*;

@Entity
@Table(name="Sys_Role_Permission") 
public class SysRolePermission implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private Integer permissionId;//权限序号
	private Integer roleId;//角色序号
	private Integer id;//角色权限序号
	
	public SysRolePermission() {}
	
	/**权限序号**/
	public Integer getPermissionId() {
		return this.permissionId;
	}
	public void setPermissionId(Integer permissionId) {
		this.permissionId = permissionId;
	}
	
	/**角色序号**/
	public Integer getRoleId() {
		return this.roleId;
	}
	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}
	
	/**角色权限序号**/
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Integer getId() {
		return this.id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SysRolePermission [")
				.append("permissionId=").append(permissionId).append(",")
				.append("roleId=").append(roleId).append(",")
				.append("id=").append(id)
				.append("]");
		return builder.toString();
	}

}