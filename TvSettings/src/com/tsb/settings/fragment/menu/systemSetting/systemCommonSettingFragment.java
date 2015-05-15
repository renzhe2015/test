package com.tsb.settings.fragment.menu.systemSetting;

import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;

import com.tsb.settings.R;
import com.tsb.settings.TvManagerHelper;
import com.tsb.settings.TvResource;
import com.tsb.settings.fragment.menu.BaseMenuFragment;
import com.tsb.settings.menu.MenuItem;
import com.tsb.settings.menu.SpinnerMenuItem;
import com.tsb.settings.menu.MenuItem.OnValueChangeListener;
import com.tsb.settings.util.FragmentUtils;

public class systemCommonSettingFragment extends BaseMenuFragment implements OnClickListener {	
	private static final int ENGLISH = 0;
	private static final int CHANISE = 1;
	private static final int[] LANGUAGE_SETTING = {
		ENGLISH,
		CHANISE,
	};
	
	private static final int SLEEP_POWEROFF_CLOSE = 0;
	private static final int SLEEP_POWEROFF_10_MIN = 1;
	private static final int SLEEP_POWEROFF_20_MIN = 2;
	private static final int SLEEP_POWEROFF_30_MIN = 3;
	private static final int SLEEP_POWEROFF_60_MIN = 4;
	private static final int SLEEP_POWEROFF_90_MIN = 5;
	private static final int SLEEP_POWEROFF_120_MIN = 6;
	private static final int SLEEP_POWEROFF_180_MIN = 7;
	private static final int SLEEP_POWEROFF_240_MIN = 8;
	private static final int[] SLEEP_POWEROFF_TIME = {
		SLEEP_POWEROFF_CLOSE,
		SLEEP_POWEROFF_10_MIN,
		SLEEP_POWEROFF_20_MIN,
		SLEEP_POWEROFF_30_MIN,
		SLEEP_POWEROFF_60_MIN,
		SLEEP_POWEROFF_90_MIN,
		SLEEP_POWEROFF_120_MIN,
		SLEEP_POWEROFF_180_MIN,
		SLEEP_POWEROFF_240_MIN
	};
	
	// Items
	private SpinnerMenuItem mItemLanguage;
	private SpinnerMenuItem mItemInputType;
	private SpinnerMenuItem mItemSleepPowerOff;
//	private SpinnerMenuItem mItemBootSourceSignal;
	
	private List<InputMethodInfo> mInputMethodProperties;
	private String mLastInputMethodId;
	private InputMethodManager imm;
	private String[] mInputMethods;
	private int[] mInputType;
	
	private int mPowerOffTime = 0;
	
	private TvManagerHelper tm;
	private String[] mSourceName;
	private int[] mSourceId;
	
	@Override
	protected View onInflateLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.layout_fragment_base_menu_dialog, container, false);
		return v;
	}
	
	@Override
	public void onCreateMenuItems(List<MenuItem> items) {

		Resources r = getResources();
		tm = TvManagerHelper.getInstance(getActivity());
		
		// Language 
		mItemLanguage = MenuItem.createSpinnerItem(R.string.tcl_system_language_setting);
		mItemLanguage.setSpinnerOptionsByArray(r.getStringArray(R.array.language_setting), LANGUAGE_SETTING);
		mItemLanguage.setCurrentValue(OSDLanguage.getCurOsdLanguage(getActivity()));
		mItemLanguage.setOnValueChangeListener(new OnValueChangeListener() {

			@Override
			public void onValueChanged(MenuItem item, int value) {
				try {
					OSDLanguage.setOSDLanguage(value, getActivity());
				} catch (Exception e) {
					Log.i("Exception", e.toString());
				}
			}
		});
		items.add(mItemLanguage);
		
		// Input Type  
		mItemInputType = MenuItem.createSpinnerItem(R.string.tcl_system_inputmethod);

		imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		setmInputMethodProperties(imm.getInputMethodList());
		setmLastInputMethodId(Settings.Secure.getString(getActivity().getContentResolver(),
				Settings.Secure.DEFAULT_INPUT_METHOD));
		
		mInputMethods = new String[]{getString(R.string.tcl_msg_none_inputmethod)};
		mInputType = new int[getmInputMethodProperties().size()];
		if (getmInputMethodProperties() != null && getmInputMethodProperties().size() > 0) {	
			mInputMethods = new String[getmInputMethodProperties().size()];
			for (int i = 0; i < getmInputMethodProperties().size(); i++) {
				InputMethodInfo property = getmInputMethodProperties().get(i);
				mInputMethods[i] = (String) property 
						.loadLabel(getActivity().getPackageManager());
				mInputType[i] = i;
			}
		}
		
		mItemInputType.setSpinnerOptionsByArray(mInputMethods, mInputType);		
		mItemInputType.setOnValueChangeListener(new OnValueChangeListener() {	
			
			@Override
			public void onValueChanged(MenuItem item, int value) {
				Settings.Secure.putInt(getActivity().getContentResolver(),
	                    Settings.Secure.INPUT_METHOD_SELECTOR_VISIBILITY, value);
			}
		});
		items.add(mItemInputType);
		
		// Sleep Power Off
		mItemSleepPowerOff = MenuItem.createSpinnerItem(R.string.tcl_system_sleep);
		mItemSleepPowerOff.setSpinnerOptionsByArray(r.getStringArray(R.array.system_sleep_times), SLEEP_POWEROFF_TIME);
		mItemSleepPowerOff.setOnValueChangeListener(new OnValueChangeListener() {
			
			@Override
			public void onValueChanged(MenuItem item, int value) {	
				
				setmPowerOffTime(Core.sleepTimeItems_value[value]);
				
			}
		});
		items.add(mItemSleepPowerOff);
		
		// Boot Source Signal
//		mItemBootSourceSignal = MenuItem.createSpinnerItem(R.string.tcl_boot_source_signal);
		
//		List<Integer> sourceList = tm.getInputSourceList();
//		mSourceName = new String[sourceList.size()];
//		mSourceId = new int[sourceList.size()];		
//		for (int i = 0; i < sourceList.size(); i++) {
//			int id = sourceList.get(i);			
//			String name = TvResource.getInputSourceLabel(getActivity(), id);
//			mSourceName[i] = name;
//			mSourceId[i] = id;
//		}
//		
//		mItemBootSourceSignal.setSpinnerOptionsByArray(mSourceName, mSourceId);
//		mItemBootSourceSignal.setOnValueChangeListener(new OnValueChangeListener() {
//			
//			@Override
//			public void onValueChanged(MenuItem item, int value) {
//				
//				/*先保存到数据库，开机或待机才产生效果*/
////				tm.setInputSource(mSourceId[value]);
//			}
//		});
//		items.add(mItemBootSourceSignal);
//		
	}
		
	@Override
	public int getTitle() {
		return R.string.tcl_tv_system_common;
	}

	@Override
	public void onClick(View v) {
	    FragmentUtils.popBackSubFragment(this);
	}	
	
	public String[] getInputMethods() {
		return mInputMethods;
	}

	public void setInputMethods(String[] mInputMethods) {
		this.mInputMethods = mInputMethods;
	}

	public String getmLastInputMethodId() {
		return mLastInputMethodId;
	}

	public void setmLastInputMethodId(String mLastInputMethodId) {
		this.mLastInputMethodId = mLastInputMethodId;
	}

	public List<InputMethodInfo> getmInputMethodProperties() {
		return mInputMethodProperties;
	}

	public void setmInputMethodProperties(List<InputMethodInfo> mInputMethodProperties) {
		this.mInputMethodProperties = mInputMethodProperties;
	}

	public int getmPowerOffTime() {
		return mPowerOffTime;
	}

	public void setmPowerOffTime(int mPowerOffTime) {
		this.mPowerOffTime = mPowerOffTime;
	}

}
