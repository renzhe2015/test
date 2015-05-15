package com.tsb.settings.fragment.menu.pic;

import java.util.List;

import android.app.Instrumentation;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.tsb.settings.R;
import com.tsb.settings.TvManagerHelper;
import com.tsb.settings.fragment.MainMenuFragment;
import com.tsb.settings.fragment.menu.BaseMenuFragment;
import com.tsb.settings.menu.MenuItem;
import com.tsb.settings.menu.SeekBarMenuItem;
import com.tsb.settings.menu.SpinnerMenuItem;
import com.tsb.settings.menu.MenuItem.OnValueChangeListener;
import com.tsb.settings.util.FragmentUtils;
import com.tsb.settings.util.LogUtil;

public class SoundAdvanceSettingFragment extends BaseMenuFragment implements OnClickListener {
	private static String TAG = "SoundAdvanceSettingFragment";
	// Items
	//平衡
	private SeekBarMenuItem mBalanceItem;
	//低音
	private SeekBarMenuItem mBassItem;
	//高音
	private SeekBarMenuItem mTrebleClock;
	//声道
	private SpinnerMenuItem mSoundTrack;
	
	private int sourceId;
	
	private TvManagerHelper tm;
	
	private final int[] VALUE_TRACK = {
		TvManagerHelper.TV_STRING_CHANNEL_LEFT,
		TvManagerHelper.TV_STRING_CHANNEL_RIGHT,
		TvManagerHelper.TV_STRING_STEREO
	};

	@Override
	protected View onInflateLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.layout_fragment_base_menu_dialog, container, false);
		return v;
	}
	
	@Override
	public void onCreateMenuItems(List<MenuItem> items) {
		tm = TvManagerHelper.getInstance(getActivity());
		Resources r = getResources();
		sourceId = tm.getInputSource();
		// 平衡
		mBalanceItem = MenuItem.createSeekBarItem(R.string.STRING_S_BALANCE).setBoundary(-50, 50);
		mBalanceItem.setOnValueChangeListener(new OnValueChangeListener() {
					
		@Override
		public void onValueChanged(MenuItem item, int value) {
				tm.SetAudioMode(TvManagerHelper.TV_STRING_AUTO_USER);
				tm.setAudioBalancing(value);
//				updateSoundSetting(tm);
			}
		});
		items.add(mBalanceItem);
		
		// 低音
		mBassItem = MenuItem.createSeekBarItem(R.string.STRING_BASS).setBoundary(-50, 50);
		mBassItem.setOnValueChangeListener(new OnValueChangeListener() {
			
			@Override
			public void onValueChanged(MenuItem item, int value) {
				LogUtil.logD(TAG, "setbass value=" + value, true);
				tm.SetAudioMode(TvManagerHelper.TV_STRING_AUTO_USER);
				tm.Setbass(value);
//				updateSoundSetting(tm);
			}
		});
		items.add(mBassItem);
		
		
		// 高音
		mTrebleClock = MenuItem.createSeekBarItem(R.string.STRING_TREBLE).setBoundary(-50, 50);
		mTrebleClock.setOnValueChangeListener(new OnValueChangeListener() {
			
			@Override
			public void onValueChanged(MenuItem item, int value) {
				tm.SetAudioMode(TvManagerHelper.TV_STRING_AUTO_USER);
				tm.SetTreble(value);
//				updateSoundSetting(tm);
			}
		});
		items.add(mTrebleClock);
		
		 //声道
		if(TvManagerHelper.isDtvSource(sourceId)){
			mSoundTrack = MenuItem.createSpinnerItem(R.string.STRING_SOUND_TRACK);
			mSoundTrack.setSpinnerOptionsByArray(r.getStringArray(R.array.sound_track), VALUE_TRACK);
			mSoundTrack.setOnValueChangeListener(new OnValueChangeListener() {
			
				@Override
				public void onValueChanged(MenuItem item, int value) {
					tm.SetAudioMode(TvManagerHelper.TV_STRING_AUTO_USER);
					tm.SetAudioChannelSwap(value);
//					updateSoundSetting(tm);
				}
			});
			items.add(mSoundTrack);
		}
		
//		updateSoundSetting(tm);
		initSoundParameters();
	}
	
	public void initSoundParameters() {
		new Thread() {
			@Override
			public void run() {
				LogUtil.logD(TAG, "initPicParameters thread", true);
				mInitHandler.sendEmptyMessageDelayed(REFRESH_ITEM, 50);
			}
		}.start();
	}
	
	public static final int REFRESH_ITEM = 1;
	Handler mInitHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REFRESH_ITEM:
				if (!isVisible()) {
					return;
				}
				LogUtil.logD(TAG, "REFRESH_ITEM start updatePictureSetting", false);
				updateSoundSetting(tm);
				break;

			default:
				break;
			}
		}
	};
	
	private void updateSoundSetting(TvManagerHelper tm) {
		mBalanceItem.setCurrentProgress(tm.mTvManager.getBalance());
		LogUtil.logD(TAG, "update sound setting,bass value=" + tm.Getbass(), true);
		mBassItem.setCurrentProgress(tm.Getbass());
		mTrebleClock.setCurrentProgress(tm.GetTreble());
		if(TvManagerHelper.isDtvSource(sourceId))
			mSoundTrack.setCurrentValue(tm.mTvManager.getAudioChannelSwap());
	}

	@Override
	public int getTitle() {
		return R.string.STRING_PC_SETTINGS;
	}

	@Override
	public void onClick(View v) {
	    FragmentUtils.popBackSubFragment(this);
	}
	
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		FragmentUtils.mHandler.removeMessages(0);
		FragmentUtils.mHandler.sendEmptyMessageDelayed(0, 15000);
		View itemView = mListView.getSelectedView();
		String deviceName = InputDevice.getDevice(event.getDeviceId()).getName();
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_LEFT:
			if (itemView != null && event.getAction() == KeyEvent.ACTION_DOWN) {
				MenuItem item = (MenuItem) itemView.getTag();
				item.selectPrev();
				mListView.invalidateViews();
			}
			return true;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			if (itemView != null && event.getAction() == KeyEvent.ACTION_DOWN) {
				MenuItem item = (MenuItem) itemView.getTag();
				item.selectNext();
				mListView.invalidateViews();
			}
			return true;
		case KeyEvent.KEYCODE_DPAD_UP:
			return super.onKey(v, keyCode, event);
		case KeyEvent.KEYCODE_DPAD_DOWN:
			return super.onKey(v, keyCode, event);
		case KeyEvent.KEYCODE_MENU:
			return super.onKey(v, keyCode, event);
		case KeyEvent.KEYCODE_BACK:
			FragmentUtils.popBackSubFragment(this);
			return true;		
		case KeyEvent.KEYCODE_TV_INPUT:
			if("Smart_TV_Keypad".equals(deviceName)){
				final KeyEvent tEvent1 = new KeyEvent( event.getAction(), KeyEvent.KEYCODE_DPAD_CENTER );
				new Thread(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						//super.run();
						try{
						    Instrumentation inst = new Instrumentation();
						    Log.d("lly", "press Input key");
						    inst.sendKeyDownUpSync( tEvent1.getKeyCode());
						        
						}catch(Exception e ){
						    Log.e("TAG","Exception when sendKeyDownUpSync : " + e.toString() );
						}
					}
						
				}.start();
				return true;
			}else{
				return false;
			}
		default:
			return false;
		}
	}
	
	private void updateBassSetting(TvManagerHelper tm) {
		int tempTreble = tm.GetTreble();
		tm.SetAudioMode(TvManagerHelper.TV_STRING_AUTO_USER);
		mBassItem.setCurrentProgress(tm.Getbass());
		tm.SetTreble(tempTreble);
		mTrebleClock.setCurrentProgress(tempTreble);
	}
	
	private void updateTrebleSetting(TvManagerHelper tm) {
		int tempBass = tm.Getbass();
		tm.SetAudioMode(TvManagerHelper.TV_STRING_AUTO_USER);
		mTrebleClock.setCurrentProgress(tm.GetTreble());
		tm.Setbass(tempBass);
		mBassItem.setCurrentProgress(tempBass);
	}

}
