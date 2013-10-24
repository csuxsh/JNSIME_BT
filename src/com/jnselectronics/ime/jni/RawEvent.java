package com.jnselectronics.ime.jni;

/**
 *  存储操控器发来的按键的数据的数据结构
 * 
 * @author Steven
 *
 */
public class RawEvent {
	
	public RawEvent(int keyCode, int scanCode, int value, int deviceId)
	{
		this.value = value;
		this.keyCode = keyCode;
		this.scanCode = scanCode;
		this.deviceId = deviceId;
	}
	public RawEvent()
	{
		
	}
	public int scanCode = 0;
	public int value = 0;
	public int keyCode = 0;
	public int deviceId = 0;
}
