package com.tsb.settings.fragment.menu.adtv;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.TvManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.tsb.settings.R;
import com.tsb.settings.TvClientTypeList;
import com.tsb.settings.TvManagerHelper;
import com.tsb.settings.broadcast.TvBroadcastDefs;
import com.tsb.settings.util.FragmentUtils;
import com.tsb.settings.util.LogUtil;

public class AtvManualTuningFragment extends Fragment implements OnKeyListener{
	private String TAG = "AtvManualTuningFragment";
	
	public static final String ARG_INDEX = "channel_index";
	private LinearLayout mBeginLayout;
	private LinearLayout mEndLayout;
	private LinearLayout mOptionSearchLayout;
	private LinearLayout mFoundLayout;
	
	private EditText mBeginValue;
	private EditText mEndValue;
	private TextView mUnit1;
	private TextView mUnit2;
	private Button mManualSearch;
	private ProgressBar mProgress;
	private TextView mFoundText;
	
	
	private TvManagerHelper mTvManager;
	private AlertDialog alertDialog;
	private static SharedPreferences sharedPref;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mTvManager = TvManagerHelper.getInstance(getActivity());
	}
	
	boolean isSearching = false;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_fragment_atv_manual_tuning, container, false);
		mBeginLayout = (LinearLayout) view.findViewById(R.id.freq_begin);
		mEndLayout = (LinearLayout) view.findViewById(R.id.freq_end);
		mOptionSearchLayout = (LinearLayout) view.findViewById(R.id.option_search);
		mFoundLayout = (LinearLayout) view.findViewById(R.id.found_layout);
		mBeginValue = (EditText) view.findViewById(R.id.freq_begin_value);
		mEndValue = (EditText) view.findViewById(R.id.freq_end_value);
		mUnit1 = (TextView)view.findViewById(R.id.freq_unit1);
		mUnit2 = (TextView)view.findViewById(R.id.freq_unit2);
		mBeginValue.addTextChangedListener(mTextBeginWatcher);

		//if(mTvManager.getCurFrequency() > 0)
		if(mTvManager.getAtvChannelList().size() > 0){
			if(mTvManager.getCurFrequency()/10000%10==0)
				mBeginValue.setText(String.valueOf(mTvManager.getCurFrequency()/10000/100.0+"0"));			
			else
				mBeginValue.setText(String.valueOf(mTvManager.getCurFrequency()/10000/100.0));
		}
		else
			mBeginValue.setText("47.25");
		mEndValue.setText("865.25");
		mBeginValue.setOnEditorActionListener(mEditorActionListener); 
		mEndValue.setOnEditorActionListener(mEditorActionListener); 
        
		mBeginValue.requestFocus();
		mBeginValue.setSelection(mBeginValue.getText().length());
		mManualSearch = (Button) view.findViewById(R.id.btn_manual_search);
		mManualSearch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				mSearchTask.start();
				if (isSearching) {
			        alertDialog =  new AlertDialog.Builder(getActivity())
					.setTitle(R.string.tcl_kindly_reminder)
					.setMessage(R.string.tcl_search_exit)
					.setPositiveButton(R.string.STRING_YES,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0, int arg1) {						
									alertDialog.dismiss();
									if(isSearching == true)
										stopAtvSearch();
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
					if(mTvManager.mTvManager.getClientType().equals(TvClientTypeList.RowaClient_82)){
						params.width = 395;    
						params.height = 268;    
					}else{
						params.width = 590;    
						params.height = 400; 
					}
			        alertDialog.getWindow().setAttributes(params);  
					
				} else {
					InputMethodManager inputMethodManager = (InputMethodManager) getActivity()
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					if(inputMethodManager.isActive()&&getActivity().getCurrentFocus()!=null)
						inputMethodManager.hideSoftInputFromWindow(
								getActivity().getCurrentFocus().getApplicationWindowToken(), 0);
					
					String str = mBeginValue.getText().toString();
					if(str.equals(""))
					{
						mBeginValue.setText("47.25");
						Toast.makeText(getActivity(), R.string.tcl_atv_manual_input_startfreq, Toast.LENGTH_LONG).show();
						return;
					}					
					float beginValue = Float.valueOf(str)*1000000;	
					
					str = mEndValue.getText().toString();
					if(str.equals(""))
					{
						mEndValue.setText("865.25");
						Toast.makeText(getActivity(), R.string.tcl_atv_manual_input_endfreq, Toast.LENGTH_LONG).show();
						return;
					}
					
					float endValue = Float.valueOf(str)*1000000;
					
					if(beginValue < 47250000)
					{
						Toast.makeText(getActivity(), R.string.tcl_atv_manual_startfreq_min, Toast.LENGTH_LONG).show();
						mBeginValue.setText("47.25");
					}else if(beginValue > 865250000)
					{
						Toast.makeText(getActivity(), R.string.tcl_atv_manual_startfreq_max, Toast.LENGTH_LONG).show();
						mBeginValue.setText("865.25");	
						mEndValue.setText("865.25");	
					}
					else if(endValue < beginValue)
					{
						Toast.makeText(getActivity(), R.string.tcl_atv_manual_endfreq_min, Toast.LENGTH_LONG).show();						
						mBeginValue.setText(mBeginValue.getText().toString());
					}
					else if(endValue > 865250000)
					{
						Toast.makeText(getActivity(), R.string.tcl_atv_manual_endfreq_min, Toast.LENGTH_LONG).show();						
						mBeginValue.setText("865.25");						
					}
					else
					{				
						mBeginLayout.setVisibility(View.GONE);
						mEndLayout.setVisibility(View.GONE);
						mOptionSearchLayout.setVisibility(View.VISIBLE);
						mProgress.setVisibility(View.VISIBLE);
						mFoundLayout.setVisibility(View.GONE);
											
						LogUtil.logD(TAG, "manual search atv.HZ start=" + beginValue +";end=" + endValue, true);
						mTvManager.mTvManager.setScanFrequency((int)beginValue, (int)endValue);
//						mTvManager.mTvManager.tvAutoScanStart(true);
						mTvManager.startAtvSeekScanning(true);
						mManualSearch.setText(R.string.cancel_tv_tunning);
						isSearching = true;
//						mTvManager.mTvManager.tvAutoScanStop();
//						mTvManager.mTvManager.tvAutoScanStart(true);
					}
				}
				
			}
		});

		mProgress = (ProgressBar) view.findViewById(R.id.progress);
		mFoundText = (TextView) view.findViewById(R.id.text_found);
		
		/*隐藏无信号提示框*/
		FragmentManager fm = getFragmentManager();
		Fragment f = fm.findFragmentByTag("no_signal");		
		if (f != null) 
			fm.beginTransaction().remove(f).commit();
		
		return view;
	}
	
	private OnEditorActionListener mEditorActionListener = new OnEditorActionListener() {
		@Override
		public boolean onEditorAction(TextView view, int arg1, KeyEvent arg2) {
			if (arg1 == EditorInfo.IME_ACTION_NEXT) {
				if (view.getId() == R.id.freq_begin_value){
					mEndValue.requestFocus();
					mEndValue.setSelection(mEndValue.getText().length());
				}
				else if (view.getId() == R.id.freq_end_value) {
					mManualSearch.requestFocus();
					InputMethodManager inputMethodManager = (InputMethodManager) getActivity()
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					inputMethodManager.hideSoftInputFromWindow(
							view.getApplicationWindowToken(), 0);
				}
			}
			return true;
		}
	};
	
    private TextWatcher mTextBeginWatcher = new TextWatcher()
    {
        @Override
        public void afterTextChanged(Editable s)
        {
         
        }

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub			
		}

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub			
		}
    };	
    
    private TextWatcher mTextEndWatcher = new TextWatcher()
    {
        @Override
        public void afterTextChanged(Editable s)
        {
        	if((null == mBeginValue.getText())||(mBeginValue.getText().toString().equals("")))
        		return;
            Editor editor = sharedPref.edit();            
            if(Float.valueOf(mEndValue.getText().toString()) > 0)
            {
	        	editor.putString("EndFreq", mEndValue.getText().toString()); 
	        	editor.commit();
            }
        }

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub			
		}

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub			
		}
    };	    
    
//    private String getEditStrUnit(String value){
//    	int i = 0;
//    	String unit = new String("");
//    	String str = value;
//    	for(i = 0; i<str.length(); i++)
//    	{
//    		if(((str.charAt(i) > '0')&&(str.charAt(i) < '9'))||(str.charAt(i) == '.'))
//    		{
//    			continue;
//    		}
//    		unit = String.format("%s%c", unit,str.charAt(i));  
//    	}
//    	
//    	return unit;
//    }
//    
//    private String getEditStrValue(String string){
//    	int i = 0;
//    	String value = new String("");
//    	String str = string;
//    	for(i = 0; i<str.length(); i++)
//    	{
//    		if(((str.charAt(i) > '0')&&(str.charAt(i) < '9'))||(str.charAt(i) == '.'))
//    		{
//    			value = String.format("%s%c", value, str.charAt(i)); 
//    		}
//    		else{
//    			break;
//    		}
//    	}
//    	return value;
//    }
    
    

	Thread mSearchTask = new Thread() {
		public void run() {
			//1 弹出搜索进度条
//			Message msg1 = uiHandler.obtainMessage(UiHandler.SEEK_SHOW);
//			Bundle data = new Bundle();
//			msg1.setData(data);
//			msg1.sendToTarget();
			
			//TODO 2收到搜索频道
//			mTvManager.mTvManager.set;
			
			//3 搜索结束，取消搜索进度条
//			Message msg2 = uiHandler.obtainMessage(UiHandler.SEEK_HIDE);
//			msg2.sendToTarget();
		};
	};
	private IntentFilter mIntentFilter = new IntentFilter(TvBroadcastDefs.ACTION_TV_MEDIA_MESSAGE);
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TvBroadcastDefs.ACTION_TV_MEDIA_MESSAGE.equals(action)) {
                int msg = intent.getIntExtra(TvBroadcastDefs.EXTRA_TV_MEDIA_MESSAGE, 0);
                LogUtil.logD(TAG, "ACTION_TV_MEDIA_MESSAGE EXTRA_TV_MEDIA_MESSAGE=" + msg, false);
                switch (msg) {
       //         	case TvManager.TV_MEDIA_MSG_SCAN_AUTO_COMPLETE:
//                		mScanFinished = true;
                	case TvManager.RT_MSG_SLR_SCAN_FREQ_UPDATE:
                		// Update immediately anyway.
	                	updateView();
                		break;
                	case TvManager.RT_MSG_SLR_SCAN_AUTO_COMPLETE:
                	case TvManager.RT_MSG_SLR_SCAN_MANUAL_COMPLETE:
                	case TvManager.RT_MSG_SLR_SCAN_SEEK_COMPLETE:
                		// Update immediately anyway.
                		LogUtil.logD(TAG, "atv manual scan,stop", true);
//                		updateView();
                		if(null != alertDialog && alertDialog.isShowing())
                			alertDialog.dismiss();	
                		mTvManager.mTvManager.saveChannel();
                		isSearching = false;
                		FragmentUtils.popBackSubFragment(AtvManualTuningFragment.this);
                		break;
               /* 	case TvManager.TV_MEDIA_MSG_SCAN_MANUAL_COMPLETE:
                		// Update immediately anyway.
                		LogUtil.logD(TAG, "manual scan stop", true);
//                		updateView();
                		break;
    		*/
                	default:
                		break;
                }
            }
        }
    };
	
//	private UiHandler uiHandler = new UiHandler();
//	class UiHandler extends Handler {
//		public static final int SEEK_SHOW = 1;
//		public static final int SEEK_HIDE = 2;
//		@Override
//		public void handleMessage(Message msg) {
//			switch(msg.what) {
//			case SEEK_SHOW:
//				Bundle data = msg.getData();
//				
//				break;
//			case SEEK_HIDE:
//				
//				break;
//				
//			default :
//				break;
//			}
//		}
//	}
	
	@Override
    public void onStop() {
        super.onStop();
                
        /*显示无信号提示框*/	
      /*  TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());
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
	public void onDestroy() {
		super.onDestroy();
		
		SharedPreferences sharedPref = getActivity().getSharedPreferences("searchMenuFocus", Context.MODE_PRIVATE); 
		Editor editor = sharedPref.edit();
		editor.putInt("focusId", 2);	
		editor.commit();	
	}

    protected void updateView() {
    	int progress = mTvManager.mTvManager.tvScanInfo(1);
    	int found = mTvManager.mTvManager.tvScanInfo(2);
    	LogUtil.logD(TAG, "update manual search progress=" + progress + ";found=" + found, false);
		mProgress.setProgress(progress);
		mFoundText.setText("" + found);
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
	
	private void stopAtvSearch() {
		FragmentUtils.popBackSubFragment(AtvManualTuningFragment.this);
		mTvManager.stopAtvScanning();
		isSearching = false;
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			stopAtvSearch();
			return true;
		default:
			break;
		}
		return false;
	}
	
}
