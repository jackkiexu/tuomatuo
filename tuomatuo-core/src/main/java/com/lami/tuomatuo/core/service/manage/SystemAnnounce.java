package com.lami.tuomatuo.core.service.manage;

/** 系统公告
 */
public class SystemAnnounce {
	
	public static int PRODUCT_FIXED_MHAO = 1; // 固定密号
	public static int PRODUCT_RELATION_MHAO = 2;// 关系密号
	public static int PRODUCT_MHAO = 3; // 所有密号服务
	
	public Integer productCategory; // 产品类别
	public String  theme; // 主题
	public String  content; // 内容;
	
	public SystemAnnounce(Integer productCategory, String theme, String content) {
		this.productCategory = productCategory;
		this.theme = theme;
		this.content = content;
	}
	
	public Integer getProductCategory() {
		return productCategory;
	}
	public void setProductCategory(Integer productCategory) {
		this.productCategory = productCategory;
	}
	public String getTheme() {
		return theme;
	}
	public void setTheme(String theme) {
		this.theme = theme;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	@Override
	public String toString() {
		return "SystemAnnounce [productCategory=" + productCategory
				+ ", theme=" + theme + ", content=" + content + "]";
	}
}
