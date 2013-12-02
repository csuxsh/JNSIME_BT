package com.viaplay.ime;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.viaplay.ime.R;
import com.viaplay.ime.jni.InputAdapter;
import com.viaplay.ime.uiadapter.JnsIMEBTDeviceListAdapter;
import com.viaplay.ime.uiadapter.JnsIMEControlListAdapter;


import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 显示当前已经连接的操控器
 * 
 * @author Steven
 *
 */
public class JnsIMEControllerActivity  extends Activity{
	private static final String TAG = "BlueoceanControllerActivity";
	public static JnsIMEControlListAdapter adapter = new JnsIMEControlListAdapter();
	public static Handler handler;
	final BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
	//JnsIMEApplication app; 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_controller);
		final ListView list = (ListView) this.findViewById(R.id.listView1);
		adapter.setParent(this);
		adapter.setDeviceSet(JnsIMEApplication.mDeviceInfoList);
		list.setAdapter(adapter);
		//app = (JnsIMEApplication) this.getApplication();
		handler = new Handler()
		{
			@SuppressLint("HandlerLeak")
			public void handleMessage(Message msg)
			{
				adapter.notifyDataSetChanged();
			}
		};
		/*
		new Thread(new Runnable()
		{
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Message msg = new Message();
				msg.what = 0;
				handler.sendMessage(msg);
			}
			
		}).start(); */
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.e(TAG, " onkeydown keycode = " + keyCode + " scancode = " + event.getScanCode());
		return super.onKeyDown(keyCode, event);
	}
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		JnsIMECoreService.activitys.remove(this);
	}
	
}
