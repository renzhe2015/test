package com.tsb.settings.fragment.menu.pic;

import java.util.List;

import android.app.Instrumentation;
import android.content.Context;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tsb.settings.R;
import com.tsb.settings.fragment.menu.BaseMenuFragment;
import com.tsb.settings.menu.MenuItem;
import com.tsb.settings.menu.SeekBarMenuItem;
import com.tsb.settings.menu.MenuItem.OnValueChangeListener;
import com.tsb.settings.util.FragmentUtils;

public class PointerSpeedFragment extends BaseMenuFragment{
	private SeekBarMenuItem mPointerSpeed;
	private InputManager mIm;
	private int speed;
	
	@Override
	protected View onInflateLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.layout_fragment_base_menu_dialog, container, false);
		return v;
	}

	@Override
	public void onCreateMenuItems(List<MenuItem> items) {
		// TODO Auto-generated method stub
		
		mIm = (InputManager) getActivity().getSystemService(Context.INPUT_SERVICE);
		speed = mIm.getPointerSpeed(getActivity());
		mPointerSpeed = MenuItem.createSeekBarItem(R.string.tcl_tv_system_pointer_speed).
		    		setBoundary(InputManager.MIN_POINTER_SPEED, InputManager.MAX_POINTER_SPEED);
		mPointerSpeed.setCurrentProgress(speed);
		mPointerSpeed.setOnValueChangeListener(new OnValueChangeListener() {
			
			@Override
			public void onValueChanged(MenuItem item, int value) {
					mIm.setPointerSpeed(getActivity(), value);
				}
			});
		items.add(mPointerSpeed);
	}

	@Override
	public int getTitle() {
		// TODO Auto-generated method stub
		return 0;
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

}
