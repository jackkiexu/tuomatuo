package com.lami.tuomatuo.core.base;

public abstract class Flagable {
	public static final String DELETEFLAG="isDelete";
	public static final Integer ISDELETE_TRUE=1; 
	public static final Integer ISDELETE_FALSE=0;
	private Integer isDelete;
	public Integer getIsDelete() {
		return isDelete;
	}
	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
	}
	
}
