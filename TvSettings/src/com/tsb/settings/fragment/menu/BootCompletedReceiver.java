package com.tsb.settings.fragment.menu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;

public class BootCompletedReceiver extends BroadcastReceiver{
	SharedPreferences settings;
	static Bundle extras;
	/*
	 * 接收开机启动完成广播，判断儿童模式状态决定是否需要启动儿童模式服务
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		//		Log.d("ChildrenMode", "====>System boot completed!");
		//		sharedPref = arg0.getSharedPreferences("cm", Context.MODE_WORLD_READABLE);
		//		boolean status = sharedPref.getBoolean("Status", false);
		//		
		//		if(true == status){
		//			extras = new Bundle();
		//			extras.putBoolean("BootCompleted", true);	
		//			Intent intent = new Intent(arg0, ChildrenModeService.class);
		//			intent.putExtras(extras);
		//			arg0.startService(intent);
		//		}
//		Toast.makeText(context, "启动完毕", Toast.LENGTH_SHORT).show();
		settings = context.getSharedPreferences("powerDownTimeFile",Context.MODE_PRIVATE);
		Editor editor = settings.edit();
		editor.putInt("powerDownTime", -1);
		editor.putInt("position", 0);
		editor.commit();
	}

}
