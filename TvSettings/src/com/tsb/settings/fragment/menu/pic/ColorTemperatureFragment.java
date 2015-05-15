package com.tsb.settings.fragment.menu.pic;

import android.app.TvManager;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.tsb.settings.R;
import com.tsb.settings.fragment.menu.BaseMenuFragment;
import com.tsb.settings.menu.MenuItem;
import com.tsb.settings.menu.SeekBarMenuItem;
import com.tsb.settings.menu.SpinnerMenuItem;
import com.tsb.settings.menu.MenuItem.OnValueChangeListener;
import com.tsb.settings.util.FragmentUtils;

import java.util.List;

public class ColorTemperatureFragment extends BaseMenuFragment implements OnClickListener {
	
	private static final int[] VALUE_COLOR_TEMP = {
//		TvManager.SLR_COLORTEMP_USER,
		TvManager.SLR_COLORTEMP_NORMAL,
//		TvManager.SLR_COLORTEMP_WARMER,
		TvManager.SLR_COLORTEMP_WARM,
		TvManager.SLR_COLORTEMP_COOL,
//		TvManager.SLR_COLORTEMP_COOLER
	};
	
	// Items
	private SpinnerMenuItem mItemMode;
	private SeekBarMenuItem mItemRed;
	private SeekBarMenuItem mItemGreen;
	private SeekBarMenuItem mItemBlue;
	
	@Override
	protected View onInflateLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.layout_fragment_base_menu_dialog, container, false);
		return v;
	}
	
	@Override
	public void onCreateMenuItems(List<MenuItem> items) {
		final TvManager tm = (TvManager) getActivity().getSystemService(Context.TV_SERVICE);
		Resources r = getResources();
		// Picture Mode
		mItemMode = MenuItem.createSpinnerItem(R.string.STRING_COLOUR_TEMP);
		mItemMode.setSpinnerOptionsByArray(r.getStringArray(R.array.setting_pic_color_temp), VALUE_COLOR_TEMP);
		mItemMode.setOnValueChangeListener(new OnValueChangeListener() {

			@Override
			public void onValueChanged(MenuItem item, int value) {
				tm.setColorTempMode(value);
				updateColorSetting(tm);
			}

		});
		mItemMode.setCurrentValue(tm.getColorTempMode());
		items.add(mItemMode);
		
		// Red Level
		mItemRed = MenuItem.createSeekBarItem(R.string.STRING_RED_LEVEL).setBoundary(0, 100);
		mItemRed.setOnValueChangeListener(new OnValueChangeListener() {
			
			@Override
			public void onValueChanged(MenuItem item, int value) {
				tm.setColorTempROffset(value*10);
			}
		});
		items.add(mItemRed);
		
		// Green Level
		mItemGreen = MenuItem.createSeekBarItem(R.string.STRING_GREEN_LEVEL).setBoundary(0, 100);
		mItemGreen.setOnValueChangeListener(new OnValueChangeListener() {
			
			@Override
			public void onValueChanged(MenuItem item, int value) {
				tm.setColorTempGOffset(value*10);
			}
		});
		items.add(mItemGreen);
		
		// Blue Level
		mItemBlue = MenuItem.createSeekBarItem(R.string.STRING_BLUE_LEVEL).setBoundary(0, 100);
		mItemBlue.setOnValueChangeListener(new OnValueChangeListener() {
			
			@Override
			public void onValueChanged(MenuItem item, int value) {
				tm.setColorTempBOffset(value*10);
			}
		});
		items.add(mItemBlue);
		
		/*// Reset
		items.add(MenuItem.createTextItem(R.string.STRING_RESET)
				.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						tm.mTvManager.resetColorTemp();
						updateColorSetting(tm.mTvManager);
					}
				}));
		*/
		updateColorSetting(tm);
	}
	
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		View itemView = mListView.getSelectedView();
		if (KeyEvent.ACTION_DOWN == event.getAction()) {
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
			case KeyEvent.KEYCODE_BACK:
				FragmentUtils.popBackSubFragment(this);
				return true;
				
			
			default:
				return false;
			}
		}
		
		return true;
	}	
	
	private void updateColorSetting(TvManager tm) {
		mItemRed.setCurrentProgress(tm.getColorTempROffset()/10);
		mItemGreen.setCurrentProgress(tm.getColorTempGOffset()/10);
		mItemBlue.setCurrentProgress(tm.getColorTempBOffset()/10);
	}

	@Override
	public int getTitle() {
		return R.string.STRING_COLOUR_TEMP;
	}

	@Override
	public void onClick(View v) {
	    FragmentUtils.popBackSubFragment(this);
	}

}
