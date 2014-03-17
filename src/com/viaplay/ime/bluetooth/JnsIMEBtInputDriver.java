package com.viaplay.ime.bluetooth;

import com.viaplay.im.hardware.JoyStickTypeF;
import com.viaplay.ime.jni.InputAdapter;
import com.viaplay.ime.jni.JoyStickEvent;
import com.viaplay.ime.jni.RawEvent;

/***
 * 蓝牙输入驱动
 * <p>将蓝牙发过来的数据转换成各类事件，并分发
 * 
 * @author Steven
 *
 */
public class JnsIMEBtInputDriver {

	//(public RawEvent(int keyCode, int scanCode, int value, int deviceId))
	final static int HAT_X[] = {

		0, 1, 1, 1, 0, 0xff, 0xff, 0xff, 0, 0, 0, 0, 0, 0, 0, 0
	};
	final static int HAT_Y[] = {

		0xff, 0xff, 0, 1, 1, 1, 0, 0xff, 0, 0, 0, 0, 0, 0, 0, 0
	};
	


	final static int[] GAME_ABXY_SCANCODE = 
	{
		0,
		0,
		0,
		0,
		JoyStickTypeF.BUTTON_A_SCANCODE,
		JoyStickTypeF.BUTTON_B_SCANCODE,
		JoyStickTypeF.BUTTON_X_SCANCODE,
		JoyStickTypeF.BUTTON_Y_SCANCODE,
	};
	final static int[] GAME_BUTTON1_SCANCODE =
		{
		JoyStickTypeF.BUTTON_L1_SCANCODE,
		JoyStickTypeF.BUTTON_R1_SCANCODE,
		JoyStickTypeF.BUTTON_HOME_SCANCODE,
		JoyStickTypeF.BUTTON_BACK_SCANCODE,
		JoyStickTypeF.BUTTON_MENU_SCANCODE,
		JoyStickTypeF.BUTTON_L2_SCANCODE,
		JoyStickTypeF.BUTTON_R2_SCANCODE,
		JoyStickTypeF.BUTTON_SELECT_SCANCODE,
		};

	final static int[] GAME_BUTTON2_SCANCODE =
		{
		JoyStickTypeF.BUTTON_START_SCANCODE,
		0,
		0,
		0,
		0,
		0,
		0,
		0
		};
	
	
	
	private static void joyDataAnalyse(JnsIMEBtDataStruct data)
	{
		JoyStickEvent joyevent = new JoyStickEvent();
		joyevent.setX(data.data[JnsIMEBtDataProcess.ABS_X_INDEX]);
		joyevent.setY(data.data[JnsIMEBtDataProcess.ABS_Y_INDEX]);
		joyevent.setZ(data.data[JnsIMEBtDataProcess.ABS_Z_INDEX]);
		joyevent.setRz(data.data[JnsIMEBtDataProcess.ABS_RZ_INDEX]);
		joyevent.setGas(data.data[JnsIMEBtDataProcess.GAS_INDEX]);
		joyevent.setBrake(data.data[JnsIMEBtDataProcess.BRAKE_INDEX]);
		joyevent.setHat_x(HAT_X[data.data[JnsIMEBtDataProcess.HAT_INDEX] & 0xf] );
		joyevent.setHat_y(HAT_Y[data.data[JnsIMEBtDataProcess.HAT_INDEX] & 0xf] );
		dispathJoyEvent(joyevent);
	}
	private static void gameButtonABXYAnalyse(JnsIMEBtDataStruct data) {
		byte diff = (byte) ((data.data[JnsIMEBtDataProcess.HAT_INDEX] & 0xf0) ^ 
				(0xf0 & JnsIMEBtDataProcess.gamePadData[JnsIMEBtDataProcess.HAT_INDEX]));
		for(int i = 0; i < GAME_ABXY_SCANCODE.length;  i ++)
		{
			if((diff & (0x01 << i)) != 0)
			{
				RawEvent keyevent = new RawEvent(0, GAME_ABXY_SCANCODE[i], 
						(data.data[JnsIMEBtDataProcess.HAT_INDEX] & (0x01 << i)) >> i, 
						0);
				dispathKeyEvent(keyevent);
			}
		}
	}
	private static void gameButton1Analyse(JnsIMEBtDataStruct data)
	{
		byte diff = (byte) (data.data[JnsIMEBtDataProcess.BUTTON1_INDEX] ^ 
				JnsIMEBtDataProcess.gamePadData[JnsIMEBtDataProcess.BUTTON1_INDEX]);
		for(int i = 0; i < GAME_BUTTON1_SCANCODE.length;  i ++)
		{
			if((diff & (0x01 << i)) != 0)
			{
				RawEvent keyevent = new RawEvent(0, GAME_BUTTON1_SCANCODE[i], 
						(data.data[JnsIMEBtDataProcess.BUTTON1_INDEX] & (0x01 << i)) >> i, 
						0);
				dispathKeyEvent(keyevent);
			}
		}
	}
	private static void gameButton2Analyse(JnsIMEBtDataStruct data)
	{
		byte diff = (byte) (data.data[JnsIMEBtDataProcess.BUTTON2_INDEX] ^ 
				JnsIMEBtDataProcess.gamePadData[JnsIMEBtDataProcess.BUTTON2_INDEX]);
		for(int i = 0; i < GAME_BUTTON2_SCANCODE.length;  i ++)
		{
			if((diff & (0x01 << i)) != 0)
			{
				RawEvent keyevent = new RawEvent(0, GAME_BUTTON2_SCANCODE[i], 
						(data.data[JnsIMEBtDataProcess.BUTTON2_INDEX] & (0x01 << i)) >> i, 
						0);
				dispathKeyEvent(keyevent);
			}
		}
	}
	static void setData(JnsIMEBtDataStruct data)
	{

		if(data.getDataTag() == JnsIMEBtDataProcess.GAME_DATA_TAG)
		{
			joyDataAnalyse(data);
		}
		if(data.getDataTag() == JnsIMEBtDataProcess.GAME_ABXY_DATA_TAG)
		{
			gameButtonABXYAnalyse(data);
		}
		if(data.getDataTag() == JnsIMEBtDataProcess.GAME_BUTTON1_DATA_TAG)
		{
			gameButton1Analyse(data);
		}
		if(data.getDataTag() == JnsIMEBtDataProcess.GAME_BUTTON2_DATA_TAG)
		{
			gameButton2Analyse(data);
		}
	}

	static void dispathKeyEvent(RawEvent keyevent)
	{
		//SendEvent.getSendEvent().sendKey(keyevent);
		synchronized(InputAdapter.keyQueue)
		{
			if(InputAdapter.keyQueue.add(keyevent))
			{
				InputAdapter.hasKeyFlag++;
			}
			InputAdapter.keyQueue.notify();
		}
	}
	static void dispathJoyEvent(JoyStickEvent joyevent)
	{
		//SendEvent.getSendEvent().sendJoy(joyevent);
		synchronized(InputAdapter.joyQueue)
		{
			if(InputAdapter.joyQueue.add(joyevent))
			{
				InputAdapter.hasJoyFlag++;
			}
			InputAdapter.joyQueue.notify();
		}
	}

}
