package com.tsb.settings.fragment.menu.pic;

import android.app.TvManager;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.tsb.settings.R;
import com.tsb.settings.TvManagerHelper;
import com.tsb.settings.fragment.menu.BaseMenuFragment;
import com.tsb.settings.menu.MenuItem;
import com.tsb.settings.menu.SeekBarMenuItem;
import com.tsb.settings.menu.SpinnerMenuItem;
import com.tsb.settings.menu.MenuItem.OnValueChangeListener;
import com.tsb.settings.util.FragmentUtils;
import com.tsb.settings.util.LogUtil;

import java.util.List;

public class PCSettingFragment extends BaseMenuFragment implements OnClickListener {

	protected static final String TAG = "PCSettingFragment";
	// Items
	//水平位置
	private SeekBarMenuItem mHorizationItem;
	//垂直位置
	private SeekBarMenuItem mVerticalItem;
	//采样时钟
	private SeekBarMenuItem mSamplingClock;
	//相位调整
	private SeekBarMenuItem mClockPhase;
	
	private TvManagerHelper tm;

	@Override
	protected View onInflateLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.layout_fragment_base_menu_dialog, container, false);
		return v;
	}
	
	@Override
	public void onCreateMenuItems(List<MenuItem> items) {
		tm = TvManagerHelper.getInstance(getActivity());
		Resources r = getResources();
		// 水平位置
		mHorizationItem = MenuItem.createSeekBarItem(R.string.STRING_HORIZONTAL_POSITION).setBoundary(0, 100);
		mHorizationItem.setOnValueChangeListener(new OnValueChangeListener() {
					
		@Override
		public void onValueChanged(MenuItem item, int value) {
				onChangePCSetting(tm.mTvManager, (SeekBarMenuItem) item, value);
				tm.setVgaHPosition((char) (value & 0xff));
			}
		});
		items.add(mHorizationItem);
		
		// 垂直位置
		mVerticalItem = MenuItem.createSeekBarItem(R.string.STRING_VERTICAL_POSITION).setBoundary(0, 100);
		mVerticalItem.setOnValueChangeListener(new OnValueChangeListener() {
			
			@Override
			public void onValueChanged(MenuItem item, int value) {
				onChangePCSetting(tm.mTvManager, (SeekBarMenuItem) item, value);
				tm.setVgaVPosition((char) (value & 0xff));
			}
		});
		items.add(mVerticalItem);
		
		
		// 采样时钟
		mSamplingClock = MenuItem.createSeekBarItem(R.string.STRING_SAMPLING_CLOCK).setBoundary(0, 100);
		mSamplingClock.setOnValueChangeListener(new OnValueChangeListener() {
			
			@Override
			public void onValueChanged(MenuItem item, int value) {
				onChangePCSetting(tm.mTvManager, (SeekBarMenuItem) item, value);
				tm.setVgaClock((char) (value & 0xff));
			}
		});
		items.add(mSamplingClock);
		
		// 相位调整
		mClockPhase = MenuItem.createSeekBarItem(R.string.STRING_CLOCK_PHASE).setBoundary(0, 100);
		mClockPhase.setOnValueChangeListener(new OnValueChangeListener() {
					
		@Override
		public void onValueChanged(MenuItem item, int value) {
			onChangePCSetting(tm.mTvManager, (SeekBarMenuItem) item, value);
				tm.setVgaPhase((char) (value & 0xff));
			}
		});
		items.add(mClockPhase);
		
//		updatePCSetting(tm.mTvManager);
		initPCParameters();
	}
	
	public void initPCParameters() {
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
				updatePCSetting(tm.mTvManager);
				break;

			default:
				break;
			}
		}
	};
	
	private void onChangePCSetting(TvManager tm, SeekBarMenuItem item, int value) {
		
	}
	
	private static void updateItemIfNotEqual(MenuItem base, SeekBarMenuItem item, int value) {
		if (base != item) {
			item.setCurrentProgress(value, true);
		}
	}
	
	private void updatePCSetting(TvManager tm) {
		mHorizationItem.setCurrentProgress(tm.getVgaHPosition());
		mVerticalItem.setCurrentProgress(tm.getVgaVPosition());
		mSamplingClock.setCurrentProgress(tm.getVgaClock());
		mClockPhase.setCurrentProgress(tm.getVgaPhase());
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
		View itemView = mListView.getSelectedView();
		FragmentUtils.mHandler.removeMessages(0);
		FragmentUtils.mHandler.sendEmptyMessageDelayed(0, 15000);
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
		case KeyEvent.KEYCODE_MENU:
			Intent intent = new Intent("hideMainMenuNow");
			getActivity().sendBroadcast(intent);
			return true;
		default:
			return false;
		}
	}

}
