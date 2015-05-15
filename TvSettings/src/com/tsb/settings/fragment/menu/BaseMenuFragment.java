package com.tsb.settings.fragment.menu;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.Instrumentation;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.tsb.settings.R;
import com.tsb.settings.TvClientTypeList;
import com.tsb.settings.TvManagerHelper;
import com.tsb.settings.fragment.MainMenuFragment;
import com.tsb.settings.menu.MenuItem;
import com.tsb.settings.util.FragmentUtils;
import com.tsb.settings.util.LogUtil;

public abstract class BaseMenuFragment extends Fragment implements OnItemClickListener, OnKeyListener{

	public static final String ARG_PARENT_ID = "parent_id";
	public static final String ARG_LAYOUT_ID = "layout_id";
	public static Activity mActivity;
	public static String clientType;
	private static String TAG = "BaseMenuFragment";
	protected static class MenuAdapter extends BaseAdapter {

		private List<MenuItem> mItems;

		public MenuAdapter(List<MenuItem> items) {
			super();
			mItems = items;
		}

		@Override
		public int getCount() {
			return mItems.size();
		}

		@Override
		public Object getItem(int position) {
			return mItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LogUtil.logD(TAG, "getView position=" + position, true);
			View v = mItems.get(position).getView(convertView, parent);
			v.setEnabled(isEnabled(position));
			LayoutParams params = v.getLayoutParams(); 
			if(clientType.equals(TvClientTypeList.RowaClient_82))
				params.height = 49;
			else params.height = 73;
			v.setLayoutParams(params);  
			return v;
		}

		@Override
		public boolean isEnabled(int position) {
			return mItems.get(position).isEnable();
		}

		@Override
		public boolean areAllItemsEnabled() {
			return false;
		}

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = getActivity();
	}

	abstract public void onCreateMenuItems(List<MenuItem> items);
	abstract public int getTitle();

	protected final List<MenuItem> mItems = new ArrayList<MenuItem>();

	protected ListView mListView;
	protected MenuAdapter mAdapter;

	public int mLastSelection = -1;
	//****add by huangfh@tcl.com 08-29
	protected int selection;
	//******end
	@Override
	public void onCreate(Bundle savedInstanceState) {
		clientType=TvManagerHelper.getInstance(getActivity()).mTvManager.getClientType();
		super.onCreate(savedInstanceState);
	}

	protected View onInflateLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		int layout = R.layout.layout_fragment_base_menu;
		Bundle bundle = getArguments();
		if (bundle != null) {
			layout = bundle.getInt(ARG_LAYOUT_ID, layout);
		}
		return inflater.inflate(layout, container, false);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = onInflateLayout(inflater, container, savedInstanceState);
		mItems.clear();
		onCreateMenuItems(mItems);	
		// Setup ListView
		mListView = (ListView) view.findViewById(R.id.list);
		LogUtil.logD(TAG, "onCreateView,Create second fragment.mListView=" + mListView, true);
		mAdapter = new MenuAdapter(mItems);		
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
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					// TODO Auto-generated method stub

				}
			});
			//******end
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
	public void onDestroyView() {
		super.onDestroyView();
		if(mListView != null){
			if (mListView.isFocused()) {
				mLastSelection = mListView.getSelectedItemPosition();
			} else {
				mLastSelection = -1;
			}
		}
		mItems.clear();
	}

	protected void updateContent() {
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		if (savedInstanceState != null) {
			mLastSelection = savedInstanceState.getInt("last_list_selection", -1);
		}
		if (mLastSelection >= 0) {
			mListView.requestFocus();
			mListView.setSelection(mLastSelection);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("last_list_selection", mLastSelection);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
		showPopViewMouse(position);
		FragmentUtils.mHandler.removeMessages(0);
		FragmentUtils.mHandler.sendEmptyMessageDelayed(0, 15*1000);
	}
	
	private void showPopViewMouse(int position) {
		mListView.setSelection(position);
		mLastSelection = position;
		mListView.invalidateViews();
		new Thread() {
			public void run() {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Message msg = mUIHandler.obtainMessage(MOUSE_UI_REFRESH);
				msg.sendToTarget();
			};
		}.start();
	}
	
	private void showPopViewRC(final int type) {
		mListView.invalidateViews();
		new Thread() {
			public void run() {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Message msg = mUIHandler.obtainMessage(type);
				msg.sendToTarget();
			};
		}.start();
	}
	
	static final int MOUSE_UI_REFRESH = 1;
	static final int RC_RIGHT_UI_REFRESH = 2;
	static final int RC_CENTER_UI_REFRESH = 3;
	private Handler mUIHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MOUSE_UI_REFRESH:
				MenuItem item = mItems.get(mLastSelection);
				View itemView = item.getItemView();
				LogUtil.logD(TAG, "onItemClick,BaseMenuFragment itemView=" + itemView, true);
				if (itemView != null ) {
					item.selectNext();
				}
				break;
			case RC_RIGHT_UI_REFRESH:
				View itemView1 = mListView.getSelectedView();
				if (itemView1 != null ) {

					MenuItem item1 = (MenuItem) itemView1.getTag();
					item1.selectNext();
				}
				break;
			case RC_CENTER_UI_REFRESH:
				View itemView2 = mListView.getSelectedView();
				if (itemView2 != null ) {
					int position = mListView.getSelectedItemPosition();
					MenuItem item2 = mItems.get(position);
					item2.performClick();
					item2 = (MenuItem) itemView2.getTag();
					item2.showView();
				}
				break;
			default :
				break;
			}
		};
	};
	

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(mListView!=null&&mListView.getSelectedView()!=null)	//从网络信息退出时取消二级菜单的棕色背景
			mListView.getSelectedView().setBackgroundDrawable(null);
		if(FragmentUtils.mHandler!=null){
			FragmentUtils.mHandler.removeMessages(0);
			FragmentUtils.mHandler.sendEmptyMessageDelayed(0, 15*1000);
		}
	}
	
	
	
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			FragmentUtils.mHandler.removeMessages(0);
			FragmentUtils.mHandler.sendEmptyMessageDelayed(0, 15*1000);
			View itemView = mListView.getSelectedView();
			switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_LEFT:
				v.getRootView().findViewById(MainMenuFragment.mLastSelectionId2).requestFocus();
				MainMenuFragment.mLastSelectionId2=0;
				return true;
			case KeyEvent.KEYCODE_DPAD_UP:
				if (itemView != null)	
				{
					if (mListView.getSelectedItemPosition() == 0) {

						View tmpView = mListView.getChildAt(mListView.getCount() - 1);					
						if(tmpView.isEnabled() == false)
							mListView.setSelection(mListView.getCount() - 2);
						else
							mListView.setSelection(mListView.getCount() - 1);
						return true;
					}
					if (mListView.getSelectedItemPosition() == 1) {
						View tmpView = mListView.getChildAt(0);
						if(tmpView.isEnabled() == false){
							mListView.setSelection(mListView.getCount() - 1);
							return true;
						}


					}
				}
				return false;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				if (itemView != null)
				{				
					int curPosition=mListView.getSelectedItemPosition();
					int nextPosition=curPosition;
					int firstEnablePosition=-1;
					for(int i=0;i<mListView.getCount();i++){
						View tmpView = mListView.getChildAt(i);
						if(tmpView.isEnabled()){
							firstEnablePosition=i;
							break;
						}
					}
					if(curPosition<mListView.getCount()-1){
						for(int i=curPosition+1;i<mListView.getCount();i++){
							View tmpView = mListView.getChildAt(i);
							if(tmpView.isEnabled()){
								nextPosition=i;
								break;
							}else
							{
								if(i+1 > mListView.getCount()-1){
									nextPosition=firstEnablePosition;
									break;
								}
								else
									nextPosition = i+1;
							}
							if(i==mListView.getCount()-1){
								nextPosition=firstEnablePosition;
							}
						} 

					}
					else{
						nextPosition=firstEnablePosition;
					}

					mListView.setSelection(nextPosition);
					return true;
				}
				return false;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				if (itemView != null ) {
					showPopViewRC(RC_RIGHT_UI_REFRESH);
				}
				return true;

			case KeyEvent.KEYCODE_DPAD_CENTER:
				if (itemView != null) {
					showPopViewRC(RC_CENTER_UI_REFRESH);
				}
				return true;
			case KeyEvent.KEYCODE_MENU:
				try {
					FragmentManager fm = getFragmentManager();
					FragmentUtils.popAllBackStacks(fm);  //Hide all the Fragments
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				
			case KeyEvent.KEYCODE_TV_INPUT:
				String deviceName = InputDevice.getDevice(event.getDeviceId()).getName();
				if("Smart_TV_Keypad".equals(deviceName)){
					final KeyEvent tEvent1 = new KeyEvent( event.getAction(), KeyEvent.KEYCODE_DPAD_CENTER );
					new Thread(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							//super.run();
							try{
								Instrumentation inst = new Instrumentation();
//								Log.d("lly", "Left11111111111");
								inst.sendKeyDownUpSync( tEvent1.getKeyCode());

							}catch(Exception e ){
								Log.e("TAG","Exception when sendKeyDownUpSync : " + e.toString() );
							}
						}

					}.start();
					return true;
				}
				return false;
			default:
				return false;
			}
		}
		return false;
	}

	protected ListView getListView() {
		return mListView;
	}

	public Fragment getBaseFragment() {
		Fragment f = getParentFragment();
		if (f == null) {
			f = this;
		}
		return f;
	}

	protected static int indexOf(int[] array, int value) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == value) {
				return i;
			}
		}
		return -1;
	}
	
}
