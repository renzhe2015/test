package com.tsb.settings.fragment.menu;

import java.util.List;

import android.app.TvManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.tsb.settings.R;
import com.tsb.settings.TvManagerHelper;
import com.tsb.settings.fragment.PopBottomFragment;
import com.tsb.settings.fragment.menu.pic.PCSettingFragment;
import com.tsb.settings.fragment.menu.pic.PictureSettingFragment;
import com.tsb.settings.fragment.menu.pic.VgaAdjustFragment;
import com.tsb.settings.menu.MenuItem;
import com.tsb.settings.menu.OptionMenuItem;
import com.tsb.settings.menu.SeekBarMenuItem;
import com.tsb.settings.menu.MenuItem.OnValueChangeListener;
import com.tsb.settings.util.FragmentUtils;
import com.tsb.settings.util.LogUtil;

public class PictureMenuFragment extends SecondLevelMenuFragment {
	private static final String TAG = "PictureMenuFragment";
	private OptionMenuItem mPicModeItem;
	private OptionMenuItem mNatureLightItem;
	private OptionMenuItem mDynamicContrastItem;
	private OptionMenuItem mBackLightItem;
	private OptionMenuItem mGeometrySetItem;	
	private OptionMenuItem mRatioModeItem;
	private OptionMenuItem mAdvanceItem;
	private String[] mRatioMode;	
	
	private static final int[] PICTURE_MODES = {
		TvManagerHelper.PICTURE_MODE_STANDARD,
		TvManagerHelper.PICTURE_MODE_BRIGHT,
		TvManagerHelper.PICTURE_MODE_GENTLE,
		TvManagerHelper.PICTURE_MODE_USER
	};
	private final int[] VALUE_PIC_RATIO = {
			TvManager.SCALER_RATIO_16_9,//16：9
			TvManager.SCALER_RATIO_4_3, //4：3
			TvManager.SCALER_RATIO_BBY_AUTO,//自动	
			TvManager.SCALER_RATIO_PANORAMA,//智能全景
	};
	private static final int[] VALUE_COLOR_TEMP = {
		TvManager.SLR_COLORTEMP_WARM,
		TvManager.SLR_COLORTEMP_NORMAL,
		TvManager.SLR_COLORTEMP_COOL,
	};
	
	private static final int[] VALUE_BOOLEAN = {
		-1,
		1
	};
	private static final int PC_AUTO = 1;
	private static final int PC_MANUALLY = 2;
	private static final int[] VALUE_PC_GEOMETRY = {
		PC_AUTO, //自动调试
		PC_MANUALLY  //手动调试
	};
	
	private static TvManagerHelper tm;
	//******add by huangfh@tcl.com 15-8-22
	private int sourceID;
	//******end 
	@Override
	public void onCreateMenuItems(List<MenuItem> items) {
		LogUtil.logD(TAG, "onCreateMenuItems", true);
		tm = TvManagerHelper.getInstance(getActivity());
		
		 sourceID=tm.getInputSource();
		// Picture Setting
		mPicModeItem = MenuItem.createOptionItem(R.string.tcl_pic_setting);
		mPicModeItem.mClickEqualsRightKey = true;
//		mPicModeItem.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				FragmentUtils.showSubFragment(getBaseFragment(), PictureSettingFragment.class);
//			}
//		});
		items.add(mPicModeItem);
		
		if(!TvManagerHelper.isPCSource(tm.getInputSource())){
			mRatioMode = getResources().getStringArray(R.array.pic_ratio);
			mRatioModeItem = MenuItem.createOptionItem(R.string.tcl_scale_model);
			mRatioModeItem.mClickEqualsRightKey = true;
			items.add(mRatioModeItem);
		}

		
		mNatureLightItem = MenuItem.createOptionItem(R.string.tcl_pic_nature_light);
		mNatureLightItem.mClickEqualsRightKey = true;
		
		mDynamicContrastItem = MenuItem.createOptionItem(R.string.tcl_pic_dynamic_contrast);
		mDynamicContrastItem.mClickEqualsRightKey = true;
		
		mAdvanceItem = MenuItem.createOptionItem(R.string.tcl_pic_advance);
		mAdvanceItem.setRightKeyEventEqualsOnClick(true);
		mAdvanceItem.needPopWindow(false);
		mAdvanceItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentUtils.showSubFragment(getBaseFragment(), PictureSettingFragment.class);
			}
		});

		mBackLightItem = MenuItem.createOptionItem(R.string.tcl_pic_backlight);
		mBackLightItem.needPopWindow(false);
		mBackLightItem.setRightKeyEventEqualsOnClick(true);
		mBackLightItem.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final SeekBarMenuItem backLightSeekItem = MenuItem.createSeekBarItem(R.string.STRING_BACKLIGHT);
				backLightSeekItem.setCurrentProgress(tm.mTvManager.getBacklight());
				backLightSeekItem.setOnValueChangeListener(new OnValueChangeListener() {
					
					@Override
					public void onValueChanged(MenuItem item, int value) {
//						onChangePictureUserMode(tm);
//						if (tm.GetPictureMode() != TvManager.PICTURE_MODE_USER)
//							tm.setPictureMode(TvManagerHelper.PICTURE_MODE_USER);						
						tm.mTvManager.setBacklight(value);
//						backLightSeekItem.setCurrentProgress(value);
					}
				});
				FragmentUtils.showPopBottomFragment(getBaseFragment(), PopBottomFragment.class,null,backLightSeekItem);
			}
		});
		items.add(mBackLightItem);
		
		mGeometrySetItem = MenuItem.createOptionItem(R.string.tcl_pic_geometry_set);
		mGeometrySetItem.mClickEqualsRightKey = true;
		// remove by huangfh 14-8-22 int sourceID=tm.getInputSource();
		mGeometrySetItem.setEnable(TvManagerHelper.isPCSource(sourceID)&&tm.isSignalDisplayReady());
//		items.add(mGeometrySetItem);
		
		//*****add by huangfh@tcl.com 14-08-22
		if(!TvManagerHelper.isPCSource(sourceID)){
			items.add(mNatureLightItem);
			items.add(mDynamicContrastItem);
			items.add(mAdvanceItem);
		}
		else{
			items.add(mGeometrySetItem);
		}
		//*****end
//		items.add(MenuItem.createTextItem(R.string.tcl_setting_reset)
//				.setOnClickListener(new OnClickListener() {
//					
//					@Override
//					public void onClick(View v) {
//						// TODO Auto-generated method stub
//						
//					}
//				}));
		
		initPicParameters();
		
	}
	
	public void initPicParameters() {
		new Thread() {
			@Override
			public void run() {
				mHandler.sendEmptyMessageDelayed(REFRESH_ITEM, SecondLevelMenuFragment.DATA_INIT_DELAY);
			}
		}.start();
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initPicParameters();
		
	}
	
	OnItemClickListener picModeItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1,
				int position, long arg3) {
			LogUtil.logD(TAG, "PIC Mode popview click,position=" + position, false);
			if (mPicModeItem.getPopListTitle().length > position && position >= 0) {
				mPicModeItem.setTextValue(mPicModeItem.getPopListTitle()[position]);
				mPicModeItem.getPopView().dismiss();
				mPicModeItem.setSelection(position);
				tm.setPictureMode(PICTURE_MODES[position]);
				mNatureLightItem.setCurrentValue(tm.GetDCR() == 0 ? -1 : 1);
				mDynamicContrastItem.setCurrentValue((tm.GetDCC() ? 1 : -1)); 
				mBackLightItem.setTextValue(tm.GetBacklight() + "");
//				mColorTempItem.setCurrentValue(tm.GetColorTempMode(), true);
			}
		}
	};
	
	private OnItemClickListener ratioListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			tm.mTvManager.setAspectRatio(VALUE_PIC_RATIO[position]);
			mRatioModeItem.setCurrentValue(VALUE_PIC_RATIO[position]);
			mRatioModeItem.getPopView().dismiss();
		}
	};	
	
	OnItemClickListener natureLightClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1,
				int position, long arg3) {
			if (mNatureLightItem.getPopListTitle().length > position && position >= 0) {
				mNatureLightItem.setTextValue(mNatureLightItem.getPopListTitle()[position]);
				mNatureLightItem.getPopView().dismiss();
				mNatureLightItem.setSelection(position);
				//打开关闭自然光:-1 关，1 开
				tm.SetDCR(VALUE_BOOLEAN[position]);
			}
		}
	};
	
	OnItemClickListener dynamicContrastClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1,
				int position, long arg3) {
			if (mDynamicContrastItem.getPopListTitle().length > position && position >= 0) {
				mDynamicContrastItem.setTextValue(mDynamicContrastItem.getPopListTitle()[position]);
				mDynamicContrastItem.getPopView().dismiss();
				mDynamicContrastItem.setSelection(position);
				//打开关闭动态背光:-1 关，1 开
				tm.setDCC((1 == VALUE_BOOLEAN[position]), 
						(1 == VALUE_BOOLEAN[position]));
			}
		}
	};
	
	OnItemClickListener pcGeometryClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1,
				int position, long arg3) {
			if (mGeometrySetItem.getPopListTitle().length > position && position >= 0) {
//				mGeometrySetItem.setTextValue(mGeometrySetItem.getPopListTitle()[position]);
				mGeometrySetItem.getPopView().dismiss();
				mGeometrySetItem.setSelection(position);
				if (VALUE_PC_GEOMETRY[position] == PC_AUTO) {					
					//TODO 等待调整结束后，做相应动作
					FragmentUtils.showSubFragment(getBaseFragment(), VgaAdjustFragment.class);
				} else if (VALUE_PC_GEOMETRY[position] == PC_MANUALLY) {
					//弹出手动调整界面
					FragmentUtils.showSubFragment(getBaseFragment(), PCSettingFragment.class);
				}
				
			}
		}
	};
	
	
	private void refreshItems() {
		if (!isVisible()) {
			return;
		}
		LogUtil.logD(TAG, "REFRESH_ITEM start setOptionsByArray", false);
		mPicModeItem.setOptionsByArray(getResources().getStringArray(R.array.setting_pic_modes), PICTURE_MODES
				, picModeItemClickListener);
		if(!TvManagerHelper.isPCSource(tm.getInputSource()))
			mRatioModeItem.setOptionsByArray(mRatioMode, VALUE_PIC_RATIO, ratioListener);
		
		mNatureLightItem.setOptionsByArray(getResources().getStringArray(R.array.tabsetting_option_OnOff), 
				VALUE_BOOLEAN, natureLightClickListener);
		mDynamicContrastItem.setOptionsByArray(getResources().getStringArray(R.array.tabsetting_option_OnOff), 
				VALUE_BOOLEAN, dynamicContrastClickListener);
		mGeometrySetItem.setOptionsByArray(getResources().getStringArray(R.array.pc_geometry), 
				VALUE_PC_GEOMETRY, pcGeometryClickListener);
		
		LogUtil.logD(TAG, "REFRESH_ITEM start setCurrentValue", true);
		
		mPicModeItem.setCurrentValue(tm.mTvManager.getPictureMode());
		if(!TvManagerHelper.isPCSource(tm.getInputSource()))
			mRatioModeItem.setCurrentValue(tm.mTvManager.getAspectRatio(/*tm.getInputSource()*/));
		
		mBackLightItem.setTextValue(tm.GetBacklight() + "");
		if(!TvManagerHelper.isPCSource(sourceID)){
			mNatureLightItem.setCurrentValue(tm.GetDCR());
			mDynamicContrastItem.setCurrentValue((tm.GetDCC() ? 1 : -1));					
		}
		else{
			mGeometrySetItem.updateEnable(mGeometrySetItem.isEnable());
			mGeometrySetItem.needRightArraw(mGeometrySetItem.isEnable());
		}
	}
	
	public static final int REFRESH_ITEM = 1;
	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REFRESH_ITEM:
				refreshItems();
				break;

			default:
				break;
			}
		};
	};
	
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if((keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)
				&&(((mListView.getSelectedItemPosition() == 2)&&!TvManagerHelper.isPCSource(sourceID))||((mListView.getSelectedItemPosition() == 1)&&TvManagerHelper.isPCSource(sourceID))))
		{
			final SeekBarMenuItem backLightSeekItem = MenuItem.createSeekBarItem(R.string.STRING_BACKLIGHT);
			backLightSeekItem.setCurrentProgress(tm.mTvManager.getBacklight());
			backLightSeekItem.setOnValueChangeListener(new OnValueChangeListener() {
				
				@Override
				public void onValueChanged(MenuItem item, int value) {
//					onChangePictureUserMode(tm);
					if (tm.GetPictureMode() != TvManager.PICTURE_MODE_USER)
						tm.setPictureMode(TvManagerHelper.PICTURE_MODE_USER);
					tm.mTvManager.setBacklight(value);
//					backLightSeekItem.setCurrentProgress(value);
				}
			});
			FragmentUtils.showPopBottomFragment(getBaseFragment(), PopBottomFragment.class,null,backLightSeekItem);			
			return true;
		}
		else
		{
			return super.onKey(v, keyCode, event);
		}
	}
		
	@Override
	public int getTitle() {
		return R.string.STRING_PICTURE;
	}

	protected void sendSafeEyeOffBroadcast() {
		Intent it = new Intent("com.odm.safeeye.opened");
		getActivity().sendBroadcast(it);
	}

	protected void sendSafeEyeOnBroadcast() {
		Intent it = new Intent("com.odm.safeeye.closed");
		getActivity().sendBroadcast(it);
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
			
			// Change to user mode
			tm.setPictureMode(TvManagerHelper.PICTURE_MODE_USER);
			tm.SetBrightness(bright);
			tm.SetContrast(contrast);
			tm.SetSaturation(color);
			if((TvManagerHelper.isAtvSource(tm.getInputSource())&&
					tm.getCurrentAtvColorStd()==TvManager.RT_COLOR_STD_NTSC)
				||(TvManagerHelper.isAV1Source(tm.getInputSource())&&
					(tm.mTvManager.GetIsNtscVideo()== true)))
				tm.SetHue(tint);
			tm.SetSharpness(true, sharpness);
			tm.setColorTempMode(ColorTempMode);
		}
	}

}
