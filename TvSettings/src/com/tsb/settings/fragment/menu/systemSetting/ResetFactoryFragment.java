package com.tsb.settings.fragment.menu.systemSetting;


import java.io.IOException;
import java.util.List;

import android.app.Instrumentation;
import android.app.ProgressDialog;
import android.app.TvManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RecoverySystem;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.tsb.settings.BaseActivity;
import com.tsb.settings.R;
import com.tsb.settings.fragment.BaseFragment;
import com.tsb.settings.fragment.MainMenuFragment;
import com.tsb.settings.fragment.menu.BaseMenuFragment;
import com.tsb.settings.menu.MenuItem;
import com.tsb.settings.util.FragmentUtils;

public class ResetFactoryFragment extends BaseMenuFragment implements OnKeyListener{
	Button btnSure;
	Button btnCancel;
	
	@Override
	protected View onInflateLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.layout_activity_reset_factory, container, false);
		
		btnSure = (Button)v.findViewById(R.id.reset_factory_sure);
        btnCancel = (Button)v.findViewById(R.id.reset_factory_cancel);
        btnCancel.setOnKeyListener(this);
        btnSure.setOnKeyListener(this);
        btnSure.setBackgroundResource(R.drawable.item_focus);
        btnSure.setOnFocusChangeListener(resetFactoryChangeListener);
        btnCancel.setOnFocusChangeListener(resetFactoryChangeListener);
        btnSure.setOnClickListener(resetFactoryOnClick);
        btnCancel.setOnClickListener(resetFactoryOnClick);
        return v;
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
    }
    
    View.OnFocusChangeListener resetFactoryChangeListener = new View.OnFocusChangeListener(){
        public void onFocusChange(View v,boolean hasFocus)
        {      
    		switch(v.getId())
    		{    			 				
        		case R.id.reset_factory_sure:
        			if(true == hasFocus) 
        			{
        				btnSure.setBackgroundResource(R.drawable.item_focus); 
        				btnCancel.setBackgroundResource(R.drawable.item_normal); 
        			}
        			break;
        		case R.id.reset_factory_cancel:
        			if(true == hasFocus)  
        			{
        				btnCancel.setBackgroundResource(R.drawable.item_focus);
        				btnSure.setBackgroundResource(R.drawable.item_normal); 
        			}
        			break;        			
    			default:
    				break;
    		}            
        }
    };
    
    
    View.OnClickListener resetFactoryOnClick = new View.OnClickListener()
    {
        public void onClick(View v)
        {        	
            switch (v.getId())
            {
	    		case R.id.reset_factory_sure:
					try{
						ProgressDialog pd = new ProgressDialog(getActivity());
						pd.setCancelable(false);
						pd.show();
						pd.setContentView(R.layout.reset_dialog);
						FragmentUtils.mHandler.removeMessages(0);
					}catch(Exception e){
						Log.d("ResetFactoryFragment", ""+e.toString());
					}
					
					new Thread(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							TvManager tm = (TvManager) getActivity().getSystemService(Context.TV_SERVICE);
							tm.SetOnOffFunctionValue("SetResetTV", false);
							mHandler.sendEmptyMessageDelayed(10, 4000);
							/*设置默认语言为中文*/
//							try {
//								OSDLanguage.setOSDLanguage(0, getActivity());
//							} catch (Exception e) {
//								Log.i("Exception", e.toString());
//							}
						}
					}.start();
	    			
//	    			getActivity().finish();
//	    			PowerManager pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);  
//	    			pm.reboot("recovery");
//				try {
//					RecoverySystem.rebootWipeUserData(getActivity());
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//	    			
//	    			getActivity().sendBroadcast(new Intent("android.intent.action.MASTER_CLEAR"));
//	    			exitResetFactory();
	    			break;
	    		case R.id.reset_factory_cancel:
	    			exitResetFactory();
	    			break;            
            }
        }
    };
    
    private Handler mHandler = new Handler() {
    	public void handleMessage(Message msg) {
    		getActivity().sendBroadcast(new Intent("android.intent.action.MASTER_CLEAR"));
    		if(msg.what != 0){
    			mHandler.sendEmptyMessageDelayed(--msg.what, 3000);
    			Log.d("liluyuan", "left "+msg.what+" times");
    		}
    	}
    };
    
    private void exitResetFactory()
    {
    	FragmentUtils.popBackSubFragment(this);
    }
    
    @Override
	public void onDestroyView() {
		super.onDestroyView();
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

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		String deviceName = InputDevice.getDevice(event.getDeviceId()).getName();
		if(event.getAction() == KeyEvent.ACTION_DOWN){
			FragmentUtils.mHandler.removeMessages(0);
			FragmentUtils.mHandler.sendEmptyMessageDelayed(0, 15000);
			if(KeyEvent.KEYCODE_MENU == keyCode){
				return super.onKey(v, keyCode, event);
			}else if(keyCode == KeyEvent.KEYCODE_TV_INPUT&&"Smart_TV_Keypad".equals(deviceName)){
				final KeyEvent tEvent1 = new KeyEvent( event.getAction(), KeyEvent.KEYCODE_DPAD_CENTER );
				new Thread(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						//super.run();
						try{
						    Instrumentation inst = new Instrumentation();
						    Log.d("lly", "press Input key");
						    inst.sendKeyDownUpSync( tEvent1.getKeyCode());
						        
						}catch(Exception e ){
						    Log.e("TAG","Exception when sendKeyDownUpSync : " + e.toString() );
						}
					}
						
				}.start();
				return true;
			}else{
				return false;
			}
		}
		return false;
	}
	
}
