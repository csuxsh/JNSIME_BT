package com.viaplay.ime.bean;

import com.viaplay.im.hardware.JoyStickTypeF;
/**
 * Type的按键映射类，区别在于scancode的按键的索引
 * 
 *  
 * @author Steven.xu
 *
 */
public class JnsIMETypeFKeyMap extends JnsIMEKeyMap{

	@Override
	public int getScanCode() {
		// TODO Auto-generated method stub
		return JoyStickTypeF.gamePadButoonScanCode[this.getGamPadIndex()/JoyStickTypeF.DISPLAY_ROW][this.getGamPadIndex()%JoyStickTypeF.DISPLAY_ROW];
	}

}
