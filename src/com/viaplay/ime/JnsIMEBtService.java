package com.viaplay.ime;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.viaplay.ime.R;
import com.viaplay.ime.bluetooth.JnsIMEBtServerThread;
import com.viaplay.ime.jni.JoyStickEvent;
import com.viaplay.ime.jni.RawEvent;
import com.viaplay.ime.uiadapter.JnsIMEBTDeviceListAdapter;
import com.viaplay.ime.uiadapter.JnsIMEControlListAdapter;
import com.viaplay.ime.util.SendEvent;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

/**
 * 后台运行的蓝牙操作服务
 *  
 * @author Steven
 *
 */
public class JnsIMEBtService extends Service {

	ListView devicesView;
	Dialog devicelistdialog;
	public Handler procehandler;
	public final static int START_CONNECT = 0x01;
	public final static int CONNECT_TIMEOUT = 0x02;
	public final static int CONNECT_OK = 0x03;
	public final static int SEARCH_DEVICE = 0x04;
	final BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
	Map<BluetoothDevice, Integer> deviceMap = new HashMap<BluetoothDevice, Integer>(); 
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate()
	{
		super.onCreate();
		JnsIMEApplication app = (JnsIMEApplication) this.getApplication();
		app.btService = this;
		BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
		if(!mAdapter.isEnabled())
			requestBTEnable();
		/*
		if(mAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)
		{
			 Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);  
			 // 设置蓝牙可见性，最多300秒   
			 intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);  
			 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			 this.startActivity(intent);  

		}
		 */
		Set<BluetoothDevice> devices = mAdapter.getBondedDevices();
		List<BluetoothDevice> devicelist =  FilterDevice(devices);
		// 设备连接进度条
		procehandler = new Handler()
		{
			ProgressDialog pDialog = new ProgressDialog(JnsIMEBtService.this);
			
			
			@SuppressLint("NewApi")
			@SuppressWarnings("deprecation")
			public void handleMessage(Message msg)
			{
				switch(msg.what)
				{
				case START_CONNECT:
					connectDevices();
					/*
					pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					pDialog.setMessage("connecting device...");
					pDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);  

					WindowManager.LayoutParams lp = pDialog.getWindow().getAttributes();    
					WindowManager wm = (WindowManager)JnsIMEBtService.this   
					.getSystemService(Context.WINDOW_SERVICE);    
					Display display = wm.getDefaultDisplay();    
					if (display.getHeight() > display.getWidth())    
					{    
						lp.width = (int) (display.getWidth() * 1.0);    
					}    
					else    
					{    
						lp.width = (int) (display.getWidth() * 0.25);    
					}    

					pDialog.getWindow().setAttributes(lp);  
					Log.d("JnsEnvInit", "showdialog");
					pDialog.show();  
					*/
					break;
				case CONNECT_TIMEOUT:
					pDialog.setButton("Sure", new DialogInterface.OnClickListener()
					{

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							pDialog.cancel();
						}			
					});
					break;
				case CONNECT_OK:
					pDialog.cancel();
					Toast.makeText(JnsIMEBtService.this, "connect ok", Toast.LENGTH_SHORT).show();
					break;
				case SEARCH_DEVICE:
					showBtDeviceList();
				}
				super.handleMessage(msg);
			}
		};
		
		Message msg = new Message();
		if(devicelist.size() == 0)
			msg.what = this.SEARCH_DEVICE;
		else
			msg.what = this.START_CONNECT;
			this.procehandler.sendMessage(msg);
		
	}
	/**
	 * 对码指定设备
	 * 
	 * @param device 需要对码的蓝牙设备
	 * @return
	 */
	int bondDevice(BluetoothDevice device)
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
	void connectDevices()
	{
		Set<BluetoothDevice> devices = mAdapter.getBondedDevices();
		List<BluetoothDevice> devicelist =  FilterDevice(devices);
		
		for(BluetoothDevice device:devicelist)
		{	
			JnsIMEBtServerThread btserver = new JnsIMEBtServerThread(device, this);
			btserver.start();
		}
	}
	@SuppressWarnings("deprecation")
	/**
	 *  若只有一个已对码的蓝牙设备，则直接连接
	 *  <p>若有多个已对码设备则，弹出列表让用户选择
	 *  <p>若无已对码设备，则引导用户对码蓝牙设备
	 */
	void showBtDeviceList()
	{
		Intent in = new Intent(this,JnsIMEBtListActivity.class);
		in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.startActivity(in);
		/*
		Set<BluetoothDevice> devices = mAdapter.getBondedDevices();
		List<BluetoothDevice> devicelist =  FilterDevice(devices);

		
		/*
		else if(devicelist.size() == 0)
		{
			devicelistdialog = null;
			devicesView = new ListView(this);
			devicesView.setOnItemClickListener(new OnItemClickListener()
			{

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					// TODO Auto-generated method stub
					Toast.makeText(JnsIMEBtService.this, "start JnsIMEBtServer", Toast.LENGTH_LONG).show();
					JnsIMEBtServerThread btserver = new JnsIMEBtServerThread((BluetoothDevice) devicesView.getItemAtPosition(arg2), JnsIMEBtService.this);
					btserver.start();
					devicelistdialog.cancel();
				}

			});
			deviceAdapter.setDeviceSet(devicelist);
			devicesView.setAdapter(deviceAdapter);
			devicelistdialog = new AlertDialog.Builder(this).setTitle("select a device to connect").setView(devicesView).create();
			devicelistdialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);  
			WindowManager.LayoutParams lp = devicelistdialog.getWindow().getAttributes();    
			WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);    
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
		*/
			/*
			mAdapter.startDiscovery();
			devicelistdialog = null;
			devicesView = new ListView(this);
			devicesView.setOnItemClickListener(new OnItemClickListener()
			{

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					// TODO Auto-generated method stub
					Toast.makeText(JnsIMEBtService.this, "start JnsIMEBtServer", Toast.LENGTH_LONG).show();
					@SuppressWarnings("unused")
					int state;
					if(((BluetoothDevice) devicesView.getItemAtPosition(arg2)).getBondState() == BluetoothDevice.BOND_NONE)
					{	
						state = bondDevice((BluetoothDevice) devicesView.getItemAtPosition(arg2));
					}
					JnsIMEBtServerThread btserver = new JnsIMEBtServerThread((BluetoothDevice) devicesView.getItemAtPosition(arg2), JnsIMEBtService.this);
					btserver.start();
					mAdapter.cancelDiscovery();
					devicelistdialog.cancel();
				}

			});
			devicesView.setAdapter(deviceAdapter);
			devicelistdialog = new AlertDialog.Builder(this).setTitle("select a device to connect").setView(devicesView).create();
			devicelistdialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);  
			WindowManager.LayoutParams lp = devicelistdialog.getWindow().getAttributes();    
			WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);    
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
			devicelistdialog.show();  */
		/*

		 */
	}

	/**
	 * 根据设备名过滤不支持的蓝牙设备
	 * 
	 * @param devices 蓝牙设备集合
	 * @return
	 */
	public List<BluetoothDevice> FilterDevice(Set<BluetoothDevice> devices) {
		// TODO Auto-generated method stub
		List<BluetoothDevice> list = new  ArrayList<BluetoothDevice>();
		Iterator<BluetoothDevice> iterator = devices.iterator();
		while(iterator.hasNext())
		{
			BluetoothDevice device = iterator.next();
			for(int i = 0; i < JnsIMEApplication.mDeviceInfoList.size(); i++)
			{
				if(device.getAddress().equals(JnsIMEApplication.mDeviceInfoList.get(i).getAdress()))
				{ 
					list.add(device);
					continue;
				}
			}
		}
		return list;
	}
	@SuppressWarnings("deprecation")
	/**
	 * 请求打开蓝牙
	 */
	void requestBTEnable()
	{
		OnClickListener ocl = new OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				switch(which)
				{
				case DialogInterface.BUTTON_POSITIVE:
					BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
					adapter.enable();
					while(!adapter.isEnabled());
				//	showBtDeviceList();
					break;
				}

			}

		};
		Dialog dialog = new AlertDialog.Builder(this).setMessage(this.getString(R.string.bt_notice) ).setPositiveButton("sure",
				ocl).setNegativeButton("cancle", ocl).create();
		dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);  
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();    
		WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);    
		Display display = wm.getDefaultDisplay();    
		if (display.getHeight() > display.getWidth())    
		{    
			lp.width = (int) (display.getWidth() * 1.0);    
		}    
		else    
		{    
			lp.width = (int) (display.getWidth() * 0.5);    
		}    
		dialog.getWindow().setAttributes(lp);  
		Log.d("JnsEnvInit", "showdialog");
		dialog.show();  		
	}
}
