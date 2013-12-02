package com.viaplay.ime;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.viaplay.ime.bean.JnsIMEBtDeviceInfo;
import com.viaplay.ime.bluetooth.JnsIMEBtServerThread;
import com.viaplay.ime.uiadapter.JnsIMEControlListAdapter;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class JnsIMEBtListActivity extends Activity {

	ListView devicesView;
	ProgressBar pb;
	TextView alert;
	static List<JnsIMEBtDeviceInfo> devList = new ArrayList<JnsIMEBtDeviceInfo>();
	static JnsIMEControlListAdapter adapter  = new JnsIMEControlListAdapter();
	public Handler discoveryHandler;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.bt_device_list);
		final BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
		JnsIMEApplication app = (JnsIMEApplication) this.getApplicationContext();
		app.btlistActivity = this;
		mAdapter.startDiscovery();
		devicesView = (ListView) this.findViewById(R.id.btlist);
		pb = (ProgressBar) this.findViewById(R.id.progressBar1);
		alert = (TextView) this.findViewById(R.id.alert);
		adapter.setParent(this);
		setDeviceList();
		devicesView.setAdapter(adapter);
		devicesView.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

				if(((JnsIMEBtDeviceInfo)devicesView.getItemAtPosition(arg2)).getDevie() == null)
				{	
					JnsIMEBtListActivity.this.finish();
					return;
				}	
				Toast.makeText(JnsIMEBtListActivity.this, "start JnsIMEBtServer", Toast.LENGTH_LONG).show();
				@SuppressWarnings("unused")
				BluetoothDevice device = (BluetoothDevice) ((JnsIMEBtDeviceInfo)devicesView.getItemAtPosition(arg2)).getDevie();
				if(device.getBondState() == BluetoothDevice.BOND_NONE)
				{	
					bondDevice(device);
				}
				JnsIMEBtServerThread btserver = new JnsIMEBtServerThread(device, JnsIMEBtListActivity.this);
				btserver.start();
				mAdapter.cancelDiscovery();
				JnsIMEBtListActivity.this.finish();
				//JnsIMBtSearchDevice.devicelistdialog.cancel();
			}

		});
		

		discoveryHandler = new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				mAdapter.startDiscovery();
			    
				//	timer.cancel();
			//	timer.schedule(task, 12000);
				alert.setText(R.string.search_end);
				pb.setVisibility(View.GONE);
				super.handleMessage(msg);
			}
		};
	}
	static void setDeviceList()
	{
		devList.clear();
		/*
		for(JnsIMEBtDeviceInfo devInfo :JnsIMEApplication.mDeviceInfoList)
		{
			devList.add(devInfo);
		}
		 */
		for(BluetoothDevice device: JnsIMEBtReceiver.devices)
		{
			JnsIMEBtDeviceInfo devInfo = new JnsIMEBtDeviceInfo();
			devInfo.setName(device.getName());
			devInfo.setAdress(device.getAddress());
			devInfo.setDevie(device);
			devInfo.setStatus(0);
			devList.add(devInfo);
		}
		adapter.setDeviceSet(devList);
		adapter.notifyDataSetChanged();
	}
	/**
	 * ����ָ���豸
	 * 
	 * @param device ��Ҫ����������豸
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