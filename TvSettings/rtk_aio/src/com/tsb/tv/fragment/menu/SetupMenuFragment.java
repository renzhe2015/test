package com.tsb.tv.fragment.menu;

import android.view.View;
import android.view.View.OnClickListener;

import com.tsb.tv.R;
import com.tsb.tv.TvManagerHelper;
import com.tsb.tv.fragment.menu.setup.AtvManualTuningFragment;
import com.tsb.tv.fragment.menu.setup.AutoTuningFragment;
import com.tsb.tv.fragment.menu.setup.ConfirmDialogFragment;
import com.tsb.tv.fragment.menu.setup.DtvManualTuningFragment;
import com.tsb.tv.fragment.menu.setup.ResetTvFragment;
import com.tsb.tv.fragment.menu.setup.SystemInfoFragment;
import com.tsb.tv.fragment.quickset.QuickSelectCountryFragment;
import com.tsb.tv.menu.MenuItem;
import com.tsb.tv.menu.MenuItem.OnValueChangeListener;
import com.tsb.tv.util.FragmentUtils;

import java.util.List;

public class SetupMenuFragment extends BaseMenuFragment {

	private static final int[] VALUES_COUNTRY = {
			TvManagerHelper.COUNTRY_BAHRAIN,
			TvManagerHelper.COUNTRY_INDIA,
			TvManagerHelper.COUNTRY_INDONESIA,
			TvManagerHelper.COUNTRY_ISRAEL,
			TvManagerHelper.COUNTRY_KUWAIT,
			TvManagerHelper.COUNTRY_MALAYSIA,
			TvManagerHelper.COUNTRY_PHILIPPINES,
			TvManagerHelper.COUNTRY_QATAR,
			TvManagerHelper.COUNTRY_SAUDI_ARABIA,
			TvManagerHelper.COUNTRY_SINGAPORE,
			TvManagerHelper.COUNTRY_THAILAND,
			TvManagerHelper.COUNTRY_UNITED_ARAB_EMIRATES,
			TvManagerHelper.COUNTRY_VIETNAM };
	
	private static final int[] VALUES_TV_LOCATION = {
			TvManagerHelper.TV_LOCATION_HOME, TvManagerHelper.TV_LOCATION_STORE };
	
	@Override
	public void onCreateMenuItems(List<MenuItem> items) {
		final TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());
		final int currentSource = tm.getInputSource();
		// Country: 
		/* [AIO]final String[] contries = getResources().getStringArray(R.array.setup_country);
		int countryIdx = indexOf(VALUES_COUNTRY, tm.getCountry());
		items.add(
			MenuItem.createSpinnerItem(R.string.STRING_COUNTRY)
			.setSpinnerOptionsByArray(contries)
			.setCurrentPosition(countryIdx)
			.setOnValueChangeListener(new OnValueChangeListener() {
				
				@Override
				public void onValueChanged(MenuItem item, int value) {
					tm.setCountry(VALUES_COUNTRY[value]);
				}
			})
		); */
		
		// Auto Tuning
		/* [AIO] items.add(
			MenuItem.createTextItem(R.string.STRING_AUTO_TUNING)
			.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					ConfirmDialogFragment.show(getParentFragment(), AutoTuningFragment.class,
							null, R.string.STRING_AUTO_TUNING, R.string.STRING_QUICKSETUP_INFO);
				}
			})
		); */

		// Manual Tuning ATV
		/* [AIO] items.add(
			MenuItem.createTextItem(R.string.STRING_ATV_MANUAL_TUNING)
			.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
				    FragmentUtils.showSubFragment(getParentFragment(), AtvManualTuningFragment.class);
				}
			})
			.setEnable(TvManagerHelper.isAtvSource(currentSource))
		); */

		// Manual Tuning DTV
		/* [AIO] items.add(
			MenuItem.createTextItem(R.string.STRING_DTV_MANUAL_TUNING)
			.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
				    FragmentUtils.showSubFragment(getParentFragment(), DtvManualTuningFragment.class);
				}
			})
			.setEnable(TvManagerHelper.isDtvSource(currentSource)
					|| TvManagerHelper.isIdtvSource(currentSource))
		); */		

		// Quick Setup 
		items.add(
			MenuItem.createTextItem(R.string.STRING_QUICK_SETUP)
			.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					ConfirmDialogFragment.show(getParentFragment(),
							QuickSelectCountryFragment.class, null,
							R.string.STRING_QUICK_SETUP, R.string.STRING_QUICKSETUP_INFO);
				}
			})
		);
	
		// Location: [Home, Store]
		final String[] locations = getResources().getStringArray(R.array.setup_option_location);
		int locationIdx = indexOf(VALUES_TV_LOCATION, tm.getTvLocation());
		items.add(
			MenuItem.createSpinnerItem(R.string.STRING_LOCATION)
			.setSpinnerOptionsByArray(locations)
			.setCurrentPosition(locationIdx)
			.setOnValueChangeListener(new OnValueChangeListener() {
				
				@Override
				public void onValueChanged(MenuItem item, int value) {
					tm.setTvLocation(VALUES_TV_LOCATION[value]);
				}
			})
		);	

		// System Information
		items.add(
			MenuItem.createTextItem(R.string.STRING_SYSTEM_INFO)
			.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					FragmentUtils.showSubFragment(getParentFragment(), SystemInfoFragment.class);
				}
			})
		);
		
		// Reset
		items.add(
			MenuItem.createTextItem(R.string.STRING_RESET_TV)
			.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
				    FragmentUtils.showSubFragment(getParentFragment(), ResetTvFragment.class);
				}
			})
		);
	}

	@Override
	public int getTitle() {
		return R.string.STRING_SETUP;
	}

}
