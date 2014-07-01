package com.viaplay.ime.jni;

/**
 *  ��?���2��???�¡��騤���?���?����?��y?Y��?��y?Y?��11
 * 
 * @author Steven
 *
 */
public class RawEvent {
	
	public RawEvent(int keyCode, int scanCode, int value, int deviceId, int type)
	{
		this.value = value;
		this.keyCode = keyCode;
		this.scanCode = scanCode;
		this.deviceId = deviceId;
		this.type = type;
	}
	public RawEvent(int keyCode, int scanCode, int value, int deviceId)
	{
		this.value = value;
		this.keyCode = keyCode;
		this.scanCode = scanCode;
		this.deviceId = deviceId;
		this.type = 1;
	}
	public RawEvent()
	{
		
	}
	public int scanCode = 0;
	public int value = 0;
	public int keyCode = 0;
	public int deviceId = 0;
	public int type = 0;
}
