package com.jnselectronics.ime.bean;

/**
 * ���ڰ�����Ϸ�б����ݿ��bean��
 * 
 * @author Steven.xu
 *
 */
	
public class GameInfo {
	
	/**
	 *  Ӧ�õİ���
	 */
	private String pkgname;
	/**
	 *  Ӧ���Ƿ��Ѿ���װ���豸��
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
