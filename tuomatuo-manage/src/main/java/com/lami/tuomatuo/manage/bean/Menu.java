package com.lami.tuomatuo.manage.bean;

import java.util.TreeMap;

public class Menu {
	private int id;
	private String name;
	private TreeMap<String,String> menuList;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public TreeMap<String, String> getMenuList() {
		return menuList;
	}
	public void setMenuList(TreeMap<String, String> menuList) {
		this.menuList = menuList;
	}
	
	
}
