package com.tsb.settings.fragment.menu.pref;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.tsb.settings.R;
import com.tsb.settings.TvManagerHelper;
import com.tsb.settings.fragment.menu.BaseMenuFragment;
import com.tsb.settings.menu.MenuItem;
import com.tsb.settings.menu.MenuItem.OnValueChangeListener;
import com.tsb.settings.util.FragmentUtils;

import java.util.List;

@Deprecated
public class DisplaySettingFragment extends BaseMenuFragment implements OnClickListener {

	private MenuItem mItemStretch;
	
	@Override
	protected View onInflateLayout(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.layout_fragment_base_menu_dialog, container, false);
		return v;
	}

	@Override
	public void onCreateMenuItems(List<MenuItem> items) {
		final TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());
		
		// Auto Format
		boolean autoFormat = tm.mTvManager.getDisplayAutoFormat();
		items.add(MenuItem.createSpinnerItem(R.string.STRING_AUTO_FORMAT)
				.addSpinnerOption(R.string.STRING_OFF)
				.addSpinnerOption(R.string.STRING_ON)
				.setCurrentValue(autoFormat ? R.string.STRING_ON : R.string.STRING_OFF)
				.setOnValueChangeListener(new OnValueChangeListener() {
					
					@Override
					public void onValueChanged(MenuItem item, int value) {
						boolean autoFormat = value == R.string.STRING_ON;
						tm.mTvManager.setDisplayAutoFormat(autoFormat);
						mItemStretch.setEnable(!autoFormat);
					}
				}));
		
		// 4:3 Stretch
		mItemStretch = MenuItem.createSpinnerItem(R.string.STRING_43_STRETCH)
				.addSpinnerOption(R.string.STRING_OFF)
				.addSpinnerOption(R.string.STRING_ON)
				.setCurrentValue(tm.mTvManager.getDisplay43StretchFormat() ?
						R.string.STRING_ON : R.string.STRING_OFF)
				.setEnable(!autoFormat)
				.setOnValueChangeListener(new OnValueChangeListener() {
					
					@Override
					public void onValueChanged(MenuItem item, int value) {
						tm.mTvManager.setDisplay43StretchFormat(value == R.string.STRING_ON);
					}
				});
		items.add(mItemStretch);
	}

	@Override
	public int getTitle() {
		return R.string.STRING_DISPLAY_SETTINGS;
	}

	@Override
	public void onClick(View v) {
		FragmentUtils.popBackSubFragment(this);
	}

}
