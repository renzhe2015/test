package com.tsb.settings.settings.view;

import android.app.FragmentManager;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.tsb.settings.R;
import com.tsb.settings.TvClientTypeList;
import com.tsb.settings.TvManagerHelper;
import com.tsb.settings.fragment.MainMenuFragment;
import com.tsb.settings.util.FragmentUtils;

public class ListPopView extends PopView implements OnKeyListener {

	private ListView listView;
	private LinearLayout layout;
	private String[] lists;
	private int size;
	private Context context;
	public ListPopView(Context mContext, View parent, String[] lists,
			int postion) {
		super(mContext, parent);
		context = mContext;
		LayoutInflater layoutInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layout = (LinearLayout) layoutInflater.inflate(R.layout.popview_list,
				null);
		listView = (ListView) layout.findViewById(R.id.lv_popview_list);
		listView.setAdapter(new ArrayAdapter<String>(mContext,
				R.layout.popview_list_item, lists));
		listView.setOnKeyListener(this);
		listView.setSelection(postion);
		this.lists = lists;
		size = lists.length;
		if(TvManagerHelper.getInstance(context).mTvManager.getClientType().equals(TvClientTypeList.RowaClient_82))
		{
			width = 147;
			height = 54 * size - 13 * (size - 1);
			yoff = 9 * (size - 1);
		}
		else {
			width=220;
			height = 80 * size - 20 * (size - 1);
			yoff = 13 * (size - 1);
		}


		initContentView(layout);
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		listView.setOnItemClickListener(listener);
	}

	@Override
	public boolean onKey(View arg0, int keycode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			FragmentUtils.mHandler.removeMessages(0);
			FragmentUtils.mHandler.sendEmptyMessageDelayed(0, 15*1000);
			String deviceName = InputDevice.getDevice(event.getDeviceId()).getName();
			if (keycode == KeyEvent.KEYCODE_DPAD_LEFT) {
				handler.sendEmptyMessageDelayed(0, 200); // use this 200 delay wait
				return true;							// for key up
			}
			else if (keycode == KeyEvent.KEYCODE_DPAD_DOWN) {
				if(!nextIsSeletedable())
					listView.setSelection(0);
			}
			else if (keycode == KeyEvent.KEYCODE_DPAD_UP) {
				if(!preIsSeletedable())
					listView.setSelection(listView.getCount()-1);
			}
			else if (keycode == KeyEvent.KEYCODE_MENU){
				FragmentUtils.popAllBackStacks(MainMenuFragment.fm);
			}else if (keycode == KeyEvent.KEYCODE_TV_INPUT && "Smart_TV_Keypad".equals(deviceName)){
				final KeyEvent tEvent1 = new KeyEvent( event.getAction(), KeyEvent.KEYCODE_DPAD_CENTER );
				new Thread(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						//super.run();
						try{
							Instrumentation inst = new Instrumentation();
							Log.d("lly", "Left11111111111");
							inst.sendKeyDownUpSync( tEvent1.getKeyCode());

						}catch(Exception e ){
							Log.e("TAG","Exception when sendKeyDownUpSync : " + e.toString() );
						}
					}

				}.start();
				return true;
			}
		}
		return false;
	}

	public boolean nextIsSeletedable(){
		System.out.println(listView.getSelectedItemPosition()+"_____");
		if(listView.getSelectedItemPosition()==listView.getCount()-1)
			return false;
		for(int i=listView.getSelectedItemPosition();i<listView.getCount();i++){
			View v=(View) listView.getChildAt(listView.getSelectedItemPosition());
			if(v !=null && v.isEnabled())
				return true;
		}
		return false;
	}
	public boolean preIsSeletedable(){
		if(listView.getSelectedItemPosition()==0)
			return false;
		for(int i=listView.getSelectedItemPosition();i>-1;i--){
			View v=(View) listView.getChildAt(listView.getSelectedItemPosition());
			if(v.isEnabled())
				return true;
		}
		return false;
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			ListPopView.this.dismiss();
		};
	};



	@Override
	public int getSelectedPosition(String object) {
		for (int i = 0; i < size; i++) {
			if (object.equals(lists[i])) {
				return i;
			}
		}
		return listView.getSelectedItemPosition();
	}

	@Override
	public int getSelectedPosition() {
		return listView.getSelectedItemPosition();
	}


}
