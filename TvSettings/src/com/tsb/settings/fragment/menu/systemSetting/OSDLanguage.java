package com.tsb.settings.fragment.menu.systemSetting;

import java.util.Locale;

import com.tsb.settings.fragment.menu.util.Global;
import com.tsb.settings.fragment.menu.util.SharedData;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.RemoteException;
import android.util.Log;

public class OSDLanguage {
	public static void setOSDLanguage(int choose, Context context)
			throws RemoteException {
		
		IActivityManager am = ActivityManagerNative.getDefault();
		Configuration config = am.getConfiguration();
		try {
			switch (choose) {
			case Global.LOCALE_ENGLISH:
				config.locale = Locale.ENGLISH;
				break;
			case Global.LOCALE_CHINESE:
				config.locale = Locale.SIMPLIFIED_CHINESE;
				break;
			}
			config.userSetLocale = true;
			am.updateConfiguration(config);
			SharedData.getInst(context).saveData(Global.OSD_LANGUAGE, choose);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int getOSDLanguage() throws RemoteException {

		IActivityManager am = ActivityManagerNative.getDefault();
		Configuration config = am.getConfiguration();
		if ("zh".equals(config.locale.getLanguage())) {
			return Core.OSD_L.OSD_CHINISE;
		} else if ("en".equals(config.locale.getLanguage())) {
			return Core.OSD_L.OSD_ENGLISH;
		} else {
			return Core.OSD_L.OSD_ENGLISH;
		}
	}
	
	public static int getCurOsdLanguage(Context context){
		int mCurLanguage = 0;
		try {
			mCurLanguage = OSDLanguage.getOSDLanguage();
		} catch (RemoteException e) {
			mCurLanguage = SharedData.getInst(context).readData(
					Global.OSD_LANGUAGE, Core.OSD_L.OSD_CHINISE);
		}
		
		return mCurLanguage;
	}
}
