package com.viaplay.ime.bluetooth;

import java.lang.reflect.Method;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.viaplay.ime.JnsIMEBtReceiver;
import com.viaplay.ime.uiadapter.JnsIMEBTDeviceListAdapter;

public class JnsIMBtSearchDevice {
	
	
	static ListView devicesView;
	static Dialog devicelistdialog;
	static JnsIMEBTDeviceListAdapter deviceAdapter = new JnsIMEBTDeviceListAdapter(JnsIMEBtReceiver.devices);

	
	public static void searchDevice(Context context)
	{
		final BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
		devicesView = new ListView(context);
		mAdapter.startDiscovery();
		Dialog devicelistdialog = null;
		final Context mContext  = context;
		devicesView.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Toast.makeText(mContext, "start JnsIMEBtServer", Toast.LENGTH_LONG).show();
				@SuppressWarnings("unused")
				int state;
				if(((BluetoothDevice) devicesView.getItemAtPosition(arg2)).getBondState() == BluetoothDevice.BOND_NONE)
				{	
					state = bondDevice((BluetoothDevice) devicesView.getItemAtPosition(arg2));
				}
				JnsIMEBtServerThread btserver = new JnsIMEBtServerThread((BluetoothDevice) devicesView.getItemAtPosition(arg2), mContext);
				btserver.start();
				mAdapter.cancelDiscovery();
				//JnsIMBtSearchDevice.devicelistdialog.cancel();
			}

		});
		devicesView.setAdapter(deviceAdapter);
		devicelistdialog = new AlertDialog.Builder(mContext).setTitle("select a device to connect").setView(devicesView).create();
		devicelistdialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);  
		WindowManager.LayoutParams lp = devicelistdialog.getWindow().getAttributes();    
		WindowManager wm = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);    
		Display display = wm.getDefaultDisplay();    
		if (display.getHeight() > display.getWidth())    
		{    
			lp.width = (int) (display.getWidth() * 1.0);    
		}    
		else    
		{    
			lp.width = (int) (display.getWidth() * 0.5);    
		}    
		devicelistdialog.getWindow().setAttributes(lp);  
		Log.d("JnsEnvInit", "showdialog");
		devicelistdialog.show();  
	}
	/**
	 * 对码指定设备
	 * 
	 * @param device 需要对码的蓝牙设备
	 * @return
	 */
	static int bondDevice(BluetoothDevice device)
	{
		try {  
			Method createBondMethod = BluetoothDevice.class.getMethod("createBond");  
			createBondMethod.invoke(device);  
			return device.getBondState();
		} catch (Exception e) {   
			e.printStackTrace();  
			return BluetoothDevice.BOND_NONE;
		}  

	}

}
