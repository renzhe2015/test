package com.tsb.settings.fragment.menu.adtv;

import android.app.Fragment;
import android.app.TvManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tsb.settings.R;
import com.tsb.settings.TvManagerHelper;
import com.tsb.settings.TvManagerHelper.ChannelInformation;
import com.tsb.settings.broadcast.TvBroadcastDefs;

import java.util.List;

public class DtvManualTuningResultFragment extends Fragment implements OnItemClickListener {
	
	private static final boolean DEBUG = true;
	private static final String TAG = "DtvManualTuningResult";
	
	public static Bundle buildArguments(int index, String name, int frequency, int bandwidth,
			boolean signalBooster) {
		Bundle args = new Bundle();
		args.putInt(ARG_INDEX, index);
		args.putString(ARG_NAME, name);
		args.putInt(ARG_FREQ, frequency);
		args.putInt(ARG_BAND, bandwidth);
		args.putBoolean(ARG_BOOST, signalBooster);
		return args;
	}
	
	private static final String ARG_INDEX = "index";
	private static final String ARG_NAME = "name";
	private static final String ARG_FREQ = "frequency";
	private static final String ARG_BAND = "bandwidth";
	private static final String ARG_BOOST = "booster";

	private ListView mListView;
	private ProgressBar mProgress;
	private ArrayAdapter<ChannelInformation> mAdapter;

	private List<ChannelInformation> mDataBefore;
	private List<ChannelInformation> mDataAfter;
	private TvManagerHelper mTvManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mTvManager = TvManagerHelper.getInstance(getActivity());
		
		// Get arguments
		Bundle args = getArguments();
		int index = args.getInt(ARG_INDEX);
//		String name = args.getString(ARG_NAME);
		int frequency = args.getInt(ARG_FREQ);
		int bandwidth = args.getInt(ARG_BAND);
//		boolean booster = args.getBoolean(ARG_BOOST);
		
		// Get dataBefore
		mDataBefore = mTvManager.getDtvChannelList();
		
		// Register receiver
		IntentFilter filter = new IntentFilter(TvBroadcastDefs.ACTION_TV_MEDIA_MESSAGE);
		getActivity().registerReceiver(mReceiver, filter);
		
		// Start scan
		mTvManager.stopDtvScanning();
		mTvManager.startDtvScanning(frequency, bandwidth , index, TvManager.SOURCE_DTV1);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(mReceiver);
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			int messageId = intent.getIntExtra(TvBroadcastDefs.EXTRA_TV_MEDIA_MESSAGE, -1);
			if (DEBUG) {
				Log.d(TAG, String.format("Receive message: action = %s, id = %d", intent.getAction(), messageId));
			}
			if (messageId == TvManager.RT_MSG_SLR_SCAN_FREQ_UPDATE) {
				// (Only update after finished.)
				
			} else if (messageId == TvManager.RT_MSG_SLR_SCAN_AUTO_COMPLETE) {
				mTvManager.mTvManager.tvScanManualComplete();
			 	mDataAfter = mTvManager.getDtvChannelList();
				updateViewContent();
			}
		}
		
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_fragment_dtv_manual_tuning_result, container, false);
		mListView = (ListView) view.findViewById(R.id.list);
		mAdapter = new MyAdapter(inflater.getContext(), R.layout.item_dtv_manual_tuning);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);

		mListView.requestFocus();
		mListView.setOnKeyListener(mOnListKey);
		
		mProgress = (ProgressBar) view.findViewById(R.id.progress);
		updateViewContent();
		view.requestFocus();
		return view;
	}
	
	private void updateViewContent() {
		boolean isScanning = mDataAfter == null;
		mAdapter.clear();
		
		if (isScanning) {
			mProgress.setVisibility(View.VISIBLE);
			mAdapter.addAll(mDataBefore);
			
		} else {
			mProgress.setVisibility(View.INVISIBLE);
			final int count = mDataAfter.size();
			for (int i = 0; i < count; i++) {
				ChannelInformation c = mDataAfter.get(i);
				c.isNew = !mDataBefore.contains(c);
				mAdapter.add(c);
			}
		}
		mAdapter.notifyDataSetChanged();
	}
	
	/**
	 * Circular child focusing
	 */
	private final OnKeyListener mOnListKey = new OnKeyListener() {
		
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				switch (keyCode) {
				case KeyEvent.KEYCODE_DPAD_UP:
					if (mListView.getSelectedItemPosition() == 0) {
						mListView.setSelection(mListView.getCount() - 1);
						return true;
					}
					break;
				case KeyEvent.KEYCODE_DPAD_DOWN:
					if (mListView.getSelectedItemPosition() == mListView.getCount() - 1) {
						mListView.setSelection(0);
						return true;
					}
					break;
				default:
					break;
				}
			}
			return false;
		}
	};

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		
	}

	private static class MyAdapter extends ArrayAdapter<ChannelInformation> {

		public MyAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(parent.getContext()).inflate(
						R.layout.item_dtv_manual_tuning, parent, false);
			}
			ChannelInformation c = getItem(position);
			CheckBox cb = (CheckBox) convertView.findViewById(R.id.check_new);
			TextView textPosition = (TextView) convertView.findViewById(R.id.text_position);
			TextView textName = (TextView) convertView.findViewById(R.id.text_name);
			TextView textType = (TextView) convertView.findViewById(R.id.text_type);
			
			cb.setChecked(c.isNew);
			textPosition.setText(String.format("%03d", c.channelNumber));
			textName.setText(c.channelName);
			textType.setText("frequency = " + c.frequency);
			
			return convertView;
		}
		
		
	}
}
