package com.tsb.settings;

import android.os.Bundle;

import com.tsb.settings.fragment.DfbFragment;
import com.tsb.settings.fragment.BaseFragment.OnFragmentTransactionListener;

public class DfbActivity extends BaseActivity implements OnFragmentTransactionListener{


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
			.add(android.R.id.content, new DfbFragment(), "DFB")
			.commit();
		}
	}
	
}
