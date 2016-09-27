package com.lami.tuomatuo.model.manage;

import javax.persistence.*;

@Entity
@Table(name="Sys_Permission") 
public class SysPermission implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;//权限序号
	private String name;//权限名称
	private String path;//权限路径
	private String methods;//权限方法
	private String menu;//导航标识
	private Integer parentId;//所属父节点 
	
	public SysPermission() {}
	
	/**父节点序号**/
	public Integer getParentId() {
		return parentId;
	}


	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}


	/**权限序号**/
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Integer getId() {
		return this.id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	/**权限名称**/
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	/**权限路径**/
	public String getPath() {
		return this.path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
	/**权限方法**/
	public String getMethods() {
		return this.methods;
	}
	public void setMethods(String methods) {
		this.methods = methods;
	}
	
	/**导航标识**/
	public String getMenu() {
		return this.menu;
	}
	public void setMenu(String menu) {
		this.menu = menu;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SysPermission [")
				.append("id=").append(id).append(",")
				.append("name=").append(name).append(",")
				.append("path=").append(path).append(",")
				.append("methods=").append(methods).append(",")
				.append("menu=").append(menu)
				.append("]");
		return builder.toString();
	}

}