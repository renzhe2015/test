
package com.tsb.settings.fragment.menu.adtv;

import android.app.Fragment;
import android.app.TvManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.tsb.settings.R;
import com.tsb.settings.broadcast.TvBroadcastDefs;
import com.tsb.settings.fragment.BaseFragment;

public class CheckUpdateFragment extends BaseFragment implements OnClickListener {
	
	public static Fragment instance(boolean hasUpdate) {
		CheckUpdateFragment f = new CheckUpdateFragment();
		if (hasUpdate) {
			Bundle arg = new Bundle();
			arg.putInt("stat", STAT_UPDATE_INFO_READY);
			f.setArguments(arg);
		}
	    return f;
    }
	
	private static final int STAT_CHECKING = 0;
	private static final int STAT_NO_UPDATE = 1;
	private static final int STAT_UPDATE_INFO_READY = 2;
	private static final int STAT_UPDATE_STARTED = 3;
	private static final int STAT_UPDATE_DOWNLOAD = 4;
	private static final int STAT_UPDATE_VERIFY = 5;
	private static final int STAT_UPDATE_OK = 6;
	
	private static final int STAT_UPDATE_DOWNLOAD_ERROR = 10;
	private static final int STAT_UPDATE_VERIFY_ERROR = 11;
	

	private TvManager mTvManager;
	private BroadcastReceiver mReceiver;
	
	private View mButtonUpdate;
	private Button mButtonClose;
	private View mProgress;
	private TextView mTextMessage;
	
	private int mStat = -1;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    mTvManager = (TvManager) getActivity().getSystemService(Context.TV_SERVICE);
	    Bundle args = getArguments();
	    if (args != null) {
	    	mStat = args.getInt("stat", -1);
	    }
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.layout_fragment_check_update, container, false);
		
		mButtonClose = (Button) v.findViewById(R.id.button_cancel);
		mButtonUpdate = v.findViewById(R.id.button_update);
		mButtonClose.setOnClickListener(this);
		mButtonUpdate.setOnClickListener(this);
		
		mProgress = v.findViewById(R.id.progress);
		mTextMessage = (TextView) v.findViewById(R.id.text_message);
		return v;
	}
	
	@Override
    public void onStart() {
	    super.onStart();
	    mReceiver = new MyReceiver();
	    IntentFilter filter = new IntentFilter(TvBroadcastDefs.ACTION_TV_MEDIA_MESSAGE);
	    getActivity().registerReceiver(mReceiver, filter);
	    
	    // Initialize status
	    if (mStat < 0) {
	    	// Start checking
	    	boolean checked = mTvManager.checkOTAUpdate();
	    	if (checked) {
	    		refresh(STAT_CHECKING);
	    	} else {
	    		refresh(STAT_NO_UPDATE);
	    	}
	    } else {
	    	refresh(mStat);
	    }
    }

	@Override
    public void onStop() {
	    super.onStop();
	    getActivity().unregisterReceiver(mReceiver);
    }
	
	private void refresh(int stat) {
		mStat = stat;
		
		// Update button
		mButtonUpdate.setVisibility(
				stat == STAT_UPDATE_INFO_READY ?
				View.VISIBLE : View.GONE);
		
		// Close button
		mButtonClose.setText(
				stat == STAT_UPDATE_OK ?
				R.string.STRING_OK : R.string.STRING_CLOSE);
		mButtonClose.setVisibility(
				stat == STAT_CHECKING ||
				stat == STAT_NO_UPDATE ||
				stat == STAT_UPDATE_INFO_READY ||
				stat == STAT_UPDATE_OK ||
				stat == STAT_UPDATE_VERIFY_ERROR ||
				stat == STAT_UPDATE_DOWNLOAD_ERROR
				? View.VISIBLE: View.INVISIBLE);

		// Spinner
		mProgress.setVisibility(
				stat == STAT_CHECKING ||
				stat == STAT_UPDATE_STARTED ||
				stat == STAT_UPDATE_DOWNLOAD ||
				stat == STAT_UPDATE_VERIFY
				? View.VISIBLE: View.INVISIBLE);
		
		// Message
		switch(stat) {
			case STAT_CHECKING:
				mTextMessage.setText(R.string.msg_checking_update);
				break;
			case STAT_NO_UPDATE:
				mTextMessage.setText(R.string.msg_no_update_available);
				break;
			case STAT_UPDATE_INFO_READY:
				mTextMessage.setText(R.string.msg_new_update_available);
				break;
			case STAT_UPDATE_STARTED:
				mTextMessage.setText(R.string.msg_prepare_update);
				break;
			case STAT_UPDATE_DOWNLOAD:
				mTextMessage.setText(R.string.msg_downlaoding_update);
				break;
			case STAT_UPDATE_DOWNLOAD_ERROR:
				mTextMessage.setText(R.string.msg_download_fail);
				break;
			case STAT_UPDATE_VERIFY:
				mTextMessage.setText(R.string.msg_verify_update);
				break;
			case STAT_UPDATE_VERIFY_ERROR:
				mTextMessage.setText(R.string.msg_verify_update_fail);
				break;
			case STAT_UPDATE_OK:
				mTextMessage.setText(R.string.msg_update_success);
				break;
			default:
				break;
		}
	}
	
	@Override
    public void onClick(View v) {
	    switch(v.getId()) {
	    	case R.id.button_update:
	    		mTvManager.startOTAUpdate(0);
	    		refresh(STAT_UPDATE_STARTED);
	    		break;
	    	case R.id.button_cancel:
	    		getFragmentManager().beginTransaction().remove(this).commit();
	    		break;
	    }
    }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			// Lock back key
			case KeyEvent.KEYCODE_BACK:
				boolean locked = false;
				if (mButtonClose != null) {
					locked = mButtonClose.getVisibility() != View.VISIBLE;
				}
				return locked;
			default:
				return super.onKeyDown(keyCode, event);
		}
	}

	private class MyReceiver extends BroadcastReceiver {

		@Override
        public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        if (TvBroadcastDefs.ACTION_TV_MEDIA_MESSAGE.equals(action)) {
	        	int msg = intent.getIntExtra(TvBroadcastDefs.EXTRA_TV_MEDIA_MESSAGE, 0);
	        	switch (msg) {
	        		case TvManager.RT_MSG_SI_SSU_SWINFO_NOT_FOUND:
	        			refresh(STAT_NO_UPDATE);
	        			break;
	        		case TvManager.RT_MSG_SI_SSU_SWINFO_READY:
	        		case TvManager.RT_MSG_SI_SSU_BKGD_SCAN_FIND_IMAGE:
	        			refresh(STAT_UPDATE_INFO_READY);
	        			mButtonUpdate.requestFocus();
	        			break;
	        		case TvManager.RT_MSG_SI_SSU_SW_VERIFY_UPDATE:
	        			refresh(STAT_UPDATE_VERIFY);
	        			break;
	        		case TvManager.RT_MSG_SI_SSU_SW_VERIFY_ERROR:
	        			refresh(STAT_UPDATE_VERIFY_ERROR);
	        			break;
	        		case TvManager.RT_MSG_SI_SSU_SW_DOWNLOAD_UPDATE:
	        			refresh(STAT_UPDATE_DOWNLOAD);
	        			break;
	        		case TvManager.RT_MSG_SI_SSU_SW_DOWNLOAD_OK:
	        			refresh(STAT_UPDATE_OK);
	        			break;
	        		case TvManager.RT_MSG_SI_SSU_SW_DOWNLOAD_ERROR:
	        			refresh(STAT_UPDATE_DOWNLOAD_ERROR);
	        			break;
	        		default:
	        			break;
	        	}
	        }
        }
		
	}

}
