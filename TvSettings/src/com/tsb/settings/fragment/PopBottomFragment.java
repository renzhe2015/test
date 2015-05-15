package com.tsb.settings.fragment;

import java.util.List;

import android.app.FragmentManager;
import android.app.TvManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tsb.settings.R;
import com.tsb.settings.fragment.menu.BaseMenuFragment;
import com.tsb.settings.menu.MenuItem;
import com.tsb.settings.menu.SeekBarMenuItem;
import com.tsb.settings.menu.SpinnerMenuItemExt;
import com.tsb.settings.util.FragmentUtils;
import com.tsb.settings.util.LogUtil;

/**显示底部单条信息窗口*/
public class PopBottomFragment extends BaseMenuFragment {
	
	private int num;
	private Handler mHandler = new UiHandler();
	// Items
	private SeekBarMenuItem mSeekMenuItem;
	//****add by huangfh@tcl.com 08-29
	private SpinnerMenuItemExt mSpinnerMenuItem;
	//*****end
	@Override
	protected View onInflateLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.layout_fragment_base_menu_dialog_bottom, container, false);
		num = 0;
		return v;
	}
	
	public PopBottomFragment() {
		
	}
	
	@Override
	public void onCreateMenuItems(List<MenuItem> items) {
		final TvManager tm = (TvManager) getActivity().getSystemService(Context.TV_SERVICE);
		LogUtil.logD("Create PopBottom MenuItems", true);
		initView();
	}
	
	private void initView() {
		if (mSeekMenuItem != null) {
			mItems.add(mSeekMenuItem);
		}
		//******add by huangfh@tcl.com 08-29
		if(mSpinnerMenuItem!=null){
			mItems.add(mSpinnerMenuItem);
		}
		//****end
	}

	/**在创建Fragment后，创建View之前添加SeekBarMenuItem*/
	public void addSeekBarMenuItem(SeekBarMenuItem item) {
		mSeekMenuItem = item;
	}
	//******add by huangfh@tcl.com 08-29
	public void addSeekBarMenuItem(SpinnerMenuItemExt item) {
		mSpinnerMenuItem = item;
	}
	//*******end
	@Override
	public int getTitle() {
		return R.string.tcl_pop_bottom_fragment;
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		FragmentUtils.mHandler.removeMessages(0);
		FragmentUtils.mHandler.sendEmptyMessageDelayed(0, 15000);
		View itemView = mListView.getSelectedView();
		Log.d("lly", "keyCode is "+keyCode);
		String deviceName = InputDevice.getDevice(event.getDeviceId()).getName();
		if("Smart_TV_Keypad".equals(deviceName)&&keyCode == KeyEvent.KEYCODE_TV_INPUT){
			keyCode = KeyEvent.KEYCODE_DPAD_CENTER;
		}
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_LEFT:
			if (itemView != null && event.getAction() == KeyEvent.ACTION_DOWN) {
				MenuItem item = (MenuItem) itemView.getTag();
				item.selectPrev();
				mListView.invalidateViews();
			}
//			v.getRootView().findViewById(R.id.container_tabs).requestFocus();
			return true;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			if (itemView != null && event.getAction() == KeyEvent.ACTION_DOWN) {
				MenuItem item = (MenuItem) itemView.getTag();
				item.selectNext();
				mListView.invalidateViews();
			}
			return true;
			
		case KeyEvent.KEYCODE_MENU:
			return super.onKey(v, keyCode, event);
		case KeyEvent.KEYCODE_BACK:
			FragmentUtils.popBackSubFragment(this);
			return true;
		case KeyEvent.KEYCODE_DPAD_CENTER:
			if(event.getAction() == KeyEvent.ACTION_DOWN){
				if(mSeekMenuItem != null){
					if(mSeekMenuItem.seekBarTitle == R.string.STRING_CONTRAST)
						num++;
						Log.d("lly", num+" ====>press Center key form PopBottomFragment");
						mHandler.removeMessages(0);
						mHandler.sendEmptyMessageDelayed(0, 1000);
						return true;
				}
				else return true;
			}
		default:
			return false;
		}
	}
	
	class UiHandler extends Handler {
		
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if(getActivity() == null)
				return;
			
			FragmentManager fm = getActivity().getFragmentManager();
			if(num == 6){
				FragmentUtils.popAllBackStacks(fm);
				Intent it = new Intent();
				ComponentName component = new ComponentName("com.rtk.TSBFactoryMode", "com.rtk.TSBFactoryMode.TSBDesignMode");
				it.setComponent(component);
				getActivity().startActivity(it);
			}else
			if(num == 8) {
				FragmentUtils.popAllBackStacks(fm);
				Intent intent = new Intent();
				intent.setComponent(new ComponentName("com.rtk.engmenu", "com.rtk.engmenu.prjActivity"));
		        try {
		            startActivity(intent);
		        } catch (ActivityNotFoundException e) {
		            Toast.makeText(getActivity(), "com.rtk.engmenu is not found", Toast.LENGTH_LONG).show();
		        }
			}else{
				num = 0;
			}
		}
		
		
	}

}
