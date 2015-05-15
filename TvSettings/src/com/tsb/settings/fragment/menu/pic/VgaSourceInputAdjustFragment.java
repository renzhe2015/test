package com.tsb.settings.fragment.menu.pic;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tsb.settings.R;
import com.tsb.settings.fragment.BaseFragment;


public class VgaSourceInputAdjustFragment extends BaseFragment {		
	private View mView;	
	private TextView text;
	public static final int ADJUSTFINSH = 0;
	public static FragmentManager fm;
	private static Fragment fg;
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.layout_fragment_vga_adjust, container, false);
		text = (TextView)mView.findViewById(R.id.text_vga_adjust);
		text.setText(R.string.VGA_ADJUST_FINISH);
		initVgaParameters();
		fm = getFragmentManager();
		fg = this;
		return mView;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mView = null;
	}
	
	@Override
	public void onResume() {
		super.onResume();			
	}
	
	public void initVgaParameters() {
		new Thread() {
			@Override
			public void run() {
				mHandler.sendEmptyMessageDelayed(ADJUSTFINSH, 2000);
			}
		}.start();
	}	

	@Override
	public void onPause() {
		super.onPause();
	}	
	
	public static Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);			
			fm.beginTransaction().remove(fg).commit();
		}
	};	
}
