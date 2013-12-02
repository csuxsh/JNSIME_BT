package com.viaplay.ime.uiadapter;

import java.util.ArrayList;
import java.util.List;

import com.viaplay.ime.R;
import com.viaplay.ime.bean.JnsIMEBtDeviceInfo;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class JnsIMEControlListAdapter extends BaseAdapter {

	
	List<JnsIMEBtDeviceInfo> mDeviceInfoList= new ArrayList<JnsIMEBtDeviceInfo>();
	Activity activity;


	public void setParent(Activity activity)
	{
		this.activity = activity;
	}
	public void setDeviceSet(List<JnsIMEBtDeviceInfo> devicelist)
	{
		//devicelist = devices;
		mDeviceInfoList.clear();
		for(JnsIMEBtDeviceInfo deviceInfo : devicelist)
		{
			mDeviceInfoList.add(deviceInfo);
		}
		
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mDeviceInfoList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mDeviceInfoList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		String status = "";
		if (convertView == null) {
			convertView = activity.getLayoutInflater().inflate(R.layout.control_list, parent, false);		}
		TextView name = (TextView) convertView.findViewById(R.id.name); //(TextView) convertView;
		TextView address = (TextView) convertView.findViewById(R.id.adress);
		if(mDeviceInfoList.get(position).getStatus() == JnsIMEBtDeviceInfo.CONNECTED)
			status = "  connected";
		else
			if (mDeviceInfoList.get(position).getStatus() == JnsIMEBtDeviceInfo.DISCONNECT)
			status = "  connecting...";
		else if((mDeviceInfoList.get(position).getStatus() == JnsIMEBtDeviceInfo.IDENTIFIED_OVER))
			status = "  Identified over";
		name.setText(mDeviceInfoList.get(position).getName());
		address.setText(mDeviceInfoList.get(position).getAdress()+status);
		return convertView;
	}

}
