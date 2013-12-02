package com.viaplay.ime.bean;

/**
 * 用于绑定在游戏列表数据库的bean类
 * 
 * @author Steven.xu
 *
 */
	
public class GameInfo {
	
	private String pkgname;
	private boolean exists;
	private String lable;
	
	
	public String getLable() {
		return lable;
	}
	public void setLable(String lable) {
		this.lable = lable;
	}
	public String getPkgname() {
		return pkgname;
	}
	public void setPkgname(String pkgname) {
		this.pkgname = pkgname;
	}
	public boolean isExists() {
		return exists;
	}
	public void setExists(boolean exists) {
		this.exists = exists;
	}
}
