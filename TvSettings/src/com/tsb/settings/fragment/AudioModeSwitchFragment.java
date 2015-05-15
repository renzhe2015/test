package com.tsb.settings.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tsb.settings.R;
import com.tsb.settings.TvManagerHelper;
import com.tsb.settings.TvManagerHelper.AudioInformation;

public class AudioModeSwitchFragment extends BaseFragment {
	
	public static final int KEYCODE = 255;//KeyEvent.KEYCODE_AUDIO;

	private static final long DELAY_DISMISS = 3000;
	private TvManagerHelper mTvManager;
	private Handler mHandler;

	// View
	private TextView mTextView;
	
	private final Runnable mRunDismiss = new Runnable() {
		
		@Override
		public void run() {
			getFragmentManager().beginTransaction().remove(AudioModeSwitchFragment.this).commit();
		}
	};

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mTvManager = TvManagerHelper.getInstance(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHandler = new Handler();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.layout_fragment_text_upper_right,
				container, false);
		mTextView = (TextView) v.findViewById(R.id.text_upper_right);
		return v;
	}

	@Override
	public void onStart() {
		super.onStart();
		updateView();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		// Post dismiss
		mHandler.postDelayed(mRunDismiss , DELAY_DISMISS);
	}

	@Override
	public void onPause() {
		super.onPause();
		mHandler.removeCallbacks(mRunDismiss);
	}

	private void updateView() {
	    int source = mTvManager.getInputSource();
	    if (TvManagerHelper.isAtvSource(source)) {
	        AudioInformation info = mTvManager.getCurrentAudioInformations();
	        mTextView.setText(info.language);
	    } else if (TvManagerHelper.isDtvSource(source)) {
	        int primaryLang = mTvManager.mTvManager.getDTVAudioPrimaryLang();
	        int secondLang = mTvManager.mTvManager.getDTVAudioSecondaryLang();
	        int type = mTvManager.mTvManager.getDTVAudioType();
	        String txt = String.format("%d, %d, %d",  primaryLang, secondLang, type);
	        mTextView.setText(txt);
	    }
	        
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KEYCODE:
			return true;
		default:
			return false;
		}
	}

}
