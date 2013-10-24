package com.jnselectronics.ime.util;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.jnselectronics.im.hardware.JoyStickTypeF;
import com.jnselectronics.ime.JnsIMECoreService;
import com.jnselectronics.ime.JnsIMEInputMethodService;
import com.jnselectronics.ime.bean.JnsIMEPosition;
import com.jnselectronics.ime.bean.JnsIMEProfile;
import com.jnselectronics.ime.jni.JoyStickEvent;
import com.jnselectronics.ime.jni.RawEvent;
/**
 * 与输入法连接 以及 jnsinput.jar通信的类
 * <p>这是一个单列类，只能通过{@link getSendEvent}getSendEvent获得对象，所有操控器需要转换的数据均由此类发出。
 * 
 * @author steven
 *
 */
public class SendEvent {


	private final static String TAG= "SendEvent";
	/**
	 * 处理摇杆移动的间隔时间 （ms）.
	 */
	private final static int  STICK_MOVE_IRQ_TIME = 5;
	/**
	 * 发送到jnsinput触摸消息的标示头
	 */
	private final static String TOUCH = "injectTouch";
	/**
	 * 发送到jnsinput按键消息的标示头
	 */
	private final static String KEY = "injectKey";
	/**
	 * 发送到jnsinput消息的解析分隔符
	 */
	private final static String TOKEN=  ":";
	/**
	 * 连接到jnsinput得socket对象
	 */
	private static Socket socket;
	private static PrintWriter pw;
	private static DataInputStream dis;

	/**
	 * 右摇杆按下标记
	 */
	private boolean rightMotionKey = false;
	/**
	 * 左摇杆按下标记
	 */
	private boolean leftMotionKey = false;
	/**
	 * 右摇杆移动的横坐标
	 */
	private float rightJoystickCurrentPosX = 0.0f;
	/**
	 * 右摇杆移动的纵坐标
	 */
	private float rightJoystickCurrentPosY = 0.0f;
	/**
	 * 左摇杆移动的横坐标
	 */
	private float leftJoystickCurrentPosX = 0.0f;
	/**
	 * 左摇杆移动的纵坐标
	 */
	private float leftJoystickCurrentPosY = 0.0f;
	/**
	 * 摇杆移动的横坐标
	 */
	private float joystickR = 0.0f;
	/**
	 * 右摇杆当前配置的半径
	 */
	private float rightJoystickCurrentR = 0.0f;
	/**
	 * 左摇杆当前配置的半径
	 */
	private float leftJoystickCurrentR = 0.0f;
	/**
	 * 左摇杆当前是否按下
	 */
	private boolean LeftJoystickPresed = false;
	/**
	 * 右摇杆当前是否按下
	 */
	private boolean RightJoystickPresed = false;
	/**
	 * 上次处理左摇杆事件的时间
	 */
	private long last_left_press_time = 0;
	/**
	 * 上次处理右摇杆事件的时间
	 */
	private long last_right_press_time = 0;
	/**
	 * 判断左摇杆当前是否处在左移状态
	 */
	private boolean joy_xi_pressed = false;
	/**
	 * 判断左摇杆当前是否处在右移状态
	 */
	private boolean joy_xp_pressed =false;
	/**
	 * 判断左摇杆当前是否处在下移状态
	 */
	private boolean joy_yi_pressed = false;
	/**
	 * 判断左摇杆当前是否处在上移状态
	 */
	private boolean joy_yp_pressed =false;
	/**
	 * 判断左右摇杆当前是否处在左移状态
	 */
	private boolean joy_zi_pressed = false;
	/**
	 * 判断左右摇杆当前是否处在右移状态
	 */
	private boolean joy_zp_pressed =false;
	/**
	 * 判断左右摇杆当前是否处在下移状态
	 */
	private boolean joy_rzi_pressed = false;
	/**
	 * 判断左右摇杆当前是否处在上移状态
	 */
	private boolean joy_rzp_pressed =false;


	private static SendEvent sendEvent = null;

	private SendEvent()
	{
		super();
	}

	/**
	 * 获得一个SendEvent对象
	 **/
	public static SendEvent getSendEvent()
	{
		if(null == sendEvent)
			sendEvent = new SendEvent();
		return sendEvent;
	}
	/**
	 * 启动socket连接到jnsinput.jar 
	 * 
	 *  @return 连接成功返回true,否则返回false
	 */
	public  boolean connectJNSInputServer() {

		boolean connect= false;

		while(!connect)
		{	
			try {  
				connect = true;
				socket = new Socket("localhost", 44444);
				socket.setTcpNoDelay(true);
				dis = new DataInputStream(socket.getInputStream());
				pw = new PrintWriter(socket.getOutputStream());
				Log.e(TAG, "socket isConnected = " + socket.isConnected());
			} 
			catch(Exception e)
			{	
				e.printStackTrace();
				JnsEnvInit.startJnsInputServier();
				connect = false;
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		//isConnectiong = false;
		return true;
	}


	/**
	 * 查找scancode是否已存在于keylist中 
	 * 
	 * @author Steven.xu
	 * 
	 * @param keylist 需要查找的keylist对象
	 * @param scancode 指定的扫描码
	 * 
	 * @return 连接成功返回true,否则返回false
	 */
	@SuppressWarnings("unused")
	private static JnsIMEProfile iteratorKeyList(List<JnsIMEProfile> keylist, int scancode)
	{
		Log.d(TAG, "list size"+keylist.size());
		if(keylist==null)
			return null;
		for(JnsIMEProfile keyProfile : keylist)
			if(keyProfile.key == scancode&& keyProfile.posType >1)
				return keyProfile;
		return null;
	}
	/**
	 * 检查已发出的事件是否已经松开
	 * 
	 * @author Steven.xu
	 * 
	 * @return 已松开返回true, 否则返回false
	 */
	public boolean getEventDownLock() throws Exception
	{
		String data[];
		pw.print("geteventlock\n");
		pw.flush();

		@SuppressWarnings("deprecation")
		String response = dis.readLine();
		Log.d(TAG, response);
		data = response.split(":");
		if(data[0].equals("lock"))
		{
			return Boolean.getBoolean(data[1]);
		}

		return false;

	}
	/**
	 * 注入操控器的keyevent事件
	 * 
	 * 如果该按键已经配置的触摸按键，则忽略keymaping的配置，直接将按键发送至jnsinput.jar,注入touch事件，
	 * 如果没有配置触摸按键，配置了keymmaping,则调用输入法连接的sendKeyEent将keyEvent注入到对应的应用，
	 * 如果均未配置则忽略
	 * 
	 * @author Steven.xu
	 * 
	 * @param keyevent 要发送的keyevent对象
	 * 
	 * @return 发送成功返回true,失败返回false
	 */
	public  boolean sendKey(RawEvent keyevent)
	{ 
		//if(JnsIMEInputMethodService.currentAppName.equals(pkgName))
		//	return true;
		Log.d(TAG,"scancode="+keyevent.scanCode);
		JnsIMEProfile keyProfile =  iteratorKeyList(JnsIMECoreService.keyList, keyevent.scanCode);
		if(null == keyProfile)
		{	
			Log.d(TAG, "keyprofile  is  null");
			if(!JnsIMECoreService.keyMap.containsKey(keyevent.scanCode))
				return false;
			try
			{
				keyevent.keyCode = JnsIMECoreService.keyMap.get(keyevent.scanCode);
				JnsIMEInputMethodService.ims.getCurrentInputConnection().sendKeyEvent(
						new KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
								keyevent.value, keyevent.keyCode,0, 0, 0, keyevent.scanCode));
				//pw.print(keyString(keyevent));
				//pw.flush();
			}
			catch(Exception e)
			{	
				//connectJNSInputServer();
			}
		}
		Log.d(TAG, "keyprofile  is not null");

		try{
			//	socket.getOutputStream().write(posString((int)keyProfile.posX, (int)keyProfile.posY, keyevent.value).getBytes());
			pw.print(posString((int)keyProfile.posX, (int)keyProfile.posY, keyevent.value));
			pw.flush();
			Log.d(TAG,"send pos x="+keyProfile.posX+", pos y = "+keyProfile.posY+"action = "+keyevent.value);
			Log.d(TAG, "current time is "+System.currentTimeMillis());
		}
		catch(Exception e)
		{
			//	e.printStackTrace();
			//connectJNSInputServer();
		}
		return true;
	}
	/**
	 * 注入操控器的摇杆事件
	 * 
	 * @author Steven.xu
	 * 
	 * @param joyevent 要发送的joyevent对象
	 * 
	 * @return 发送成功返回true,失败返回false
	 */
	public void sendJoy(JoyStickEvent joyevent)
	{
		//if(JnsIMEInputMethodService.currentAppName.equals(pkgName))
		//	return ;
		processRightJoystickData(joyevent.getZ(), joyevent.getRz(), joyevent.getDeviceId());
		processLeftJoystickData(joyevent.getX(), joyevent.getY(), joyevent.getDeviceId());
	}
	private static String keyString(RawEvent keyevent)
	{
		if(keyevent.value == KeyEvent.ACTION_DOWN)
			JnsIMECoreService.eventDownLock++;
		else if(keyevent.value == KeyEvent.ACTION_UP)
			JnsIMECoreService.eventDownLock--;
		return KEY+TOKEN+keyevent.keyCode+TOKEN+keyevent.scanCode+TOKEN+keyevent.value+ TOKEN +keyevent.deviceId+"\n";
	}
	private static String posString(int x, int y, int value)
	{
		if(value == KeyEvent.ACTION_DOWN)
			JnsIMECoreService.eventDownLock++;
		else if(value == KeyEvent.ACTION_UP)
			JnsIMECoreService.eventDownLock--;
		return TOUCH+TOKEN+x+TOKEN+y+TOKEN+0xFF+TOKEN+value+"\n";
	}
	private static String posString(float x, float y, int tag, int value)
	{
		if(value == KeyEvent.ACTION_DOWN)
			JnsIMECoreService.eventDownLock++;
		else if(value == KeyEvent.ACTION_UP)
			JnsIMECoreService.eventDownLock--;
		return TOUCH+TOKEN+x+TOKEN+y+TOKEN+tag+TOKEN+value+"\n";
	}

	/**
	 * 计算摇杆偏移的正弦值
	 * 
	 * @author Steven.xu
	 * 
	 * @param bx 操控着摇杆的横向便宜量，-127 ~ 127
	 * @param by 操控器摇杆的纵向偏移量 。-127 ~ 127
	 * @param joystickType 摇杆的类型 TYPE_LEFT_JOYSTICK 或者 TYPE_RIGHT_JOYSTICK
	 * @return 摇杆偏移的正弦值
	 */
	private double calcSinA(int bx, int by, int joystickType) {
		int ox = 0x7f;
		int oy = 0x7f;
		int x = Math.abs(ox - bx);
		int y = Math.abs(oy - by);
		double r = Math.sqrt(Math.pow((double) x, 2) + Math.pow((double)y, 2));
		if (joystickType == JnsIMEPosition.TYPE_LEFT_JOYSTICK) {
			this.leftJoystickCurrentR = (float) r;
		} else if (joystickType == JnsIMEPosition.TYPE_RIGHT_JOYSTICK) {
			this.rightJoystickCurrentR = (float) r;
		}
		this.joystickR = 127;
		double sin = ((double)y) / r;
		return sin;
	}

	/**
	 * 处理操控器右摇杆的数据，
	 * 
	 * <p>处理完成后会跟根据配置文件直接发送到jnsinput或者是注入到应用
	 * 
	 * @author Steven.xu
	 * 
	 * @param i 操控着摇杆的横向便宜量，-127 ~ 127
	 * @param j 操控器摇杆的纵向偏移量 。-127 ~ 127
	 * @param deviceId 操控器在device中的id,程序中没有去获取可以直接输0
	 */
	private void processRightJoystickData(int i, int j, int deviceId) { // x = buffer[3] y = buffer[4]
		int ox = 0x7f;
		int oy = 0x7f;
		int ux = i;
		int uy = j;
		if (i < 0) ux = 256 + i;
		if (j < 0) uy = 256 + j;
		boolean touchMapped = false;


		if (JnsIMECoreService.keyList != null) 
		{
			for (JnsIMEProfile bp:JnsIMECoreService.keyList)
			{
				if (bp.posR > 0 && bp.posType == JnsIMEPosition.TYPE_RIGHT_JOYSTICK) 
				{
					touchMapped = true;
					double sin = calcSinA(ux, uy, JnsIMEPosition.TYPE_RIGHT_JOYSTICK);
					double touchR1 = (bp.posR/this.joystickR) * this.rightJoystickCurrentR;
					// Log.e(TAG, "touchR1 = " + touchR1 + " bp.posR" + bp.posR + " joystickR = " + joystickR + " rightJoystickCurrentR = " + rightJoystickCurrentR);
					double y = touchR1 * sin;
					double x = Math.sqrt(Math.pow(touchR1, 2) - Math.pow(y, 2));
					float rawX = 0.0f;
					float rawY = 0.0f;
					if (ux < ox && uy < oy) {  //鍧愭爣杞翠笂鍗婇儴鐨勫乏
						rawX = bp.posX - (float)x;
						rawY = bp.posY - (float)y;
						rightMotionKey = true;
						//	Log.e(TAG, "axis positive left part");
					} else if (ux > ox && uy < oy) {  //鍧愭爣杞翠笂鍗婇儴鐨勫彸
						rawX = bp.posX + (float) x;
						rawY = bp.posY - (float) y;
						rightMotionKey = true;
						//	Log.e(TAG, "axis positive right part");
					} else if (ux < ox && uy > oy) { //鍧愭爣杞翠笅鍗婇儴鐨勫乏
						rawX = bp.posX  - (float) x;
						rawY = bp.posY + (float) y;
						rightMotionKey = true;
						//	Log.e(TAG, "axis negtive left part");
					} else if (ux > ox && uy > oy) { //鍧愭爣杞翠笅鍗婇儴鐨勫彸
						rawX = bp.posX + (float) x;
						rawY = bp.posY + (float) y;
						rightMotionKey = true;
						//	Log.e(TAG, "axis negtiveleft part");
					} else if (ux == ox && uy < oy) { //Y杞村彉鍖?
						rawX = bp.posX;
						rawY = bp.posY - (float)y;
						rightMotionKey = true;
						//	Log.e(TAG, "axis Y < 0x7f");
					} else if (ux == ox && uy > oy) { //Y杞村彉鍖?
						rawX = bp.posX;
						rawY = bp.posY + (float) y;
						rightMotionKey = true;
						//	Log.e(TAG, "axis Y > 0x7f");
					} else if (ux < ox && uy == oy) { //X杞村彉鍖?
						rawX = bp.posX - (float)x;
						rawY = bp.posY;
						rightMotionKey = true;
						Log.e(TAG, "axis X < 0x7f");
					} else if (ux > ox && uy == oy) { //X杞村彉鍖?
						rawX = bp.posX + (float) x;
						rawY = bp.posY;
						rightMotionKey = true;
						Log.e(TAG, "axis X  > 0x7f");
					} else if (ux == ox && uy == oy && rightMotionKey) {
						Log.e(TAG, "right  you release map");
						pw.print(posString(bp.posX, bp.posY, JoyStickTypeF.RIGHT_JOYSTICK_TAG, MotionEvent.ACTION_MOVE));
						pw.flush();
						pw.print(posString(bp.posX, bp.posY, JoyStickTypeF.RIGHT_JOYSTICK_TAG, MotionEvent.ACTION_UP));
						pw.flush();
						rightMotionKey = false;
						RightJoystickPresed = false;
					}
					if (rightMotionKey) {
						if(!RightJoystickPresed)
						{
							pw.print(posString(bp.posX, bp.posY, JoyStickTypeF.RIGHT_JOYSTICK_TAG, MotionEvent.ACTION_DOWN));
							pw.flush();
							RightJoystickPresed = true;								
						}

						if(RightJoystickPresed)
						{
							if((rawX != rightJoystickCurrentPosX) && (rawY != rightJoystickCurrentPosY))
								if(System.currentTimeMillis() - last_right_press_time > STICK_MOVE_IRQ_TIME)
								{		
									pw.print(posString(rawX, rawY, JoyStickTypeF.RIGHT_JOYSTICK_TAG, MotionEvent.ACTION_MOVE));
									pw.flush();
									last_right_press_time = System.currentTimeMillis();
								}
						}
					}
					rightJoystickCurrentPosX = rawX;
					rightJoystickCurrentPosY = rawY;
				}
			}
		}
		Log.d(TAG,"z="+ux+", rz="+uy);
		if(!touchMapped)
		{
			int z = ux;
			int rz = uy;
			RawEvent keyevent;

			if(JnsIMECoreService.keyMap.containsKey(JoyStickTypeF.BUTTON_ZP_SCANCODE))
			{
				if(z > 200 )
				{
					if(!joy_zp_pressed)
					{
						joy_zp_pressed = true;
						keyevent = new RawEvent(JnsIMECoreService.keyMap.get(JoyStickTypeF.BUTTON_ZP_SCANCODE),
								JoyStickTypeF.BUTTON_ZP_SCANCODE, KeyEvent.ACTION_DOWN, deviceId);
						pw.print(keyString(keyevent));
						pw.flush();
					}
				}
				else
				{
					if(joy_zp_pressed)
					{
						joy_zp_pressed = false;
						keyevent = new RawEvent(JnsIMECoreService.keyMap.get(JoyStickTypeF.BUTTON_ZP_SCANCODE),
								JoyStickTypeF.BUTTON_ZP_SCANCODE, KeyEvent.ACTION_UP,deviceId);
						pw.print(keyString(keyevent));
						pw.flush();
					}
				}
			}

			if(JnsIMECoreService.keyMap.containsKey(JoyStickTypeF.BUTTON_ZI_SCANCODE))
			{
				if(z < 50)
				{
					if(!joy_zi_pressed)
					{
						joy_zi_pressed = true;
						keyevent = new RawEvent(JnsIMECoreService.keyMap.get(JoyStickTypeF.BUTTON_ZI_SCANCODE),
								JoyStickTypeF.BUTTON_ZI_SCANCODE, KeyEvent.ACTION_DOWN, deviceId);
						pw.print(keyString(keyevent));
						pw.flush();
					}
				}	
				else
				{	
					if(joy_zi_pressed)
					{
						joy_zi_pressed = false;
						keyevent = new RawEvent(JnsIMECoreService.keyMap.get(JoyStickTypeF.BUTTON_ZI_SCANCODE),
								JoyStickTypeF.BUTTON_ZI_SCANCODE, KeyEvent.ACTION_UP, deviceId);
						pw.print(keyString(keyevent));
						pw.flush();
					}
				}
			}

			if(JnsIMECoreService.keyMap.containsKey(JoyStickTypeF.BUTTON_RZI_SCANCODE))
			{				
				if(rz > 200)
				{

					if(!joy_rzi_pressed)
					{

						joy_rzi_pressed = true;
						keyevent = new RawEvent(JnsIMECoreService.keyMap.get(JoyStickTypeF.BUTTON_RZI_SCANCODE),
								JoyStickTypeF.BUTTON_RZI_SCANCODE, KeyEvent.ACTION_DOWN, deviceId);
						pw.print(keyString(keyevent));
						pw.flush();
					}
				}
				else
				{	
					if(joy_rzi_pressed)
					{

						joy_rzi_pressed = false;
						keyevent = new RawEvent(JnsIMECoreService.keyMap.get(JoyStickTypeF.BUTTON_RZI_SCANCODE),
								JoyStickTypeF.BUTTON_RZI_SCANCODE, KeyEvent.ACTION_UP, deviceId);
						pw.print(keyString(keyevent));
						pw.flush();
					}
				}

			}

			if( JnsIMECoreService.keyMap.containsKey(JoyStickTypeF.BUTTON_RZP_SCANCODE))
			{
				if(rz < 50)
				{
					if(!joy_rzp_pressed)
					{
						joy_rzp_pressed = true;
						keyevent = new RawEvent(JnsIMECoreService.keyMap.get(JoyStickTypeF.BUTTON_RZP_SCANCODE),
								JoyStickTypeF.BUTTON_RZP_SCANCODE, KeyEvent.ACTION_DOWN, deviceId);
						pw.print(keyString(keyevent));
						pw.flush();
					}
				}
				else
				{
					if(joy_rzp_pressed)
					{
						joy_rzp_pressed = false;
						keyevent = new RawEvent(JnsIMECoreService.keyMap.get(JoyStickTypeF.BUTTON_RZP_SCANCODE),
								JoyStickTypeF.BUTTON_RZP_SCANCODE, KeyEvent.ACTION_UP, deviceId);
						pw.print(keyString(keyevent));
						pw.flush();
					}
				}
			}
		}
	}
	/**
	 * 处理操控器左摇杆的数据，
	 * 
	 * <p>处理完成后会跟根据配置文件直接发送到jnsinput或者是注入到应用
	 * 
	 * @author Steven.xu
	 * 
	 * @param i 操控着摇杆的横向便宜量，-127 ~ 127
	 * @param j 操控器摇杆的纵向偏移量 。-127 ~ 127
	 * @param deviceId 操控器在device中的id,程序中没有去获取可以直接输0
	 */
	private void processLeftJoystickData(int i, int j, int deviceId) { // x = buffer[3] y = buffer[4]
		int ox = 0x7f;
		int oy = 0x7f;
		int ux = i;
		int uy = j;
		if (i < 0) ux = 256 + i;
		if (j < 0) uy = 256 + j;
		boolean touchMapped = false;
		//		 if (bx != 0x7f || by != 0x7f) {
		if (JnsIMECoreService.keyList != null) 
		{
			for (JnsIMEProfile bp: JnsIMECoreService.keyList) 
			{

				if (bp.posR > 0 && bp.posType == JnsIMEPosition.TYPE_LEFT_JOYSTICK) 
				{
					touchMapped = true;
					Log.d(TAG, "r="+bp.posR+", postype="+bp.posType);
					double sin = calcSinA(ux, uy, JnsIMEPosition.TYPE_LEFT_JOYSTICK);
					//						 double y = bp.posR * sin;
					//						 double x = Math.sqrt(Math.pow(bp.posR, 2) - Math.pow(y, 2));
					double touchR1 = (bp.posR/this.joystickR) * this.leftJoystickCurrentR;
					//	 Log.e(TAG, "touchR1 = " + touchR1 + " bp.posR" + bp.posR + " joystickR = " + joystickR + " leftJoystickCurrentR = " + leftJoystickCurrentR);
					double y = touchR1 * sin;
					double x = Math.sqrt(Math.pow(touchR1, 2) - Math.pow(y, 2));
					float rawX = 0.0f;
					float rawY = 0.0f;
					Log.d(TAG, "ox ="+ox+",ux="+ux+",oy="+oy+",uy="+uy);
					if (ux < ox && uy < oy) {  //鍧愭爣杞翠笂鍗婇儴鐨勫乏
						rawX = bp.posX - (float)x;
						rawY = bp.posY - (float)y;
						leftMotionKey = true;
						Log.d(TAG, "axis positive left part");
					} else if (ux > ox && uy < oy) {  //鍧愭爣杞翠笂鍗婇儴鐨勫彸
						rawX = bp.posX + (float) x;
						rawY = bp.posY - (float) y;
						leftMotionKey = true;
						Log.d(TAG, "axis positive right part");
					} else if (ux < ox && uy > oy) { //鍧愭爣杞翠笅鍗婇儴鐨勫乏
						rawX = bp.posX  - (float) x;
						rawY = bp.posY + (float) y;
						leftMotionKey = true;
						Log.d(TAG, "axis negtive left part");
					} else if (ux > ox && uy > oy) { //鍧愭爣杞翠笅鍗婇儴鐨勫彸
						rawX = bp.posX + (float) x;
						rawY = bp.posY + (float) y;
						leftMotionKey = true;
						Log.d(TAG, "axis negtiveleft part");
					} else if (ux == ox && uy < oy) { //Y杞村彉鍖?
						rawX = bp.posX;
						rawY = bp.posY - (float)y;
						leftMotionKey = true;
						Log.d(TAG, "axis Y < 0x7f");
					} else if (ux == ox && uy > oy) { //Y杞村彉鍖?
						rawX = bp.posX;
						rawY = bp.posY + (float) y;
						leftMotionKey = true;
						Log.d(TAG, "axis Y > 0x7f");
					} else if (ux < ox && uy == oy) { //X杞村彉鍖?
						rawX = bp.posX - (float)x;
						rawY = bp.posY;
						leftMotionKey = true;
						Log.d(TAG, "axis X < 0x7f");
					} else if (ux > ox && uy == oy) { //X杞村彉鍖?
						rawX = bp.posX + (float) x;
						rawY = bp.posY;
						leftMotionKey = true;
						Log.d(TAG, "axis X  > 0x7f");
					} else if (ux == ox && uy == oy && leftMotionKey) {
						Log.e(TAG, "left joystick you release map");
						pw.print(posString(bp.posX, bp.posY, JoyStickTypeF.LEFT_JOYSTICK_TAG, MotionEvent.ACTION_MOVE));
						pw.flush();
						pw.print(posString(bp.posX, bp.posY, JoyStickTypeF.LEFT_JOYSTICK_TAG, MotionEvent.ACTION_UP));
						pw.flush();
						leftMotionKey = false;
						LeftJoystickPresed = false;
					}

					Log.d(TAG, "leftMotionKey="+leftMotionKey);

					if (leftMotionKey) 
					{
						Log.d(TAG, "LeftJoystickPresed="+LeftJoystickPresed);
						if(!LeftJoystickPresed)
						{	
							//pw.print(posString(bp.posX, bp.posY, JoyStickTypeF.LEFT_JOYSTICK_TAG, MotionEvent.ACTION_DOWN));
							//pw.flush();
							try {
								socket.getOutputStream().write(posString(bp.posX, bp.posY, JoyStickTypeF.LEFT_JOYSTICK_TAG, MotionEvent.ACTION_DOWN).getBytes());
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							LeftJoystickPresed = true;
						}

						if(LeftJoystickPresed)
						{
							if((rawX != leftJoystickCurrentPosX) && (rawY != leftJoystickCurrentPosY))
								if(System.currentTimeMillis() - last_left_press_time > STICK_MOVE_IRQ_TIME)
								{	
									pw.print(posString(rawX, rawY, JoyStickTypeF.LEFT_JOYSTICK_TAG, MotionEvent.ACTION_MOVE));
									pw.flush();
									last_left_press_time = System.currentTimeMillis();
								}
						}

					}
					leftJoystickCurrentPosX = rawX;
					leftJoystickCurrentPosY = rawY;
				}
			}
		}
		Log.d(TAG,"z="+ux+", y="+uy);
		if(!touchMapped)
		{
			int x = ux;
			int y = uy;
			RawEvent keyevent;
			// 将右摇杆转化成按键映射

			// 摇杆上移
			if(JnsIMECoreService.keyMap.containsKey(JoyStickTypeF.BUTTON_XP_SCANCODE))
			{	
				if(x > 200)
				{	
					if(!joy_xp_pressed)
					{	
						joy_xp_pressed = true;
						keyevent = new RawEvent(JnsIMECoreService.keyMap.get(JoyStickTypeF.BUTTON_XP_SCANCODE),
								JoyStickTypeF.BUTTON_XP_SCANCODE, KeyEvent.ACTION_DOWN, deviceId);
						pw.print(keyString(keyevent));
						pw.flush();
					}
				}
				else 
				{
					if(joy_xp_pressed)
					{
						joy_xp_pressed=false;
						keyevent = new RawEvent(JnsIMECoreService.keyMap.get(JoyStickTypeF.BUTTON_XP_SCANCODE),
								JoyStickTypeF.BUTTON_XP_SCANCODE, KeyEvent.ACTION_UP, deviceId);
						pw.print(keyString(keyevent));
						pw.flush();
					}
				}
			}

			// 摇杆下移
			if(JnsIMECoreService.keyMap.containsKey(JoyStickTypeF.BUTTON_XI_SCANCODE))
			{
				if(x < 50)
				{
					if(!joy_xi_pressed)
					{	
						this.joy_xi_pressed = true;
						keyevent = new RawEvent(JnsIMECoreService.keyMap.get(JoyStickTypeF.BUTTON_XI_SCANCODE),
								JoyStickTypeF.BUTTON_XI_SCANCODE, KeyEvent.ACTION_DOWN, deviceId);
						pw.print(keyString(keyevent));
						pw.flush();
					}
				}
				else
				{
					if(joy_xi_pressed)
					{	
						this.joy_xi_pressed = false;
						keyevent = new RawEvent(JnsIMECoreService.keyMap.get(JoyStickTypeF.BUTTON_XI_SCANCODE),
								JoyStickTypeF.BUTTON_XI_SCANCODE, KeyEvent.ACTION_UP, deviceId);
						pw.print(keyString(keyevent));
						pw.flush();
					}
				}
			}

			if(JnsIMECoreService.keyMap.containsKey(JoyStickTypeF.BUTTON_YI_SCANCODE))
			{		
				if(y > 200)
				{
					if(!joy_yi_pressed)
					{	
						joy_yi_pressed = true;
						keyevent = new RawEvent(JnsIMECoreService.keyMap.get(JoyStickTypeF.BUTTON_YI_SCANCODE),
								JoyStickTypeF.BUTTON_YI_SCANCODE, KeyEvent.ACTION_DOWN, deviceId);
						pw.print(keyString(keyevent));
						pw.flush();
					}
				}
				else
				{
					if(joy_yi_pressed)
					{	
						joy_yi_pressed = false;
						keyevent = new RawEvent(JnsIMECoreService.keyMap.get(JoyStickTypeF.BUTTON_YI_SCANCODE),
								JoyStickTypeF.BUTTON_YI_SCANCODE, KeyEvent.ACTION_UP, deviceId);
						pw.print(keyString(keyevent));
						pw.flush();
					}

				}
			}

			if(JnsIMECoreService.keyMap.containsKey(JoyStickTypeF.BUTTON_YP_SCANCODE))
			{	
				if(y < 50)
				{
					if(!joy_yp_pressed)
					{
						joy_yp_pressed = true;
						keyevent = new RawEvent(JnsIMECoreService.keyMap.get(JoyStickTypeF.BUTTON_YP_SCANCODE),
								JoyStickTypeF.BUTTON_YP_SCANCODE, KeyEvent.ACTION_DOWN, deviceId);
						pw.print(keyString(keyevent));
						pw.flush();
					}
				}
				else
				{
					if(joy_yp_pressed)
					{
						joy_yp_pressed = false;
						keyevent = new RawEvent(JnsIMECoreService.keyMap.get(JoyStickTypeF.BUTTON_YP_SCANCODE),
								JoyStickTypeF.BUTTON_YP_SCANCODE, KeyEvent.ACTION_UP, deviceId);
						pw.print(keyString(keyevent));
						pw.flush();
					}
				}
			}
		}
	}

}