package com.jnselectronics.ime.bluetooth;

public class JnsIMEBtDataStruct {
	
	int dataTag;
	byte[] data;
	
	public int getDataTag() {
		return dataTag;
	}
	public void setDataTag(int gameDataTag) {
		this.dataTag = gameDataTag;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}

}
