package com.viaplay.ime.bluetooth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.viaplay.ime.JnsIMEBtService;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Message;
import android.util.Log;
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
		long timeOut = 30000;
		long startTimer = System.currentTimeMillis();

		for(int i = 0; i < connectingList.size(); i++) 
		{
			BluetoothDevice coDevice = connectingList.get(i);
			if(this.device.getAddress().equals(coDevice.getAddress()))
			{				
				Log.e(device.getName(), " is connecting");
				return;
			}
		}
		synchronized(connectingList)
		{
			connectingList.add(device);
		}
		try {
			//Message msg = new Message();
			///	msg.what = JnsIMEBtService.START_CONNECT;
			//mContext.procehandler.sendMessage(msg);
			//if(device.getBondState() == BluetoothDevice.BOND_NONE)
			//	JnsIMBtSearchDevice.bondDevice(device);
			socket = mAdapter.getRemoteDevice(device.getAddress()).createInsecureRfcommSocketToServiceRecord(myuuid);
			socket.connect();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			try {
				socket.close();
				socket= null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			e2.printStackTrace();
		}
		while(socket == null || !socket.isConnected() )
		{	
			//if((System.currentTimeMillis() - startTimer) > timeOut)
			//{
			//Message msg = new Message();
			//msg.what = JnsIMEBtService.CONNECT_TIMEOUT;
			//mContext.procehandler.sendMessage(msg);
			//	return;
			//}
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
					socket= null;
				//	e.printStackTrace();
				} 
				catch (IOException e1)
				{

					e1.printStackTrace();
				}
			}
			try {
				Thread.sleep(2500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if(socket != null)
		{
			JnsIMEBtDataProcess dataprocess = new JnsIMEBtDataProcess(socket, device, mContext);
			//	JnsIMEBtDeamoThread deamoThread = new JnsIMEBtDeamoThread(device, mContext);
			//	deamoThread.socket = socket;
			//	deamoThread.start();
			dataprocess.start();
			Message msg = new Message();
			synchronized(connectingList)
			{
				connectingList.remove(device);
			}	
			//	msg.what = JnsIMEBtService.CONNECT_OK;
			//	mContext.procehandler.sendMessage(msg);
		}
	}
	//}
}
