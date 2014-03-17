package com.viaplay.im.hardware;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.view.KeyEvent;



@SuppressLint("UseSparseArrays")
public class JoyStickTypeF {
	public static final int BUTTON_SEARCH_SCANCODE = 217;
	public static final int BUTTON_A_SCANCODE = 304;
	public static final int BUTTON_B_SCANCODE = 305;
	public static final int BUTTON_C_SCANCODE = 306;
	public static final int BUTTON_X_SCANCODE = 307;
	public static final int BUTTON_Y_SCANCODE = 308;
	public static final int BUTTON_Z_SCANCODE = 309;
	public static final int BUTTON_UP_SCANCODE = 103;
	public static final int BUTTON_DOWN_SCANCODE = 108;
	public static final int BUTTON_RIGHT_SCANCODE = 106;
	public static final int BUTTON_LEFT_SCANCODE = 105;
	public static final int BUTTON_SELECT_SCANCODE = 314;
	public static final int BUTTON_START_SCANCODE = 315;
	public static final int BUTTON_L1_SCANCODE = 310;
	public static final int BUTTON_L2_SCANCODE = 312;
	public static final int BUTTON_R1_SCANCODE = 311;
	public static final int BUTTON_R2_SCANCODE = 313;
	public static final int STICK_L = 15;
	public static final int STICK_R = 16;
	public static final int BUTTON_XP_SCANCODE = 0x7f01;
	public static final int BUTTON_XI_SCANCODE = 0x7f02;
	public static final int BUTTON_YP_SCANCODE = 0x7f03;
	public static final int BUTTON_YI_SCANCODE = 0x7f04;
	public static final int BUTTON_ZP_SCANCODE = 0x7f05;
	public static final int BUTTON_ZI_SCANCODE = 0x7f06;
	public static final int BUTTON_RZP_SCANCODE = 0x7f07;
	public static final int BUTTON_RZI_SCANCODE = 0x7f08;
	public static final int BUTTON_GAS_SCANCODE = 0x7f09;
	public static final int BUTTON_BRAKE_SCANCODE = 0x7f0a;
	public static final int BUTTON_HOME_SCANCODE = 172;
	public static final int BUTTON_BACK_SCANCODE = 158;
	public static final int BUTTON_MENU_SCANCODE = 127;
	public static final int RIGHT_JOYSTICK_TAG = 1;
	public static final int LEFT_JOYSTICK_TAG = 2;
	public static final int JOYSTICK_ZOOM_1_TAG =0x3;
	public static final int JOYSTICK_ZOOM_2_TAG =0x4;
	
	
	public final static int BUTTON_L1=18;//3;
	public final static int BUTTON_SELECT=7;
	public final static int BUTTON_START=8;
	public final static int BUTTON_R1=29;//12;
	public final static int BUTTON_L2=3;//18;
	public final static int BUTTON_R2=12;//29;
	public final static int BUTTON_UP=33;
	public final static int BUTTON_Y=46;
	public final static int BUTTON_LEFT=48;
	public final static int BUTTON_RIGHT=50;
	public final static int BUTTON_X=61;
	public final static int BUTTON_B=63;
	public final static int BUTTON_DOWN=65;
	public final static int BUTTON_A=78;
	public final static int BUTTON_YP=81;
	public final static int BUTTON_RZP=94;
	public final static int BUTTON_XI=96;
	public final static int BUTTON_XP=98;
	public final static int BUTTON_ZI=109;
	public final static int BUTTON_ZP=111;
	public final static int BUTTON_YI=113;
	public final static int BUTTON_RZI=126;
	
	/*
	public final static int DISPLAY_COL = 8;
	{
		"",
		"L1",
		"Select",
		"Start",
		"R1",
		"L2",
		"R2",
		"Up",
		"Y",
		"Left",
		"Right",
		"X",
		"B",
		"Down",
		"A",
		"Y+",
		"RZ+",
		"X-",
		"X+",
		"Z-",
		"Z+",
		"Y-",
		"RZ+"	
	};
	*/
	
	public final static int DISPLAY_ROW = 16;
	public final static int DISPLAY_COL = 8;  
	public static Map<Integer,Integer> typeFKeyMap = new HashMap<Integer,Integer>();
	
	
	public final static int gamePadButoonScanCode[][] = 
	{
		{0,  0,  0,  BUTTON_L2_SCANCODE,  0,  0,  0,  BUTTON_SELECT_SCANCODE,  BUTTON_START_SCANCODE,  0,  0,  0, BUTTON_R2_SCANCODE, 0,  0,  0},
		{0,  0,  BUTTON_L1_SCANCODE,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, BUTTON_R1_SCANCODE, 0,  0},
		{0,  BUTTON_UP_SCANCODE,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, BUTTON_Y_SCANCODE, 0},
		{BUTTON_LEFT_SCANCODE,  0,	BUTTON_RIGHT_SCANCODE,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  BUTTON_X_SCANCODE, 0,BUTTON_B_SCANCODE},
		{0,  	BUTTON_DOWN_SCANCODE, 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, BUTTON_A_SCANCODE,  0},
		{0,  BUTTON_YP_SCANCODE, 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, BUTTON_RZP_SCANCODE,  0},
		{BUTTON_XI_SCANCODE,  0 ,BUTTON_XP_SCANCODE,0,  0,  0,  0,  0,  0,  0,  0,  0,   0,  BUTTON_ZI_SCANCODE, 0, BUTTON_ZP_SCANCODE},
		{0,  BUTTON_YI_SCANCODE,0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  BUTTON_RZI_SCANCODE,0},
	};
	public final static int gamePadButoonIndex[][] = 
	{
		{0,  0,  0,  BUTTON_L2,  0,  0,  0,  BUTTON_SELECT,  BUTTON_START,  0,  0,  0, BUTTON_R2, 0,  0,  0},
		{0,  0,  BUTTON_L1,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, BUTTON_R1, 0,  0},
		{0,  BUTTON_UP,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, BUTTON_Y, 0},
		{BUTTON_LEFT,  0,	BUTTON_RIGHT,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  BUTTON_X, 0,BUTTON_B},
		{0,  	BUTTON_DOWN, 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, BUTTON_A,  0},
		{0,  BUTTON_YP, 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, BUTTON_RZP,  0},
		{BUTTON_XI,  0 ,BUTTON_XP,0,  0,  0,  0,  0,  0,  0,  0,  0,   0,  BUTTON_ZI, 0, BUTTON_ZP},
		{0,  BUTTON_YI,0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  BUTTON_RZI,0},
	};
	/*
	public final static String gamePadButoonLable[][] = 
	{
		{null,  null,  null,  "L1",  null,  null,  null,  "Select",  "Start",  null,  null,  null,  "R1", null,  null,  null},
		{null,  null,  "L2",  null,  null,  null,  null,  null,  null,  null,  null,  null,  null,  "R2", null,  null},
		{null,  "Up",  null,  null,  null,  null,  null,  null,  null,  null,  null,  null,  null,  null,  "Y", null},
		{"Left",  null,	"Right",  null,  null,  null,  null,  null,  null,  null,  null,  null,  null,  "X", null, "B"},
		{null,  	"Down", null,  null,  null,  null,  null,  null,  null,  null,  null,  null,  null,  null, "A",  null},
		{null,  "Y+", null,  null,  null,  null,  null,  null,  null,  null,  null,  null,  null,  null, "RZ+",  null},
		{"X-",  null ,"X+",null,  null,  null,  null,  null,  null,  null,  null,  null,   null,  "Z-", null, "Z-"},
		{null,   "Y-",null,  null,  null,  null,  null,  null,  null,  null,  null,  null,  null,  null,  "RZ+",null},
	};*/
	public final static String gamePadButoonLable[][] = 
	{
		{null,  null,  null,  "",  null,  null,  null,  "",  "",  null,  null,  null,  "", null,  null,  null},
		{null,  null,  "",  null,  null,  null,  null,  null,  null,  null,  null,  null,  null,  "", null,  null},
		{null,  "",  null,  null,  null,  null,  null,  null,  null,  null,  null,  null,  null,  null,  "", null},
		{"",  null,	"",  null,  null,  null,  null,  null,  null,  null,  null,  null,  null,  "", null, ""},
		{null,  "", null,  null,  null,  null,  null,  null,  null,  null,  null,  null,  null,  null, "",  null},
		{null,  "", null,  null,  null,  null,  null,  null,  null,  null,  null,  null,  null,  null, "",  null},
		{"",  null ,"",null,  null,  null,  null,  null,  null,  null,  null,  null,   null,  "", null, ""},
		{null,   "",null,  null,  null,  null,  null,  null,  null,  null,  null,  null,  null,  null,  "",null},
	};
	
	
	
	static {
		typeFKeyMap.put(BUTTON_A_SCANCODE, KeyEvent.KEYCODE_BUTTON_A);
		typeFKeyMap.put(BUTTON_B_SCANCODE, KeyEvent.KEYCODE_BUTTON_B);
		typeFKeyMap.put(BUTTON_C_SCANCODE, KeyEvent.KEYCODE_BUTTON_C);
		typeFKeyMap.put(BUTTON_X_SCANCODE, KeyEvent.KEYCODE_BUTTON_X);
		typeFKeyMap.put(BUTTON_Y_SCANCODE, KeyEvent.KEYCODE_BUTTON_Y);
		typeFKeyMap.put(BUTTON_Z_SCANCODE, KeyEvent.KEYCODE_BUTTON_Z);
		typeFKeyMap.put(BUTTON_L1_SCANCODE, KeyEvent.KEYCODE_BUTTON_L1);
		typeFKeyMap.put(BUTTON_GAS_SCANCODE, KeyEvent.KEYCODE_BUTTON_L2);
		typeFKeyMap.put(BUTTON_R1_SCANCODE, KeyEvent.KEYCODE_BUTTON_R1);
		typeFKeyMap.put(BUTTON_L2_SCANCODE, KeyEvent.KEYCODE_BUTTON_L2);
		typeFKeyMap.put(BUTTON_R2_SCANCODE, KeyEvent.KEYCODE_BUTTON_R2);
		typeFKeyMap.put(BUTTON_BRAKE_SCANCODE, KeyEvent.KEYCODE_BUTTON_R2);
		typeFKeyMap.put(BUTTON_HOME_SCANCODE, KeyEvent.KEYCODE_HOME);
		typeFKeyMap.put(BUTTON_BACK_SCANCODE, KeyEvent.KEYCODE_BACK);
		typeFKeyMap.put(BUTTON_MENU_SCANCODE, KeyEvent.KEYCODE_MENU);
		
		typeFKeyMap.put(BUTTON_SELECT_SCANCODE, KeyEvent.KEYCODE_BUTTON_SELECT);
		typeFKeyMap.put(BUTTON_START_SCANCODE, KeyEvent.KEYCODE_BUTTON_START);
		typeFKeyMap.put(BUTTON_UP_SCANCODE, KeyEvent.KEYCODE_DPAD_UP);
		typeFKeyMap.put(BUTTON_YP_SCANCODE, KeyEvent.KEYCODE_DPAD_UP);
		typeFKeyMap.put(BUTTON_DOWN_SCANCODE, KeyEvent.KEYCODE_DPAD_DOWN);
		typeFKeyMap.put(BUTTON_YI_SCANCODE, KeyEvent.KEYCODE_DPAD_DOWN);
		typeFKeyMap.put(BUTTON_LEFT_SCANCODE, KeyEvent.KEYCODE_DPAD_LEFT);
		typeFKeyMap.put(BUTTON_XI_SCANCODE, KeyEvent.KEYCODE_DPAD_LEFT);
		typeFKeyMap.put(BUTTON_RIGHT_SCANCODE, KeyEvent.KEYCODE_DPAD_RIGHT);
		typeFKeyMap.put(BUTTON_XP_SCANCODE, KeyEvent.KEYCODE_DPAD_RIGHT);
		typeFKeyMap.put(BUTTON_RZP_SCANCODE, KeyEvent.KEYCODE_I);
		typeFKeyMap.put(BUTTON_ZI_SCANCODE, KeyEvent.KEYCODE_J);
		typeFKeyMap.put(BUTTON_RZI_SCANCODE, KeyEvent.KEYCODE_K);
		typeFKeyMap.put(BUTTON_ZP_SCANCODE, KeyEvent.KEYCODE_L);

	}
}
