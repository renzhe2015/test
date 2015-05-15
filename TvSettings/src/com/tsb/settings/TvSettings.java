package com.tsb.settings;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.tsb.settings.fragment.MainMenuFragment;
import com.tsb.settings.fragment.menu.adtv.AdtvAutoTuningNotifyFragment;
import com.tsb.settings.fragment.menu.adtv.AutoTuningFragment;
import com.tsb.settings.fragment.menu.net.SpeedTestFragment;
import com.tsb.settings.util.FragmentUtils;
import com.tsb.settings.util.LogUtil;

public class TvSettings extends Activity {
	public static final String STACK_MENU = "menu";
	public static final String TAG_LITE = "lite";
	public static boolean isFormTv=false;
	private boolean noChannel=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if(getIntent()!=null){
			noChannel=getIntent().getBooleanExtra("noChannel", false);
			isFormTv=getIntent().getBooleanExtra("fromTV", false);
		}
		FragmentManager fm = getFragmentManager();
		Fragment f = fm.findFragmentByTag(STACK_MENU);
		if(noChannel){ 
			Fragment fragment = Fragment.instantiate(this, AdtvAutoTuningNotifyFragment.class.getName());
			fm.beginTransaction()
			.add(android.R.id.content, fragment, TAG_LITE)
			.commit();
		}
		else if(f==null){
			f = new MainMenuFragment();
			int tab=getIntent().getIntExtra("TVMainMenu", -1);
			int networktype=getIntent().getIntExtra("NetworkType", -1);
			if(tab>=0){
				Bundle arg = new Bundle();
				arg.putInt(MainMenuFragment.ARG_TAB_INDEX, tab);
				arg.putInt(MainMenuFragment.ARG_NETWORKTYPE, networktype);
				f.setArguments(arg);
			}
			fm.beginTransaction()
			.add(android.R.id.content, f, STACK_MENU)
			.addToBackStack(STACK_MENU)
			.commit();
		}
	}
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		if(event.getAction()==KeyEvent.ACTION_DOWN&&(event.getKeyCode()==KeyEvent.KEYCODE_BACK||event.getKeyCode()==KeyEvent.KEYCODE_DPAD_LEFT)&&(FragmentUtils.getCurFragment() instanceof SpeedTestFragment))
		{
			MainMenuFragment.fromNetSetting=false;
			FragmentManager fm = MainMenuFragment.fm;
			FragmentTransaction ft = fm.beginTransaction();
			ft.detach(FragmentUtils.getCurFragment());
			ft.commit();
			findViewById(R.id.layout_third_menu).setVisibility(View.GONE);
			findViewById(MainMenuFragment.mLastSelectionId2).requestFocus();

		}
		return super.dispatchKeyEvent(event);
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		LogUtil.logD("TvSettings", "onKeyDown", true);
		FragmentUtils.mHandler.removeMessages(0);
		FragmentUtils.mHandler.sendEmptyMessageDelayed(0, 15*1000);
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		LogUtil.logD("TvSettings", "onTouchEvent", true);
		FragmentUtils.mHandler.removeMessages(0);
		FragmentUtils.mHandler.sendEmptyMessageDelayed(0, 15*1000);
		return super.onTouchEvent(event);
	}
}
