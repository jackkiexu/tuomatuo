package com.lami.tuomatuo.manage.bean;

/**
 * 
 * @author ben
 * @version 1.0 文件名称：DataSourceLookupKey.java
 */
public enum DataSourceLookupKey {
	MASTER_DATASOURCE("master"), DWH_DATASOURCE("dwh");

	private String value;

	private DataSourceLookupKey(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return this.value;
	}

}