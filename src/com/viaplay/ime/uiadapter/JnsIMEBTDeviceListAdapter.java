package com.viaplay.ime.uiadapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import android.bluetooth.BluetoothDevice;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 蓝牙设备列表适配器
 * 
 * 
 * @author Steven
 *
 */

public class JnsIMEBTDeviceListAdapter extends BaseAdapter {
	
	//Set<BluetoothDevice> devices = null;
	List<BluetoothDevice> devicelist = new ArrayList<BluetoothDevice>();
	
	public JnsIMEBTDeviceListAdapter(List<BluetoothDevice> devicelist)
	{
		setDeviceSet(devicelist);
	}
	public void setDeviceSet(List<BluetoothDevice> devices)
	{
		//devicelist = devices;
		devicelist.clear();
		
		Iterator<BluetoothDevice> iterator =  devices.iterator();
		while(iterator.hasNext())
		{
			BluetoothDevice device = iterator.next();
			if(device.getBondState() == BluetoothDevice.BOND_BONDED)
				continue;
			devicelist.add(device);
		}
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return devicelist.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return devicelist.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		//if (convertView == null) {
		///	convertView = MainActivity.this.getLayoutInflater().inflate(R.layout.text, parent, false);
		//}
		TextView textview = new TextView(parent.getContext()); //(TextView) convertView;
		textview.setText(devicelist.get(position).getName());
		textview.setTextSize(26);
		return textview;
	}

}
