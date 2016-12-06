package com.lami.tuomatuo.core.model.manage;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="Sys_Operator_Log") 
public class SysOperatorLog implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;//操作员日志序号
	private Integer operatorId;//操作员序号
	private Date createTime;//创建时间
	private String active;//操作内容
	private Integer isDelete;//是否标记删除
	
	public SysOperatorLog() {}
	
	/**操作员日志序号**/
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Integer getId() {
		return this.id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	/**操作员序号**/
	public Integer getOperatorId() {
		return this.operatorId;
	}
	public void setOperatorId(Integer operatorId) {
		this.operatorId = operatorId;
	}
	
	/**创建时间**/
	public Date getCreateTime() {
		return this.createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	/**操作内容**/
	public String getActive() {
		return this.active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	
	/**是否标记删除**/
	public Integer getIsDelete() {
		return this.isDelete;
	}
	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SysOperatorLog [")
				.append("id=").append(id).append(",")
				.append("operatorId=").append(operatorId).append(",")
				.append("createTime=").append(createTime).append(",")
				.append("active=").append(active).append(",")
				.append("isDelete=").append(isDelete)
				.append("]");
		return builder.toString();
	}

}