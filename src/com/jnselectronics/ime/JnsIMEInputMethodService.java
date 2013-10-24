package com.jnselectronics.ime;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.jnselectronics.im.hardware.JoyStickTypeF;
import com.jnselectronics.ime.bean.JnsIMEProfile;
import com.jnselectronics.ime.jni.InputAdapter;
import com.jnselectronics.ime.util.JnsEnvInit;
import com.jnselectronics.ime.util.SendEvent;


import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.InputMethodService;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

public class JnsIMEInputMethodService extends InputMethodService {

	private final static String TAG = "JnsIMEMethod";
	private final static String KEY_MAP_FILE_TAG = ".keymap";
	private boolean isTakePic = false;
	/**
	 *  上次的应用名称
	 */
	static String lastAppName = "";
	/**
	 *  系统输入法是否为本输入法
	 */
	private boolean jnsIMEInUse = false;
	/**
	 *  截图进程,需要root
	 */
	private static Process process=null;
	private static DataOutputStream dos = null;
	public static JnsIMEInputMethodService ims=null;
	/**
	 * 当前有效的应用名(本应用以为的)
	 */
	public static String validAppName = "";
	/**
	 * 当前的应用名
	 */
	public static  String currentAppName = "";
	
	@SuppressLint("SdCardPath")
	@Override
	public void onCreate()
	{
		super.onCreate();
		jnsIMEInUse = true;
		ims = this;
		createDefautKeyFile();
		new Thread(new Runnable()
		{

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(true)
				{	  
					ActivityManager am = (ActivityManager) JnsIMEInputMethodService.this.getSystemService(ACTIVITY_SERVICE);
					String tmp = am.getRunningTasks(1).get(0).topActivity.getPackageName();
					currentAppName = tmp;
					if(!lastAppName.equals(tmp))
					{
						if(jnsIMEInUse)
							reLoadPofileFile(tmp);
					}
					if(!tmp.equals(JnsIMEInputMethodService.this.getPackageName()))
					{	
						JnsIMEInputMethodService.validAppName = tmp;
					}
					lastAppName = tmp;
					//		Log.d("IMTTEST", ""+am.getRunningTasks(1).get(0).topActivity.getPackageName());
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		}).start();
	}
	/**
	 * 创建默认的触摸映射,用于蓝牙模式.因为蓝牙模式数据需要全部转发,读event模式不需要转发数据
	 */
	private void createDefautKeyFile()
	{
		File selffile = new File(this.getFilesDir() + "/" + this.getPackageName()+KEY_MAP_FILE_TAG);
		if(!selffile.exists())
		{	
			FileOutputStream fos = null;
			try {
				fos = this.openFileOutput(this.getPackageName()+KEY_MAP_FILE_TAG  , Context.MODE_PRIVATE);
				writeDefaut(fos);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		File default1 = new File(this.getFilesDir() + "/default1"+KEY_MAP_FILE_TAG);
		if(!default1.exists())
		{
			FileOutputStream fos = null;
			try {
				fos = this.openFileOutput("default1"+KEY_MAP_FILE_TAG  , Context.MODE_PRIVATE);
				writeDefaut(fos);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private void writeDefaut(FileOutputStream fos)
	{
		try {
			fos.write((JoyStickTypeF.BUTTON_A+":GameA"+":"+JoyStickTypeF.BUTTON_A_SCANCODE+":"+KeyEvent.KEYCODE_BUTTON_A+"\n").getBytes());
			fos.write((JoyStickTypeF.BUTTON_B+":GameB"+":"+JoyStickTypeF.BUTTON_B_SCANCODE+":"+KeyEvent.KEYCODE_BUTTON_B+"\n").getBytes());
			fos.write((JoyStickTypeF.BUTTON_X+":GameX"+":"+JoyStickTypeF.BUTTON_X_SCANCODE+":"+KeyEvent.KEYCODE_BUTTON_X+"\n").getBytes());
			fos.write((JoyStickTypeF.BUTTON_Y+":GameY"+":"+JoyStickTypeF.BUTTON_Y_SCANCODE+":"+KeyEvent.KEYCODE_BUTTON_Y+"\n").getBytes());
			fos.write((JoyStickTypeF.BUTTON_L1+":L1"+":"+JoyStickTypeF.BUTTON_L1_SCANCODE+":"+KeyEvent.KEYCODE_BUTTON_L1+"\n").getBytes());
			fos.write((JoyStickTypeF.BUTTON_L2+":L2"+":"+JoyStickTypeF.BUTTON_L2_SCANCODE+":"+KeyEvent.KEYCODE_BUTTON_L2+"\n").getBytes());
			fos.write((JoyStickTypeF.BUTTON_R1+":R1"+":"+JoyStickTypeF.BUTTON_R1_SCANCODE+":"+KeyEvent.KEYCODE_BUTTON_R1+"\n").getBytes());
			fos.write((JoyStickTypeF.BUTTON_R2+":R2"+":"+JoyStickTypeF.BUTTON_R2_SCANCODE+":"+KeyEvent.KEYCODE_BUTTON_R2+"\n").getBytes());
			fos.write((JoyStickTypeF.BUTTON_DOWN+":Down"+":"+JoyStickTypeF.BUTTON_DOWN_SCANCODE+":"+KeyEvent.KEYCODE_DPAD_DOWN+"\n").getBytes());
			fos.write((JoyStickTypeF.BUTTON_UP+":Up"+":"+JoyStickTypeF.BUTTON_UP_SCANCODE+":"+KeyEvent.KEYCODE_DPAD_UP+"\n").getBytes());
			fos.write((JoyStickTypeF.BUTTON_LEFT+":Left"+":"+JoyStickTypeF.BUTTON_LEFT_SCANCODE+":"+KeyEvent.KEYCODE_DPAD_LEFT+"\n").getBytes());
			fos.write((JoyStickTypeF.BUTTON_RIGHT+":Right"+":"+JoyStickTypeF.BUTTON_RIGHT_SCANCODE+":"+KeyEvent.KEYCODE_DPAD_RIGHT+"\n").getBytes());
			fos.write((JoyStickTypeF.BUTTON_YI+":Down"+":"+JoyStickTypeF.BUTTON_YI_SCANCODE+":"+KeyEvent.KEYCODE_DPAD_DOWN+"\n").getBytes());
			fos.write((JoyStickTypeF.BUTTON_YP+":Up"+":"+JoyStickTypeF.BUTTON_YP_SCANCODE+":"+KeyEvent.KEYCODE_DPAD_UP+"\n").getBytes());
			fos.write((JoyStickTypeF.BUTTON_XI+":Left"+":"+JoyStickTypeF.BUTTON_XI_SCANCODE+":"+KeyEvent.KEYCODE_DPAD_LEFT+"\n").getBytes());
			fos.write((JoyStickTypeF.BUTTON_XP+":Right"+":"+JoyStickTypeF.BUTTON_XP_SCANCODE+":"+KeyEvent.KEYCODE_DPAD_RIGHT+"\n").getBytes());
			fos.write((JoyStickTypeF.BUTTON_SELECT+":Select"+":"+JoyStickTypeF.BUTTON_SELECT_SCANCODE+":"+KeyEvent.KEYCODE_BUTTON_SELECT+"\n").getBytes());
			fos.write((JoyStickTypeF.BUTTON_START+":Start"+":"+JoyStickTypeF.BUTTON_START_SCANCODE+":"+KeyEvent.KEYCODE_BUTTON_START+"\n").getBytes());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	@Override
	public void onDestroy () 
	{
		this.onCreateInputView();
		super.onDestroy();
		ims = null;
		jnsIMEInUse = false;
		JnsIMECoreService.keyList.clear();
		JnsIMECoreService.keyMap.clear();
	}
	/**
	 * 查询当前的事件是否来源于摇杆或者头盔
	 * 
	 * @param event 当前的keyevent
	 * @return 来源于摇杆或头盔则会将scancode改成对应的scancode
	 */
	KeyEvent mathJoyStick(KeyEvent event)
	{
		if(event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP && event.getScanCode() == 0)
		{
			if(InputAdapter.gHatUpPressed)
			{	
				if(event.getAction() == KeyEvent.ACTION_UP)
					InputAdapter.gHatUpPressed = false;
				return new KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
						event.getAction(), KeyEvent.KEYCODE_DPAD_UP, 0, 0, 0, 
						JoyStickTypeF.BUTTON_UP_SCANCODE, 0);
			}
			return new KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
					event.getAction(), KeyEvent.KEYCODE_DPAD_UP, 0, 0, 0, 
					JoyStickTypeF.BUTTON_YP_SCANCODE, 0);
		}
		if(event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN && event.getScanCode() == 0)
		{	
			if(InputAdapter.gHatDownPressed)
			{
				if(event.getAction() == KeyEvent.ACTION_UP)
					InputAdapter.gHatDownPressed = false;
				return new KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 
						event.getAction(), KeyEvent.KEYCODE_DPAD_DOWN, 0, 0, 0, 
						JoyStickTypeF.BUTTON_DOWN_SCANCODE, 0);

			}
			return new KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 
					event.getAction(), KeyEvent.KEYCODE_DPAD_DOWN, 0, 0, 0,
					JoyStickTypeF.BUTTON_YI_SCANCODE, 0);
		}
		if(event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT && event.getScanCode() == 0)
		{
			if(InputAdapter.gHatLeftPressed)
			{
				if(event.getAction() == KeyEvent.ACTION_UP)
					InputAdapter.gHatLeftPressed = false;
				return  new KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
						event.getAction(), KeyEvent.KEYCODE_DPAD_LEFT, 0, 0, 0, 
						JoyStickTypeF.BUTTON_LEFT_SCANCODE, 0);
			}
			return  new KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 
					event.getAction(), KeyEvent.KEYCODE_DPAD_LEFT, 0, 0, 0, 
					JoyStickTypeF.BUTTON_XI_SCANCODE, 0);
		}
		if(event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT && event.getScanCode() == 0)
		{
			if(InputAdapter.gHatRightPressed)
			{
				if(event.getAction() == KeyEvent.ACTION_UP)
					InputAdapter.gHatRightPressed = false;
					return new KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 
							event.getAction(), KeyEvent.KEYCODE_DPAD_RIGHT, 0, 0, 0, 
							JoyStickTypeF.BUTTON_RIGHT_SCANCODE, 0);
			}
			return new KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 
					event.getAction(), KeyEvent.KEYCODE_DPAD_RIGHT, 0, 0, 0, 
					JoyStickTypeF.BUTTON_XP_SCANCODE, 0);
		}
		return null;
	}
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(KeyEvent.KEYCODE_SEARCH == keyCode && (!JnsIMECoreService.touchConfiging) && JnsEnvInit.rooted)
		{
			if(currentAppName.equals(this.getPackageName()))
				return false;
			
			JnsIMECoreService.touchConfiging = true;
			Toast.makeText(this, this.getString(R.string.screen_shot), Toast.LENGTH_SHORT).show();
			
			new Thread(new Runnable()
			{
				@Override
				public void run() 
				{
					// TODO Auto-generated method stub
			//		isTakePic = com.jnselectronics.ime.jni.ScreenShot.getScreenShot();
					if(JnsEnvInit.rooted)
						Screencap();
					Log.d("JnsIME", "take pic "+isTakePic);
					Intent in = new Intent(JnsIMEInputMethodService.this.getApplicationContext(), JnsIMETpConfigActivity.class);
					//in.putExtra("screenshot", isTakePic);
					in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
					JnsIMEInputMethodService.this.startActivity(in);
				}
			}).start();
			return true;
		}
	
		/*
		KeyEvent tmpEvent = mathJoyStick(event);
		if(tmpEvent != null)
			event = tmpEvent;
		if(iteratorKeyList(JnsIMECoreService.keyList, event.getScanCode())!= null)
			return true;
		if(JnsIMECoreService.keyMap.containsKey(event.getScanCode()))
		{	
			if(!JnsEnvInit.rooted)
			{	
				event =  new KeyEvent(event.getDownTime(), event.getDownTime(), 
						KeyEvent.ACTION_DOWN, JnsIMECoreService.keyMap.get(event.getScanCode()),
						0, event.getMetaState(), event.getDeviceId(), 0);
				//event =  new KeyEvent(KeyEvent.ACTION_DOWN, JnsIMECoreService.keyMap.get(event.getScanCode()));
				this.getCurrentInputConnection().sendKeyEvent(event);
			}
			return true;
		}
		*/
		return false;
	}
	/**
	 *  截图方法。2.3以上通过执行screencap命令截图，2.2以下直接读fb截图
	 */
	void Screencap()
	{
		int sdkNum =  Build.VERSION.SDK_INT;
		try {
			if(sdkNum < 9)
				com.jnselectronics.ime.jni.ScreenShot.getScreenShot();
			else 
			{	//if(process == null && JnsEnvInit.rooted)
				//{
					process = Runtime.getRuntime().exec("su");
					dos =new DataOutputStream(process.getOutputStream());
				//}
				dos.writeBytes("screencap -p /mnt/sdcard/jnsinput/tmp.bmp\n");
				dos.flush();
				dos.writeBytes("exit\n");
				dos.flush();
				process.waitFor();
			}

		} catch (IOException e) {	
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		if(currentAppName.equals(this.getPackageName()))
			return false;
		@SuppressWarnings("unused")
		KeyEvent tmpEvent = mathJoyStick(event);
		if(keyCode == KeyEvent.KEYCODE_SEARCH)
			return true;
		/*
		if(tmpEvent != null)
		{
			if( iteratorKeyList(JnsIMECoreService.keyList, tmpEvent.getScanCode())!= null)
			{
				event = tmpEvent;
				return true;
			}
			if(JnsIMECoreService.keyMap.containsKey(tmpEvent.getScanCode()))
			{
				event = tmpEvent;
				return true;
			}
		}
		if( iteratorKeyList(JnsIMECoreService.keyList, event.getScanCode())!= null)
			return true;
		if(JnsIMECoreService.keyMap.containsKey(event.getScanCode()))
		{
			if(!JnsEnvInit.rooted)
			{	
				//event =  new KeyEvent(event.getDownTime(), event.getDownTime(), 
			///			KeyEvent.ACTION_UP, JnsIMECoreService.keyMap.get(event.getScanCode()),
			//			0, event.getMetaState(), event.getDeviceId(), 0);
				event =  new KeyEvent(event.getDownTime(), event.getDownTime(), 
						KeyEvent.ACTION_UP, JnsIMECoreService.keyMap.get(event.getScanCode()),
						0, event.getMetaState(), event.getDeviceId(), 0);
				this.getCurrentInputConnection().sendKeyEvent(event);
			}
			return true;
		}*/
		return false;
	}
	@SuppressLint("UseSparseArrays")
	/**
	 * 当应用进行切换时候跟着切换对应的配置文件
	 * 
	 * @param 应用包名
	 */
	private void reLoadPofileFile(String name)
	{
		Log.d(TAG, "reload file");
		//if(JnsIMEInputMethodService.currentAppName.equals(this.getPackageName()))
		//	return;
		if (JnsIMECoreService.keyList == null) JnsIMECoreService.keyList = new ArrayList<JnsIMEProfile>();
		if (JnsIMECoreService.keyMap == null) JnsIMECoreService.keyMap = new HashMap<Integer, Integer>();
		try {
			while(SendEvent.getSendEvent().getEventDownLock())
			{
				try {
					Log.d(TAG, "has motion not release");
					Thread.sleep(20);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d(TAG,"SOCKET EOOR");
			e.printStackTrace();
		}
		Log.d("SendEvent", "lock="+JnsIMECoreService.eventDownLock);
		Log.d("SendEvent", "reload file" + name);
		if (JnsIMECoreService.keyList.size() > 0)  JnsIMECoreService.keyList.clear();
		if (!JnsIMECoreService.keyMap.isEmpty())  JnsIMECoreService.keyMap.clear();
		Log.d(TAG, "reload file" + name);
		reloadTouchMap(name);
		reloadKeyMap(name + KEY_MAP_FILE_TAG);
	}
	private boolean reloadTouchMap(String name)
	{
		try 
		{
			FileReader fr = new FileReader(this.getFilesDir() + "/" + name);
			@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(fr);
			Log.d(TAG, "find file"+name);
			String val = br.readLine();
			while (null != val) {
				if (val.equals('\n')) val = br.readLine();
				JnsIMEProfile bp = new JnsIMEProfile();
				if (val != null && !val.equals("")) {
					bp.key = Integer.valueOf(val);	
				}
				val = br.readLine();
				if ( val != null && !val.equals("")) {
					bp.keyCode = Integer.valueOf(val);
				}
				val = br.readLine();
				if ( val != null && !val.equals("")) {
					bp.posX = Float.valueOf(val);
				}
				val = br.readLine();
				if (val != null && !val.equals("")) {
					bp.posY = Float.valueOf(val);
				}
				val = br.readLine();
				if (val != null && !val.equals("")) {
					bp.posR = Float.valueOf(val);
				}
				val = br.readLine();
				if (val != null && !val.equals("")) {
					bp.posType = Float.valueOf(val);
				}
				//					listConfig.add(bp);
				JnsIMECoreService.keyList.add(bp);
				val = br.readLine();
				//	BlueoceanCore.gameStart = true;
			}
			Log.d(TAG, "KeyList size ="+JnsIMECoreService.keyList.size());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			Log.d(TAG, "can not find file"+name);
			if(JnsIMECoreService.keyList==null)
				return false;
		}catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
	@SuppressWarnings("resource")
	@SuppressLint("UseSparseArrays")
	private boolean reloadKeyMap(String name)
	{
		try {
			FileReader fr = new FileReader(this.getFilesDir() + "/" + name);
			BufferedReader br = new BufferedReader(fr);
			Log.d(TAG, "find file"+name);

			String val = br.readLine();
			while (val != null)
			{
				String data[] = val.split(":");
				JnsIMECoreService.keyMap.put(Integer.parseInt(data[2]), Integer.parseInt(data[3]));
				val = br.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			FileReader defaultfr;
			try {
				defaultfr = new FileReader(this.getFilesDir() + "/" + "default"+(JnsIMECoreService.currentDeaultIndex+1)+KEY_MAP_FILE_TAG);
				BufferedReader br = new BufferedReader(defaultfr);
				Log.d(TAG, "find file"+name);

				String val = br.readLine();
				while (val != null)
				{
					String data[] = val.split(":");
					JnsIMECoreService.keyMap.put(Integer.parseInt(data[2]), Integer.parseInt(data[3]));
					val = br.readLine();
				}
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				//e1.printStackTrace();
				return false;
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}
		return true;
	}
	@SuppressWarnings("unused")
	private static JnsIMEProfile iteratorKeyList(List<JnsIMEProfile> keylist, int scancode)
	{
		if(keylist==null)
			return null;
		for(JnsIMEProfile keyProfile : keylist)
			if(keyProfile.key == scancode)
				return keyProfile;
		return null;
	}

}
