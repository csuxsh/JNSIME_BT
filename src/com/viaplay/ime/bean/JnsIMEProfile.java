package com.viaplay.ime.bean;

/**
 * 触摸映射的配置文�? * 
 * @author Steven
 *
 */

public class JnsIMEProfile {
	public static final int LEFT_JOYSTICK = 0;
	public static final int RIGHT_JOYSTICK = 1;
	public int keyCode;
	public int key;
	public float posX; 
	public float posY; 
	public float posR; 
	public float posType; 
	
	public JnsIMEProfile()
	{
		posType = 2;
	}
}
