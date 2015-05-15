package com.tsb.settings.fragment.menu.pic;

import java.util.List;

import android.app.FragmentManager;
import android.app.TvManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tsb.settings.R;
import com.tsb.settings.TvManagerHelper;
import com.tsb.settings.broadcast.TvBroadcastDefs;
import com.tsb.settings.fragment.MainMenuFragment;
import com.tsb.settings.fragment.PopBottomFragment;
import com.tsb.settings.fragment.menu.BaseMenuFragment;
import com.tsb.settings.menu.MenuItem;
import com.tsb.settings.menu.SeekBarMenuItem;
import com.tsb.settings.menu.SpinnerMenuItem;
import com.tsb.settings.menu.SpinnerMenuItemExt;
import com.tsb.settings.menu.MenuItem.OnValueChangeListener;
import com.tsb.settings.util.FragmentUtils;
import com.tsb.settings.util.LogUtil;

public class PictureSettingFragment extends BaseMenuFragment implements OnClickListener {

	private static final int[] VALUE_MODES = {
		TvManager.PICTURE_MODE_VIVID,
		TvManager.PICTURE_MODE_STD,
		TvManager.PICTURE_MODE_GENTLE,
		TvManager.PICTURE_MODE_USER
	};
	
	private static final int[] VALUE_COLOR_TEMP = {
		TvManager.SLR_COLORTEMP_WARM,
		TvManager.SLR_COLORTEMP_NORMAL,
		TvManager.SLR_COLORTEMP_COOL,
	};	
	
	// Items
	private SpinnerMenuItem mItemMode;

	private SeekBarMenuItem mItemContrast;

	private SeekBarMenuItem mItemBright;

	private SeekBarMenuItem mItemColor;

	private SeekBarMenuItem mItemTint;

	private SeekBarMenuItem mItemSharpness;
	
	private SpinnerMenuItemExt mColorTempItem;
	
	private Boolean isNTSC=true;
	private int sourceId;
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
		// Picture Mode
//		mItemMode = MenuItem.createSpinnerItem(R.string.STRING_PICTUR_MODE);
//		mItemMode.setSpinnerOptionsByArray(r.getStringArray(R.array.setting_pic_modes), VALUE_MODES);
//		mItemMode.setCurrentValue(tm.mTvManager.getPictureMode());
//		mItemMode.setOnValueChangeListener(new OnValueChangeListener() {
//
//			@Override
//			public void onValueChanged(MenuItem item, int value) {
//				tm.setPictureMode(value);
//				updatePictureSetting(tm);
//			}
//
//		});
//		items.add(mItemMode);
		
		// Brightness
		mItemBright = MenuItem.createSeekBarItem(R.string.STRING_BRIGHTNESS).setBoundary(0, 100);
		mItemBright.setOnValueChangeListener(new OnValueChangeListener() {
					
		@Override
		public void onValueChanged(MenuItem item, int value) {
//			onChangePictureSetting(tm, (SeekBarMenuItem) item, value);
			tm.SetBrightness(value);
							
//				tm.setPictureMode(TvManagerHelper.PICTURE_MODE_USER);
			}
		});
		items.add(mItemBright);
		
		// Contrast
		mItemContrast = MenuItem.createSeekBarItem(R.string.STRING_CONTRAST).setBoundary(0, 100);
		mItemContrast.setOnValueChangeListener(new OnValueChangeListener() {
			
			@Override
			public void onValueChanged(MenuItem item, int value) {
//				onChangePictureSetting(tm, (SeekBarMenuItem) item, value);
				tm.SetContrast(value);
								
//				tm.setPictureMode(TvManagerHelper.PICTURE_MODE_USER);
			}
		});
		items.add(mItemContrast);
		
		
		// Color 彩色
		mItemColor = MenuItem.createSeekBarItem(R.string.STRING_COLOUR).setBoundary(0, 100);
		mItemColor.setOnValueChangeListener(new OnValueChangeListener() {
			
			@Override
			public void onValueChanged(MenuItem item, int value) {
//				onChangePictureSetting(tm, (SeekBarMenuItem) item, value);
				tm.SetSaturation(value);
								
//				tm.setPictureMode(TvManagerHelper.PICTURE_MODE_USER);
			}
		});
		items.add(mItemColor);
		
		// Sharpness 锐度
		mItemSharpness = MenuItem.createSeekBarItem(R.string.STRING_SHARPNESS).setBoundary(0, 100);
		mItemSharpness.setOnValueChangeListener(new OnValueChangeListener() {
					
		@Override
		public void onValueChanged(MenuItem item, int value) {
//			onChangePictureSetting(tm, (SeekBarMenuItem) item, value);	
			tm.SetSharpness(true, value);
							
//				tm.setPictureMode(TvManagerHelper.PICTURE_MODE_USER);
			}
		});
//		items.add(mItemSharpness);
		
		// Tint色调
		sourceId=tm.getInputSource();
		//*****add by huangfh@tcl.com 14-08-22
		if(!TvManagerHelper.isPCSource(sourceId)){
			items.add(mItemSharpness);
		}
		//*****end

		if((TvManagerHelper.isAtvSource(sourceId)&&
				tm.getCurrentAtvColorStd()==TvManager.RT_COLOR_STD_NTSC)
			||(TvManagerHelper.isAV1Source(sourceId)&&
				(tm.mTvManager.GetIsNtscVideo()== true)))
		{
			
			mItemTint = MenuItem.createSeekBarItem(R.string.STRING_TINT).setBoundary(0, 100);
			mItemTint.setOnValueChangeListener(new OnValueChangeListener() {
				
				@Override
				public void onValueChanged(MenuItem item, int value) {
//					onChangePictureSetting(tm, (SeekBarMenuItem) item, value);
					tm.SetHue(value);
					
//					tm.setPictureMode(TvManagerHelper.PICTURE_MODE_USER);
				}
			});
			items.add(mItemTint);
			isNTSC=true;
		}
		else{
			isNTSC=false;
		}
		
		// ColorTemp 色温
		mColorTempItem = MenuItem.createSpinnerItemExt(R.string.STRING_COLOUR_TEMP);
		mColorTempItem.setSpinnerOptionsByArray(r.getStringArray(R.array.color_temperature), VALUE_COLOR_TEMP);
		mColorTempItem.setCurrentValue(tm.GetColorTempMode(), true);
		mColorTempItem.setOnValueChangeListener(new OnValueChangeListener() {
			
			@Override
			public void onValueChanged(MenuItem item, int value) {
//				onChangePictureUserMode(tm);
				tm.setColorTempMode(value);
//				onColorTempModeChange(tm);					
			}
		});
		items.add(mColorTempItem);		
		initPicParameters();
		
		
	}
	
	public void initPicParameters() {
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
				updatePictureSetting(tm);
				break;

			default:
				break;
			}
		}
	};
	
	private void onColorTempModeChange(TvManagerHelper tm){
		mItemBright.setCurrentProgress(tm.GetBrightness(), true);
		mItemContrast.setCurrentProgress(tm.GetContrast(), true);
		mItemColor.setCurrentProgress(tm.GetSaturation(), true);
		mItemSharpness.setCurrentProgress(tm.GetSharpness(), true);
		if(isNTSC)
			mItemTint.setCurrentProgress(tm.GetHue(), true);
	}
	
	private void picChange2UseMode( int dir){
		TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());
		if (tm.GetPictureMode() == TvManager.PICTURE_MODE_USER)
			return;
		
		int bright = tm.GetBrightness() + dir;
		int contrast = tm.GetContrast() + dir;
		int color = tm.GetSaturation() + dir;
		int tint = tm.GetHue() + dir;
		int sharpness = tm.GetSharpness() + dir;
		int ColorTempMode = tm.GetColorTempMode() + dir;	
		int backLight = tm.GetBacklight() + dir;
		
		tm.setPictureMode(TvManagerHelper.PICTURE_MODE_USER);
		tm.SetBrightness(bright);
		tm.SetContrast(contrast);
		tm.SetSaturation(color);
		if(isNTSC)
			tm.SetHue(tint);
		tm.SetSharpness(true, sharpness);
		tm.setColorTempMode(ColorTempMode);
		tm.SetBacklight(backLight);
	}
	
	private void onChangePictureSetting(TvManagerHelper tm, SeekBarMenuItem item, int value) {
		if (tm.GetPictureMode() != TvManager.PICTURE_MODE_USER) {
			// Get current values
			int bright = tm.GetBrightness();
			int contrast = tm.GetContrast();
			int backlight = tm.GetBacklight();
			int color = tm.GetSaturation();
			int tint = tm.GetHue();
			int sharpness = tm.GetSharpness();
			int ColorTempMode = tm.GetColorTempMode();
			
			// Change to user mode
			tm.setPictureMode(TvManagerHelper.PICTURE_MODE_USER);
			tm.SetBrightness(bright);
			tm.SetContrast(contrast);
			tm.SetSaturation(color);
			if(isNTSC)
				tm.SetHue(tint);
			tm.SetSharpness(true, sharpness);
			tm.setColorTempMode(ColorTempMode);
			tm.SetBacklight(backlight);
			
			//mItemMode.setCurrentValue(TvManager.PICTURE_MODE_USER, true);
			
			// Current item might be modified.
			// Restore it.
			item.setCurrentProgress(value, true);
			
			// Copy previous values to user setting
			updateItemIfNotEqual(item, mItemBright, bright);
			updateItemIfNotEqual(item, mItemContrast, contrast);
			updateItemIfNotEqual(item, mItemColor, color);
			if(isNTSC)
				updateItemIfNotEqual(item, mItemTint, tint);
			updateItemIfNotEqual(item, mItemSharpness, sharpness);
			mColorTempItem.setCurrentValue(ColorTempMode, true);
		}
	}
	
	private void onChangePictureUserMode(TvManagerHelper tm) {
		if (tm.GetPictureMode() != TvManager.PICTURE_MODE_USER) {
			// Get current values
			int bright = tm.GetBrightness();
			int contrast = tm.GetContrast();
			int color = tm.GetSaturation();
			int tint = tm.GetHue();
			int sharpness = tm.GetSharpness();
			int ColorTempMode = tm.GetColorTempMode();
			int backLight = tm.GetBacklight();
			
			// Change to user mode
			tm.setPictureMode(TvManagerHelper.PICTURE_MODE_USER);
			tm.SetBrightness(bright);
			tm.SetContrast(contrast);
			tm.SetSaturation(color);
			if(isNTSC)
				tm.SetHue(tint);
			tm.SetSharpness(true, sharpness);
			tm.setColorTempMode(ColorTempMode);
			tm.SetBacklight(backLight);
		}
	}
	
	private static void updateItemIfNotEqual(MenuItem base, SeekBarMenuItem item, int value) {
		if (base != item) {
			item.setCurrentProgress(value, true);
		}
	}
	
	private void updatePictureSetting(TvManagerHelper tm) {
		mItemBright.setCurrentProgress(tm.GetBrightness());
		mItemContrast.setCurrentProgress(tm.GetContrast());
		mItemColor.setCurrentProgress(tm.GetSaturation());
		mItemSharpness.setCurrentProgress(tm.GetSharpness());
		if(isNTSC)
			mItemTint.setCurrentProgress(tm.GetHue());
	}

	@Override
	public int getTitle() {
		return R.string.STRING_PIC_SETTINGS;
	}

	@Override
	public void onClick(View v) {
	    FragmentUtils.popBackSubFragment(this);
	}
	
	int num = -1;
	// Input status
	private int mCurrentInput = -1;
	private int mCurrentDigit = 0;
	private Handler mHandler = new UiHandler();
	private static final long DELAY_SMALL = 1000;
	private static final long DELAY_LARGE = 3000;
	private static final long DELAY_DISMISS = 3000;
	private static final int MAX_DIGIT = 4;

	private static final String TAG = "PictureSetting";
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		View itemView = mListView.getSelectedView();
		String deviceName = InputDevice.getDevice(event.getDeviceId()).getName();
		if("Smart_TV_Keypad".equals(deviceName)&&keyCode == KeyEvent.KEYCODE_TV_INPUT){
			keyCode = KeyEvent.KEYCODE_DPAD_CENTER;
		}
		if (KeyEvent.ACTION_DOWN == event.getAction()) {
			FragmentUtils.mHandler.removeMessages(0);
			FragmentUtils.mHandler.sendEmptyMessageDelayed(0, 15000);
			switch (keyCode) {
			//********add by huangfh@tcl.com 08-29
			case KeyEvent.KEYCODE_DPAD_CENTER:
			case KeyEvent.KEYCODE_DPAD_LEFT:
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				switch(selection){
				case 0:
					FragmentUtils.showPopBottomFragment(getBaseFragment(), PopBottomFragment.class,null,mItemBright);
					break;
				case 1:
					FragmentUtils.showPopBottomFragment(getBaseFragment(), PopBottomFragment.class,null,mItemContrast);
					break;
				case 2:
					FragmentUtils.showPopBottomFragment(getBaseFragment(), PopBottomFragment.class,null,mItemColor);
					break;
				case 3:
					if(!TvManagerHelper.isPCSource(sourceId)){
						FragmentUtils.showPopBottomFragment(getBaseFragment(), PopBottomFragment.class,null,mItemSharpness);
					}
					else{
					FragmentUtils.showPopBottomFragment(getBaseFragment(), PopBottomFragment.class,null,mColorTempItem);
					}
					break;
				case 4:
					if(isNTSC){
						FragmentUtils.showPopBottomFragment(getBaseFragment(), PopBottomFragment.class,null,mItemTint);
					}
					else{
						FragmentUtils.showPopBottomFragment(getBaseFragment(), PopBottomFragment.class,null,mColorTempItem);
					}
					break;
				case 5:
					FragmentUtils.showPopBottomFragment(getBaseFragment(), PopBottomFragment.class,null,mColorTempItem);
					break;
				}
				return true;
			//*********end 
//				if (itemView != null && event.getAction() == KeyEvent.ACTION_DOWN) {
//					MenuItem item = (MenuItem) itemView.getTag();
////					if(mListView.getSelectedItemPosition() == mListView.getCount()-1)
//						picChange2UseMode(0);
////					else
////						picChange2UseMode(-1);
//					item.selectPrev();
//					mListView.invalidateViews();
//				}
//				return true;
//			case KeyEvent.KEYCODE_DPAD_RIGHT:
//				if (itemView != null && event.getAction() == KeyEvent.ACTION_DOWN) {
//					MenuItem item = (MenuItem) itemView.getTag();
////					if(mListView.getSelectedItemPosition() == mListView.getCount()-1)
//						picChange2UseMode(0);
////					else
////						picChange2UseMode(1);
//					item.selectNext();
//					mListView.invalidateViews();
//				}
//				return true;
			case KeyEvent.KEYCODE_DPAD_UP:
				return super.onKey(v, keyCode, event);
			case KeyEvent.KEYCODE_DPAD_DOWN:
				return super.onKey(v, keyCode, event);
			case KeyEvent.KEYCODE_MENU:
				return super.onKey(v, keyCode, event);
			case KeyEvent.KEYCODE_BACK:
				FragmentUtils.popBackSubFragment(this);
				return true;
				
			case KeyEvent.KEYCODE_0:num = 0;break;
			case KeyEvent.KEYCODE_1:num = 1;break;
			case KeyEvent.KEYCODE_2:num = 2;break;
			case KeyEvent.KEYCODE_3:num = 3;break;
			case KeyEvent.KEYCODE_4:num = 4;break;
			case KeyEvent.KEYCODE_5:num = 5;break;
			case KeyEvent.KEYCODE_6:num = 6;break;
			case KeyEvent.KEYCODE_7:num = 7;break;
			case KeyEvent.KEYCODE_8:num = 8;break;
			case KeyEvent.KEYCODE_9:num = 9;break;
			
			default:
				return false;
			}
			setFactoryMode(num);
		}
		
		return true;
	}
	
	private void setFactoryMode(int num) {
		if (mListView.getSelectedItem() != mItemContrast) {
			return;
		}
		if (mCurrentDigit <= 0) {
			mCurrentInput = num;
		} else {
			mCurrentInput *= 10;
			mCurrentInput += num;
		}
		mCurrentDigit++;
		LogUtil.logD(TAG, "PictureSetting mCurrentInput=" + mCurrentInput +";mCurrentDigit=" + mCurrentDigit, true);
		if (mCurrentDigit >= MAX_DIGIT) {
			mHandler.postDelayed(mRunConfirmInput, DELAY_SMALL);
		} else {
			mHandler.postDelayed(mRunConfirmInput, DELAY_LARGE);
		}
	}
	
	class UiHandler extends Handler {
		public static final int OPEN_DESIGN_MODE = 1;
		public static final int OPEN_FACTORY_MODE = 2;
		public static final int OPEN_ENG_MENU = 3;
		@Override
		public void handleMessage(Message msg) {
			FragmentManager fm = getActivity().getFragmentManager();
			switch (msg.what) {
			case OPEN_DESIGN_MODE:
				LogUtil.logD(TAG, "Open design mode ", true);
//				FragmentUtils.popBackSubFragment(PictureSettingFragment.this);
		        FragmentUtils.popAllBackStacks(fm);
				Intent it = new Intent();
				ComponentName component = new ComponentName("com.rtk.TSBFactoryMode", "com.rtk.TSBFactoryMode.TSBDesignMode");
				it.setComponent(component);
				getActivity().startActivity(it);
				
//				Intent pIntent = new Intent();
//				pIntent.setAction(TvBroadcastDefs.FACTORY_ENTER_DMODE);
//				getActivity().sendBroadcast(pIntent);
				break;
			case OPEN_FACTORY_MODE:
				LogUtil.logD(TAG, "Open factory mode ", true);
				Intent it1 = new Intent();
//				FragmentUtils.popBackSubFragment(PictureSettingFragment.this);
		        FragmentUtils.popAllBackStacks(fm);
				ComponentName component1 = new ComponentName("com.rtk.TSBFactoryMode", "com.rtk.TSBFactoryMode.TSBFactoryMode");
				it1.setComponent(component1);
				getActivity().startActivity(it1);
				
//				Intent pIntent1 = new Intent();
//				pIntent1.setAction(TvBroadcastDefs.FACTORY_ENTER_PMODE);
//				getActivity().sendBroadcast(pIntent1);
				break;
			case OPEN_ENG_MENU:
				FragmentUtils.popAllBackStacks(fm);
				Intent intent = new Intent();
				intent.setComponent(new ComponentName("com.rtk.engmenu", "com.rtk.engmenu.prjActivity"));
		        try {
		            startActivity(intent);
		        } catch (ActivityNotFoundException e) {
		            Toast.makeText(getActivity(), "com.rtk.engmenu is not found", Toast.LENGTH_LONG).show();
		        }
				break;
			default:
				break;
			}
		}
	}

	private Runnable mRunConfirmInput = new Runnable() {
		
		@Override
		public void run() {
			if (mCurrentDigit != 0) {
				Message msg =mHandler.obtainMessage();
				LogUtil.logD(TAG, "input mCurrentInput = " + mCurrentInput, true);
				if (mCurrentInput == 1950) {
					msg.what = UiHandler.OPEN_DESIGN_MODE;
				} else if(mCurrentInput == 9735) {
					msg.what = UiHandler.OPEN_FACTORY_MODE;
				}else if(mCurrentInput == 9751) {
					msg.what = UiHandler.OPEN_ENG_MENU;
				}
				mHandler.sendMessage(msg);
				mCurrentDigit = 0;
			}
		}
	};
	
	
	
}
