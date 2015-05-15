package com.tsb.settings.fragment.menu.adtv;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.TvManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.tsb.settings.R;
import com.tsb.settings.TvManagerHelper;
import com.tsb.settings.fragment.BaseFragment;
import com.tsb.settings.util.FragmentUtils;

public class AdtvAutoTuningNotifyFragment extends BaseFragment implements OnClickListener {
	private TextView bntCancel;
	private TextView bntOk;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.layout_fragment_base_dialog_confirm, container, false);
		bntOk = (TextView)v.findViewById(R.id.button_ok);		
		bntCancel = (TextView)v.findViewById(R.id.button_cancel);
		bntOk.setOnClickListener(this);
		bntCancel.setOnClickListener(this);
		TextView textTitle = (TextView) v.findViewById(R.id.text_title);
		TextView textMessage = (TextView) v.findViewById(R.id.text_message);
		TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());
		if(!isTvNoChannel()){
			if (TvManager.isAtvSource(tm.getInputSource())) {
				textTitle.setText(R.string.STRING_AUTO_TUNING_ATV);
			} else {
				textTitle.setText(R.string.STRING_AUTO_TUNING_DTV);
			}
			textMessage.setText(R.string.STRING_QUICKSETUP_INFO);
		}else{
			textMessage.setText(R.string.STRING_CHANNEL_SEARCH);	
			textMessage.setTextSize(20);
		}
		v.findViewById(R.id.button_ok).requestFocus();
		
		/*隐藏无信号提示框*/
		FragmentManager fm = getFragmentManager();
		Fragment f = fm.findFragmentByTag("no_signal");		
		if (f != null) 
			fm.beginTransaction().remove(f).commit();
		
		return v;
	}

	   @Override
	    public void onStop() {
	        super.onStop();

	        /*显示无信号提示框*/
	    /*    TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());	        		
		    boolean hasSignal = tm.isSignalDisplayReady();
			if (!hasSignal) {
				FragmentManager fm = getFragmentManager();
				Fragment f = fm.findFragmentByTag("no_signal");
				if (f == null) {
					f = new NoSignalFragment();
					fm.beginTransaction()
							.add(R.id.container_background, f, "no_signal")
							.commit();
				}
			}*/
	    }
	   
	@Override
	public void onClick(View v) {
		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		switch (v.getId()) {
			case R.id.button_ok:
				//fm.beginTransaction().remove(this).commit();
				fm.beginTransaction().remove(this).commit();
				Fragment fragment = Fragment.instantiate(getActivity(), AutoTuningFragment.class.getName());
				fm.beginTransaction().replace(android.R.id.content, fragment, "lite").commit();
				//FragmentUtils.showSubFragment(this, AutoTuningFragment.class, null);
				break;
			case R.id.button_cancel:				
			fm.beginTransaction().remove(this).commit();
			getActivity().finish();
//				FragmentUtils.popBackSubFragment(this);
				break;
			default:
				break;
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
//		Tv_strategy.isAutoChannelSearch = false;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {		
		case KeyEvent.KEYCODE_DPAD_LEFT:
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			if(bntOk.isFocused())
				bntCancel.requestFocus();
			else
				bntOk.requestFocus();
			return true;		
		default:
			return false;
		}
	}
	private boolean isTvNoChannel() {
		TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());
		if (TvManagerHelper.isAtvSource(tm.getInputSource())) {
			if(tm.getAtvChannelList().size() == 0)
				return true;
		}else if(TvManagerHelper.isDtvSource(tm.getInputSource())){
			if(tm.getDtvChannelList().size() == 0)
				return true;
		}
		return false;
	}
}
