package com.tsb.settings.fragment.menu.systemSetting;

import java.util.ArrayList;
import java.util.List;

import android.app.TvManager;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.tsb.settings.R;
import com.tsb.settings.TvManagerHelper;
import com.tsb.settings.fragment.menu.BaseMenuFragment;
import com.tsb.settings.menu.MenuItem;

public class inputTypeSetting extends BaseMenuFragment{
	private ListView mListView;
	private ArrayAdapter<String> mAdapter;
	private TvManagerHelper tm;
	private InputMethodManager imm;
	private List<InputMethodInfo> mInputMethodProperties;
	private String mLastInputMethodId;
	private int[] mInputType;
	private String[] mInputMethods;
	//add by huangfh@tcl.com 14-8-21
	private int mCurInputMethod = 0;
	private int mLastInputMethod=0;
	//end add by huangfh
	@Override
	protected View onInflateLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.layout_activity_input_type, container, false);
		tm = TvManagerHelper.getInstance(getActivity());
		mListView = (ListView)v.findViewById(R.id.input_type_list);
		mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mAdapter = new ArrayAdapter<String>(getActivity().getBaseContext(),
				R.layout.item_text);
		
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(inputTypeListItemClickListener);
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
		imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		setmInputMethodProperties(imm.getInputMethodList());
		setmLastInputMethodId(Settings.Secure.getString(getActivity().getContentResolver(),
			Settings.Secure.DEFAULT_INPUT_METHOD));
		mInputMethods = new String[]{getString(R.string.tcl_msg_none_inputmethod)};
		mInputType = new int[getmInputMethodProperties().size()];
		if (getmInputMethodProperties() != null && getmInputMethodProperties().size() > 0) {	
			mInputMethods = new String[getmInputMethodProperties().size()];
			for (int i = 0; i < getmInputMethodProperties().size(); i++) {
				InputMethodInfo property = getmInputMethodProperties().get(i);
				mInputMethods[i] = (String) property 
						.loadLabel(getActivity().getPackageManager());
				//add by huangfh@tcl.com 14-8-21
				final String id = property.getId();
				boolean hasIt = id.equals(mLastInputMethodId);
				if(hasIt){
					mLastInputMethod=i;
				}
				//end add by huangfh@tcl.com
				mInputType[i] = i;
			}
		}   	

		// Setup list view
		mAdapter.clear();
		mAdapter.addAll(mInputMethods);
		mAdapter.notifyDataSetChanged();

		mListView.setSelection(mLastInputMethod);
		mListView.setItemChecked(1, true);
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
	
	OnItemClickListener inputTypeListItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1,
				int position, long arg3) {
			//add by huangfh@tcl.com 14-8-21
			mCurInputMethod = position;
			if (mInputMethodProperties != null
					&& mInputMethodProperties.size() != 0) {
				InputMethodInfo property = mInputMethodProperties
						.get(mCurInputMethod);
				String mCurrentInputMethodId = property.getId();
				try {
					Settings.Secure.putString(getActivity().getContentResolver(),
							Settings.Secure.DEFAULT_INPUT_METHOD,
							mCurrentInputMethodId != null ? mCurrentInputMethodId : "");
				} catch (Exception e) {
					Log.i("Exception", e.toString());
				}
			}
			//end add by huangfh@tcl.com
				
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
	
	public String[] getInputMethods() {
		return mInputMethods;
	}

	public void setInputMethods(String[] mInputMethods) {
		this.mInputMethods = mInputMethods;
	}

	public String getmLastInputMethodId() {
		return mLastInputMethodId;
	}

	public void setmLastInputMethodId(String mLastInputMethodId) {
		this.mLastInputMethodId = mLastInputMethodId;
	}

	public List<InputMethodInfo> getmInputMethodProperties() {
		return mInputMethodProperties;
	}

	public void setmInputMethodProperties(List<InputMethodInfo> mInputMethodProperties) {
		this.mInputMethodProperties = mInputMethodProperties;
	}

}
