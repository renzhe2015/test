package com.tsb.settings.util;

import android.util.Log;

public class LogUtil {
	private static String TAG = "TVSetting";
	
	
	public static void logD(String info, boolean enable) {
		logD("", info, enable);
	}
	
	public static void logD(String cls, String info, boolean enable) {
		if (enable) {
			Log.d(TAG, "[" + cls + "] " + info);
		}
	}
}
