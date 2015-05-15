package com.tsb.settings.settings.view;

import java.util.Timer;
import java.util.TimerTask;

import com.tsb.settings.fragment.MainMenuFragment;
import com.tsb.settings.settings.view.Const;

import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;
import android.util.Log;

public class TaskTimer {

	public static final String TAG = "TaskTimer-vicki";
	private static int iTime = 15000;
	private static Timer timer = new Timer();
	private static boolean lock = false;

	public TaskTimer() {
	}

	public synchronized static void onTimer(Context context) {
		Log.i(TAG, "onTimer" + context.getClass() + ":" + iTime);
		Log.i("TaskTimer:", "onTimer lock:" + lock);
		if (!lock) {
			if (timer != null)
				timer.cancel();
			timer = null;
			timer = new Timer();
			final Context mContext = context;
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					if(!"on".equals(SystemProperties.get("sys.scan.state"))){
						Log.i(TAG, "Timer is finishAll ");
//						TaskManager.getInstance().finishAll();
						TaskManager.getInstance().clear();
						
						/**
						 * caoxiao add 2013/11/18 可能出现 Caused by: java.lang.IllegalStateException: Must be called from main thread of process异常
						 */
						mContext.sendBroadcast(new Intent(Const.BROADCAST_FINISH_SETTINGS));
						timer.cancel();
						timer = null;
						timer = new Timer();
					}else{
						Log.i(TAG, "scaning,not finish activity ");
					}
				}
			}, iTime);
		}
	}

	public synchronized static void setDelayTime(int time) {
		if (!lock) {
			Log.i(TAG, "setDelayTime");
			iTime = time;
		}
	}

	public synchronized static void cancel() {
		if (!lock) {
			Log.i(TAG, "cancel");
			if (timer != null)
				timer.cancel();
			timer = null;
		}
	}

	public synchronized static void reset() {
		if (!lock) {
			Log.i(TAG, "reset");
			iTime = 15000;
		}
	}

	public synchronized static void lock() {
		Log.i(TAG, "lock");
		lock = true;
	}

	public synchronized static void unlock() {
		Log.i(TAG, "unlock");
		lock = false;
	}
}
