package com.jnselectronics.ime.bluetooth;

import java.io.IOException;
import java.util.UUID;

import com.jnselectronics.ime.JnsIMEBtService;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Message;
import android.widget.Toast;

/**
 * 蓝牙服务子线程主要用于连接蓝牙启动蓝牙守护进程,需求android4.0以上
 * 
 * @author Steven
 *
 */

public class JnsIMEBtServerThread extends Thread{

	BluetoothSocket socket;
	BluetoothDevice device;
	JnsIMEBtService mContext;

	BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
	UUID myuuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	@SuppressLint("NewApi")
	public JnsIMEBtServerThread(BluetoothDevice device, JnsIMEBtService context)
	{
		this.device = device;
		mContext = context;
	}

	@SuppressLint("NewApi")
	public void run()
	{
		long timeOut = 30000;
		long startTimer = System.currentTimeMillis();
	
		try {
			Message msg = new Message();
			msg.what = JnsIMEBtService.START_CONNECT;
			mContext.procehandler.sendMessage(msg);
			socket = mAdapter.getRemoteDevice(device.getAddress()).createInsecureRfcommSocketToServiceRecord(myuuid);
			socket.connect();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		while(!socket.isConnected() )
		{	
			if((System.currentTimeMillis() - startTimer) > timeOut)
			{
				Message msg = new Message();
				msg.what = JnsIMEBtService.CONNECT_TIMEOUT;
				mContext.procehandler.sendMessage(msg);
				return;
			}
			try 
			{
				socket = mAdapter.getRemoteDevice(device.getAddress()).createInsecureRfcommSocketToServiceRecord(myuuid);//createRfcommSocketToServiceRecord(myuuid);
				socket.connect();
			}
			catch (IOException e)
			{
				try 
				{
					socket.close();
					e.printStackTrace();
				} 
				catch (IOException e1)
				{

					e1.printStackTrace();
				}
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if(socket != null)
		{
			JnsIMEBtDataProcess dataprocess = new JnsIMEBtDataProcess(socket);
			JnsIMEBtDeamoThread deamoThread = new JnsIMEBtDeamoThread(socket, device, mContext);
			deamoThread.start();
			dataprocess.start();
			Message msg = new Message();
			msg.what = JnsIMEBtService.CONNECT_OK;
			mContext.procehandler.sendMessage(msg);
		}
	}
	//}
}
