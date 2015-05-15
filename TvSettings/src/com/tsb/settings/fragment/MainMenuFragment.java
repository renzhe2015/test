package com.tsb.settings.fragment;

import java.io.File;
import java.util.Locale;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Instrumentation;
import android.os.Bundle;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.tsb.settings.R;
import com.tsb.settings.TvManagerHelper;
import com.tsb.settings.TvSettings;
import com.tsb.settings.fragment.menu.BaseMenuFragment;
import com.tsb.settings.fragment.menu.ChannelSearchMenuFragment;
import com.tsb.settings.fragment.menu.PictureMenuFragment;
import com.tsb.settings.fragment.menu.SoundMenuFragment;
import com.tsb.settings.fragment.menu.SystemSettingMenuFragment;
import com.tsb.settings.fragment.menu.net.NetworkFragment;
import com.tsb.settings.menu.DeenListView;
import com.tsb.settings.menu.OptionMenuItem;
import com.tsb.settings.settings.view.LayoutItem;
import com.tsb.settings.util.FragmentUtils;
import com.tsb.settings.util.LogUtil;

public class MainMenuFragment extends BaseFragment {

	public static final String ARG_TAB_INDEX = "tab_index";
	private String cls = "MainMenuFragment";
	public static final String ARG_NETWORKTYPE = "networktype";

	private Fragment mCurrentFragment;

	private int mLastSelectionId = -1;
	public static int mLastSelectionId2 = 0;
	private int networktype;
	private View mSelectedTab;
	private TextView Ratio;
	private TextView Sound;
	private TextView Mode;
	private int currentSourceType;
	public static FragmentManager fm;
	public static boolean isRemoteControlPressed = false;

	public static boolean fromLanguageSetting=false;
	public static boolean fromNetSetting=false;
	private final int HOTEL_CHANNEL_LOCK=2;
	private final int HOTEL_LOCK=1;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view;
		view= inflater.inflate(R.layout.layout_fragment_main_menu, null);
		/*非ATV、DTV隐藏频道*/
		TextView scan = (TextView)view.findViewById(R.id.btn_tab_scan);
		TextView sound = (TextView)view.findViewById(R.id.btn_tab_sound);
		TextView net = (TextView)view.findViewById(R.id.btn_tab_network);	
		TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());
		Log.i("hfh", TvSettings.isFormTv+"  "+tm.mTvManager.getHotelInitData(HOTEL_LOCK)+" "+tm.mTvManager.getHotelInitData(1));
		if (!(TvManagerHelper.isAtvSource(tm.getInputSource())
				||TvManagerHelper.isDtvSource(tm.getInputSource()))||!TvSettings.isFormTv||(tm.mTvManager.getHotelInitData(HOTEL_LOCK)==1&&tm.mTvManager.getHotelInitData(HOTEL_CHANNEL_LOCK)==1)){
		//if (!(TvManagerHelper.isAtvSource(tm.getInputSource())
				//	||TvManagerHelper.isDtvSource(tm.getInputSource()))||!TvSettings.isFormTv){
			scan.setVisibility(View.GONE);
			sound.setNextFocusDownId(net.getId());
			net.setNextFocusUpId(sound.getId());
		} else {
			scan.setVisibility(View.VISIBLE);
			sound.setNextFocusDownId(scan.getId());
			net.setNextFocusUpId(scan.getId());
		}
		fm = getActivity().getFragmentManager();

		// Setup tabs
		ViewGroup tabHost = (ViewGroup) view.findViewById(R.id.container_tabs);
		for (int i = 0; i < tabHost.getChildCount(); i++) {
			View tab = tabHost.getChildAt(i);
			tab.setOnFocusChangeListener(mOnTabFocused);
			tab.setOnKeyListener(mOnTabPressed);
			tab.setClickable(true);
			tab.setFocusableInTouchMode(true);
		}

		// Get focus tab id
		Bundle args = getArguments();
		if (savedInstanceState != null) {
			// With save instance states
			mLastSelectionId = savedInstanceState.getInt("selection", R.id.btn_tab_picture);
		} else if (args != null && mLastSelectionId < 0) {
			// First create with arguments
			networktype=args.getInt(ARG_NETWORKTYPE,-1);
			int idx = args.getInt(ARG_TAB_INDEX, 0);
			if (idx < tabHost.getChildCount()) {
				mLastSelectionId = tabHost.getChildAt(idx).getId();
			}
		} else if (mLastSelectionId < 0) {
			// First create
			mLastSelectionId = R.id.btn_tab_picture;
		}// else use retained value

		// Request focus
		if(null == view.findViewById(mLastSelectionId))
			view.findViewById(R.id.btn_tab_picture).requestFocus();
		else
			view.findViewById(mLastSelectionId).requestFocus();
		return view;
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if(FragmentUtils.mHandler != null)
			FragmentUtils.mHandler.removeMessages(0);
	}

	@Override
	public void onStop(){
		super.onStop();
		OptionMenuItem.ExsitPopView();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("selection", mLastSelectionId);
	}

	private View.OnKeyListener mOnTabPressed = new View.OnKeyListener() {

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				FragmentUtils.mHandler.removeMessages(0);
				FragmentUtils.mHandler.sendEmptyMessageDelayed(0, 15*1000);  //add by lly,for hiding main menu
				switch (keyCode) {
				case KeyEvent.KEYCODE_MENU:				
				case KeyEvent.KEYCODE_SETUP:
					FragmentUtils.popAllBackStacks(fm);
					return true;
				case KeyEvent.KEYCODE_DPAD_RIGHT:
					Log.d("MainMenuFragment", "====>press Right key");
					mLastSelectionId2=mLastSelectionId;
					((TextView)v).setTextColor(getResources().getColor(R.color.font_focus_color));
					isRemoteControlPressed = true;
					if(v.getId()==R.id.btn_tab_network){
						LayoutItem networkChoice=(LayoutItem)v.getRootView().findViewById(R.id.spinner_network_choice);
						if(networkChoice!=null){
							networkChoice.requestFocus();
						}
					}
					else{
						DeenListView lv = (DeenListView) v.getRootView().findViewById(R.id.list);
						if (lv != null) {
							Log.d("MainMenuFragment", "====>Second menu request focus");
							lv.requestFocus();
							lv.setSelection(0,false);
						}
					}
					return true;
				case KeyEvent.KEYCODE_DPAD_UP:
				case KeyEvent.KEYCODE_DPAD_DOWN:
					((TextView)v).setTextColor(getResources().getColor(R.color.font_enable_color));
					break;
				default:
					return false;
				}
			}
			return false;
		}
	};

	private View.OnFocusChangeListener mOnTabFocused = new View.OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (hasFocus && !isDetached()&&!fromNetSetting) {
				((TextView)v).setTextColor(getResources().getColor(R.color.font_focus_color));
				final int id = v.getId();	
				mLastSelectionId = id;
				showChildFragment(id);
			}else {
				if(!isRemoteControlPressed)
					((TextView)v).setTextColor(getResources().getColor(R.color.font_enable_color));
				else {
					((TextView)v).setTextColor(getResources().getColor(R.color.font_focus_color));
					isRemoteControlPressed = false;
				}
					
			}
		}
	};
	private Fragment createNetworkFragment(int id) {
		Fragment fragment;
		fragment=new NetworkFragment();
		Bundle arg = new Bundle();
		arg.putInt(BaseMenuFragment.ARG_PARENT_ID, id);
		arg.putInt(NetworkFragment.ARG_NETWORKTYPE, networktype);
		fragment.setArguments(arg);
		return fragment;

	}
	private BaseMenuFragment createChildFragment(int id) {
		BaseMenuFragment fragment;
		switch(id) {
		case R.id.btn_tab_picture:
		default:
			fragment = new PictureMenuFragment();
			break;
		case R.id.btn_tab_sound:
			fragment = new SoundMenuFragment();
			break;
		case R.id.btn_tab_scan:
			fragment = new ChannelSearchMenuFragment();
			break;
		case R.id.btn_tab_setup:
			fragment = new SystemSettingMenuFragment();
			break;
		}

		Bundle arg = new Bundle();
		arg.putInt(BaseMenuFragment.ARG_PARENT_ID, id);
		fragment.setArguments(arg);
		return fragment;
	}

	private void showChildFragment(int id) {
		FragmentManager fm=getChildFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		String tag = getFragmentTag(id);
		mCurrentFragment=FragmentUtils.getCurFragment();
		//		// Hide previous fragment
		if (mCurrentFragment != null) {
			if (tag.equals(mCurrentFragment.getTag())) {
				return;
			}
			ft.detach(mCurrentFragment);
		}		
		if(id==R.id.btn_tab_network){
			Fragment fragment=(Fragment)fm.findFragmentByTag(tag);
			if (fragment == null) {
				fragment = createNetworkFragment(id);

				ft.add(R.id.container_menu, fragment, tag);
			} else if(!fragment.isAdded()){
				ft.attach(fragment);
			}
			ft.commitAllowingStateLoss();
			FragmentUtils.setCurFragment(fragment);
//			mCurrentFragment = fragment;
		}
		// Show fragment
		else{
			BaseMenuFragment fragment = (BaseMenuFragment) fm.findFragmentByTag(tag);
			if (fragment == null) {
				fragment = createChildFragment(id);

				ft.add(R.id.container_menu, fragment, tag);
			} else {
				ft.attach(fragment);
			}
			ft.commitAllowingStateLoss();
			FragmentUtils.setCurFragment(fragment);
//			mCurrentFragment = fragment;
		}

	}
	private static final String getFragmentTag(int id) {
		return String.format(Locale.US, "menu::%d", id);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(FragmentUtils.mHandler!=null){
			FragmentUtils.mHandler.removeMessages(0);
			FragmentUtils.mHandler.sendEmptyMessageDelayed(0, 15*1000);
		}
	}
	//*******end

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if(!fromLanguageSetting)
				getActivity().finish();
		fromLanguageSetting=false;
		fromNetSetting=false;
		super.onDestroy();
	}

}
