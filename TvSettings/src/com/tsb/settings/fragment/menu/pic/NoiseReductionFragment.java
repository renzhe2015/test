package com.tsb.settings.fragment.menu.pic;

import android.app.TvManager;
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

public class NoiseReductionFragment extends BaseMenuFragment implements OnClickListener {
	
	private static final int[] VALUE_DRN = {
		TvManager.DNR_OFF,
		TvManager.DNR_LOW,
		TvManager.DNR_MEDIUM,
		TvManager.DNR_HIGH,
		TvManager.DNR_AUTO
	};

	@Override
	protected View onInflateLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.layout_fragment_base_menu_dialog, container, false);
		return v;
	}
	
	@Override
	public void onCreateMenuItems(List<MenuItem> items) {
		final TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());
		/*// TODO: Auto Clean
		items.add(MenuItem.createSpinnerItem(R.string.STRING_AUTO_CLEAN)
				.addSpinnerOption(R.string.STRING_OFF)
				.addSpinnerOption(R.string.STRING_ON)
				.setCurrentPosition(tm.mTvManager.getAutoNR() ? 1 : 0)
				.setOnValueChangeListener(new OnValueChangeListener() {

					@Override
					public void onValueChanged(MenuItem item, int value) {
						tm.mTvManager.setAutoNR(value == R.string.STRING_ON);
					}

				}));

		// TODO: MPEG NR
		items.add(MenuItem.createSpinnerItem(R.string.STRING_MPEGNR)
				.addSpinnerOption(R.string.STRING_OFF)
				.addSpinnerOption(R.string.STRING_ON)
				.setCurrentPosition(tm.mTvManager.getMpegNR() > 0 ? 1 : 0)
				.setOnValueChangeListener(new OnValueChangeListener() {

					@Override
					public void onValueChanged(MenuItem item, int value) {
						tm.mTvManager.setMpegNR(value == R.string.STRING_ON ? 1 : 0);
					}

				}));
		*/
		// DNR
		items.add(MenuItem.createSpinnerItem(R.string.STRING_DNR)
				.setSpinnerOptionsByArray(getResources().getStringArray(R.array.setting_dnr_mode), VALUE_DRN)
				.setCurrentValue(tm.mTvManager.getDNR())
				.setOnValueChangeListener(new OnValueChangeListener() {

					@Override
					public void onValueChanged(MenuItem item, int value) {
						tm.mTvManager.setDNR(value);
					}

				}));
	
	}
	
	@Override
	public int getTitle() {
		return R.string.STRING_NOISE_REDUCTION;
	}

	@Override
	public void onClick(View v) {
	    FragmentUtils.popBackSubFragment(this);
	}

}
