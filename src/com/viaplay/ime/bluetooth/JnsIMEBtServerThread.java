package com.viaplay.ime.bluetooth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

/**
 * 蓝牙服务子线程主要用于连接蓝牙启动蓝牙守护进程,需求android4.0以上
 * 
 * @author Steven
 *
 */

public class JnsIMEBtServerThread extends Thread{
	
	
	final static String TAG ="BTserver";
 
	BluetoothSocket socket;
	BluetoothDevice device;
	Context mContext;
	public static List<BluetoothDevice> connectingList = new ArrayList<BluetoothDevice>();

	BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
	UUID myuuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	@SuppressLint("NewApi")
	public JnsIMEBtServerThread(BluetoothDevice device, Context context)
	{
		this.device = device;
		mContext = context;
	}

	@SuppressLint("NewApi")
	public void run()
	{
		for(int i = 0; i < connectingList.size(); i++) 
		{
			BluetoothDevice coDevice = connectingList.get(i);
			if(this.device.getAddress().equals(coDevice.getAddress()))
			{				
				Log.e(TAG,device.getName()+ " is connecting");
				return;
			}
		}
		synchronized(connectingList)
		{
			connectingList.add(device);
		}		
		long startTime = System.currentTimeMillis();
		while(socket == null)
		{	
			try 
			{
				socket = mAdapter.getRemoteDevice(device.getAddress()).createInsecureRfcommSocketToServiceRecord(myuuid);//createRfcommSocketToServiceRecord(myuuid);
				socket.connect();
				Log.e(TAG,device.getName()+ " connect ok");
				
			}
			catch (IOException e)
			{
				Log.e(TAG,device.getName()+ " connect failed");
				try 
				{
					socket.close();
					socket= null;
					e.printStackTrace();
				} 
				catch (IOException e1)
				{
					e1.printStackTrace();
					socket= null;
				}
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(System.currentTimeMillis() - startTime > 10000)
			{
				synchronized(connectingList)
				{
					connectingList.remove(device);
				}
				Log.d(TAG, "device:"+device.getAddress()+"connect time out");
				return;
			}
		}
		JnsIMEBtDataProcess dataprocess = new JnsIMEBtDataProcess(socket, device, mContext);
		dataprocess.start();
		synchronized(connectingList)
		{
			connectingList.remove(device);
		}	
		
	}
}
