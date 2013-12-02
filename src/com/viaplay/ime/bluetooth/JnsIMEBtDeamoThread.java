package com.viaplay.ime.bluetooth;

import com.viaplay.ime.JnsIMEBtService;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

/**
 * �����ػ����̣���ǰ���ӵ���������֮�������������ӽ��̣�����������
 * 
 * @author Steven
 *
 */
public class JnsIMEBtDeamoThread extends Thread {
	
	BluetoothSocket socket = null;
	BluetoothDevice device = null;
	Context mContext;
	
	public JnsIMEBtDeamoThread( BluetoothDevice device, Context mContext)
	{
		this.device = device;
		mContext = mContext;
	}
	
	
	@SuppressLint("NewApi")
	public void run()
	{
		while(true)
		{	
			if(socket == null || !socket.isConnected())
			{

				JnsIMEBtServerThread btserver = new JnsIMEBtServerThread(device, mContext);
				btserver.start();
				break;
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
