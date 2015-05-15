package com.tsb.settings.fragment.menu.adtv;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.TvManager;
import android.app.FragmentManager.BackStackEntry;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tsb.settings.R;
import com.tsb.settings.TvManagerHelper;
import com.tsb.settings.broadcast.TvBroadcastDefs;
import com.tsb.settings.fragment.BaseFragment;
import com.tsb.settings.util.FragmentUtils;

public class AutoTuningFragment extends BaseFragment {
	
	private TvManager mTvManager;
	
	private TextView mTextTitle;
	private TextView mTextLabel;
	private TextView mTextFound;
	private ProgressBar mProgress;
	private Button mBtnCancle;
	private AlertDialog alertDialog;
	
	private boolean mScanFinished = false;
	
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TvBroadcastDefs.ACTION_TV_MEDIA_MESSAGE.equals(action)) {
                int msg = intent.getIntExtra(TvBroadcastDefs.EXTRA_TV_MEDIA_MESSAGE, 0);
                switch (msg) {
                	case TvManager.RT_MSG_SLR_SCAN_AUTO_COMPLETE:
                		if(null != alertDialog && alertDialog.isShowing())
                			alertDialog.dismiss();
                		mScanFinished = true;
                	case TvManager.RT_MSG_SLR_SCAN_FREQ_UPDATE:
                		// Update immediately anyway.
	                	updateView();
                		break;
                	default:
                		break;
                }
            }
        }
    };
    
    private IntentFilter mIntentFilter = new IntentFilter(TvBroadcastDefs.ACTION_TV_MEDIA_MESSAGE);

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mTvManager = TvManagerHelper.getInstance(getActivity()).mTvManager;
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();

		SharedPreferences sharedPref = getActivity().getSharedPreferences("searchMenuFocus", Context.MODE_PRIVATE); 
		Editor editor = sharedPref.edit();
		editor.putInt("focusId", 1);	
		editor.commit();	
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_fragment_auto_tuning, container, false);
		mTextTitle = (TextView) view.findViewById(R.id.text_title);
		TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());
		if (TvManager.isAtvSource(tm.getInputSource())) {
			mTextTitle.setText(R.string.STRING_AUTO_TUNING_ATV);
		} else {
			mTextTitle.setText(R.string.STRING_AUTO_TUNING_DTV);
		}
		mTextLabel = (TextView) view.findViewById(R.id.tv_auto_search_label);
		if (TvManager.isAtvSource(tm.getInputSource())) {
			mTextLabel.setText(R.string.tcl_atv_auto_search_number);
		} else {
			mTextLabel.setText(R.string.tcl_dtv_auto_search_number);
		}
		mTextFound = (TextView) view.findViewById(R.id.text_found);
		mProgress = (ProgressBar) view.findViewById(R.id.progress);
		mBtnCancle = (Button) view.findViewById(R.id.button_cancel);
		mBtnCancle.setOnKeyListener(mOnTabPressed);
		/*初始化搜索到的节目数为0*/
		mTextFound.setText("0");
		
		
		mBtnCancle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
								
		        alertDialog =  new AlertDialog.Builder(getActivity())
		        .setTitle(R.string.tcl_kindly_reminder)
				.setMessage(R.string.tcl_search_exit)
				.setPositiveButton(R.string.STRING_YES,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {						
								alertDialog.dismiss();
								if(!mScanFinished)
									finishScan();
							}
						})
				.setNegativeButton(R.string.STRING_NO, 
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0,
									int arg1) {						
								alertDialog.dismiss();	
							}
						}).create();
		        alertDialog.show(); 
				WindowManager.LayoutParams params = alertDialog.getWindow().getAttributes();    
				params.width = 590;    
				params.height = 410;    
				alertDialog.getWindow().setAttributes(params);  
			}
		});
//		updateView();
	
		return view;
	}
	
	@Override
    public void onStart() {
        super.onStart();
        if (mTvManager.isTvScanning()) {
            mTvManager.tvAutoScanStop();
        }
        mTvManager.tvAutoScanStart(false);
//        updateView();
    }
	
    @Override
    public void onStop() {
        super.onStop();
//        mTvManager.tvAutoScanStop();
        mTvManager.tvAutoScanComplete();
        
//        /*搜索结束后播放第一个节目*/
//        TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());
//		if (TvManagerHelper.isAtvSource(tm.getInputSource()) 
//				&& (tm.getAtvChannelList().size() > 0)
//			||(TvManagerHelper.isDtvSource(tm.getInputSource())
//				&& (tm.getDtvChannelList().size() > 0)))
//		{
//			//tm.setCurrentChannelByIndex(1, null, null);
//		}
//		
//     	        	        
//        /*显示无信号提示框*/		
//	    boolean hasSignal = tm.isSignalDisplayReady();
//		if (!hasSignal) {
//			FragmentManager fm = getFragmentManager();
//			Fragment f = fm.findFragmentByTag("no_signal");
//			if (f == null) {
//				f = new NoSignalFragment();
//				fm.beginTransaction()
//						.add(R.id.container_background, f, "no_signal")
//						.commit();
//			}
//		}

    }

    @Override
	public void onResume() {
		super.onResume();
		getActivity().registerReceiver(mBroadcastReceiver, mIntentFilter);
	}

	@Override
	public void onPause() {
		super.onPause();
		getActivity().unregisterReceiver(mBroadcastReceiver);
	}
	
	private void updateView() {
		int progress = 0;
		int found = 0;
        final boolean isScanning = mTvManager.isTvScanning();

        if(isScanning || mScanFinished) {
	        progress = mTvManager.tvScanInfo(1);
	        found = mTvManager.tvScanInfo(2);
	    }
	    
	    mTextFound.setText(String.valueOf(found));
	    mProgress.setProgress(progress);
	    
	    if (mScanFinished) {
	    	finishScan();
	    }
	}
	
	
    private void finishScan() {
        FragmentManager fm = getFragmentManager();
        // Close all fragments
        if(fm == null)
        	return;
        
        int count = fm.getBackStackEntryCount();
        if(count > 0)
        {
        	//FragmentUtils.popAllBackStacks(fm);  
        	/*退出到主菜单*/
        	BackStackEntry first = fm.getBackStackEntryAt(1);
			fm.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        else{
        	fm.beginTransaction().remove(this).commit();
        	getActivity().finish();
        }
    }
    private View.OnKeyListener mOnTabPressed = new View.OnKeyListener() {

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			if (keyCode == KeyEvent.KEYCODE_BACK&&event.getAction() == KeyEvent.ACTION_DOWN) {
	        	alertDialog =  new AlertDialog.Builder(getActivity())
		        .setTitle(R.string.tcl_kindly_reminder)
				.setMessage(R.string.tcl_search_exit)
				.setPositiveButton(R.string.STRING_YES,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {						
								alertDialog.dismiss();
								if(!mScanFinished)
									finishScan();
							}
						})
				.setNegativeButton(R.string.STRING_NO, 
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0,
									int arg1) {						
								alertDialog.dismiss();	
							}
						}).create();
		        alertDialog.show(); 
				WindowManager.LayoutParams params = alertDialog.getWindow().getAttributes();    
				params.width = 590;    
				params.height = 410;    
				alertDialog.getWindow().setAttributes(params);         	
	            return true;
	        }
			return false;
		}
    	
    };
}
