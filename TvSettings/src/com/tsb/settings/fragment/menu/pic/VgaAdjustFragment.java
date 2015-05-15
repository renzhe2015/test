package com.tsb.settings.fragment.menu.pic;

import android.app.TvManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tsb.settings.R;
import com.tsb.settings.TvManagerHelper;
import com.tsb.settings.broadcast.TvBroadcastDefs;
import com.tsb.settings.fragment.BaseFragment;
import com.tsb.settings.util.FragmentUtils;

public class VgaAdjustFragment extends BaseFragment {		
	private View mView;	
	private TextView text;
	public static final int ADJUSTSTART = 1;

	private IntentFilter mIntentFilter = new IntentFilter(TvBroadcastDefs.CMD_BCT_TV_VGA_ADJUST_STATUS);
	private BroadcastReceiver mVgaAdjustBroadcastReceiver = new BroadcastReceiver() {
        
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TvBroadcastDefs.CMD_BCT_TV_VGA_ADJUST_STATUS.equals(action)) {
            	mView.setVisibility(View.VISIBLE);
                int msg = intent.getIntExtra(TvBroadcastDefs.PDU_BCT_TV_VGA_ADJUST_STATUS, -1);
                switch (msg) {
                	case TvManager.RT_MSG_SLR_AUTO_ADJUST_BEGIN:
                		text.setText(R.string.VGA_ADJUSTTING);                		
                		break;
                	case TvBroadcastDefs.CC_TV_VGA_ADJUST_SUCCESS:
                	case TvManager.RT_MSG_SLR_AUTO_ADJUST_END:
                		mHandler.sendEmptyMessageDelayed(0, 2000);/*延迟2S退出，显示正在调整*/               		
                		break;
                	case TvBroadcastDefs.CC_TV_VGA_ADJUST_FAILED:
                		if(null != getActivity())
                			Toast.makeText(getActivity(), R.string.VGA_ADJUST_FAILED, Toast.LENGTH_SHORT).show();
                		FragmentUtils.popBackSubFragment(VgaAdjustFragment.this);
                		break;
                	case TvBroadcastDefs.CC_TV_VGA_ADJUST_DOING:
                		text.setText(R.string.VGA_ADJUSTTING);
                		break;
                	default:
                		break;
                }
            }
        }
    };
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.layout_fragment_vga_adjust, container, false);
		text = (TextView)mView.findViewById(R.id.text_vga_adjust);
						
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
		if(null != getActivity())
			getActivity().registerReceiver(mVgaAdjustBroadcastReceiver, mIntentFilter);
		initVgaParameters();		
	}
	
	public void initVgaParameters() {
		new Thread() {
			@Override
			public void run() {
				mHandler.sendEmptyMessageDelayed(ADJUSTSTART, 0);
			}
		}.start();
	}	

	@Override
	public void onPause() {
		super.onPause();
		if(null != getActivity())
			getActivity().unregisterReceiver(mVgaAdjustBroadcastReceiver);
	}	
	
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			
			if(null == getActivity())
				return;
			
			if(msg.what == ADJUSTSTART)
				TvManagerHelper.getInstance(getActivity()).mTvManager.setVgaAutoAdjust();
			else
			{			
				Toast.makeText(getActivity(), R.string.VGA_ADJUST_FINISH, Toast.LENGTH_SHORT).show();
	    		FragmentUtils.popBackSubFragment(VgaAdjustFragment.this);
			}
		}
	};	
}
