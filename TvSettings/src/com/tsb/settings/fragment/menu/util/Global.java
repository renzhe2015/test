package com.tsb.settings.fragment.menu.util;

import android.view.KeyEvent;

public class Global {
	public static String OSD_LANGUAGE="OSD_LANGUAGE";  //key of language
	public static String SETUP_WIZARD = "SETUP_WIZARD"; //key of setup wizard
	public static String PICTURE_MODE = "PICTURE_MODE"; //key of picture mode
	public static String SOUND_SCENE = "SOUND_SCENE"; //key of sound scene
	public static String USER_BACKLIGHT = "USER_BACKLIGHT"; //key of sound scene
	public static String PIP_POSITION = "PIP_POSITION";        //key of pip
	public static final int LOCALE_CHINESE=0;  //chinese
	public static final int LOCALE_ENGLISH=1; //english
	public static final int SPECIAL_NO_SIGNAL = 0;  //nav no signal
	public static final int SPECIAL_PLEASE_SCAN = 1;  //nav please scan
	public static final int DIANDU_AUTO=0;  //AUTO
	public static final int DIANDU_MANU=1; //MANU
	public static final int DIANDU_DISABLE=2; //DISABLE
	public static final int DIANDU_PATH0=0;  //AUTO
	public static final int DIANDU_PATH1=1; //MANU
	
	public static final String BROADCAST_SECOND_ONRESUME = "second_onresume";
	public static class TYPE {
		public static int Pic = 0;
		public static int Sound = 1;
		public static int Channel = 2;
		public static int App = 3;
		public static int Network = 4;
		public static int System = 5;
	}
	
	public static String mCurClientType = "";

	public static class CLIENTTYPE {
		public static final String RTK_M90 = "TCL-CN-RT95-M90-UDM";
	}
	
//	 public static boolean isWifiPowerEnable() {
//		 int projectId = TDeviceInfo.getInstance().getProjectID();
//		 boolean status = false;
//		 if (TDeviceInfo.getInstance().getWifiStatus(projectId) == 1) {
//			 status = true;
//		 }
//		 return false;
//	 }
	
}
