package com.jnselectronics.ime.bean;

import com.jnselectronics.im.hardware.JoyStickTypeF;
/**
 * Type�İ���ӳ���࣬��������scancode�İ���������
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
