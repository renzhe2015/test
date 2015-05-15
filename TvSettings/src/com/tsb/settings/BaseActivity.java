package com.tsb.settings;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.KeyEvent;

import com.tsb.settings.fragment.BaseFragment;
import com.tsb.settings.fragment.DfbFragment;
import com.tsb.settings.fragment.BaseFragment.OnFragmentTransactionListener;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseActivity extends Activity implements OnFragmentTransactionListener{

	private List<BaseFragment> mFragments = new ArrayList<BaseFragment>();
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Start DFB
		TvManagerHelper tm = TvManagerHelper.getInstance(this);
		FragmentManager fm = getFragmentManager();
		Fragment dfb = fm.findFragmentByTag("DFB");
		if (dfb == null && tm.isGingaExisted()) {
			dfb = new DfbFragment();
			getFragmentManager().beginTransaction()
			.add(android.R.id.content, dfb, "DFB").commit();
		}
	}

	@Override
	public void onFragmentResume(BaseFragment fragment) {
		mFragments.add(fragment);
	}

	@Override
	public void onFragmentPause(BaseFragment fragment) {
		mFragments.remove(fragment);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// Intercept key events for attached fragments
		int count = mFragments.size();
		for (int i = count - 1; i >=0; i--) {
			BaseFragment f = mFragments.get(i);
			if (!f.isDetached() && event.dispatch(f, null, f)) {
				return true;
			}
		}
		return super.dispatchKeyEvent(event);
	}

}
