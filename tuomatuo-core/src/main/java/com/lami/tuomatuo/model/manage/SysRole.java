package com.lami.tuomatuo.model.manage;

import javax.persistence.*;

@Entity
@Table(name="Sys_Role") 
public class SysRole implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private String name;//角色名称
	private Integer id;//角色序号
	private String details;//角色说明
	
	public SysRole() {}
	
	/**角色名称**/
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	/**角色序号**/
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Integer getId() {
		return this.id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	/**角色说明**/
	public String getDetails() {
		return this.details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SysRole [")
				.append("name=").append(name).append(",")
				.append("id=").append(id).append(",")
				.append("details=").append(details)
				.append("]");
		return builder.toString();
	}

}