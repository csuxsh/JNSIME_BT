package com.viaplay.ime;


import com.viaplay.ime.R;
import com.viaplay.ime.bluetooth.JnsIMEBtServerThread;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import android.util.Log;

/**
 * 主界面TabActivity的Setting页
 * 
 * @author Steven
 *
 */

public class JnsIMESettingActivity extends PreferenceActivity implements OnPreferenceClickListener, OnPreferenceChangeListener{
	public static final String TAG = "BlueoceanControllerActivity";
	Preference quit;
	Preference changeime;
	Preference help;
	Preference search;
	static CheckBoxPreference cp;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		quit = this.findPreference(this.getString(R.string.quit));
		changeime = this.findPreference(this.getString(R.string.changeime));
		help = this.findPreference(this.getString(R.string.help));
		search = this.findPreference(this.getString(R.string.Search_Device));
			cp = (CheckBoxPreference) this.findPreference(this.getString(R.string.floatViewS));
		quit.setOnPreferenceClickListener(this);
		changeime.setOnPreferenceClickListener(this);
		help.setOnPreferenceClickListener(this);
		search.setOnPreferenceClickListener(this);
		cp.setOnPreferenceChangeListener(this);
		JnsIMECoreService.activitys.add(this);
		SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(this);
		if(pre.getBoolean("floatViewS", false))
		{
			cp.setChecked(true);
		}
		else
			cp.setChecked(false);
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.e(TAG, "  onkeydown keycode = " + keyCode + " scancode = " + event.getScanCode());
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onPreferenceClick(Preference arg0) {
		// TODO Auto-generated method stub
		if (arg0.getKey().equals(quit.getKey())) {
			this.finish();
		}
		if(arg0.getKey().equals(changeime.getKey()))
		{
			Intent intent = new Intent();
			intent.setAction("android.settings.SHOW_INPUT_METHOD_PICKER");
			this.sendBroadcast(intent);
		}
		if(arg0.getKey().equals(help.getKey()))
		{
			Intent intent = new Intent();
			intent.setClass(this, com.viaplay.ime.JnsIMEHelpActivity.class);
			this.startActivity(intent);
		}
		if(arg0.getKey().equals(search.getKey()))
		{
			JnsIMEApplication application = (JnsIMEApplication) this.getApplication();
			while(application.btService == null)
			{	
				Toast.makeText(this, "BTService starting....", Toast.LENGTH_SHORT).show();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Message msg = new Message();
			msg.what = JnsIMEBtService.SEARCH_DEVICE;
			application.btService.procehandler.sendMessage(msg);
			
			/*
			Intent intent = new Intent();
			intent.setClass(this, com.viaplay.ime.JnsIMEBtService.class);
			try{
			stopService(intent);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			startService(intent);
			*/
		}
		return false;
	}
		@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// TODO Auto-generated method stub
		if(preference == cp)
		{	
			if(!cp.isChecked())
			{
				//cp.setSummary("true");
				cp.setChecked(true);
				SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(this);
				Editor edit = pre.edit();
				edit.putBoolean("floatViewS", true);
				edit.commit();
				if(JnsIMEInputMethodService.floatingHandler != null)
				{
					Message msg = new Message();
					msg.what = 1;
					JnsIMEInputMethodService.floatingHandler.sendMessage(msg);
				}
			}
			else
			{
				cp.setChecked(false);
				SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(this);
				Editor edit = pre.edit();
				edit.putBoolean("floatViewS", false);
				edit.commit();
				if(JnsIMEInputMethodService.floatingHandler != null)
				{
					Message msg = new Message();
					msg.what = 2;
					JnsIMEInputMethodService.floatingHandler.sendMessage(msg);
				}
			}
		}
		return false;
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		JnsIMECoreService.activitys.remove(this);
	}
}
