package com.viaplay.ime.jni;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.viaplay.im.hardware.JoyStickTypeF;
import com.viaplay.ime.JnsIMECoreService;
import com.viaplay.ime.JnsIMEInputMethodService;
import com.viaplay.ime.util.SendEvent;

import android.content.Context;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;


/**
 * 用于应用从event读取数据时候封装分发的类，在蓝牙模式下没有使用
 * 
 * @author Steven
 *
 */
public class InputAdapter {

	private static final int SELECT_SCANCODE = 314;
	private static final int START_SCANCODE = 315;
	/**
	 *  用于检查select和start键的按下情况
	 */
	private static byte mCheckByte = 0x00;
	/**
	 * 标记当前是否处于JNSIME模式
	 */
	private static boolean  mIMEMode = false;
	public static boolean uInputMode = false;
	public static Context mcontext;
	private static final String TAG = "JNS_InputAdapter";
	private static RawEvent keyEvent = new RawEvent();
	private static RawEvent oldKeyEvent = new RawEvent();
	private static JoyStickEvent JoyEvent = new JoyStickEvent();
	private static JoyStickEvent pJoyEvent = new JoyStickEvent();
	/**
	 *  头盔键是否被按下 仅够本类使用来左判断
	 */
	private static boolean hatUpPressed = false;
	private static boolean hatDownPressed = false;
	private static boolean hatLeftPressed = false;
	private static boolean hatRightPressed = false;
	private static boolean gasPressed = false;
	private static boolean brakePressed = false;

	/**
	 *  头盔键是否被按下，用于输入法服务来区分 当前发出的scancode为0的方向键是由摇杆还是头盔发出。
	 */
	public static boolean gHatUpPressed = false;
	public static boolean gHatDownPressed = false;
	public static boolean gHatLeftPressed = false;
	public static boolean gHatRightPressed = false;

	private static boolean leftStickPressed = false;
	private static boolean rightStickPressed = false;

	public static int  hasJoyFlag = 0;
	public static int  hasKeyFlag = 0;
	public static Queue<JoyStickEvent> joyQueue = new ConcurrentLinkedQueue<JoyStickEvent>();
	public static Queue<RawEvent> keyQueue = new ConcurrentLinkedQueue<RawEvent>();
	//private static JoyStickEvent oldJoyEvent = new JoyStickEvent();





	private static Runnable getKeyRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (true) {
				getKey();
				//Log.d(TAG, "keyEvent.scanCode="+keyEvent.scanCode+"keyEvent.value"+keyEvent.value );
				if(JnsIMEInputMethodService.validAppName.equals("com.silvertree.cordy"))
					keyEvent.deviceId = 0;
				if(uInputMode && !JnsIMECoreService.keyMap.containsKey(keyEvent.scanCode) && 
						SendEvent.iteratorKeyList(JnsIMECoreService.keyList, keyEvent.scanCode) == null)
				{
					SendEvent.sendKeyEvent(new RawEvent(0, keyEvent.scanCode, keyEvent.value, JoyEvent.deviceId,  1));	
					setAxis(new RawEvent(0, 0, 0,JoyEvent.deviceId, 0));
				}
				if (keyEvent.value == 1) 
				{
					keyEvent.value = KeyEvent.ACTION_DOWN;
					CheckIMESwitch();
					onRawKeyDown(keyEvent);
				} 
				else if(keyEvent.value == 2)
				{
					//	onRawKeyLongDown(keyEvent);
				}
				else if(keyEvent.value == 0)
				{
					if(keyEvent.scanCode == START_SCANCODE) 
						mCheckByte  = (byte) (mCheckByte & 0xfe);
					if(keyEvent.scanCode == SELECT_SCANCODE) 
						mCheckByte =  (byte) (mCheckByte & 0xfd);
					//Log.d(TAG, "get a key up");
					keyEvent.value = KeyEvent.ACTION_UP;
					onRawKeyUp(keyEvent);
				}

			}
		}

	};
	/*
	 *   添加 R2 L2 于 GAS BRAKE的支持
	 */
	private static Runnable getJoyStickRunnable = new Runnable() {

		@Override
		public void run() 
		{
			//Log.d(TAG, "x = "+JoyEvent.x+ ", y = "+JoyEvent.y + "z = "+JoyEvent.z+  "rz = "+JoyEvent.rz);

			// TODO Auto-generated method stub
			while (true) 
			{
				if(getJoyStick())
				{
					if(uInputMode && 
							!JnsIMECoreService.keyMap.containsKey(JoyStickTypeF.BUTTON_XP_SCANCODE) &&
							!JnsIMECoreService.keyMap.containsKey(JoyStickTypeF.BUTTON_XI_SCANCODE) &&
							!JnsIMECoreService.keyMap.containsKey(JoyStickTypeF.BUTTON_YI_SCANCODE) &&
							!JnsIMECoreService.keyMap.containsKey(JoyStickTypeF.BUTTON_YP_SCANCODE) &&
							SendEvent.iteratorKeyList(JnsIMECoreService.keyList, JoyStickTypeF.STICK_L) == null
							)
					{	
						if(pJoyEvent.x != JoyEvent.x)
							setAxis(new RawEvent(0, 0, JoyEvent.x,JoyEvent.deviceId, 3));
						if(pJoyEvent.y != JoyEvent.y)
							setAxis(new RawEvent(0, 1, JoyEvent.y,JoyEvent.deviceId, 3));
					}
					if(uInputMode && 
							!JnsIMECoreService.keyMap.containsKey(JoyStickTypeF.BUTTON_ZP_SCANCODE) &&
							!JnsIMECoreService.keyMap.containsKey(JoyStickTypeF.BUTTON_ZI_SCANCODE) &&
							!JnsIMECoreService.keyMap.containsKey(JoyStickTypeF.BUTTON_RZI_SCANCODE) &&
							!JnsIMECoreService.keyMap.containsKey(JoyStickTypeF.BUTTON_RZP_SCANCODE) &&
							SendEvent.iteratorKeyList(JnsIMECoreService.keyList, JoyStickTypeF.STICK_R) == null
							)
					{	
						if(pJoyEvent.z != JoyEvent.z)
							setAxis(new RawEvent(0, 2, JoyEvent.z,JoyEvent.deviceId, 3));
						if(pJoyEvent.rz != JoyEvent.rz)
							setAxis(new RawEvent(0, 5, JoyEvent.rz,JoyEvent.deviceId, 3));
					}
					if(uInputMode && 
							!JnsIMECoreService.keyMap.containsKey(JoyStickTypeF.BUTTON_LEFT_SCANCODE) &&
							!JnsIMECoreService.keyMap.containsKey(JoyStickTypeF.BUTTON_RIGHT_SCANCODE) &&
							!JnsIMECoreService.keyMap.containsKey(JoyStickTypeF.BUTTON_UP_SCANCODE) &&
							!JnsIMECoreService.keyMap.containsKey(JoyStickTypeF.BUTTON_DOWN_SCANCODE) &&
							SendEvent.iteratorKeyList(JnsIMECoreService.keyList, JoyStickTypeF.BUTTON_LEFT_SCANCODE) == null &&
							SendEvent.iteratorKeyList(JnsIMECoreService.keyList, JoyStickTypeF.BUTTON_RIGHT_SCANCODE) == null &&
							SendEvent.iteratorKeyList(JnsIMECoreService.keyList, JoyStickTypeF.BUTTON_UP_SCANCODE) == null &&
							SendEvent.iteratorKeyList(JnsIMECoreService.keyList, JoyStickTypeF.BUTTON_DOWN_SCANCODE) == null 
						)
					{	
						if(pJoyEvent.hat_x != JoyEvent.hat_x)
							setAxis(new RawEvent(0, 0x10, JoyEvent.hat_x,JoyEvent.deviceId, 3));
						if(pJoyEvent.hat_y != JoyEvent.hat_y)
							setAxis(new RawEvent(0, 0x11, JoyEvent.hat_y,JoyEvent.deviceId, 3));
					}
					if(pJoyEvent.brake != JoyEvent.brake)
						setAxis(new RawEvent(0, 0x0a, JoyEvent.brake,JoyEvent.deviceId, 3));
					if(pJoyEvent.gas != JoyEvent.gas)
						setAxis(new RawEvent(0, 0x09, JoyEvent.gas,JoyEvent.deviceId, 3));
					setAxis(new RawEvent(0, 0, 0,JoyEvent.deviceId, 0));
					pJoyEvent.x = JoyEvent.x;
					pJoyEvent.y = JoyEvent.y;
					pJoyEvent.z = JoyEvent.z;
					pJoyEvent.rz = JoyEvent.rz;
					pJoyEvent.brake = JoyEvent.brake;
					pJoyEvent.gas = JoyEvent.gas;
					pJoyEvent.hat_x = JoyEvent.hat_x;
					pJoyEvent.hat_y = JoyEvent.hat_y;
					
					
					if(JnsIMEInputMethodService.validAppName.equals("com.silvertree.cordy"))
						JoyEvent.deviceId = 0;
					if((JoyEvent.hat_y == 0) && hatUpPressed)
					{
						RawEvent keyevent = new RawEvent(0, JoyStickTypeF.BUTTON_UP_SCANCODE, KeyEvent.ACTION_UP, JoyEvent.deviceId);
						hatUpPressed = false;
						onRawKeyUp(keyevent);
					}
					if((JoyEvent.hat_y == 0) && hatDownPressed)
					{
						RawEvent keyevent = new RawEvent(0, JoyStickTypeF.BUTTON_DOWN_SCANCODE, KeyEvent.ACTION_UP, JoyEvent.deviceId);
						hatDownPressed = false;
						onRawKeyUp(keyevent);
					}
					if((JoyEvent.hat_y == -1) && (!hatUpPressed))
					{
						RawEvent keyevent = new RawEvent(0, JoyStickTypeF.BUTTON_UP_SCANCODE, KeyEvent.ACTION_DOWN, JoyEvent.deviceId);
						hatUpPressed = true;
						gHatUpPressed = true;
						onRawKeyDown(keyevent);
					}
					if((JoyEvent.hat_y == 1) && (!hatDownPressed))
					{
						RawEvent keyevent = new RawEvent(0, JoyStickTypeF.BUTTON_DOWN_SCANCODE, KeyEvent.ACTION_DOWN, JoyEvent.deviceId);
						hatDownPressed =true;
						gHatDownPressed =true;
						onRawKeyDown(keyevent);
					}
					
					if((JoyEvent.hat_x == 0) && hatRightPressed)
					{
						RawEvent keyevent = new RawEvent(0, JoyStickTypeF.BUTTON_RIGHT_SCANCODE, KeyEvent.ACTION_UP, JoyEvent.deviceId);
						hatRightPressed = false;
						onRawKeyUp(keyevent);
					}
					if((JoyEvent.hat_x == 0) && hatLeftPressed)
					{
						RawEvent keyevent = new RawEvent(0, JoyStickTypeF.BUTTON_LEFT_SCANCODE, KeyEvent.ACTION_UP, JoyEvent.deviceId);
						hatLeftPressed = false;
						onRawKeyUp(keyevent);
					}
					if((JoyEvent.hat_x == 1) && (!hatRightPressed))
					{
						RawEvent keyevent = new RawEvent(0, JoyStickTypeF.BUTTON_RIGHT_SCANCODE, KeyEvent.ACTION_DOWN, JoyEvent.deviceId);
						hatRightPressed = true;
						gHatRightPressed = true;
						onRawKeyDown(keyevent);
					}
					if((JoyEvent.hat_x == -1) && (!hatLeftPressed))
					{
						RawEvent keyevent = new RawEvent(0, JoyStickTypeF.BUTTON_LEFT_SCANCODE, KeyEvent.ACTION_DOWN, JoyEvent.deviceId);
						hatLeftPressed =true;
						gHatLeftPressed =true;
						onRawKeyDown(keyevent);
					}
					if(JoyEvent.gas == 0 && gasPressed)
					{
						RawEvent keyevent = new RawEvent(0, JoyStickTypeF.BUTTON_GAS_SCANCODE, KeyEvent.ACTION_UP, JoyEvent.deviceId);
						gasPressed = false;
						//gHatLeftPressed =true;
						if(JnsIMECoreService.touchConfiging)
							SendEvent.sendKeyEvent(keyevent);
						onRawKeyUp(keyevent);
					}
					if(JoyEvent.gas != 0  && !gasPressed)
					{
						RawEvent keyevent = new RawEvent(0, JoyStickTypeF.BUTTON_GAS_SCANCODE, KeyEvent.ACTION_DOWN, JoyEvent.deviceId);
						gasPressed = true;
						if(JnsIMECoreService.touchConfiging)
							SendEvent.sendKeyEvent(keyevent);
						//gHatLeftPressed =true;
						onRawKeyDown(keyevent);
					}
					if(JoyEvent.brake == 0 && brakePressed)
					{
						RawEvent keyevent = new RawEvent(0, JoyStickTypeF.BUTTON_BRAKE_SCANCODE, KeyEvent.ACTION_UP, JoyEvent.deviceId);
						brakePressed = false;
						if(JnsIMECoreService.touchConfiging)
							SendEvent.sendKeyEvent(keyevent);
						//gHatLeftPressed =true;
						onRawKeyUp(keyevent);
					}
					if(JoyEvent.brake != 0  && !brakePressed)
					{
						RawEvent keyevent = new RawEvent(0, JoyStickTypeF.BUTTON_BRAKE_SCANCODE, KeyEvent.ACTION_DOWN, JoyEvent.deviceId);
						brakePressed = true;
						if(JnsIMECoreService.touchConfiging)
							SendEvent.sendKeyEvent(keyevent);
						//gHatLeftPressed =true;
						onRawKeyDown(keyevent);
					}
					if(JnsIMECoreService.touchConfiging && JnsIMECoreService.ime != null)
					{
						if(((JoyEvent.x != 127) || (JoyEvent.y != 127)) && !leftStickPressed)	
						{	
							SendEvent.sendKeyEvent(new RawEvent(KeyEvent.KEYCODE_L,KeyEvent.ACTION_DOWN,0,0 ));
							leftStickPressed = true;
						}
						if(JoyEvent.x == 127 && JoyEvent.y == 127 && leftStickPressed)
						{	
							SendEvent.sendKeyEvent(new RawEvent(KeyEvent.KEYCODE_L,KeyEvent.ACTION_DOWN,0,0 ));
							leftStickPressed = false;
						}
						if(((JoyEvent.z != 127) || (JoyEvent.rz != 127)) && !rightStickPressed)
						{	
							SendEvent.sendKeyEvent(new RawEvent(KeyEvent.KEYCODE_R,KeyEvent.ACTION_DOWN,0,0 ));
							rightStickPressed = true;
						}
						if(((JoyEvent.z != 127) || (JoyEvent.rz != 127)) && rightStickPressed)
						{	
							SendEvent.sendKeyEvent(new RawEvent(KeyEvent.KEYCODE_R,KeyEvent.ACTION_DOWN,0,0 ));
							rightStickPressed = false;
						}
					}
					
						
					//Log.d(TAG, "x = "+JoyEvent.x+ ", y = "+JoyEvent.y + "z = "+JoyEvent.z+  "rz = "+JoyEvent.rz+"  hat_x = "+ JoyEvent.hat_x +" hat y ="+  JoyEvent.hat_y + 
					//		"gas = " + JoyEvent.gas + "brake = "+JoyEvent.brake);
					Message msg = new Message();
					msg.what = JnsIMECoreService.HAS_STICK_DATA;
					JnsIMECoreService.stickQueue.add(JoyEvent);
					JnsIMECoreService.DataProcessHandler.sendMessage(msg);
					
				}
			}

		}
	};
	/*
	 *  修改select + start 为弹出配置界面,以及确认摇杆。
	 */
	private static void CheckIMESwitch()
	{
		if(keyEvent.scanCode == START_SCANCODE) 
			mCheckByte  = (byte) (mCheckByte | 0x01);
		if(keyEvent.scanCode == SELECT_SCANCODE) 
			mCheckByte =  (byte) (mCheckByte | 0x02);
		//Log.d(TAG, "mCheckByte="+mCheckByte+",mIMEMode="+mIMEMode);
		if(mCheckByte == 0x03)
		{
			//	Toast.makeText(mcontext, "qiehuan ime", Toast.LENGTH_LONG).show();
			//String imeStr = "";
			/*
			if (!mIMEMode) 
			{
				//imeStr = BlueoceanCore.JNSIMEID;
				mIMEMode = true;
			} 
			else 
			{
				//imeStr = BlueoceanCore.lastIMEID;
				mIMEMode = false;
			}
			//	Intent intent = new Intent();
			//	intent.setAction("COM.BLUEOCEAN_IME_SWITCH_IME");
			//	intent.putExtra("COM.BLUEOCEAN_IME_IMEID", imeStr);
			//	mcontext.sendBroadcast(intent);
			 */
			/*
			mCheckByte = 0x00;
			Intent intent = new Intent();
			intent.setAction("android.settings.SHOW_INPUT_METHOD_PICKER");
			mcontext.sendBroadcast(intent);
			 */
			if(JnsIMEInputMethodService.jnsIMEInUse)
			{	
				if(JnsIMECoreService.ime != null)
				{	
					if(!JnsIMECoreService.ime.currentAppName.equals(JnsIMECoreService.ime.getPackageName()))
					{
						Message msg = new Message();
						msg.what = JnsIMECoreService.START_TPCFG;
						JnsIMECoreService.DataProcessHandler.sendMessage(msg);
						JnsIMECoreService.ime.startTpConfig();
						mCheckByte = 0x00;
					}
				}
			}
		}
	}
	private static synchronized void onRawKeyDown(RawEvent keyEvent) {
		//Log.e(TAG, "onRawKeyDown scanCode = " + keyEvent.scanCode + " value = " + keyEvent.value);
		Message msg = new Message();
		msg.what = JnsIMECoreService.HAS_KEY_DATA;
		RawEvent event = new RawEvent(keyEvent.keyCode, keyEvent.scanCode, keyEvent.value, keyEvent.deviceId);
		JnsIMECoreService.keyQueue.add(event);
		JnsIMECoreService.DataProcessHandler.sendMessage(msg);
		//Log.d(TAG, "current time is "+System.currentTimeMillis());
	}

	private static synchronized void onRawKeyUp(RawEvent keyEvent) {
		//Log.e(TAG, "onRawKeyUp scanCode = " + keyEvent.scanCode + " value = " + keyEvent.value);
		Message msg = new Message();
		msg.what = JnsIMECoreService.HAS_KEY_DATA;
		RawEvent event = new RawEvent(keyEvent.keyCode, keyEvent.scanCode, keyEvent.value, keyEvent.deviceId);
		JnsIMECoreService.keyQueue.add(event);
		JnsIMECoreService.DataProcessHandler.sendMessage(msg);
	}

	public static void getKeyThreadStart() {
		new Thread(getKeyRunnable).start();
		new Thread(getJoyStickRunnable).start();
	}

	public static native boolean init(int w, int h);
	public static native boolean start();
	public static native boolean stop();
	public static native boolean setAxis(RawEvent e);
	public static native boolean setButton(RawEvent e);
	
	public static native void getKey(RawEvent event);
	private static boolean getKey()
	{
		synchronized(keyQueue)
		{
			if(hasKeyFlag < 1)
			try
			{
				keyQueue.wait();
			} 
			catch (InterruptedException e) 
			{
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
			}
			keyEvent = keyQueue.poll();
			if(keyQueue == null)
				return false;
			hasKeyFlag--;
		}	
		return true;
	}
	public static native boolean getJoyStick(JoyStickEvent event);
	private static boolean getJoyStick()
	{
		synchronized(joyQueue)
		{
			if(hasJoyFlag < 1)
			try
			{
				joyQueue.wait();
			} 
			catch (InterruptedException e) 
			{
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
			}
			JoyEvent = joyQueue.poll();
			if(JoyEvent == null)
				return false;
			hasJoyFlag--;
		}	
		return true;
	}
	public static native List<String> getDeviceList();

	static {
		System.loadLibrary("jni_input_adapter");
	}
}
