package com.tsb.settings.fragment.menu;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.tsb.settings.R;
import com.tsb.settings.fragment.MainMenuFragment;
import com.tsb.settings.fragment.menu.pic.PointerSpeedFragment;
import com.tsb.settings.fragment.menu.systemSetting.AboutTvFragment;
import com.tsb.settings.fragment.menu.systemSetting.OSDLanguage;
import com.tsb.settings.fragment.menu.systemSetting.ResetFactoryFragment;
import com.tsb.settings.fragment.menu.systemSetting.SleepPowerOffSettingFragment;
import com.tsb.settings.menu.MenuItem;
import com.tsb.settings.menu.OptionMenuItem;
import com.tsb.settings.util.FragmentUtils;

public class SystemSettingMenuFragment extends SecondLevelMenuFragment {
	private OptionMenuItem systemUpgradeItem = MenuItem
			.createOptionItem(R.string.tcl_tv_system_upgrade);
	private OptionMenuItem languageItem = MenuItem
			.createOptionItem(R.string.tcl_tv_system_language);
	private OptionMenuItem sleepOffItem = MenuItem
			.createOptionItem(R.string.tcl_tv_system_sleep_off);
	private OptionMenuItem appManageItem = MenuItem
			.createOptionItem(R.string.tcl_tv_system_app_manage);
	private OptionMenuItem systemInfoItem = MenuItem
			.createOptionItem(R.string.tcl_tv_system_info);
	private OptionMenuItem pointerSpeedItem = MenuItem
			.createOptionItem(R.string.tcl_tv_system_pointer_speed);

	private static final int SYSTEM_UPGRADE = 0;
	private static final int RESET_DEFAULT_FACTORY = 1;
	private static final int[] systemUpgrade = { SYSTEM_UPGRADE,
			RESET_DEFAULT_FACTORY, };

	private static final int CHINESE = 0;
	private static final int ENGLISH = 1;
	private static final int[] languageSetting = { CHINESE, ENGLISH, };

	private static final int ALL_APP = 0;
	private static final int RUNNING_APP = 1;
	private static final int UPGRADE_APP = 2;
	private static final int USED_SPACE = 3;
	private static final int[] APP_MANAGE = { ALL_APP, RUNNING_APP,
			UPGRADE_APP, USED_SPACE, };
	// *********add by huangfh@tcl.com 08-27
	private List<InputMethodInfo> mInputMethodProperties;
	private InputMethodManager imm;
	private int[] mInputType;
	private String[] mInputMethods;
	private int mLastInputMethod = 0;
	private static int curTime = -1;
	private static String timeUnit;
	private static boolean isCN = false;
	private static boolean isOnResume = false;
	private boolean isSystemSettingsExist;
	Thread thread;

	// *******end
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// add by huangfh@tcl.com 14-9-02
		if (getResources().getConfiguration().locale.getCountry().equals("CN")) {
			timeUnit = "分钟";
			isCN = true;
		} else {
			timeUnit = "min";
			isCN = false;
		}
		isSystemSettingsExist = true;
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().registerReceiver(sleepPowerDownReceiver,
				new IntentFilter("SleepPowerDown"));

	};

	@Override
	public void onCreateMenuItems(List<MenuItem> items) {
		SharedPreferences settings = getActivity().getSharedPreferences(
				"powerDownTimeFile", Context.MODE_PRIVATE);
		curTime = settings.getInt("powerDownTime", -1);
		systemUpgradeItem.mClickEqualsRightKey = true;
		items.add(systemUpgradeItem);

		languageItem.mClickEqualsRightKey = true;
		items.add(languageItem);

		// inputTypeItem.mClickEqualsRightKey = true;
		// items.add(inputTypeItem);

		sleepOffItem.needPopWindow(false);
		sleepOffItem.setRightKeyEventEqualsOnClick(true);
		sleepOffItem.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				FragmentUtils.showSubFragment(getBaseFragment(),
						SleepPowerOffSettingFragment.class);
			}
		});
		items.add(sleepOffItem);
		// *********add by huangfh@tcl.com 08-27
		imm = (InputMethodManager) getActivity().getSystemService(
				Context.INPUT_METHOD_SERVICE);
		// add by huangfh@tcl.com 14-9-26
		mInputMethodProperties = new ArrayList<InputMethodInfo>();
		mInputMethodProperties.addAll(imm.getInputMethodList());
		for (int i = 0; i < imm.getInputMethodList().size(); i++) {
			InputMethodInfo property = imm.getInputMethodList().get(i);
			String label = (String) property.loadLabel(getActivity()
					.getPackageManager());
			if (label.equals("Android 键盘") || label.equals("AOSP"))
				mInputMethodProperties.remove(property);
			else
				continue;
		}
		// setmInputMethodProperties(mTempInputMethodProperties);
		// ******end
		mInputType = new int[mInputMethodProperties.size()];
		if (getmInputMethodProperties() != null
				&& mInputMethodProperties.size() > 0) {
			mInputMethods = new String[mInputMethodProperties.size()];
			for (int i = 0; i < mInputMethodProperties.size(); i++) {
				InputMethodInfo property = mInputMethodProperties.get(i);
				mInputMethods[i] = (String) property.loadLabel(getActivity()
						.getPackageManager());
				// add by huangfh@tcl.com 14-8-21
				final String id = property.getId();
				boolean hasIt = id.equals(Settings.Secure.getString(
						getActivity().getContentResolver(),
						Settings.Secure.DEFAULT_INPUT_METHOD));
				if (hasIt) {
					mLastInputMethod = i;
				}
				// end add by huangfh@tcl.com
				mInputType[i] = i;
			}
		}
		
		appManageItem.mClickEqualsRightKey = true;
		items.add(appManageItem);

		systemInfoItem.needPopWindow(false);
		systemInfoItem.setRightKeyEventEqualsOnClick(true);
		systemInfoItem.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				FragmentUtils.showSubFragment(getBaseFragment(),
						AboutTvFragment.class);
			}
		});
		items.add(systemInfoItem);

		pointerSpeedItem.needPopWindow(false);
		pointerSpeedItem.setRightKeyEventEqualsOnClick(true);
		pointerSpeedItem.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				FragmentUtils.showSubFragment(getBaseFragment(),
						PointerSpeedFragment.class);
			}
		});
		items.add(pointerSpeedItem);
		//When refresh the listView，cause to PopView be showed on the Top of ListView.
//		if(isMouseDeviceExist())
//			pointerSpeedItem.setEnable(true);
//		else
//			pointerSpeedItem.setEnable(false);
		
//		thread = new Thread() {
//			@Override
//			public void run() {
//				while(isSystemSettingsExist) {
//					if(isMouseDeviceExist()) {
//						mHandler.sendEmptyMessage(REFRESH_POINTER_TRUE);
//					}else {
//						mHandler.sendEmptyMessage(REFRESH_POINTER_FALSE);
//					}
//					try {
//						sleep(2000);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			}
//		};
//		thread.start();
		isOnResume = true;
		initSoundParameters();
	}

	public void initSoundParameters() {
		new Thread() {
			@Override
			public void run() {
				mHandler.sendEmptyMessageDelayed(REFRESH_ITEM,
						SecondLevelMenuFragment.DATA_INIT_DELAY);
			}
		}.start();
	}

	OnItemClickListener systemUpgradeItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			if (systemUpgradeItem.getPopListTitle().length > position
					&& position >= 0) {
				// systemUpgradeItem.setTextValue(systemUpgradeItem.getPopListTitle()[position]);
				systemUpgradeItem.getPopView().dismiss();
				systemUpgradeItem.setSelection(position);

				Intent it = new Intent(Intent.ACTION_MAIN);
				switch (position) {
				case SYSTEM_UPGRADE:
					it.setClassName("com.tcl.update",
							"com.tcl.update.NetworkUpdate");
					it.addCategory(Intent.CATEGORY_LAUNCHER);
					it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
					getActivity().startActivity(it);
					break;

				case RESET_DEFAULT_FACTORY:
					FragmentUtils.showSubFragment(getBaseFragment(),
							ResetFactoryFragment.class);
					break;
				}
			}
		}
	};

	OnItemClickListener languageItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			if (languageItem.getPopListTitle().length > position
					&& position >= 0) {
				languageItem
						.setTextValue(languageItem.getPopListTitle()[position]);
				languageItem.getPopView().dismiss();
				languageItem.setSelection(position);
				mLastSelection = 1;
				MainMenuFragment.fromLanguageSetting = true;
				switch (position) {
				case CHINESE:
					try {
						OSDLanguage.setOSDLanguage(CHINESE, getActivity());
					} catch (Exception e) {
						Log.i("Exception", e.toString());
					}
					break;

				case ENGLISH:
					try {
						OSDLanguage.setOSDLanguage(ENGLISH, getActivity());
					} catch (Exception e) {
						Log.i("Exception", e.toString());
					}
					break;
				}
			}
		}
	};

	OnItemClickListener inputTypeItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			// **********add by huangfh@tcl.com 14-8-27
			if (mInputMethodProperties != null
					&& mInputMethodProperties.size() != 0) {
				InputMethodInfo property = mInputMethodProperties.get(position);
				String mCurrentInputMethodId = property.getId();
				try {
					Settings.Secure
							.putString(
									getActivity().getContentResolver(),
									Settings.Secure.DEFAULT_INPUT_METHOD,
									mCurrentInputMethodId != null ? mCurrentInputMethodId
											: "");
				} catch (Exception e) {
					Log.i("Exception", e.toString());
				}
			}

			// inputTypeItem.setCurrentValue(position);
			// if(inputTypeItem.mTextValue.length()*inputTypeItem.mTextValue.getTextSize()>=inputTypeItem.mTextValue.getWidth()){
			// inputTypeItem.setAutoTextViewGravity(0);
			// }else{
			// inputTypeItem.setAutoTextViewGravity(1);
			// }
			// inputTypeItem.getPopView().dismiss();
			// inputTypeItem.setSelection(position);
			// **********end add by huangfh@tcl.com
		}
	};

	OnItemClickListener appManageItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			if (appManageItem.getPopListTitle().length > position
					&& position >= 0) {
				// appManageItem.setTextValue(appManageItem.getPopListTitle()[position]);
				appManageItem.getPopView().dismiss();
				appManageItem.setSelection(position);

				switch (position) {
				case ALL_APP: {
					Intent it = new Intent(
							Settings.ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS);
					getActivity().startActivity(it);
					break;
				}
				case RUNNING_APP: {
					Intent it = new Intent();
					it.setClassName("com.android.settings",
							"com.android.settings.Settings$RunningServicesActivity");
					getActivity().startActivity(it);
					break;
				}
				case UPGRADE_APP: {
					Intent it = new Intent(
							Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
					getActivity().startActivity(it);
					break;
				}
				case USED_SPACE: {
					Intent it = new Intent(Settings.ACTION_MEMORY_CARD_SETTINGS);
					getActivity().startActivity(it);
					break;
				}
				default: {
					Intent it = new Intent();
					it.setClassName("com.android.settings",
							"com.android.settings.Settings");
					getActivity().startActivity(it);
					break;
				}
				}

			}
		}
	};

	public static final int REFRESH_ITEM = 1;
	public static final int REFRESH_POINTER_TRUE = 2;
	public static final int REFRESH_POINTER_FALSE = 3;
	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REFRESH_POINTER_TRUE:
				pointerSpeedItem.setEnable(true);
				pointerSpeedItem.updateEnable(true);
				pointerSpeedItem.needRightArraw(true);
				mAdapter.notifyDataSetChanged();
				break;
			case REFRESH_POINTER_FALSE:
				pointerSpeedItem.setEnable(false);
				pointerSpeedItem.updateEnable(false);
				pointerSpeedItem.needRightArraw(false);
				mAdapter.notifyDataSetChanged();
				break;
			case REFRESH_ITEM:
				if (!isVisible()) {
					break;
				}
				systemUpgradeItem.setOptionsByArray(getResources()
						.getStringArray(R.array.system_upgrade), systemUpgrade,
						systemUpgradeItemClickListener);
				// systemUpgradeItem.setCurrentValue(SYSTEM_UPGRADE);

				languageItem
						.setOptionsByArray(
								getResources().getStringArray(
										R.array.language_setting),
								languageSetting, languageItemClickListener);
				if (CHINESE == OSDLanguage.getCurOsdLanguage(getActivity()))
					languageItem.setCurrentValue(CHINESE);
				else
					languageItem.setCurrentValue(ENGLISH);

				if (curTime == -1 || curTime == 0) {
					sleepOffItem.setTextValue(getResources().getString(
							R.string.tcl_sleepoff_close));
				} else {
					sleepOffItem.setTextValue(curTime + timeUnit);
				}
				// ******end
				appManageItem.setOptionsByArray(
						getResources().getStringArray(R.array.app_manage),
						APP_MANAGE, appManageItemClickListener);
				break;

			default:
				break;
			}
		};
	};

	@Override
	public int getTitle() {
		return R.string.tcl_tv_channel_setting;
	}

	// *********add by huangfh@tcl.com 08-27
	public List<InputMethodInfo> getmInputMethodProperties() {
		return mInputMethodProperties;
	}

	public void setmInputMethodProperties(
			List<InputMethodInfo> mInputMethodProperties) {
		this.mInputMethodProperties = mInputMethodProperties;
	}

	// ******end

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		isOnResume = false;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		getActivity().unregisterReceiver(sleepPowerDownReceiver);
		isSystemSettingsExist = false;
	}

	BroadcastReceiver sleepPowerDownReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			if (arg1.getAction().equals("SleepPowerDown")) {
				int curTime = arg1.getIntExtra("resttime", -1);
				if (isOnResume && curTime >= 0) {
					Log.d("hfh", curTime + "--------------");
					if (curTime == 0) {
						if (isCN)
							sleepOffItem.setTextValue("关闭");
						else
							sleepOffItem.setTextValue("Close");
					} else {
						sleepOffItem.setTextValue(curTime + timeUnit);
					}
				}
			}
		}
	};

	public boolean isMouseDeviceExist() {
		try {

			// 获得外接USB输入设备的信息
			Process p = Runtime.getRuntime()
					.exec("cat /proc/bus/input/devices");
			BufferedReader in = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null) {
				String deviceInfo = line.trim();
				if (deviceInfo.contains("PixArt USB Optical Mouse") ||
						deviceInfo.contains("PIXART USB OPTICAL MOUSE")) {
					return true;
				}
				// 对获取的每行的设备信息进行过滤，获得自己想要的。
				Log.d("devicelly", deviceInfo);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return true;
	}
}
