package com.jnselectronics.ime.bean;

/**
 * 用于绑定在游戏列表数据库的bean类
 * 
 * @author Steven.xu
 *
 */
	
public class GameInfo {
	
	/**
	 *  应用的包名
	 */
	private String pkgname;
	/**
	 *  应用是否已经安装在设备上
	 */
	private boolean exists;
	
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
