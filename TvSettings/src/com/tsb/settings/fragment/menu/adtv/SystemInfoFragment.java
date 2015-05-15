
package com.tsb.settings.fragment.menu.adtv;

import android.app.TvManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tsb.settings.R;
import com.tsb.settings.TvManagerHelper;
import com.tsb.settings.fragment.BaseFragment;

public class SystemInfoFragment extends BaseFragment {

	private static final String MANUFACTURER = "RealTek";
	private static final String YEAR = "2013";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.layout_fragment_system_info, container, false);

		TextView textManufacturer = (TextView) v.findViewById(R.id.text_manufacturer);
		TextView textYear = (TextView) v.findViewById(R.id.text_year);
		TextView textBoot = (TextView) v.findViewById(R.id.text_boot_version);
		TextView textApp = (TextView) v.findViewById(R.id.text_app_version);

		TvManager tm = TvManagerHelper.getInstance(getActivity()).mTvManager;

		textManufacturer.setText(MANUFACTURER);
		textYear.setText(YEAR);
		textBoot.setText("Boot version " + tm.getBootcodeVersion());
		textApp.setText("TV Application version " + tm.getSystemVersion());
		return v;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_CENTER:
			case KeyEvent.KEYCODE_ENTER:
				getFragmentManager().popBackStack();
				return true;
			default:
				return super.onKeyDown(keyCode, event);
		}
	}

}
