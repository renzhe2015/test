package com.tsb.settings.fragment.menu.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class SharedData {
	private SharedPreferences mSharedPreferences=null;
	public static SharedData sharedData=null;

	private SharedData(Context context) {
		mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public static SharedData getInst(Context context) {
		if (sharedData == null) {
			sharedData = new SharedData(context);
		}
		return sharedData;
	}

	public void saveData(String key, int value) {
		mSharedPreferences.edit().putInt(key, value).commit();
	}
	
	public void saveData(String key, boolean value) {
		mSharedPreferences.edit().putBoolean(key, value).commit();
	}

	public void saveData(String key, float value) {
		mSharedPreferences.edit().putFloat(key, value).commit();
	}
	
	public void saveData(String key, long value) {
		mSharedPreferences.edit().putLong(key, value).commit();
	}
	
	public void saveData(String key, String value) {
		mSharedPreferences.edit().putString(key, value).commit();
	}
	
	public boolean readData(String key, boolean defValue) {
		return mSharedPreferences.getBoolean(key, defValue);
	}
	
	public float readData(String key, float defValue) {
		return mSharedPreferences.getFloat(key, defValue);
	}
	
	public int readData(String key, int defValue) {
		return mSharedPreferences.getInt(key, defValue);
	}
	
	public long readData(String key, long defValue) {
		return mSharedPreferences.getLong(key, defValue);
	}
	
	public String readData(String key, String defValue) {
		return mSharedPreferences.getString(key, defValue);
	}
}
