package com.viaplay.ime;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.viaplay.ime.bean.JnsIMEBtDeviceInfo;
import com.viaplay.ime.bluetooth.JnsIMEBtServerThread;
import com.viaplay.ime.uiadapter.JnsIMEControlListAdapter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.widget.Toast;
/**
 * 监听新发现的蓝牙设备
 * 
 * @author Steven
 *
 */
public class JnsIMEBtReceiver extends BroadcastReceiver {

	public static List<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();
	
	/*
	static{
		BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
		Set<BluetoothDevice> bondDevices = mAdapter.getBondedDevices();
		Iterator<BluetoothDevice> iterator =bondDevices.iterator();
		while(iterator.hasNext())
		{
			devices.add(iterator.next());
		}
	}
	*/
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		JnsIMEApplication app = null;
		try
		{
			app = (JnsIMEApplication) context.getApplicationContext();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		if(intent.getAction().equals("android.bluetooth.device.action.PAIRING_REQUEST"))
		{
			try {
					this.setPin(device.getClass(), device, "1234");
					Toast.makeText(context, "设置配对密码为1234", Toast.LENGTH_LONG).show();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			if (intent.getAction().equals(BluetoothDevice.ACTION_ACL_DISCONNECTED ))
		{
			JnsIMEBtDeviceInfo deviceinfo = null;
			for(int i = 0; i < JnsIMEApplication.mDeviceInfoList.size(); i++)
			{
				if(device.getAddress().equals(JnsIMEApplication.mDeviceInfoList.get(i).getAdress()))
				{
					deviceinfo = JnsIMEApplication.mDeviceInfoList.get(i);
					deviceinfo.setStatus(JnsIMEBtDeviceInfo.DISCONNECT);
					JnsIMEApplication.mDeviceInfoList.set(i, deviceinfo);
				}
				if(JnsIMEControllerActivity.adapter != null && deviceinfo != null)
				{	
					JnsIMEControllerActivity.adapter.setDeviceSet(JnsIMEApplication.mDeviceInfoList);
					Message msg = new Message();
					msg.what = JnsIMEBtDeviceInfo.DISCONNECT;
					if(JnsIMEControllerActivity.handler != null)
						JnsIMEControllerActivity.handler.sendMessage(msg);
				}
			}
			if(deviceinfo != null)
			{	
				JnsIMEBtServerThread btserver = new JnsIMEBtServerThread(device, context);
				btserver.start();
			}
		}
		else if(intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED))
		{
			if(app !=null && app.btlistActivity != null)
			{
				Message msg = new Message();
				msg.what = 0;
				app.btlistActivity.discoveryHandler.sendMessage(msg);
			}
		}
		else
		{
			for(int i = 0; i< devices.size(); i++)
			{
				if(devices.get(i).getAddress().equals(device.getAddress()))
				{
					devices.set(i, device);
					JnsIMEBtListActivity.setDeviceList();
					return;
				}
			}
			devices.add(device);
			JnsIMEBtListActivity.setDeviceList();
		//	JnsIMEBtService..notifyDataSetChanged();
		}
	}
	static public boolean setPin(Class btClass, BluetoothDevice btDevice,
			String str) throws Exception

			{
		try
		{
			@SuppressWarnings("unchecked")
			Method removeBondMethod = btClass.getDeclaredMethod("setPin",new Class[]{byte[].class});
			Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice,

					new Object[]

							{str.getBytes()});
		}
		catch (SecurityException e)
		{
			// throw new RuntimeException(e.getMessage());
			e.printStackTrace();
		}
		catch (IllegalArgumentException e)
		{
			// throw new RuntimeException(e.getMessage());
			e.printStackTrace();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
			}
}
