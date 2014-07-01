package com.viaplay.ime;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.viaplay.ime.R;
import com.viaplay.ime.bluetooth.JnsIMEBtDataProcess;
import com.viaplay.ime.bluetooth.JnsIMEBtServerThread;
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
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

/**
 * ��̨���е�������������
 *  
 * @author Steven
 *
 */
public class JnsIMEBtService extends Service {

	
	public static boolean isalive = false;
	ListView devicesView;
	Dialog devicelistdialog;
	public Handler procehandler;
	public final static int START_CONNECT = 0x01;
	public final static int CONNECT_TIMEOUT = 0x02;
	public final static int CONNECT_OK = 0x03;
	public final static int SEARCH_DEVICE = 0x04;
	final BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();

	static class BtHandler extends Handler
	{
		WeakReference<JnsIMEBtService> mActivity;
		   
		BtHandler(JnsIMEBtService context) {
                mActivity = new WeakReference<JnsIMEBtService>(context);
        }

        @Override
        public void handleMessage(Message msg) {
        
          }
        
	};
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if(JnsIMEBtDataProcess.mWakeLock.isHeld())
			JnsIMEBtDataProcess.mWakeLock.release();
		this.isalive = false;
	}
	@Override
	public void onCreate()
	{
		super.onCreate();
		this.isalive = true;
		JnsIMEApplication app = (JnsIMEApplication) this.getApplication();
		app.btService = this;
		BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
		if(!mAdapter.isEnabled())
			requestBTEnable();
		
		Set<BluetoothDevice> devices = mAdapter.getBondedDevices();
		List<BluetoothDevice> devicelist =  FilterDevice(devices);
		// �豸���ӽ�����
		procehandler = new BtHandler(this)
		{
			ProgressDialog pDialog = new ProgressDialog(JnsIMEBtService.this);
			
			
			@SuppressLint("NewApi")
			@SuppressWarnings("deprecation")
			public void handleMessage(Message msg)
			{
				switch(msg.what)
				{
				case START_CONNECT:
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
		/*
		Message msg = new Message();
		if(devicelist.size() == 0)
			msg.what = JnsIMEBtService.SEARCH_DEVICE;
		else
			msg.what = JnsIMEBtService.START_CONNECT;
			this.procehandler.sendMessage(msg);
		*/
	}
	/**
	 * ����ָ���豸
	 * 
	 * @param device ��Ҫ����������豸
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
	/**
	 *  ��ֻ��һ���Ѷ���������豸����ֱ������
	 *  <p>���ж���Ѷ����豸�򣬵����б����û�ѡ��
	 *  <p>�����Ѷ����豸���������û����������豸
	 */
	void showBtDeviceList()
	{
		BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
		if(!mAdapter.isEnabled())
		{
			requestBTEnable();
			return;
		}
		Intent in = new Intent(this,JnsIMEBtListActivity.class);
		in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.startActivity(in);
	}

	/**
	 * �����豸�����˲�֧�ֵ������豸
	 * 
	 * @param devices �����豸����
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
	 * ���������
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
