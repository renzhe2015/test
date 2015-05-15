package com.tsb.settings.fragment.menu;

import java.util.List;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.tsb.settings.R;
import com.tsb.settings.menu.DeenListView;
import com.tsb.settings.menu.MenuItem;
import com.tsb.settings.util.LogUtil;

public class SecondLevelMenuFragment extends BaseMenuFragment{
	
	
	private static final String TAG = "SecondLevelMenuFragment";
	//ms
	protected static final long DATA_INIT_DELAY = 10;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = onInflateLayout(inflater, container, savedInstanceState);
		mItems.clear();
		onCreateMenuItems(mItems);	
		// Setup ListView
		mListView = (DeenListView) view.findViewById(R.id.list);
		LogUtil.logD(TAG, "onCreateView,Create second fragment.mListView=" + mListView, true);
		mAdapter = new BaseMenuFragment.MenuAdapter(mItems);		
		if(mListView != null){
			mListView.setAdapter(mAdapter);
			mListView.setOnItemClickListener(this);
			//*******add by huangfh@tcl.com 08-29 
			mListView.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					selection=position;
					LogUtil.logD(TAG, "onCreateView,onItemSelected.position=" + position, false);
//					mItems.get(position).changeStyle(true);
//					mListView.requestLayout();
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					// TODO Auto-generated method stub
					LogUtil.logD(TAG, "onCreateView,onNothingSelected.position=" + mListView.getSelectedItemPosition()
							+ ",isInTouchMode=" + parent.isInTouchMode(), false);
				}
			});
			//******end
			mListView.setFocusable(true);
			((DeenListView) mListView).setSelection(-1, true);
			mListView.setOnFocusChangeListener(new OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					LogUtil.logD(TAG, "onCreateView,onFocusChange hasFocus=" + hasFocus + ";position=" + mListView.getSelectedItemPosition()
							+ ";isInTouchMode=" + mListView.isInTouchMode(), false);
					//解决跳到三级菜单和一级菜单时返回，字体样式切换和焦点丢失问题。
					if (hasFocus) {
						((DeenListView) mListView).enableInvalidPosition(false);
					} else {
						mAdapter.notifyDataSetInvalidated();
						((DeenListView) mListView).setSelection(-1, true);
					}
				}
			});
			mListView.setOnKeyListener(this);	
			Bundle args = getArguments();
			if (args != null) {
				int parentId = args.getInt(ARG_PARENT_ID);
				mListView.setNextFocusDownId(parentId);
				mListView.setNextFocusUpId(parentId);
			}
		}

		return view;
	}
	
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
