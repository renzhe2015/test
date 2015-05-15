package com.tsb.settings.fragment.menu.adtv;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tsb.settings.R;
import com.tsb.settings.TvManagerHelper;
import com.tsb.settings.TvManagerHelper.AtvScanEntry;

import java.util.List;

public class AtvChannelManageFragment extends Fragment implements OnItemClickListener, OnItemSelectedListener {
	
	private ListView mListView;
	private ArrayAdapter<AtvScanEntry> mAdapter;

	private List<AtvScanEntry> mData;
	private TvManagerHelper mTvManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mTvManager = TvManagerHelper.getInstance(getActivity());
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Get data
		mData = mTvManager.getAtvScanEntries();

		// Setup data and the list view.
		View view = inflater.inflate(R.layout.layout_fragment_atv_channel_manage, container, false);
		mListView = (ListView) view.findViewById(R.id.list);
		mListView.setOnItemClickListener(this);
		mListView.setOnKeyListener(mOnListKey);
		mAdapter = new MyAdapter(inflater.getContext(), R.layout.item_dtv_manual_tuning);
		mListView.setAdapter(mAdapter);

		updateViewContent();
		view.requestFocus();
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		// Select current channel.
		// Note: For fragments may restore the view focusing status between onCreateView and onResume (onRestoreViewState?)
		// Make sure our selection is performed after that. 
		
		// Setup listener after selection to avoid an extra call of setCurrentchannel.
		// This will eat the first onItemSelected invoked by the setSelection below.
		mListView.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
	            mListView.setOnItemSelectedListener(AtvChannelManageFragment.this);
            }

			@Override
            public void onNothingSelected(AdapterView<?> parent) {
	            
            }
		});
		
		// Select current channel
		mListView.requestFocus();
		int idx = mTvManager.getCurrentChannelIndex();
		if (idx >= 0 && idx < mData.size()) {
			mListView.setSelection(idx);
		} else {
			mListView.setOnItemSelectedListener(this);
		}
		
	}

	@Override
	public void onPause() {
		super.onPause();
	}
	
	private void updateViewContent() {
		mAdapter.clear();
		mAdapter.addAll(mData);
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
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		AtvScanEntry info = mData.get(position);
		mTvManager.setCurrentChannelByIndex(info.index+1, null, null);
//		
//		getFragmentManager().beginTransaction()
//		.detach(this)
//		.add(getId(), new AtvManualTuningSettingFragment(), Tv_strategy.STACK_MENU)
//		.addToBackStack(Tv_strategy.STACK_MENU)
//		.commit();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		AtvScanEntry info = mData.get(position);
		mTvManager.setCurrentChannelByIndex(info.index+1, null, null);
//		mTvManager.postSetCurrentChannel(1000, info.channelNumber, null, null);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		
	}
	
	private static class MyAdapter extends ArrayAdapter<AtvScanEntry> {

		public MyAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(parent.getContext()).inflate(
						R.layout.item_atv_manual_tuning, parent, false);
			}
			AtvScanEntry c = getItem(position);
			TextView textPosition = (TextView) convertView.findViewById(R.id.text_position);
			TextView textLabel = (TextView) convertView.findViewById(R.id.text_label);
			View imageSkip = convertView.findViewById(R.id.image_skip);
			
			textPosition.setText(String.format("%03d", c.index+1));
			textLabel.setText(c.name);
			imageSkip.setVisibility(c.skipped ? View.VISIBLE : View.INVISIBLE);
			
			return convertView;
		}
		
		
	}
}
