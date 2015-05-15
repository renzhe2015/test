package com.tsb.settings.fragment;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.res.Configuration;

import com.tsb.settings.R;

public class TclDateFormat {

//	public static String getNowDateTime(Context context) {
//		//20120105	IActivityManager am = ActivityManagerNative.getDefault();
//	//	Configuration config = null;
//		/*//20120105	
//		try {
//			config = am.getConfiguration();
//		} catch (RemoteException e) {
//			e.printStackTrace();
//		}
//		*/
//		//String  formaString  = "EEEE   MM-dd";
//		//if(Locale.CHINA.equals(config.locale)){
//		String formaString  = context.getString(R.string.date_formate);
//		//}
//		return formatDate(DataReader.getCurrentDate(),formaString);
//	}

//	public static int getNowHourNum() {
//		return DataReader.getCurrentDate().getHours();
//	}
	
	public static String formatDateTime(Context context,long   datelong){
		Date date =  new Date(datelong);
		return  formatDateTime(context,date);
	} 
	
	
	public static String formatDateTime(Context context, Date curTimeDate, int day, long startHour){
		return	formatDateTime( context,SetDate(curTimeDate, day, startHour));
	} 
	
	public static String formatDateTime(Context context,Date date){
		//20120105	IActivityManager am = ActivityManagerNative.getDefault();
		Configuration config = null;
		/*//20120105	
		try {
			config = am.getConfiguration();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		*/
		//String  formaString  = "EEEE   MM-dd";
		//if(Locale.CHINA.equals(config.locale)){
		String formaString  = context.getString(R.string.date_formate);
		//}
		return formatDate(date,formaString);
	} 
	
	public static String formatGetTime(Date date){
		return formatDate(date,"HH:mm");
	} 
	
	public static String formatGetTime(long  longtime){
		Date date = new Date(longtime);
		return formatDate(date,"HH:mm");
	} 
	
	private static String formatDate(Date date,String formatString) {
		//20120105	IActivityManager am = ActivityManagerNative.getDefault();
		Configuration config = null;
		/*//20120105	
		try {
			config = am.getConfiguration();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		*/
		String nowDateTimeStr = "";
		SimpleDateFormat dateDateFormat = new SimpleDateFormat(formatString);
		if (null != config) {
			dateDateFormat = new SimpleDateFormat(formatString, config.locale);
		}
		nowDateTimeStr = dateDateFormat.format(date);
		return nowDateTimeStr;

	}

/*	public static String getWeekDayString() {
		return formatDate(DataReader.getCurrentDate(),"EEEE");
	}

	public static String getTimeString() {
		return formatDate(DataReader.getCurrentDate(),"HH:mm");
	}*/
	
	
	public static long getHourtoMsec(int hour) {
		long msec = (long) hour * 60 * 60 * 1000;
		return msec;
	}

	
	
	public static long SetDate(long curTime, int day, long startHour) {
		return curTime
				/ (1000 * 60 * 60 * 24)
				* (1000 * 60 * 60 * 24)
				+ (day * 24 + startHour + new Date(curTime).getTimezoneOffset() / 60)
				* 60 * 60 * 1000L;
	}
	
	public static long SetDate(Date curTimeDate, int day, long startHour) {
		long   curTime =  curTimeDate.getTime();
		return curTime
				/ (1000 * 60 * 60 * 24)
				* (1000 * 60 * 60 * 24)
				+ (day * 24 + startHour + curTimeDate.getTimezoneOffset() / 60)
				* 60 * 60 * 1000L;
	}
}
