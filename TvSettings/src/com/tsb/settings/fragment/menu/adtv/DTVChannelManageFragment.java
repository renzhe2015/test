package com.tsb.settings.fragment.menu.adtv;

import java.util.List;

import android.app.FragmentManager;
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
import android.widget.Toast;

import com.tsb.settings.R;
import com.tsb.settings.TvManagerHelper;
import com.tsb.settings.TvManagerHelper.ChannelInformation;
import com.tsb.settings.fragment.BaseFragment;

public class DTVChannelManageFragment extends BaseFragment implements
		OnItemClickListener {

	private static final boolean DEBUG = false;

	private ListView mListView;
	private TextView mTextMessage;
	private ArrayAdapter<ChannelInformation> mAdapter;

	private TvManagerHelper mTvManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mTvManager = TvManagerHelper.getInstance(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fragment_auto_tuning_result, container, false);
        mTextMessage = (TextView) view.findViewById(R.id.text_message);
		mListView = (ListView) view.findViewById(R.id.list);
		mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mAdapter = new ArrayAdapter<ChannelInformation>(inflater.getContext(),
				R.layout.item_text);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);

		updateListData(inflater.getContext());
		mListView.requestFocus();
		mListView.setOnKeyListener(mOnListKey);
		mListView.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				mTvManager.setCurrentChannelByIndex(mListView.getSelectedItemPosition(), null, null);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		return view;
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

	private void updateListData(Context context) {
		List<ChannelInformation> list = mTvManager.getChannelList();
		int currentIdx = mTvManager.getCurrentChannelIndex();

		if (DEBUG) {
			if (!list.isEmpty()) {
				ChannelInformation info = list.get(0);
				for (int i = 0; i < 100; i++) {
					list.add(info);
				}
			}
		}

		// Setup list view
		mAdapter.clear();
		mAdapter.addAll(list);
		mAdapter.notifyDataSetChanged();

		
		if (list.isEmpty()) {
		    mTextMessage.setVisibility(View.VISIBLE);
		} else {
		    mListView.setSelection(currentIdx);
		    mListView.setItemChecked(currentIdx, true);
		    mTextMessage.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Reset dismiss callback
		switch (keyCode) {
		// Pass to default list view implementation
		case KeyEvent.KEYCODE_SPACE:
		case KeyEvent.KEYCODE_PAGE_UP:
		case KeyEvent.KEYCODE_PAGE_DOWN:
			return false;
		default:
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (DEBUG) {
			Toast.makeText(getActivity(), "Channel switched: " + position,
					Toast.LENGTH_SHORT).show();
		}
		ChannelInformation info = mAdapter.getItem(position);
		mTvManager.setCurrentChannel(info.channelNumber);
		FragmentManager fm = getFragmentManager();
		fm.popBackStack();
	}

}
