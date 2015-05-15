package com.tsb.settings.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.app.Activity;
import android.content.Context;
import android.text.Selection;
import android.text.Spannable;
import android.widget.EditText;
import android.widget.Toast;
import java.util.Date;
import android.text.format.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.util.DisplayMetrics;

public class Util {
	private static String TAG = "Settings-Utils";
	public static String replaceBlank(String str) {  
        String dest = "";  
        if (str!=null) {  
                Pattern p = Pattern.compile("\\s*|\t|\r|\n");  
                Matcher m = p.matcher(str);  
                dest = m.replaceAll("");  
        }  
        return dest;  
	}
	public static void showToast(final Context context,
			String info) {
		// TODO Auto-generated method stub
		Toast.makeText(context, info, 500).show();
	} 

	public static void showToast2S(final Context context,
			String info) {
		// TODO Auto-generated method stub
		Toast.makeText(context, info, 2000).show();
	} 
	
	public static void setEditTextEnd(EditText editText) {
		CharSequence text = editText.getText();
		if (text instanceof Spannable) {
			Spannable spanText = (Spannable) text;
			Selection.setSelection(spanText, text.length());
		}
	}

	
	public static Date getCurrentDate()
	{

		Time stTime = new Time();
		stTime.setToNow();
		// stTime.normalize(true);
		long curTime = stTime.toMillis(true);
		Date mDate = new Date(curTime);
		return mDate;
	}

   	public static boolean isOSDFullHD(Activity context)
	{

		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);

		if (dm.heightPixels == 720)
		{
			return false;
		}
		else if (dm.heightPixels == 1080)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
   public static String formateTime(long timeMills)
   {
	   SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
	   String dateStr = formatter.format(timeMills / 1000);
	  // Log.v("Util Formate ", "dateStr:" + dateStr);
	   return dateStr;
	   /*
		* Time startTime = new Time(); startTime.set(timeMills);
		* //startTime.normalize(true);
		* 
		* SimpleDateFormat formatter = new SimpleDateFormat("HH:mm"); Date
		* curDate = new
		* Date(startTime.toMillis(true)-TVRootApp.offsetTimeInMs); String
		* dateStr = formatter.format(curDate); return dateStr;
		*/
   }
  	public static String changTime(long time){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
		return simpleDateFormat.format(calendar.getTime());
	}
	
}
