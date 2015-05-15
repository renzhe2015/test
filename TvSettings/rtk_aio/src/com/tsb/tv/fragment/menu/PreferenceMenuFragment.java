package com.tsb.tv.fragment.menu;

import android.view.View;
import android.view.View.OnClickListener;

import com.tsb.tv.R;
import com.tsb.tv.TvManagerHelper;
import com.tsb.tv.fragment.menu.pref.DisplaySettingFragment;
import com.tsb.tv.menu.MenuItem;
import com.tsb.tv.menu.MenuItem.OnValueChangeListener;
import com.tsb.tv.util.FragmentUtils;

import java.util.List;

public class PreferenceMenuFragment extends BaseMenuFragment {

	@Override
	public void onCreateMenuItems(List<MenuItem> items) {
		final TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());
		// Teletext
		/*[AIO] items.add(
			MenuItem.createSpinnerItem(R.string.STRING_P_TELETEXT)
			.addSpinnerOption(R.string.STRING_AUTO, TvManagerHelper.TELETEXT_AUTO)
			.addSpinnerOption(R.string.STRING_LIST, TvManagerHelper.TELETEXT_LIST)
			.setCurrentPosition(tm.getTeletextMode())
			.setOnValueChangeListener(new OnValueChangeListener() {
				
				@Override
				public void onValueChanged(MenuItem item, int value) {
					tm.setTeletextMode(value);
				}
			})
		);*/
		
		// TODO: Teletext language
		/*[AIO]final String[] languages = getResources().getStringArray(R.array.preference_1_4);
		items.add(
			MenuItem.createSpinnerItem(R.string.STRING_P_TELETEXT_LANG)
			.setSpinnerOptionsByArray(languages)
			.setCurrentPosition(tm.getTeletextLanguage() - 1)
			.setOnValueChangeListener(new OnValueChangeListener() {
				
				@Override
				public void onValueChanged(MenuItem item, int value) {
					int lang = value + 1;// 1~4 ?
					tm.setTeletextLanguage(lang);
				}
			})
			.setEnable(false)
		);*/
		
		// Display Settings 
		items.add(
			MenuItem.createTextItem(R.string.STRING_DISPLAY_SETTINGS)
			.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					FragmentUtils.showSubFragment(getParentFragment(), DisplaySettingFragment.class);
				}
			})
		);
		
		// TODO: AV Connection
		items.add(
			MenuItem.createTextItem(R.string.STRING_P_AV_CONNECTION)
			.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
				}
			})
			.setEnable(false)
		);
		
		// TODO: Energy Saving Settings
		items.add(
			MenuItem.createTextItem(R.string.STRING_P_ENERGY_SAVING)
			.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
				}
			})
			.setEnable(false)
		);
		
		// Blue Screen
		items.add(
			MenuItem.createSpinnerItem(R.string.STRING_P_BLUE_SCREEN)
			.addSpinnerOption(R.string.STRING_ON)
			.addSpinnerOption(R.string.STRING_OFF)
			.setCurrentPosition(tm.isBlueScreenEnabled()? 0 : 1)
			.setOnValueChangeListener(new OnValueChangeListener() {
				
				@Override
				public void onValueChanged(MenuItem item, int value) {
					tm.setBlueScreenEnable(value == R.string.STRING_ON);
				}
			})
		);
	}

	@Override
	public int getTitle() {
		return R.string.STRING_PREFERENCES;
	}

}
