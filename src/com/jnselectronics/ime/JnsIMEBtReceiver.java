package com.jnselectronics.ime;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
/**
 * 监听新发现的蓝牙设备
 * 
 * @author Steven
 *
 */
public class JnsIMEBtReceiver extends BroadcastReceiver {

	public static List<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();

	static{
		BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
		Set<BluetoothDevice> bondDevices = mAdapter.getBondedDevices();
		Iterator<BluetoothDevice> iterator =bondDevices.iterator();
		while(iterator.hasNext())
		{
			devices.add(iterator.next());
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
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
		{
			for(int i = 0; i< devices.size(); i++)
			{
				if(devices.get(i).getAddress().equals(device.getAddress()))
					return;
			}
			devices.add(device);
			JnsIMEBtService.deviceAdapter.setDeviceSet(devices);
			JnsIMEBtService.deviceAdapter.notifyDataSetChanged();
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
