package com.tsb.settings.fragment.menu;

import android.view.View;
import android.view.View.OnClickListener;

import com.tsb.settings.R;
import com.tsb.settings.TvManagerHelper;
import com.tsb.settings.menu.MenuItem;
import com.tsb.settings.menu.MenuItem.OnValueChangeListener;

import java.util.List;

public class PvrMenuFragment extends BaseMenuFragment {
	
	private static final int[] VALUE_TIME_SHIFT_SIZE = {
		TvManagerHelper.PVR_TIME_SHIFT_SIZE_512M,
		TvManagerHelper.PVR_TIME_SHIFT_SIZE_1G,
		TvManagerHelper.PVR_TIME_SHIFT_SIZE_2G,
		TvManagerHelper.PVR_TIME_SHIFT_SIZE_4G,
	};

	@Override
	public void onCreateMenuItems(List<MenuItem> items) {
		final TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());
		
		// TODO: Select Disk 
		items.add(
			MenuItem.createTextItem(R.string.STRING_SELECT_DISK)
			.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
				}
			})
			.setEnable(false)
		);
		
		// Time Shift Size
		String[] shiftSizes = getResources().getStringArray(R.array.time_shift_size);
		boolean pvrTimeShiftEnabled = tm.isPvrTimeShiftEnabled();
		final MenuItem itemTimeShiftSize =
			MenuItem.createSpinnerItem(R.string.STRING_TIMESHIFT_SIZE)
			.setSpinnerOptionsByArray(shiftSizes)
			.setCurrentPosition(indexOf(VALUE_TIME_SHIFT_SIZE, tm.getPvrTimeShiftSize()))
			.setOnValueChangeListener(new OnValueChangeListener() {
				
				@Override
				public void onValueChanged(MenuItem item, int value) {
					tm.setPvrTimeShiftSize(VALUE_TIME_SHIFT_SIZE[value]);
				}
			})
			.setEnable(pvrTimeShiftEnabled);
		items.add(itemTimeShiftSize);

		// TODO: Format Start 
		items.add(
			MenuItem.createTextItem(R.string.STRING_FORMAT_START)
			.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
				}
			})
			.setEnable(false)
		);
		
		// TODO: Speed Check
		items.add(
			MenuItem.createTextItem(R.string.STRING_SPEED_CHECK)
			.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
				}
			})
			.setEnable(false)
		);
		
		// Time Shift On/Off
		items.add(
			MenuItem.createSpinnerItem(R.string.STRING_TIMESHIFT_ONOFF)
			.addSpinnerOption(R.string.STRING_ON)
			.addSpinnerOption(R.string.STRING_OFF)
			.setCurrentPosition(pvrTimeShiftEnabled? 0 : 1)
			.setOnValueChangeListener(new OnValueChangeListener() {
				
				@Override
				public void onValueChanged(MenuItem item, int value) {
					boolean enable = value == R.string.STRING_ON;
					tm.setPvrTimeShiftEnable(enable);
					itemTimeShiftSize.setEnable(enable);
				}
			})
		);
	}

	@Override
	public int getTitle() {
		return R.string.STRING_PVR;
	}

}
