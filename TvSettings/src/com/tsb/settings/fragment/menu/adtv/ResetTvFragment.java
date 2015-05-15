
package com.tsb.settings.fragment.menu.adtv;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.tsb.settings.R;
import com.tsb.settings.TvManagerHelper;
import com.tsb.settings.fragment.BaseFragment;
import com.tsb.settings.util.FragmentUtils;

public class ResetTvFragment extends BaseFragment implements OnClickListener {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.layout_fragment_reset_tv, container, false);
		v.findViewById(R.id.button_ok).setOnClickListener(this);
		v.findViewById(R.id.button_cancel).setOnClickListener(this);
		return v;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.button_ok:
				reset();
				break;
			case R.id.button_cancel:
				break;
			default:
				break;
		}
		FragmentUtils.popBackSubFragment(this);
	}

	private void reset() {
		TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());
		tm.mTvManager.restoreSysDefSetting();

//		Runtime.m_tvManager.SetOnOffFunctionValue("SetResetTV", true);
//		// clear the preference...
//		tvPreference = Tv_strategy.getTvPerference(Tv_strategy.INSTANCE);
//		tvPreference.CleanAll();
//
//		for (int i = 0; i < Runtime.m_sourceListCount; ++i) {
//			((Tv_strategy) pActivity).SetSourceList_SkipStatus((Runtime.m_sourceList[i]), 0);
//			((Tv_strategy) pActivity).SetSourceList_Aka((Runtime.m_sourceList[i]), 0);
//
//		}
//
//		Intent iTvTimer_Intent = new Intent();
//		Bundle iTimerBundle = new Bundle();
//		iTimerBundle.putString("TSBTimerState", "Cancel");
//		iTimerBundle.putString("CallingTSBTimer", "TVApkCallingTimer");
//		iTvTimer_Intent.setAction("com.rtk.TSBTimerSettingMESSAGE");
//		iTvTimer_Intent.putExtras((Bundle) iTimerBundle);
//		pActivity.sendBroadcast(iTvTimer_Intent);
//
//		Log.d("spinner", "stopRpcServer");
//		// Runtime.m_tvManager.stopRpcServer(); //shut down...
//
//		// reboot ..
//		PowerManager pManager = (PowerManager) Tv_strategy.INSTANCE
//				.getSystemService(Context.POWER_SERVICE);
//		pManager.reboot("reboot");
	}

}
