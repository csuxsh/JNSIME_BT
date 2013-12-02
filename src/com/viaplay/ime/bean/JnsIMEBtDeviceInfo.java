package com.viaplay.ime.bean;

import android.bluetooth.BluetoothDevice;

public class JnsIMEBtDeviceInfo {
	
	String name;
	String adress;
	int status;
	int deviceID;
	BluetoothDevice devie;
	public final static int CONNECTED = 0x0a;
	public BluetoothDevice getDevie() {
		return devie;
	}
	public void setDevie(BluetoothDevice devie) {
		this.devie = devie;
	}
	public final static int DISCONNECT = 0x0b;
	public final static int IDENTIFIED_OVER = 0x0c;
	
	public int getDeviceID() {
		return deviceID;
	}
	public void setDeviceID(int deviceID) {
		this.deviceID = deviceID;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getAdress() {
		return adress;
	}
	public void setAdress(String adress) {
		this.adress = adress;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

}
