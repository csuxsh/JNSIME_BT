package com.viaplay.ime;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.viaplay.ime.bean.JnsIMEBtDeviceInfo;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class JnsIMEApplication extends Application {
	
	
	public static List<JnsIMEBtDeviceInfo> mDeviceInfoList= new ArrayList<JnsIMEBtDeviceInfo>();

	JnsIMEBtService btService = null;
	JnsIMEBtListActivity btlistActivity = null;
	@Override
	public void onCreate()
	{
		super.onCreate();
		SharedPreferences sp = this.getSharedPreferences("devicelist", Context.MODE_PRIVATE);
		@SuppressWarnings("unchecked")
		Set<Entry<String, String>> deviceSet = ((Map<String, String>) sp.getAll()).entrySet();
		Iterator<Entry<String, String>> it = deviceSet.iterator();
		while(it.hasNext())
		{
			Entry<String, String> en = it.next(); 
			JnsIMEBtDeviceInfo deviceInfo = new JnsIMEBtDeviceInfo();
			deviceInfo.setAdress(en.getKey());
			deviceInfo.setName(en.getValue());
			deviceInfo.setStatus(JnsIMEBtDeviceInfo.IDENTIFIED_OVER);
			mDeviceInfoList.add(deviceInfo);
		}
		
	}
	public static void saveDeviceInfo(Context context)
	{
		SharedPreferences sp = context.getSharedPreferences("devicelist", Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = sp.edit();
		for(JnsIMEBtDeviceInfo deviceinfo : mDeviceInfoList)
		{
			edit.putString(deviceinfo.getAdress(), deviceinfo.getName());
		}
		edit.commit();
	}
	

}
