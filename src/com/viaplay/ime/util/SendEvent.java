package com.viaplay.ime.util;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.viaplay.im.hardware.JoyStickTypeF;
import com.viaplay.ime.JnsIMECoreService;
import com.viaplay.ime.JnsIMEInputMethodService;
import com.viaplay.ime.bean.JnsIMEPosition;
import com.viaplay.ime.bean.JnsIMEProfile;
import com.viaplay.ime.jni.JoyStickEvent;
import com.viaplay.ime.jni.RawEvent;
/**
 * 涓庤緭鍏ユ硶杩炴帴 浠ュ強 jnsinput.jar閫氫俊鐨勭被
 * <p>杩欐槸涓�釜鍗曞垪绫伙紝鍙兘閫氳繃{@link getSendEvent}getSendEvent鑾峰緱瀵硅薄锛屾墍鏈夋搷鎺у櫒闇�杞崲鐨勬暟鎹潎鐢辨绫诲彂鍑恒�
 * 
 * @author steven
 *
 */
public class SendEvent {

	public final static String pkgName ="com.viaplay.ime";
	public final static String TAG= "SendEvent";
	
	private final static int  STICK_MOVE_IRQ_TIME = 1;
	/**
	 * 鍙戦�鍒癹nsinput瑙︽懜娑堟伅鐨勬爣绀哄ご
	 */
	private final static String TOUCH = "injectTouch";
	/**
	 * 鍙戦�鍒癹nsinput鎸夐敭娑堟伅鐨勬爣绀哄ご
	 */
	private final static String KEY = "injectKey";
	/**
	 * 鍙戦�鍒癹nsinput娑堟伅鐨勮В鏋愬垎闅旂
	 */
	private final static String TOKEN=  ":";
	/**
	 * 杩炴帴鍒癹nsinput寰梥ocket瀵硅薄
	 */
	private static Socket socket;
	private static PrintWriter pw;
	private static DataInputStream dis;

	/**
	 * 鍙虫憞鏉嗘寜涓嬫爣璁�
	 */
	private boolean rightMotionKey = false;
	/**
	 * 宸︽憞鏉嗘寜涓嬫爣璁�
	 */
	private boolean leftMotionKey = false;
	/**
	 * 鍙虫憞鏉嗙Щ鍔ㄧ殑妯潗鏍�
	 */
	private float rightJoystickCurrentPosX = 0.0f;
	/**
	 * 鍙虫憞鏉嗙Щ鍔ㄧ殑绾靛潗鏍�
	 */
	private float rightJoystickCurrentPosY = 0.0f;
	/**
	 * 宸︽憞鏉嗙Щ鍔ㄧ殑妯潗鏍�
	 */
	private float leftJoystickCurrentPosX = 0.0f;
	/**
	 * 宸︽憞鏉嗙Щ鍔ㄧ殑绾靛潗鏍�
	 */
	private float leftJoystickCurrentPosY = 0.0f;
	/**
	 * 鎽囨潌绉诲姩鐨勬í鍧愭爣
	 */
	private float joystickR = 0.0f;
	/**
	 * 鍙虫憞鏉嗗綋鍓嶉厤缃殑鍗婂緞
	 */
	private float rightJoystickCurrentR = 0.0f;
	/**
	 * 宸︽憞鏉嗗綋鍓嶉厤缃殑鍗婂緞
	 */
	private float leftJoystickCurrentR = 0.0f;
	/**
	 * 宸︽憞鏉嗗綋鍓嶆槸鍚︽寜涓�
	 */
	private boolean LeftJoystickPresed = false;
	/**
	 * 鍙虫憞鏉嗗綋鍓嶆槸鍚︽寜涓�
	 */
	private boolean RightJoystickPresed = false;
	/**
	 * 涓婃澶勭悊宸︽憞鏉嗕簨浠剁殑鏃堕棿
	 */
	private long last_left_press_time = 0;
	/**
	 * 涓婃澶勭悊鍙虫憞鏉嗕簨浠剁殑鏃堕棿
	 */
	private long last_right_press_time = 0;
	/**
	 * 鍒ゆ柇宸︽憞鏉嗗綋鍓嶆槸鍚﹀鍦ㄥ乏绉荤姸鎬�
	 */
	private boolean joy_xi_pressed = false;
	/**
	 * 鍒ゆ柇宸︽憞鏉嗗綋鍓嶆槸鍚﹀鍦ㄥ彸绉荤姸鎬�
	 */
	private boolean joy_xp_pressed =false;
	/**
	 * 鍒ゆ柇宸︽憞鏉嗗綋鍓嶆槸鍚﹀鍦ㄤ笅绉荤姸鎬�
	 */
	private boolean joy_yi_pressed = false;
	/**
	 * 鍒ゆ柇宸︽憞鏉嗗綋鍓嶆槸鍚﹀鍦ㄤ笂绉荤姸鎬�
	 */
	private boolean joy_yp_pressed =false;
	/**
	 * 鍒ゆ柇宸﹀彸鎽囨潌褰撳墠鏄惁澶勫湪宸︾Щ鐘舵�
	 */
	private boolean joy_zi_pressed = false;
	/**
	 * 鍒ゆ柇宸﹀彸鎽囨潌褰撳墠鏄惁澶勫湪鍙崇Щ鐘舵�
	 */
	private boolean joy_zp_pressed =false;
	/**
	 * 鍒ゆ柇宸﹀彸鎽囨潌褰撳墠鏄惁澶勫湪涓嬬Щ鐘舵�
	 */
	private boolean joy_rzi_pressed = false;
	/**
	 * 鍒ゆ柇宸﹀彸鎽囨潌褰撳墠鏄惁澶勫湪涓婄Щ鐘舵�
	 */
	private boolean joy_rzp_pressed =false;


	private static SendEvent sendEvent = null;

	private SendEvent()
	{
		super();
	}

	/**
	 * 鑾峰緱涓�釜SendEvent瀵硅薄
	 **/
	public static SendEvent getSendEvent()
	{
		if(null == sendEvent)
			sendEvent = new SendEvent();
		return sendEvent;
	}
	/**
	 * 鍚姩socket杩炴帴鍒癹nsinput.jar 
	 * 
	 *  @return 杩炴帴鎴愬姛杩斿洖true,鍚﹀垯杩斿洖false
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
				//	e.printStackTrace();
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
	 * 鏌ユ壘scancode鏄惁宸插瓨鍦ㄤ簬keylist涓�
	 * 
	 * @author Steven.xu
	 * 
	 * @param keylist 闇�鏌ユ壘鐨刱eylist瀵硅薄
	 * @param scancode 鎸囧畾鐨勬壂鎻忕爜
	 * 
	 * @return 杩炴帴鎴愬姛杩斿洖true,鍚﹀垯杩斿洖false
	 */
	@SuppressWarnings("unused")
	private static JnsIMEProfile iteratorKeyList(List<JnsIMEProfile> keylist, int scancode)
	{
		//Log.d(TAG, "list size"+keylist.size());
		if(keylist==null)
			return null;
		for(JnsIMEProfile keyProfile : keylist)
			if(keyProfile.key == scancode&& keyProfile.posType >1)
				return keyProfile;
		return null;
	}
	/**
	 * 妫�煡宸插彂鍑虹殑浜嬩欢鏄惁宸茬粡鏉惧紑
	 * 
	 * @author Steven.xu
	 * 
	 * @return 宸叉澗寮�繑鍥瀟rue, 鍚﹀垯杩斿洖false
	 */
	public boolean getEventDownLock() throws Exception
	{
		String data[];
		sendToJar("geteventlock\n");


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
	 * 娉ㄥ叆鎿嶆帶鍣ㄧ殑keyevent浜嬩欢
	 * 
	 * 濡傛灉璇ユ寜閿凡缁忛厤缃殑瑙︽懜鎸夐敭锛屽垯蹇界暐keymaping鐨勯厤缃紝鐩存帴灏嗘寜閿彂閫佽嚦jnsinput.jar,娉ㄥ叆touch浜嬩欢锛�
	 * 濡傛灉娌℃湁閰嶇疆瑙︽懜鎸夐敭锛岄厤缃簡keymmaping,鍒欒皟鐢ㄨ緭鍏ユ硶杩炴帴鐨剆endKeyEent灏唊eyEvent娉ㄥ叆鍒板搴旂殑搴旂敤锛�
	 * 濡傛灉鍧囨湭閰嶇疆鍒欏拷鐣�
	 * 
	 * @author Steven.xu
	 * 
	 * @param keyevent 瑕佸彂閫佺殑keyevent瀵硅薄
	 * 
	 * @return 鍙戦�鎴愬姛杩斿洖true,澶辫触杩斿洖false
	 */
	public  boolean sendKey(RawEvent keyevent)
	{ 
		//if(JnsIMECoreService.touchConfiging)
		//	return true;
		//Log.d(TAG,"scancode="+keyevent.scanCode);
		JnsIMEProfile keyProfile =  iteratorKeyList(JnsIMECoreService.keyList, keyevent.scanCode);
		if(null == keyProfile)
		{	
			//Log.d(TAG, "keyprofile  is  null");
			if(!JnsIMECoreService.keyMap.containsKey(keyevent.scanCode))
			{	
				keyevent.keyCode = JoyStickTypeF.typeFKeyMap.get(keyevent.scanCode);
				JnsIMECoreService.ime.getCurrentInputConnection().sendKeyEvent(
						new KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
								keyevent.value, keyevent.keyCode,0, 0, 1, keyevent.scanCode));
			}
			else
			{	
				keyevent.keyCode = JnsIMECoreService.keyMap.get(keyevent.scanCode);
				JnsIMECoreService.ime.getCurrentInputConnection().sendKeyEvent(
						new KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
								keyevent.value, keyevent.keyCode,0, 0, 1, keyevent.scanCode));
				//sendToJar(keyString(keyevent));
				//
			}
		}
		//Log.d(TAG, "keyprofile  is not null");

		try{
			//	socket.getOutputStream().write(posString((int)keyProfile.posX, (int)keyProfile.posY, keyevent.value).getBytes());
			sendToJar(posString((int)keyProfile.posX, (int)keyProfile.posY, keyevent.value));

			//Log.d(TAG,"send pos x="+keyProfile.posX+", pos y = "+keyProfile.posY+"action = "+keyevent.value);
			//Log.d(TAG, "current time is "+System.currentTimeMillis());
		}
		catch(Exception e)
		{
			//	e.printStackTrace();
			//connectJNSInputServer();
		}
		return true;
	}
	/**
	 * 娉ㄥ叆鎿嶆帶鍣ㄧ殑鎽囨潌浜嬩欢
	 * 
	 * @author Steven.xu
	 * 
	 * @param joyevent 瑕佸彂閫佺殑joyevent瀵硅薄
	 * 
	 * @return 鍙戦�鎴愬姛杩斿洖true,澶辫触杩斿洖false
	 */
	public void sendJoy(JoyStickEvent joyevent)
	{
		if(JnsIMECoreService.touchConfiging)
			return;
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
	 * 璁＄畻鎽囨潌鍋忕Щ鐨勬寮﹀�
	 * 
	 * @author Steven.xu
	 * 
	 * @param bx 鎿嶆帶鐫�憞鏉嗙殑妯悜渚垮疁閲忥紝-127 ~ 127
	 * @param by 鎿嶆帶鍣ㄦ憞鏉嗙殑绾靛悜鍋忕Щ閲�銆�127 ~ 127
	 * @param joystickType 鎽囨潌鐨勭被鍨�TYPE_LEFT_JOYSTICK 鎴栬� TYPE_RIGHT_JOYSTICK
	 * @return 鎽囨潌鍋忕Щ鐨勬寮﹀�
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
	 * 澶勭悊鎿嶆帶鍣ㄥ彸鎽囨潌鐨勬暟鎹紝
	 * 
	 * <p>澶勭悊瀹屾垚鍚庝細璺熸牴鎹厤缃枃浠剁洿鎺ュ彂閫佸埌jnsinput鎴栬�鏄敞鍏ュ埌搴旂敤
	 * 
	 * @author Steven.xu
	 * 
	 * @param i 鎿嶆帶鐫�憞鏉嗙殑妯悜渚垮疁閲忥紝-127 ~ 127
	 * @param j 鎿嶆帶鍣ㄦ憞鏉嗙殑绾靛悜鍋忕Щ閲�銆�127 ~ 127
	 * @param deviceId 鎿嶆帶鍣ㄥ湪device涓殑id,绋嬪簭涓病鏈夊幓鑾峰彇鍙互鐩存帴杈�
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
					if (ux < ox && uy < oy) {  //閸ф劖鐖ｆ潪缈犵瑐閸楀﹪鍎撮惃鍕箯
						rawX = bp.posX - (float)x;
						rawY = bp.posY - (float)y;
						rightMotionKey = true;
						//	Log.e(TAG, "axis positive left part");
					} else if (ux > ox && uy < oy) {  //閸ф劖鐖ｆ潪缈犵瑐閸楀﹪鍎撮惃鍕礁
						rawX = bp.posX + (float) x;
						rawY = bp.posY - (float) y;
						rightMotionKey = true;
						//	Log.e(TAG, "axis positive right part");
					} else if (ux < ox && uy > oy) { //閸ф劖鐖ｆ潪缈犵瑓閸楀﹪鍎撮惃鍕箯
						rawX = bp.posX  - (float) x;
						rawY = bp.posY + (float) y;
						rightMotionKey = true;
						//	Log.e(TAG, "axis negtive left part");
					} else if (ux > ox && uy > oy) { //閸ф劖鐖ｆ潪缈犵瑓閸楀﹪鍎撮惃鍕礁
						rawX = bp.posX + (float) x;
						rawY = bp.posY + (float) y;
						rightMotionKey = true;
						//	Log.e(TAG, "axis negtiveleft part");
					} else if (ux == ox && uy < oy) { //Y鏉炴潙褰夐崠?
						rawX = bp.posX;
						rawY = bp.posY - (float)y;
						rightMotionKey = true;
						//	Log.e(TAG, "axis Y < 0x7f");
					} else if (ux == ox && uy > oy) { //Y鏉炴潙褰夐崠?
						rawX = bp.posX;
						rawY = bp.posY + (float) y;
						rightMotionKey = true;
						//	Log.e(TAG, "axis Y > 0x7f");
					} else if (ux < ox && uy == oy) { //X鏉炴潙褰夐崠?
						rawX = bp.posX - (float)x;
						rawY = bp.posY;
						rightMotionKey = true;
						//Log.e(TAG, "axis X < 0x7f");
					} else if (ux > ox && uy == oy) { //X猫陆麓氓聫藴氓艗?
						rawX = bp.posX + (float) x;
						rawY = bp.posY;
						rightMotionKey = true;
						//Log.e(TAG, "axis X  > 0x7f");
					} else if (ux == ox && uy == oy && rightMotionKey) {
						//Log.e(TAG, "right  you release map");
						sendToJar(posString(bp.posX, bp.posY, JoyStickTypeF.RIGHT_JOYSTICK_TAG, MotionEvent.ACTION_MOVE));

						sendToJar(posString(bp.posX, bp.posY, JoyStickTypeF.RIGHT_JOYSTICK_TAG, MotionEvent.ACTION_UP));

						rightMotionKey = false;
						RightJoystickPresed = false;
					}
					if (rightMotionKey) {
						if(!RightJoystickPresed)
						{
							sendToJar(posString(bp.posX, bp.posY, JoyStickTypeF.RIGHT_JOYSTICK_TAG, MotionEvent.ACTION_DOWN));

							RightJoystickPresed = true;								
						}

						if(RightJoystickPresed)
						{
							if((rawX != rightJoystickCurrentPosX) || (rawY != rightJoystickCurrentPosY))
								if(System.currentTimeMillis() - last_right_press_time > STICK_MOVE_IRQ_TIME)
								{		
									sendToJar(posString(rawX, rawY, JoyStickTypeF.RIGHT_JOYSTICK_TAG, MotionEvent.ACTION_MOVE));

									last_right_press_time = System.currentTimeMillis();
								}
						}
					}
					rightJoystickCurrentPosX = rawX;
					rightJoystickCurrentPosY = rawY;
				}
			}
		}
		//Log.d(TAG,"z="+ux+", rz="+uy);
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
					sendToJar(keyevent);

				}
			}
			else
			{
				if(joy_zp_pressed)
				{
					joy_zp_pressed = false;
						keyevent = new RawEvent(JnsIMECoreService.keyMap.get(JoyStickTypeF.BUTTON_ZP_SCANCODE),
								JoyStickTypeF.BUTTON_ZP_SCANCODE, KeyEvent.ACTION_UP,deviceId);
					sendToJar(keyevent);
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
					sendToJar(keyevent);

				}
			}	
			else
			{	
				if(joy_zi_pressed)
				{
					joy_zi_pressed = false;
						keyevent = new RawEvent(JnsIMECoreService.keyMap.get(JoyStickTypeF.BUTTON_ZI_SCANCODE),
								JoyStickTypeF.BUTTON_ZI_SCANCODE, KeyEvent.ACTION_UP, deviceId);
					sendToJar(keyevent);

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
					sendToJar(keyevent);

				}
			}
			else
			{	
				if(joy_rzi_pressed)
				{

						joy_rzi_pressed = false;
						keyevent = new RawEvent(JnsIMECoreService.keyMap.get(JoyStickTypeF.BUTTON_RZI_SCANCODE),
								JoyStickTypeF.BUTTON_RZI_SCANCODE, KeyEvent.ACTION_UP, deviceId);
					sendToJar(keyevent);
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
					sendToJar(keyevent);

				}
			}
			else
			{
				if(joy_rzp_pressed)
				{
					joy_rzp_pressed = false;
						keyevent = new RawEvent(JnsIMECoreService.keyMap.get(JoyStickTypeF.BUTTON_RZP_SCANCODE),
								JoyStickTypeF.BUTTON_RZP_SCANCODE, KeyEvent.ACTION_UP, deviceId);
					sendToJar(keyevent);
					}
				}
			}
		}}
		/*
		if(!touchMapped)
		{
			int z = ux;
			int rz = uy;
			RawEvent keyevent;;
			if(z > 200 && JnsIMECoreService.keyMap.containsKey(JoyStickTypeF.BUTTON_ZP_SCANCODE)&& 
					((System.currentTimeMillis() - last_right_press_time) > 200))
			{
				keyevent = new RawEvent(JnsIMECoreService.keyMap.get(JoyStickTypeF.BUTTON_ZP_SCANCODE),
						JoyStickTypeF.BUTTON_ZP_SCANCODE, KeyEvent.ACTION_DOWN);
				sendToJar(keyString(keyevent));

				keyevent.value = KeyEvent.ACTION_UP;
				sendToJar(keyString(keyevent));

				last_right_press_time = System.currentTimeMillis();
			}
			if(z < 50 && JnsIMECoreService.keyMap.containsKey(JoyStickTypeF.BUTTON_ZI_SCANCODE)&& 
					((System.currentTimeMillis() - last_right_press_time) > 200))
			{
				keyevent = new RawEvent(JnsIMECoreService.keyMap.get(JoyStickTypeF.BUTTON_ZI_SCANCODE),
						JoyStickTypeF.BUTTON_ZI_SCANCODE, KeyEvent.ACTION_DOWN);
				sendToJar(keyString(keyevent));

				keyevent.value = KeyEvent.ACTION_UP;
				sendToJar(keyString(keyevent));

				last_right_press_time = System.currentTimeMillis();
			}
			if(rz > 200 && JnsIMECoreService.keyMap.containsKey(JoyStickTypeF.BUTTON_RZI_SCANCODE)&& 
					((System.currentTimeMillis() - last_right_press_time) > 200))
			{
				keyevent = new RawEvent(JnsIMECoreService.keyMap.get(JoyStickTypeF.BUTTON_RZI_SCANCODE),
						JoyStickTypeF.BUTTON_RZI_SCANCODE, KeyEvent.ACTION_DOWN);
				sendToJar(keyString(keyevent));

				keyevent.value = KeyEvent.ACTION_UP;
				sendToJar(keyString(keyevent));

				last_right_press_time = System.currentTimeMillis();
			}
			if(rz < 50 && JnsIMECoreService.keyMap.containsKey(JoyStickTypeF.BUTTON_RZP_SCANCODE)&& 
					((System.currentTimeMillis() - last_right_press_time) > 200))
			{
				keyevent = new RawEvent(JnsIMECoreService.keyMap.get(JoyStickTypeF.BUTTON_RZP_SCANCODE),
						JoyStickTypeF.BUTTON_RZP_SCANCODE, KeyEvent.ACTION_DOWN);
				sendToJar(keyString(keyevent));

				keyevent.value = KeyEvent.ACTION_UP;
				sendToJar(keyString(keyevent));

				last_right_press_time = System.currentTimeMillis();
			}
		}
		 */
	}
	/**
	 * 澶勭悊鎿嶆帶鍣ㄥ乏鎽囨潌鐨勬暟鎹紝
	 * 
	 * <p>澶勭悊瀹屾垚鍚庝細璺熸牴鎹厤缃枃浠剁洿鎺ュ彂閫佸埌jnsinput鎴栬�鏄敞鍏ュ埌搴旂敤
	 * 
	 * @author Steven.xu
	 * 
	 * @param i 鎿嶆帶鐫�憞鏉嗙殑妯悜渚垮疁閲忥紝-127 ~ 127
	 * @param j 鎿嶆帶鍣ㄦ憞鏉嗙殑绾靛悜鍋忕Щ閲�銆�127 ~ 127
	 * @param deviceId 鎿嶆帶鍣ㄥ湪device涓殑id,绋嬪簭涓病鏈夊幓鑾峰彇鍙互鐩存帴杈�
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
					//Log.d(TAG, "r="+bp.posR+", postype="+bp.posType);
					double sin = calcSinA(ux, uy, JnsIMEPosition.TYPE_LEFT_JOYSTICK);
					//						 double y = bp.posR * sin;
					//						 double x = Math.sqrt(Math.pow(bp.posR, 2) - Math.pow(y, 2));
					double touchR1 = (bp.posR/this.joystickR) * this.leftJoystickCurrentR;
					//	 Log.e(TAG, "touchR1 = " + touchR1 + " bp.posR" + bp.posR + " joystickR = " + joystickR + " leftJoystickCurrentR = " + leftJoystickCurrentR);
					double y = touchR1 * sin;
					double x = Math.sqrt(Math.pow(touchR1, 2) - Math.pow(y, 2));
					float rawX = 0.0f;
					float rawY = 0.0f;
					//Log.d(TAG, "ox ="+x+",ux="+ux+",oy="+y+",uy="+uy);
					if (ux < ox && uy < oy) {  //氓聺聬忙聽鈥∶铰疵ぢ概犆ヂ嵟犆┢捖♀�氓路娄
						rawX = bp.posX - (float)x;
						rawY = bp.posY - (float)y;
						leftMotionKey = true;
						//Log.d(TAG, "axis positive left part");
					} else if (ux > ox && uy < oy) {  //氓聺聬忙聽鈥∶铰疵ぢ概犆ヂ嵟犆┢捖♀�氓聫鲁
						rawX = bp.posX + (float) x;
						rawY = bp.posY - (float) y;
						leftMotionKey = true;
						//Log.d(TAG, "axis positive right part");
					} else if (ux < ox && uy > oy) { //氓聺聬忙聽鈥∶铰疵ぢ糕�氓聧艩茅茠篓莽拧鈥灻ヂ仿�
						rawX = bp.posX  - (float) x;
						rawY = bp.posY + (float) y;
						leftMotionKey = true;
						//Log.d(TAG, "axis negtive left part");
					} else if (ux > ox && uy > oy) { //氓聺聬忙聽鈥∶铰疵ぢ糕�氓聧艩茅茠篓莽拧鈥灻ヂ徛�
						rawX = bp.posX + (float) x;
						rawY = bp.posY + (float) y;
						leftMotionKey = true;
						//Log.d(TAG, "axis negtiveleft part");
					} else if (ux == ox && uy < oy) { //Y猫陆麓氓聫藴氓艗?
						rawX = bp.posX;
						rawY = bp.posY - (float)y;
						leftMotionKey = true;
						//Log.d(TAG, "axis Y < 0x7f");
					} else if (ux == ox && uy > oy) { //Y猫陆麓氓聫藴氓艗?
						rawX = bp.posX;
						rawY = bp.posY + (float) y;
						leftMotionKey = true;
						//Log.d(TAG, "axis Y > 0x7f");
					} else if (ux < ox && uy == oy) { //X猫陆麓氓聫藴氓艗?
						rawX = bp.posX - (float)x;
						rawY = bp.posY;
						leftMotionKey = true;
						//Log.d(TAG, "axis X < 0x7f");
					} else if (ux > ox && uy == oy) { //X猫陆麓氓聫藴氓艗?
						rawX = bp.posX + (float) x;
						rawY = bp.posY;
						leftMotionKey = true;
						//Log.d(TAG, "axis X  > 0x7f");
					} else if (ux == ox && uy == oy && leftMotionKey) {
						//Log.e(TAG, "left joystick you release map");
						sendToJar(posString(bp.posX, bp.posY, JoyStickTypeF.LEFT_JOYSTICK_TAG, MotionEvent.ACTION_MOVE));

						sendToJar(posString(bp.posX, bp.posY, JoyStickTypeF.LEFT_JOYSTICK_TAG, MotionEvent.ACTION_UP));

						leftMotionKey = false;
						LeftJoystickPresed = false;
					}

					//Log.d(TAG, "leftMotionKey="+leftMotionKey);

					if (leftMotionKey) 
					{
						//Log.d(TAG, "LeftJoystickPresed="+LeftJoystickPresed);
						if(!LeftJoystickPresed)
						{	
							//pw.print(posString(bp.posX, bp.posY, JoyStickTypeF.LEFT_JOYSTICK_TAG, MotionEvent.ACTION_DOWN));
							//pw.flush();

							//	socket.getOutputStream().write(posString(bp.posX, bp.posY, JoyStickTypeF.LEFT_JOYSTICK_TAG, MotionEvent.ACTION_DOWN).getBytes());
							sendToJar((posString(bp.posX, bp.posY, JoyStickTypeF.LEFT_JOYSTICK_TAG, MotionEvent.ACTION_DOWN)));

							LeftJoystickPresed = true;
						}
						
						if(LeftJoystickPresed)
						{
							if((rawX != leftJoystickCurrentPosX) || (rawY != leftJoystickCurrentPosY))
								
								if(System.currentTimeMillis() - last_left_press_time > STICK_MOVE_IRQ_TIME)
								{	
									sendToJar(posString(rawX, rawY, JoyStickTypeF.LEFT_JOYSTICK_TAG, MotionEvent.ACTION_MOVE));

									last_left_press_time = System.currentTimeMillis();
								}
						}

					}
					leftJoystickCurrentPosX = rawX;
					leftJoystickCurrentPosY = rawY;
				}
			}
		}
		//Log.d(TAG,"z="+ux+", y="+uy);
		if(!touchMapped)
		{
			int x = ux;
			int y = uy;
			RawEvent keyevent;
			// 灏嗗彸鎽囨潌杞寲鎴愭寜閿槧灏�

			// 鎽囨潌涓婄Щ
			if(JnsIMECoreService.keyMap.containsKey(JoyStickTypeF.BUTTON_XP_SCANCODE))
			{	
				if(x > 200)
				{	
					if(!joy_xp_pressed)
					{	
						joy_xp_pressed = true;
						keyevent = new RawEvent(JnsIMECoreService.keyMap.get(JoyStickTypeF.BUTTON_XP_SCANCODE),
								JoyStickTypeF.BUTTON_XP_SCANCODE, KeyEvent.ACTION_DOWN, deviceId);
					sendToJar(keyevent);

				}
			}
			else 
			{
				if(joy_xp_pressed)
				{
					joy_xp_pressed=false;
						keyevent = new RawEvent(JnsIMECoreService.keyMap.get(JoyStickTypeF.BUTTON_XP_SCANCODE),
								JoyStickTypeF.BUTTON_XP_SCANCODE, KeyEvent.ACTION_UP, deviceId);
					sendToJar(keyevent);
					}
				}
			}

			// 鎽囨潌涓嬬Щ
			if(JnsIMECoreService.keyMap.containsKey(JoyStickTypeF.BUTTON_XI_SCANCODE))
			{
				if(x < 50)
				{
					if(!joy_xi_pressed)
					{	
						this.joy_xi_pressed = true;
						keyevent = new RawEvent(JnsIMECoreService.keyMap.get(JoyStickTypeF.BUTTON_XI_SCANCODE),
								JoyStickTypeF.BUTTON_XI_SCANCODE, KeyEvent.ACTION_DOWN, deviceId);
					sendToJar(keyevent);

				}
			}
			else
			{
				if(joy_xi_pressed)
				{	
					this.joy_xi_pressed = false;
						keyevent = new RawEvent(JnsIMECoreService.keyMap.get(JoyStickTypeF.BUTTON_XI_SCANCODE),
								JoyStickTypeF.BUTTON_XI_SCANCODE, KeyEvent.ACTION_UP, deviceId);
					sendToJar(keyevent);
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
					sendToJar(keyevent);

				}
			}
			else
			{
				if(joy_yi_pressed)
				{	
					joy_yi_pressed = false;
						keyevent = new RawEvent(JnsIMECoreService.keyMap.get(JoyStickTypeF.BUTTON_YI_SCANCODE),
								JoyStickTypeF.BUTTON_YI_SCANCODE, KeyEvent.ACTION_UP, deviceId);
					sendToJar(keyevent);
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
					sendToJar(keyevent);

				}
			}
			else
			{
				if(joy_yp_pressed)
				{
					joy_yp_pressed = false;

						keyevent = new RawEvent(JnsIMECoreService.keyMap.get(JoyStickTypeF.BUTTON_YP_SCANCODE),
								JoyStickTypeF.BUTTON_YP_SCANCODE, KeyEvent.ACTION_UP, deviceId);
					sendToJar(keyevent);
					}
				}
			}
		}
		/*
		if(!touchMapped)
		{
			int x = ux;
			int y = uy;
			RawEvent keyevent;;
			if(x > 200 && JnsIMECoreService.keyMap.containsKey(JoyStickTypeF.BUTTON_XP_SCANCODE)&& 
					((System.currentTimeMillis() - last_left_press_time) > 200))
			{
				keyevent = new RawEvent(JnsIMECoreService.keyMap.get(JoyStickTypeF.BUTTON_XP_SCANCODE),
						JoyStickTypeF.BUTTON_XP_SCANCODE, KeyEvent.ACTION_DOWN);
				sendToJar(keyString(keyevent));

				keyevent.value = KeyEvent.ACTION_UP;
				sendToJar(keyString(keyevent));

				last_left_press_time = System.currentTimeMillis();
			}
			if(x < 50 && JnsIMECoreService.keyMap.containsKey(JoyStickTypeF.BUTTON_XI_SCANCODE)&& 
					((System.currentTimeMillis() - last_left_press_time) > 200))
			{
				keyevent = new RawEvent(JnsIMECoreService.keyMap.get(JoyStickTypeF.BUTTON_XI_SCANCODE),
						JoyStickTypeF.BUTTON_XI_SCANCODE, KeyEvent.ACTION_DOWN);
				sendToJar(keyString(keyevent));

				keyevent.value = KeyEvent.ACTION_UP;
				sendToJar(keyString(keyevent));

				last_left_press_time = System.currentTimeMillis();
			}
			if(y > 200 && JnsIMECoreService.keyMap.containsKey(JoyStickTypeF.BUTTON_YI_SCANCODE)&& 
					((System.currentTimeMillis() - last_left_press_time) > 200))
			{
				keyevent = new RawEvent(JnsIMECoreService.keyMap.get(JoyStickTypeF.BUTTON_YI_SCANCODE),
						JoyStickTypeF.BUTTON_YI_SCANCODE, KeyEvent.ACTION_DOWN);
				sendToJar(keyString(keyevent));

				keyevent.value = KeyEvent.ACTION_UP;
				sendToJar(keyString(keyevent));

				last_left_press_time = System.currentTimeMillis();
			}
			if(y < 50 && JnsIMECoreService.keyMap.containsKey(JoyStickTypeF.BUTTON_YP_SCANCODE)&& 
					((System.currentTimeMillis() - last_left_press_time) >200))
			{
				keyevent = new RawEvent(JnsIMECoreService.keyMap.get(JoyStickTypeF.BUTTON_YP_SCANCODE),
						JoyStickTypeF.BUTTON_YP_SCANCODE, KeyEvent.ACTION_DOWN);
				sendToJar(keyString(keyevent));

				keyevent.value = KeyEvent.ACTION_UP;
				sendToJar(keyString(keyevent));

				last_left_press_time = System.currentTimeMillis();
			}
		}
		 */
	}
	private void sendToJar(RawEvent rawevent)
	{
		if(JnsEnvInit.rooted)
		{
			pw.print(keyString(rawevent));
			pw.flush();
		}
		else
		{
			JnsIMECoreService.ime.getCurrentInputConnection().
			sendKeyEvent(new KeyEvent(rawevent.value, rawevent.keyCode));
		}
	}
	/**
	 * @param posString
	 */
	private void sendToJar(String posString)
	{
		if(JnsEnvInit.rooted)
		{
			pw.print(posString);
			pw.flush();
		}
	}

}