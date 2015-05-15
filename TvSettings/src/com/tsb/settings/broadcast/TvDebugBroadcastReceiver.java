package com.tsb.settings.broadcast;

import android.app.CiManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;


import java.util.Set;

public class TvDebugBroadcastReceiver extends BroadcastReceiver {

	private static final String TAG = "TvBroadcastReceiver";

	public void register(Context context) {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(TvBroadcastDefs.CMD_BCT_TV_SIGNAL_STATUS);
        intentFilter.addAction(TvBroadcastDefs.CMD_BCT_TV_SOURCE_SWITCH_STATUS);
        intentFilter.addAction(TvBroadcastDefs.CMD_BCT_TV_VGA_ADJUST_STATUS);
        intentFilter.addAction(TvBroadcastDefs.CMD_BCT_TV_CHANNEL_SELECT_CHANGING);
        intentFilter.addAction(TvBroadcastDefs.CMD_BCT_TV_CHANNEL_SELECT_FINISHED);
        intentFilter.addAction(RtkBroadcast.RTK_BCT_FAC_OUTSETOK);
        intentFilter.addAction(TvBroadcastDefs.CMD_BCT_TV_3D_STATUS);
        intentFilter.addAction(TvBroadcastDefs.ACTION_TV_NO_SIGNAL);
        intentFilter.addAction(TvBroadcastDefs.ACTION_TV_DISPLAY_READY);
        intentFilter.addAction(TvBroadcastDefs.ACTION_TV_MEDIA_MESSAGE);
        intentFilter.addAction(TvBroadcastDefs.CMD_BCT_TV_MUTE_CHANGE);
        intentFilter.addAction(TvBroadcastDefs.CMD_BCT_TV_VOLUME_CHANGE);
        intentFilter.addAction(TvBroadcastDefs.RTK_BCT_SEND_HOTKEYS);
        intentFilter.addAction(TvBroadcastDefs.CMD_BCT_TV_EXT1_SIGNAL_CHANGE);
        intentFilter.addAction("android.intent.action.POWER_DOWN");
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        intentFilter.addAction(CiManager.CI_EVENT_BROADCAST);
        context.registerReceiver(this, intentFilter);
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Bundle extras = intent.getExtras();
		
		// Log intents
		if (extras != null) {
			Set<String> keys = extras.keySet();
			StringBuilder args = new StringBuilder();
			args.append('[');
			for (String key : keys) {
				args.append(key);
				args.append('=');
				args.append(extras.get(key));
				args.append(',');
			}
			args.append(']');
			Log.d(TAG, String.format("onReceive(%s): %s", action, args.toString()));
		} else {
			Log.d(TAG, "onReceive(" + action + ")");
		}
	}

}
