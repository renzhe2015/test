package com.tsb.settings.fragment.menu.adtv;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import com.tsb.settings.fragment.menu.BaseMenuFragment;
import com.tsb.settings.menu.MenuItem;
import com.tsb.settings.menu.OptionMenuItem;
import com.tsb.settings.menu.SpinnerMenuItem;
import com.tsb.settings.util.FragmentUtils;
import com.tsb.settings.util.LogUtil;

public class DtvManualTuningFragment extends BaseMenuFragment implements OnClickListener {

	private static final int[] OPTIONS_BANDWIDTH = {
		TvManager.TUNING_BANDWIDTH_8MHZ,
		TvManager.TUNING_BANDWIDTH_7MHZ,
		TvManager.TUNING_BANDWIDTH_7MHZ_8MHZ
	};
	
	private static class ChannelFrequency {
		public final int index;
		
		protected String name;
		protected int frequency = -1;
//		protected String bandwidth;
		
		protected final TvManagerHelper tm;
		
		ChannelFrequency(TvManagerHelper tm, int index) {
			this.index = index;
			this.tm = tm;
		}
		
		@Override
		public String toString() {
			if (name == null) {
				name = tm.getDtvFrequencyName(index);
			}
			return index + "";
		}
		
		public int getFrequency() {
			if (frequency < 0) {
				frequency = tm.getDtvFrequency(index);
			}
			return frequency;
		}
		
//		public String getBandwidth() {
//			if (bandwidth == null) {
//				bandwidth = tm.getChannelBandwidthByTableIndex(index);
//			}
//			return bandwidth;
//		}
		
		public final Object frequencyDelegate = new Object() {
			
			@Override
			public String toString() {
				return String.valueOf(getFrequency());
			}
		};
	}
	
	public int getIndexByFrequency(float frequency) {
		for (int i = 0; i < mData.size(); i++) {
			LogUtil.logD(TAG, "getIndexByFrequency frequency=" + frequency + ";" + i 
					+ " frequency=" + mData.get(i).getFrequency(), true);
			if (mData.get(i).getFrequency() == frequency) return i;
		}
		return -1;
	}
	
	private TvManagerHelper mTvHelper;
	private static SharedPreferences sharedPref;
	private static int frequency = 0;
	
	private final List<ChannelFrequency> mData = new ArrayList<DtvManualTuningFragment.ChannelFrequency>();
	
	private EditText mChannelText;
	private EditText mFregText;
	private OptionMenuItem mItemBand;
	private Button mBtnConfirm;
	private boolean isBtnPressed = false;

	private int mLastIndex = 1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mTvHelper = TvManagerHelper.getInstance(getActivity());

		// Load data
		mData.clear();
		final int count = mTvHelper.getDtvFrequencyTableSize();
		for (int i = 0; i < count; i++) {
			ChannelFrequency item = new ChannelFrequency(mTvHelper, i);
			mData.add(item);
		}
		isBtnPressed = false;
		
	}
	String TAG = "DtvManualTuningFragment";
	@Override
	protected View onInflateLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.layout_fragment_dtv_manual_tuning, container, false);
		return v;
	}

	@Override
	public void onCreateMenuItems(List<MenuItem> items) {
		// Add options
		for (int i = 0; i < mData.size(); i++) {
			ChannelFrequency c = mData.get(i);
			LogUtil.logD("DtvManualTuning", "dtv index=" + c.index + ";freq=" + c.frequencyDelegate, true);
		}
		
		// Bandwidth option
//		mItemBand = MenuItem.createOptionItem(R.string.STRING_BAND_WIDTH);
//		items.add(mItemBand);
		
//		initPicParameters();
	}
	
//	public void initPicParameters() {
//		new Thread() {
//			@Override
//			public void run() {
//				mHandler.sendEmptyMessageDelayed(REFRESH_ITEM, 0);
//			}
//		}.start();
//	}
	
//	public static final int REFRESH_ITEM = 1;
//	Handler mHandler = new Handler() {
//		public void handleMessage(android.os.Message msg) {
//			switch (msg.what) {
//			case REFRESH_ITEM:
//				if (!isVisible()) {
//					return;
//				}
//				LogUtil.logD(TAG, "REFRESH_ITEM start setOptionsByArray", true);
//				mItemBand.setOptionsByArray(getResources().getStringArray(R.array.band_width),
//						OPTIONS_BANDWIDTH, bandWidthListener);
//				
//				mItemBand.setCurrentValue(mTvHelper.mTvManager.getChannelBandwidth(mLastIndex));
//				break;
//
//			default:
//				break;
//			}
//		};
//	};
//	
//	OnItemClickListener bandWidthListener = new OnItemClickListener() {
//		@Override
//		public void onItemClick(AdapterView<?> arg0, View arg1,
//				int position, long arg3) {
//			if (mItemBand.getPopListTitle().length > position && position >= 0) {
//				mItemBand.setTextValue(mItemBand.getPopListTitle()[position]);
//				mItemBand.getPopView().dismiss();
//				mItemBand.setSelection(position);
//				
//			}
//		}
//	};
	
	private LinearLayout mChannelLayout;
	private LinearLayout mFreqLayout;
	private LinearLayout mBandWidthLayout;
	private ProgressBar mProgress;
	private LinearLayout mFoundLayout;
	private TextView mFoundText;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, container, savedInstanceState);
		mChannelLayout = (LinearLayout) v.findViewById(R.id.item_channel_layout);
		mFreqLayout = (LinearLayout) v.findViewById(R.id.item_freq_layout);
		mBandWidthLayout = (LinearLayout) v.findViewById(R.id.item_bandwidth_layout);
		mBtnConfirm = (Button) v.findViewById(R.id.btn_manual_search);
		
		mChannelText = (EditText) v.findViewById(R.id.item_channel);
		mChannelText.setOnKeyListener(mOnEditKey);
		mChannelText.addTextChangedListener(mTextWatcher); 
		mChannelText.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				LogUtil.logD(TAG, "Channel Text editor action", true);

				if (actionId == EditorInfo.IME_ACTION_NEXT) {
					if (v.getId() == R.id.item_channel){
						mFregText.requestFocus();
					}
				}
					
				String channelStr = mChannelText.getText().toString().trim();
				if((null == channelStr)||(channelStr.equals("")))
				{		
					mChannelText.setText(sharedPref.getString("index", "1"));
					Toast.makeText(getActivity(), R.string.dtv_channel_index_error, Toast.LENGTH_SHORT).show();					
					return true;
				}				
				
				if(channelStr.length()>9)
					channelStr = channelStr.substring(0, 9);
				
				String str = channelStr.substring(0, channelStr.length()-1);
				if(str.equalsIgnoreCase("DS-24 + "))
				{
					str = channelStr.substring(channelStr.length()-1, channelStr.length());
					int value = Integer.valueOf(str);
					mFregText.setText((float)(570000000+(value-1)*8000000)/1000000 + "");
					if(value == 0)
						mLastIndex = 24;
					else
						mLastIndex = 105 + value;
					return true;
				}
				
				int index = Integer.valueOf(channelStr) - 1;
				if (checkChannelIndex(index)) {
					//float freq = mData.get(index).getFrequency()/1000000;
					mFregText.setText((float)mData.get(index).getFrequency()/1000000 + "");
//					mItemBand.setCurrentValue(mTvHelper.mTvManager.getChannelBandwidth(index));
					mLastIndex = index;
				} else {
					mChannelText.setText(sharedPref.getString("index", "1"));
					Toast.makeText(getActivity(), R.string.dtv_channel_index_error, Toast.LENGTH_SHORT).show();
				}
				mFregText.setSelection(mFregText.getText().length());
				return true;
			}
		});
		
		mChannelText.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					LogUtil.logD(TAG, "Channel Text onFocusChange", true);
					String channelStr = mChannelText.getText().toString().trim();
					if((null == channelStr)||(channelStr.equals("")))
					{
						sharedPref = getActivity().getSharedPreferences("dtvManualSearch", Context.MODE_PRIVATE);
						mChannelText.setText(sharedPref.getString("index", "1"));
						Toast.makeText(getActivity(), R.string.dtv_channel_index_error, Toast.LENGTH_SHORT).show();						
						return;
					}
					
					if(channelStr.length()>9)
						channelStr = channelStr.substring(0, 9);
					
					sharedPref = getActivity().getSharedPreferences("dtvManualSearch", Context.MODE_PRIVATE);
		    		Editor editor = sharedPref.edit();
					String str = channelStr.substring(0, channelStr.length()-1);
					if(str.equalsIgnoreCase("DS-24 + "))
					{
						editor.putString("index", mChannelText.getText().toString());
			    		editor.commit();
						str = channelStr.substring(channelStr.length()-1, channelStr.length());
						int value = Integer.valueOf(str);
						mFregText.setText((float)(570000000+(value-1)*8000000)/1000000 + "");
						if(value == 0)
							mLastIndex = 24;
						else 
							mLastIndex = 105 + value;
					}
					else if(channelStr.substring(0, 1).equalsIgnoreCase("D")&&(!str.equalsIgnoreCase("DS-24 + ")))
					{
						mChannelText.setText("DS-24 + 0");
						mChannelText.setSelection(9); 
						Toast.makeText(getActivity(), R.string.dtv_channel_index_error, Toast.LENGTH_SHORT).show();										
					}
					else
					{
						int index = Integer.valueOf(channelStr) - 1;
						if (checkChannelIndex(index)) {
				    		editor.putString("index", mChannelText.getText().toString());
				    		editor.commit();
				    		
							mLastIndex = index;
							mFregText.setText((float)mData.get(index).getFrequency()/1000000 + "");
						} else {
							mChannelText.setText(sharedPref.getString("index", "1"));
							Toast.makeText(getActivity(), R.string.dtv_channel_index_error, Toast.LENGTH_SHORT).show();
						}
					}
				}
			}
		});
		mChannelText.requestFocus();
		mFregText = (EditText) v.findViewById(R.id.item_freq);
		File file = new File("/data/data/com.tsb.tv/shared_prefs/dtvManualSearch.xml");
		if(!file.exists()){
    		sharedPref = getActivity().getSharedPreferences("dtvManualSearch", Context.MODE_PRIVATE);
    		Editor editor = sharedPref.edit();
    		int f = mTvHelper.getCurFrequency();
    		int idx = getIndexByFrequency(f);
    		if(idx > 0)
    		{
	    		editor.putString("index", String.valueOf(idx+1));
	    		editor.putString("freq", String.valueOf(f/10000/(float)100.0));    			
    		}
    		else{
	    		editor.putString("index", "1");
	    		editor.putString("freq", "52.5");
    		}
    		editor.commit();
		}	
		
		int idx = getIndexByFrequency(mTvHelper.getCurFrequency())+1;
		if(idx <= 0)
		{
			sharedPref = getActivity().getSharedPreferences("dtvManualSearch", Context.MODE_PRIVATE);
			mChannelText.setText(sharedPref.getString("index", "1"));		
			mFregText.setText(sharedPref.getString("freq", "52.5"));			
		}
		else{
			mChannelText.setText(idx + "");		
			mFregText.setText((float)mTvHelper.getCurFrequency()/10000/100.0 + "");
		}
		
		mFregText.setOnKeyListener(mOnEditKey);
		mFregText.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				LogUtil.logD(TAG, "Frequency Text editor action", true);
				
				if (actionId == EditorInfo.IME_ACTION_NEXT) {
					if (v.getId() == R.id.item_freq){
						mBtnConfirm.requestFocus();
						InputMethodManager inputMethodManager = (InputMethodManager) getActivity()
								.getSystemService(Context.INPUT_METHOD_SERVICE);
						inputMethodManager.hideSoftInputFromWindow(
								v.getApplicationWindowToken(), 0);						
					}
				}
				
				String freqStr = mFregText.getText().toString().trim();
				
				if((null == freqStr)||(freqStr.equals("")))
				{
					mFregText.setText((float)mTvHelper.mTvManager.getChannelFreqByTableIndex(mLastIndex)/1000000 + "");
					Toast.makeText(getActivity(), R.string.dtv_channel_fequency_error, Toast.LENGTH_SHORT).show();					
					return true;
				}
				
				float freq = Float.valueOf(freqStr);
				if (checkChannelFreq(freq*1000000)) {
					int index = getIndexByFrequency(freq*1000000);
					mLastIndex = index;
					if((freq >= 562)&&(freq <= 602))
					{
						mChannelText.setText("DS-24 + " + ((int)freq-562)/8);
					}
					else
					{
						mChannelText.setText((index+1) + "");
					}
//					mItemBand.setCurrentValue(mTvHelper.mTvManager.getChannelBandwidth(index));
				} else {
					mFregText.setText((float)mTvHelper.mTvManager.getChannelFreqByTableIndex(mLastIndex)/1000000 + "");
					Toast.makeText(getActivity(), R.string.dtv_channel_fequency_error, Toast.LENGTH_SHORT).show();
				}
				return true;
			}
		});
		mFregText.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					LogUtil.logD(TAG, "Frequency Text onFocusChange", true);
					
					String freqStr = mFregText.getText().toString().trim();
					if((null == freqStr)||(freqStr.equals("")))
					{
						mFregText.setText((float)mTvHelper.mTvManager.getChannelFreqByTableIndex(mLastIndex)/1000000 + "");
						Toast.makeText(getActivity(), R.string.dtv_channel_fequency_error, Toast.LENGTH_SHORT).show();					
						return;
					}
					
					float freq = Float.valueOf(freqStr);
					if (checkChannelFreq(freq*1000000)) {
						sharedPref = getActivity().getSharedPreferences("dtvManualSearch", Context.MODE_PRIVATE);
			    		Editor editor = sharedPref.edit();
			    		editor.putString("freq", mFregText.getText().toString());
			    		editor.commit();
			    		
						int index = getIndexByFrequency(freq*1000000);
						mLastIndex = index;
						if((freq >= 562)&&(freq <= 602))
						{
							mChannelText.setText("DS-24 + " + ((int)freq-562)/8);
						}
						else
						{
							mChannelText.setText((index+1) + "");
						}
//						mItemBand.setCurrentValue(mTvHelper.mTvManager.getChannelBandwidth(index));
					} else {
						mFregText.setText((float)mTvHelper.mTvManager.getChannelFreqByTableIndex(mLastIndex)/1000000 + "");
						Toast.makeText(getActivity(), R.string.dtv_channel_fequency_error, Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
						
		mBtnConfirm = (Button) v.findViewById(R.id.btn_manual_search);
		mBtnConfirm.setOnClickListener(this);
		mFoundLayout = (LinearLayout) v.findViewById(R.id.found_layout);
		mProgress = (ProgressBar) v.findViewById(R.id.progress);
		mFoundText = (TextView) v.findViewById(R.id.text_found);
		
		// Register receiver
		IntentFilter filter = new IntentFilter(TvBroadcastDefs.ACTION_TV_MEDIA_MESSAGE);
		getActivity().registerReceiver(mReceiver, filter);
		

		/*隐藏无信号提示框*/
		FragmentManager fm = getFragmentManager();
		Fragment f = fm.findFragmentByTag("no_signal");		
		if (f != null) 
			fm.beginTransaction().remove(f).commit();		
		mChannelText.setSelection(mChannelText.getText().length());
		return v;
	}
	
	TextWatcher mTextWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			String str = "0123456789DdSs-+_- ";
			String tmp = s.toString();
			
			if(start >= tmp.length())
				return;
			
			String str1 = tmp.substring(start, start+1);
			int ret = str.indexOf(str1);			
			if((ret < 0)&&(str1.length()>0))
			{
				str1 = tmp.substring(0, start) + tmp.substring(start+1, tmp.length());
				mChannelText.setText(str1);
				mChannelText.setSelection(start); 
				//Toast.makeText(getActivity(), R.string.dtv_channel_index_error, Toast.LENGTH_SHORT).show();							
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub			
		}

		@Override
		public void afterTextChanged(Editable arg0) {
			// TODO Auto-generated method stub		
			String str = mChannelText.getText().toString().trim();
			if(str.equals(""))
				return;
			
			String str1 = str.substring(0, 1);			
			if((str != null)&&(!str.equals(""))&&(!str1.equalsIgnoreCase("D")))
			{
				int value = Integer.valueOf(str);
				//if((value > 24)&&(value < 30))
				if(value == 24)	
				{
					mChannelText.setText("DS-24 + " + (value-24));
					mChannelText.setSelection(9); 
				}
			}
			else if(str1.equals("+")||(str1.equalsIgnoreCase("S")||(str1.equals("-"))||(str1.equals(" "))))
			{
				mChannelText.setText("DS-24 + 0");
				mChannelText.setSelection(9); 
				Toast.makeText(getActivity(), R.string.dtv_channel_index_error, Toast.LENGTH_SHORT).show();				
			}
			else if(str1.equalsIgnoreCase("D"))
			{
				if((str.length() == 8)&&(!str.equalsIgnoreCase("DS-24 + "))||(str.length() > 9))
				{
					mChannelText.setText("DS-24 + 0");
					mChannelText.setSelection(9); 
					Toast.makeText(getActivity(), R.string.dtv_channel_index_error, Toast.LENGTH_SHORT).show();
				}
				else if(str.length() == 9)
				{
					String str2 = str.substring(8, 9);
					int value = Integer.valueOf(str2);
					if(value > 5)
					{
						mChannelText.setText("DS-24 + 5");
						mChannelText.setSelection(9); 
						Toast.makeText(getActivity(), R.string.dtv_channel_index_error, Toast.LENGTH_SHORT).show();						
					}
				}
			}
				
		}
	};	
	
	protected boolean checkChannelFreq(float frequency) {
		int freq = (int)frequency/1000000;
		if((570 == freq)||(578 == freq)||(586 == freq)
				||(594 == freq)||(602 == freq))
			return true;
		int index = getIndexByFrequency(frequency);
		LogUtil.logD(TAG, "checkChannelFreq index = " + index, true);
		return checkChannelIndex(index);
	}

	protected boolean checkChannelIndex(int index) {
		/*在调用checkChannelIndex()前，index有做减一操作，所以最大值小于57*/
		if (index >= 0 && index < 57/*mTvHelper.getDtvFrequencyTableSize()*/) {
			return true;
		}
		return false;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		String channelStr = mChannelText.getText().toString();
		if((null == channelStr)||(channelStr.equals("")))
		{		
			mChannelText.setText(sharedPref.getString("index", "1"));
//			Toast.makeText(getActivity(), R.string.dtv_channel_index_error, Toast.LENGTH_SHORT).show();					
			return;
		}
		
		if(channelStr.length()>9)
			channelStr = channelStr.substring(0, 9);
		
		String str = channelStr.substring(0, channelStr.length()-1);
		if(str.equalsIgnoreCase("DS-24 + "))
		{
			str = channelStr.substring(channelStr.length()-1, channelStr.length());
			int value = Integer.valueOf(str);
			if(0 == value)
				mLastIndex = 24;
			else
				mLastIndex = 105 + value;
		}
		else
		{		
			int index = Integer.valueOf(channelStr) - 1;
			mLastIndex = index;
		}
	}

	private void setupBandwidthOption(int currentIndex) {
//		mItemBand.clearOptions();
		
		int band = mTvHelper.getDtvBandwidth(currentIndex);
		String title = String.format(Locale.US, "%dMHz", band);
//		mItemBand.addSpinnerOption(title, band);
	}

	@Override
	public int getTitle() {
		return R.string.STRING_DTV_MANUAL_TUNING;
	}

	private void stopDtvSearch() {
		FragmentUtils.popBackSubFragment(DtvManualTuningFragment.this);
		isSearching = false;
	}
	
	private boolean isSearching =false;
	private AlertDialog alertDialog;
	@Override
	public void onClick(View v) {
		isBtnPressed = true;
		if (isSearching) {			
			//stopDtvSearch();

	        alertDialog =  new AlertDialog.Builder(getActivity())
	        .setTitle(R.string.tcl_kindly_reminder)
			.setMessage(R.string.tcl_search_exit)
			.setPositiveButton(R.string.STRING_YES,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {						
							alertDialog.dismiss();
							if(isSearching == true)
								stopDtvSearch();
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
			if(mTvHelper.mTvManager.getClientType().equals(TvClientTypeList.RowaClient_82)){
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
			
			isSearching = true;
			String channelStr = mChannelText.getText().toString();
			if((null == channelStr)||(channelStr.equals("")))
			{		
				mChannelText.setText(sharedPref.getString("index", "1"));
				Toast.makeText(getActivity(), R.string.dtv_channel_index_error, Toast.LENGTH_SHORT).show();					
				return;
			}
			
			if(channelStr.length()>9)
				channelStr = channelStr.substring(0, 9);
			
			int index ;
			String str = channelStr.substring(0, channelStr.length()-1);
			if(str.equalsIgnoreCase("DS-24 + "))
			{
				str = channelStr.substring(channelStr.length()-1, channelStr.length());
				int value = Integer.valueOf(str);
				frequency = 570000000 + (value-1)*8000000;
				if(value == 0)
					index = 24;
				else
					index = 105 + value;
			}
			else
			{
				index = Integer.valueOf(channelStr) - 1;
				ChannelFrequency c = mData.get(index);
				//String name = c.toString();
				frequency = c.getFrequency();
			}
			int bandwidth = TvManager.TUNING_BANDWIDTH_8MHZ; //mItemBand.getCurrentValue();
			
			// Register receiver
			IntentFilter filter = new IntentFilter(TvBroadcastDefs.ACTION_TV_MEDIA_MESSAGE);
			getActivity().registerReceiver(mReceiver, filter);
					
			// Start scan
			mTvHelper.stopDtvScanning();
			mTvHelper.startDtvScanning(frequency, bandwidth , index, TvManager.SOURCE_DTV1);
			mChannelLayout.setVisibility(View.GONE);
			mFreqLayout.setVisibility(View.GONE);
			mBandWidthLayout.setVisibility(View.GONE);
			
			mProgress.setVisibility(View.VISIBLE);
			mFoundLayout.setVisibility(View.VISIBLE);
			mProgress.postInvalidate();
			mBtnConfirm.setText(R.string.cancel_tv_tunning);
			mFoundText.setText("0");
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mHandler.removeMessages(0);
		getActivity().unregisterReceiver(mReceiver);
		
		if(isBtnPressed)
			mTvHelper.stopDtvScanning();
		
		SharedPreferences sharedPref = getActivity().getSharedPreferences("searchMenuFocus", Context.MODE_PRIVATE); 
		Editor editor = sharedPref.edit();
		editor.putInt("focusId", 2);	
		editor.commit();			
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			int messageId = intent.getIntExtra(TvBroadcastDefs.EXTRA_TV_MEDIA_MESSAGE, -1);
			if (true) {
				Log.d("DtvManualTuning", String.format("Receive message: action = %s, id = %d", intent.getAction(), messageId));
			}
			if (messageId == TvManager.RT_MSG_SLR_SCAN_FREQ_UPDATE) {
				// (Only update after finished.)
				updateViewContent();
				
			} else if (messageId == TvManager.RT_MSG_SLR_SCAN_MANUAL_COMPLETE) {
				mHandler.sendEmptyMessageDelayed(0, 3000);/*延迟3S退出，显示搜索结果*/
			}
		}
		
	};
	
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(null != alertDialog && alertDialog.isShowing())
				alertDialog.dismiss();	
			
			mTvHelper.mTvManager.tvScanManualComplete();
			isSearching = false;
			FragmentUtils.popBackSubFragment(DtvManualTuningFragment.this);
		}
	};
	
	private final OnKeyListener mOnEditKey = new OnKeyListener() {		
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				switch (keyCode) {
				case KeyEvent.KEYCODE_DPAD_UP:

					break;
				case KeyEvent.KEYCODE_DPAD_DOWN:

					break;
				default:
					break;
				}
			}
			return false;
		}
	};	
	
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		View itemView = mListView.getSelectedView();
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_LEFT:
			if (itemView != null && event.getAction() == KeyEvent.ACTION_DOWN) {
				MenuItem item = (MenuItem) itemView.getTag();
				item.selectPrev();
				mListView.invalidateViews();
			}
			return true;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			if (itemView != null && event.getAction() == KeyEvent.ACTION_DOWN) {
				MenuItem item = (MenuItem) itemView.getTag();
				item.setItemView(itemView);
				item.selectNext();
				mListView.invalidateViews();
			}
			return true;
			
		case KeyEvent.KEYCODE_DPAD_CENTER:
			if (itemView != null && event.getAction() == KeyEvent.ACTION_DOWN) {
				int position = mListView.getSelectedItemPosition();
				MenuItem item = mItems.get(position);
				item.performClick();
			}
			return true;
		default:
			return false;
		}
	}

	protected void updateViewContent() {
		int progress = mTvHelper.mTvManager.tvScanInfo(1);
    	//int found = mTvHelper.mTvManager.tvScanInfo(2);
    	int found = mTvHelper.mTvManager.getChannelCountByFreq(frequency);
    	LogUtil.logD("DtvManualTuning", "update manual search progress=" + progress + ";found=" + found, true);
		mProgress.setProgress(progress);
		mFoundText.setText("" + found);
	}

}
