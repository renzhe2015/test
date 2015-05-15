
package com.tsb.settings.util;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentManager.BackStackEntry;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.tsb.settings.TvManagerHelper;
import com.tsb.settings.fragment.MainMenuFragment;
import com.tsb.settings.fragment.PopBottomFragment;
import com.tsb.settings.menu.SeekBarMenuItem;
import com.tsb.settings.menu.SpinnerMenuItemExt;

public class FragmentUtils {
	private static Fragment curFragment;
	public static Handler mHandler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);	
			
			FragmentManager fm = MainMenuFragment.fm;
			if((FragmentUtils.getCurFragment() == fm.findFragmentByTag("autosearch"))
					||(FragmentUtils.getCurFragment() == fm.findFragmentByTag("dtvManualSearch"))
					||(FragmentUtils.getCurFragment() == fm.findFragmentByTag("atvManualSearch")))
				return;
			
			FragmentUtils.popAllBackStacks(MainMenuFragment.fm);  //Hide all the Fragment
		}
		
	};
	public static void showSubFragment(Fragment parent, Class<? extends Fragment> clazz) {
		showSubFragment(parent, clazz, null);
	}

	public static void showSubFragment(Fragment parent, Class<? extends Fragment> clazz, Bundle args) {
		showSubFragment(parent, clazz.getName(), args);
	}
	
	public static void showSubFragmentWithTag(Fragment parent, Class<? extends Fragment> clazz, String tag) {
		showSubFragment(parent, clazz.getName(), null, tag);
	}

	public static void showSubFragment(Fragment parent, String clazz, Bundle args) {
		// Instantiate fragment
		Fragment fragment = Fragment.instantiate(parent.getActivity(), clazz, args);
		mHandler.removeMessages(0);
		mHandler.sendEmptyMessageDelayed(0, 15000);

		// Begin transaction
		parent.getFragmentManager().beginTransaction()
		.detach(parent)
		.add(parent.getId(), fragment)
		.addToBackStack(clazz)
		.commit();
		setCurFragment(fragment);
	}

	
	public static void showSubFragment(Fragment parent, String clazz, Bundle args, String tag) {
		// Instantiate fragment
		mHandler.removeMessages(0);
		mHandler.sendEmptyMessageDelayed(0, 15000);
		Fragment fragment = Fragment.instantiate(parent.getActivity(), clazz, args);

		// Begin transaction
		parent.getFragmentManager().beginTransaction()
		.detach(parent)
		.add(parent.getId(), fragment, tag)
		.addToBackStack(clazz)
		.commit();
		setCurFragment(fragment);
	}
	
	public static void showPopBottomFragment(Fragment parent, Class<? extends Fragment> clazz, Bundle args, SeekBarMenuItem menuItem) {
		// Instantiate fragment
		mHandler.removeMessages(0);
		mHandler.sendEmptyMessageDelayed(0, 15000);
		PopBottomFragment fragment = (PopBottomFragment) Fragment.instantiate(parent.getActivity(), clazz.getName(), args);
		
		fragment.addSeekBarMenuItem(menuItem);
		
		// Begin transaction
		parent.getFragmentManager().beginTransaction()
		.detach(parent)
		.add(parent.getId(), fragment)
		.addToBackStack(clazz.getName())
		.commit();
		setCurFragment(fragment);
	}
	//*******add by huangfh@tcl.com 08-29
	public static void showPopBottomFragment(Fragment parent, Class<? extends Fragment> clazz, Bundle args, SpinnerMenuItemExt menuItem) {
		// Instantiate fragment
		mHandler.removeMessages(0);
		mHandler.sendEmptyMessageDelayed(0, 15000);
		PopBottomFragment fragment = (PopBottomFragment) Fragment.instantiate(parent.getActivity(), clazz.getName(), args);
		
		fragment.addSeekBarMenuItem(menuItem);
		
		// Begin transaction
		parent.getFragmentManager().beginTransaction()
		.detach(parent)
		.add(parent.getId(), fragment)
		.addToBackStack(clazz.getName())
		.commit();
		setCurFragment(fragment);
	}
	//*****end
	public static void popBackSubFragment(FragmentManager fm, Class<? extends Fragment> clazz) {
		fm.popBackStack(clazz.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
	}

	public static void popBackSubFragment(Fragment subFragment) {
		if(null != subFragment)
			subFragment.getFragmentManager().popBackStack(subFragment.getClass().getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
	}

	public static void popAllBackStacks(FragmentManager fm) {
		if(fm.getBackStackEntryCount() > 1)
		{
			if((FragmentUtils.getCurFragment() == fm.findFragmentByTag("autosearch"))
					||(FragmentUtils.getCurFragment() == fm.findFragmentByTag("dtvManualSearch"))
					||(FragmentUtils.getCurFragment() == fm.findFragmentByTag("atvManualSearch"))
					||(FragmentUtils.getCurFragment() == fm.findFragmentByTag("atvManualSetting")))
				return;
		}
		int count = fm.getBackStackEntryCount();
		if (count > 0) {
			BackStackEntry first = fm.getBackStackEntryAt(0);
			fm.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
		}
	}
	
	public static Fragment getCurFragment(){
		return curFragment;
	}
	
	public static void setCurFragment(Fragment f){
		curFragment = f;
	}
}
