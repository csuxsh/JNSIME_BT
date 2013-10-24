package com.jnselectronics.ime.bluetooth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
/**
 * 处理蓝牙的数据传输。
 * 
 * 
 * @author Steven
 *
 */
public class JnsIMEBtDataProcess extends Thread{

	final static int HEAD_TAG = 0xa1;
	final static int GAME_DATA_TAG = 0x03;
	final static int GAME_DATA_LENGTH = 9;
	final static int KEYBOARD_DATA_TAG = 0x01;
	final static int KEYBOARD_DATA_LENGTH =7;
	final static int GAME_BUTTON1_DATA_TAG = 0x0e;
	final static int GAME_BUTTON2_DATA_TAG = 0x0f;
	final static int HAT_INDEX = 0;
	final static int ABS_X_INDEX = 1;
	final static int ABS_Y_INDEX = 2;
	final static int ABS_Z_INDEX = 3;
	final static int ABS_RZ_INDEX = 4;
	final static int BRAKE_INDEX = 5;
	final static int GAS_INDEX = 6;
	final static int BUTTON1_INDEX = 7;
	final static int BUTTON2_INDEX = 8;
	
	
	private OutputStreamWriter osw = null;
	private InputStreamReader isr = null;
	private final String TAG = "Bt_DataProcess";
	private BluetoothSocket socket = null;
	static byte[] gamePadData = {15,127,127,127,127,0,0,0,0};
	static byte[] keyboardData = new byte[KEYBOARD_DATA_LENGTH];
	

	public JnsIMEBtDataProcess(BluetoothSocket socket)
	{
		this.socket = socket;
		try {
			osw = new OutputStreamWriter(socket.getOutputStream());
			isr = new InputStreamReader(socket.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressLint("NewApi")
	public void run()
	{
		Log.d(TAG, "a client connect:"+socket.getRemoteDevice().getAddress());

		while(socket.isConnected())
		{
			processData();
			/*
			BufferedReader br = new BufferedReader(isr);
			try {
				String line = br.readLine();
				Log.d(TAG, "line = "+line);
				processData(line);
				PrintWriter pw = null;
				try {
					pw = new PrintWriter(osw);
					pw.write("ok\n");
					pw.flush();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					socket.close();
					e.printStackTrace();
					break;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				try {
					socket.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.printStackTrace();
				break;
			}*/
		}
		
	}
	String processGamePadData(InputStream is)
	{
		byte[] buffer = new byte[GAME_DATA_LENGTH];
		try {
			if(is.read(buffer, 0, GAME_DATA_LENGTH) == GAME_DATA_LENGTH)
			{
				if((buffer[ABS_X_INDEX] != gamePadData[ABS_X_INDEX]) ||
					(buffer[ABS_Y_INDEX] != gamePadData[ABS_Y_INDEX]) ||	
					(buffer[ABS_Z_INDEX] != gamePadData[ABS_Z_INDEX]) ||
					(buffer[ABS_RZ_INDEX] != gamePadData[ABS_RZ_INDEX]) ||
					(buffer[GAS_INDEX] != gamePadData[GAS_INDEX]) ||
					(buffer[BRAKE_INDEX] != gamePadData[BRAKE_INDEX]) ||
					(buffer[HAT_INDEX] != gamePadData[HAT_INDEX])
						) 
				{
					JnsIMEBtDataStruct data = new JnsIMEBtDataStruct();
					data.setDataTag(GAME_DATA_TAG);
					data.setData(buffer);
					JnsIMEBtInputDriver.setData(data);
				}
				if(buffer[BUTTON1_INDEX] != gamePadData[BUTTON1_INDEX])
				{
					JnsIMEBtDataStruct data = new JnsIMEBtDataStruct();
					data.setDataTag(GAME_BUTTON1_DATA_TAG);
					data.setData(buffer);
					JnsIMEBtInputDriver.setData(data);
				}
				for(int i = 0; i < gamePadData.length; i++)
				{
					gamePadData[i] = buffer[i];
				}
			}
			else
				return "erro";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return TAG;
	}
	void processData()
	{
		try {
			InputStream is = socket.getInputStream();
			if(is.read() == HEAD_TAG)
			{
				int tag = is.read();
				if(tag == GAME_DATA_TAG)
				{
					processGamePadData(is);
				}
				else if(tag == KEYBOARD_DATA_TAG)
				{
					//processKeyboardData(is);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean processKeyboardData(InputStream is) {
		// TODO Auto-generated method stub
		byte[] buffer = new byte[KEYBOARD_DATA_LENGTH];
		try {
			if(is.read(buffer, 0, KEYBOARD_DATA_LENGTH) == KEYBOARD_DATA_LENGTH)
			{
				if((buffer[ABS_X_INDEX] != gamePadData[ABS_X_INDEX]) ||
					(buffer[ABS_Y_INDEX] != gamePadData[ABS_Y_INDEX]) ||	
					(buffer[ABS_Z_INDEX] != gamePadData[ABS_Z_INDEX]) ||
					(buffer[ABS_RZ_INDEX] != gamePadData[ABS_RZ_INDEX]) ||
					(buffer[GAS_INDEX] != gamePadData[GAS_INDEX]) ||
					(buffer[BRAKE_INDEX] != gamePadData[BRAKE_INDEX]) ||
					(buffer[HAT_INDEX] != gamePadData[HAT_INDEX])
						) 
				{
					JnsIMEBtDataStruct data = new JnsIMEBtDataStruct();
					data.setDataTag(GAME_DATA_TAG);
					data.setData(buffer);
					JnsIMEBtInputDriver.setData(data);
				}
				if(buffer[BUTTON1_INDEX] != gamePadData[BUTTON1_INDEX])
				{
					JnsIMEBtDataStruct data = new JnsIMEBtDataStruct();
					data.setDataTag(GAME_BUTTON1_DATA_TAG);
					data.setData(buffer);
					JnsIMEBtInputDriver.setData(data);
				}
				if(buffer[BUTTON2_INDEX] != gamePadData[BUTTON2_INDEX])
				{
					JnsIMEBtDataStruct data = new JnsIMEBtDataStruct();
					data.setDataTag(GAME_BUTTON2_DATA_TAG);
					data.setData(buffer);
					JnsIMEBtInputDriver.setData(data);
				}
			}
			else
				return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
}
