package com.tsb.settings;

import com.tsb.settings.crashhandler.CrashHandler;

import android.app.Application;
import android.app.TvManager;
import android.util.Log;

public class TvApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		TvManager tm = (TvManager) getSystemService(TV_SERVICE);
		
		// Set the flag in TvManagerService to indicate that the TV app has been launched.
		// Launcher would determine whether to start TV or not by this flag.
		tm.setIsFirstRunTVAPK(false);
		
		//异常处理，不需要处理时注释掉这两句即可！
		//CrashHandler crashHandler = CrashHandler.getInstance();
		//crashHandler.init(getApplicationContext());
		//Log.v("CrashHandler","init CrashHandler!!!!");
	}

}
