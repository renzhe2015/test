package com.tsb.settings.fragment.menu;

import android.app.Activity;
import android.content.Intent;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;

import com.tsb.settings.R;
import com.tsb.settings.menu.MenuItem;

import java.util.List;

public class ApplicationMenuFragment extends BaseMenuFragment {

	@Override
	public void onCreateMenuItems(List<MenuItem> items) {
		// Media Player
		items.add(
			MenuItem.createTextItem(R.string.STRING_A_MEDIAPLAYER)
			.setOnClickListener(new OnClickListener() {
			
				@Override
				public void onClick(View v) {
					Activity a = getActivity();
					Intent intent = a.getPackageManager()
						.getLaunchIntentForPackage("com.rtk.mediabrowser");
					if (intent != null) {
						a.startActivity(intent);
					}
					getFragmentManager().popBackStack();
				}
		}));
		
		// TODO: On Timer
		items.add(
			MenuItem.createTextItem(R.string.STRING_A_ON_TIMER)
			.setOnClickListener(new OnClickListener() {
			
				@Override
				public void onClick(View v) {
					
				}
			})
			.setEnable(false)
		);
		
		// TODO Sleep Timer
		items.add(
			MenuItem.createTextItem(R.string.STRING_A_SLEEP_TIMER)
			.setOnClickListener(new OnClickListener() {
			
				@Override
				public void onClick(View v) {
					
				}
			})
			.setEnable(false)
		);
		
		// System Setting
		items.add(
			MenuItem.createTextItem(R.string.STRING_SYSTEM_SETTINGS)
			.setOnClickListener(new OnClickListener() {
			
				@Override
				public void onClick(View v) {
					Activity a = getActivity();
					Intent intent = new Intent(Settings.ACTION_SETTINGS);
					a.startActivity(intent);
					getFragmentManager().popBackStack();
				}
		}));
	}

	@Override
	public int getTitle() {
		return R.string.STRING_APPLICATION;
	}

}
