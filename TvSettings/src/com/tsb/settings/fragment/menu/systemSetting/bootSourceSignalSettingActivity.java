package com.tsb.settings.fragment.menu.systemSetting;

import java.util.ArrayList;
import java.util.List;

import android.app.TvManager;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.tsb.settings.BaseActivity;
import com.tsb.settings.R;
import com.tsb.settings.TvManagerHelper;
import com.tsb.settings.TvResource;
import com.tsb.settings.fragment.menu.BaseMenuFragment;
import com.tsb.settings.menu.MenuItem;

public class bootSourceSignalSettingActivity extends BaseMenuFragment{
	private ListView mListView;
	private ArrayAdapter<String> mAdapter;
	private TvManagerHelper tm;
	
	@Override
	protected View onInflateLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        setContentView(R.layout.layout_activity_boot_source_signal_setting);   
        View v = inflater.inflate(R.layout.layout_activity_boot_source_signal_setting, container, false);
        tm = TvManagerHelper.getInstance(getActivity());
        
        mListView = (ListView)v.findViewById(R.id.boot_source_signal_list);
		mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mAdapter = new ArrayAdapter<String>(getActivity().getBaseContext(),
				R.layout.item_text);
		
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(sourceListItemClickListener);

		updateListData(getActivity().getBaseContext());
		mListView.requestFocus();
		mListView.setOnKeyListener(mOnListKey);
		
		return v;
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
 
    }
    
    private void updateListData(Context context) {
    	List<Integer> mListId = tm.getInputSourceList();
    	List<String> mSourceList = new ArrayList<String>();    	
    	
		for (int i = 0; i < mListId.size(); i++) {
			int id = mListId.get(i);			
			String name = TvResource.getInputSourceLabel(getActivity(), id);
			mSourceList.add(name);
		}
		
		int currentIdx =  tm.getInputSource();

		// Setup list view
		mAdapter.clear();
		mAdapter.addAll(mSourceList);
		mAdapter.notifyDataSetChanged();

		mListView.setSelection(currentIdx);
		mListView.setItemChecked(currentIdx, true);
	}
    
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
	
	OnItemClickListener sourceListItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1,
				int position, long arg3) {	
			TvManager mTvManager = (TvManager) getActivity().getSystemService(Context.TV_SERVICE);			
			mTvManager.setBootSource(tm.getInputSourceList().get(position));			
		}
	};

	@Override
	public void onCreateMenuItems(List<MenuItem> items) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getTitle() {
		// TODO Auto-generated method stub
		return 0;
	}
   
}
