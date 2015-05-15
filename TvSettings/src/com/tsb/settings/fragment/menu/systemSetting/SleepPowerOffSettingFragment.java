package com.tsb.settings.fragment.menu.systemSetting;

import java.util.List;

import android.app.AlarmManager;
import android.app.Instrumentation;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.tsb.settings.R;
import com.tsb.settings.fragment.menu.BaseMenuFragment;
import com.tsb.settings.fragment.menu.SystemSettingMenuFragment;
import com.tsb.settings.menu.MenuItem;
import com.tsb.settings.util.FocusAnimator;
import com.tsb.settings.util.FragmentUtils;

public class SleepPowerOffSettingFragment extends BaseMenuFragment implements OnClickListener,OnKeyListener{
	private ImageView focusFrame;	
	private static TextView[] txt = new TextView[8]; 
	private static int minutes_rest_tosleep;  
	//add by huangfh
	private static int position=-1;
	private static int curTime=-1;
	private static String timeUnit;
	private static boolean isOnResume=false;
	private SharedPreferences  settings;
	public static Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if(isOnResume){
				curTime=msg.what;
				if(curTime==0){
					for(int i = 1; i<Core.sleepTimeItems_value.length; i++)
					{
						if(i == txt.length -1)
							txt[i].setBackgroundResource(R.drawable.sleep_poweroff_bg_right);
						else
							txt[i].setBackgroundResource(R.drawable.sleep_poweroff_bg_mid);
						txt[i].setText(Core.sleepTimeItems_value[i]+timeUnit);
					}
					txt[0].setBackgroundResource(R.drawable.sleep_poweroff_bg_click);
					txt[0].requestFocus();
					curTime=-1;
					position=-1;
				}
				else{
					for(int i = 1; i<Core.sleepTimeItems_value.length; i++)
					{
						txt[i].setText(Core.sleepTimeItems_value[i]+timeUnit);
						txt[position].setText(curTime+timeUnit);
					}
				}
			}
		}
	};
	//end add by huangfh
	@Override
	protected View onInflateLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.layout_activity_sleep_power_off_setting, container, false);
		//add by huangfh@tcl.com 14-8-21
		if(getResources().getConfiguration().locale.getCountry().equals("CN")){
			timeUnit="分钟";
		}
		else{
			timeUnit="min.";
		}
		isOnResume=true;
		settings = getActivity().getSharedPreferences("powerDownTimeFile",Context.MODE_PRIVATE);
		//end add by huangfh@tcl.com 14-8-21
		txt[0] = (TextView)v.findViewById(R.id.sleep_off_close);
		txt[1] = (TextView)v.findViewById(R.id.sleep_off_10);
		txt[2] = (TextView)v.findViewById(R.id.sleep_off_30);
		txt[3] = (TextView)v.findViewById(R.id.sleep_off_60);
		txt[4] = (TextView)v.findViewById(R.id.sleep_off_90);
		txt[5] = (TextView)v.findViewById(R.id.sleep_off_120);
		txt[6] = (TextView)v.findViewById(R.id.sleep_off_180);
		txt[7] = (TextView)v.findViewById(R.id.sleep_off_240);
		txt[0].setOnKeyListener(this);
		txt[1].setOnKeyListener(this);
		txt[2].setOnKeyListener(this);
		txt[3].setOnKeyListener(this);
		txt[4].setOnKeyListener(this);
		txt[5].setOnKeyListener(this);
		txt[6].setOnKeyListener(this);
		txt[7].setOnKeyListener(this);
		focusFrame = (ImageView)v.findViewById(R.id.sleep_poweroff_focus);
		for(int i = 0; i < txt.length; i++)
		{
			txt[i].setOnFocusChangeListener(mSleepPowerOffListener);
			txt[i].setOnClickListener(mSleepPowerOffOnClick);
			if(settings.getInt("position", 0) == i&&settings.getInt("powerDownTime", -1)!=-1&&i>0)
			{
				position=i;
				txt[i].setText(settings.getInt("powerDownTime", -1)+timeUnit);
				txt[i].setBackgroundResource(R.drawable.sleep_poweroff_bg_click);
				txt[i].setTextColor(getResources().getColor(R.color.text_focus));
			}
		}  	
		if(position==-1){
			txt[0].setBackgroundResource(R.drawable.sleep_poweroff_bg_click);
		}
		return v;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = onInflateLayout(inflater, container, savedInstanceState);
		return view;
	}	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}
	
	@Override
	public void onResume() {
		super.onResume();
		getActivity().registerReceiver(sleepPowerDownReceiver, new IntentFilter("SleepPowerDown"));
	};

	View.OnFocusChangeListener mSleepPowerOffListener = new View.OnFocusChangeListener(){
		public void onFocusChange(View v,boolean hasFocus)
		{   
			if(true == hasFocus)
			{
				int[] location = new  int[2] ;
				int i = 0;
				FocusAnimator focusAnimator = new FocusAnimator();

				for(i = 0; i < txt.length; i++)
				{
					if(txt[i].getId() == v.getId())
					{
						txt[i].getLocationOnScreen(location); 
						break;
					}
				}
				if(location[1] < 852)
					focusAnimator.flyFoucsFrame(focusFrame, focusFrame.getWidth(), focusFrame.getHeight(), location[0], location[1]);
				else
					focusAnimator.flyFoucsFrame(focusFrame, focusFrame.getWidth(), focusFrame.getHeight(), location[0]-6, location[1]-6);
				focusFrame.bringToFront();
			}         
		}
	};


	View.OnClickListener mSleepPowerOffOnClick = new View.OnClickListener()
	{
		public void onClick(View v)
		{   
			//add by huangfh@tcl.com 14-8-21
			txt[1].setText(getResources().getString(R.string.tcl_sleepoff_10min));
			txt[2].setText(getResources().getString(R.string.tcl_sleepoff_30min));
			txt[3].setText(getResources().getString(R.string.tcl_sleepoff_60min));
			txt[4].setText(getResources().getString(R.string.tcl_sleepoff_90min));
			txt[5].setText(getResources().getString(R.string.tcl_sleepoff_120min));
			txt[6].setText(getResources().getString(R.string.tcl_sleepoff_180min));
			txt[7].setText(getResources().getString(R.string.tcl_sleepoff_240min));
			//end add by huangfh@tcl.com 14-8-21
			for(int i = 0; i < txt.length; i++)
			{
				LayoutParams params = (LayoutParams) txt[i].getLayoutParams();
				Log.v("wsl#####", "top====" + params.topMargin);
				if(v.getId() == txt[i].getId())
				{
					position=i;
					txt[i].setBackgroundResource(R.drawable.sleep_poweroff_bg_click);  
					txt[i].setTextColor(getResources().getColor(R.color.text_focus));
					for(int j = 0; j < txt.length; j++) {
						if(i != j) {
							txt[j].setTextColor(getResources().getColor(R.color.text_unfocus));
						}
					}
					/*设置睡眠关机时间*/
					curTime= Core.sleepTimeItems_value[i];
//					SystemSettingMenuFragment.handler.sendEmptyMessage(curTime);
					getActivity().sendBroadcast(new Intent("SleepPowerDown").putExtra("resttime", curTime));
					if(v.getId() != txt[0].getId()){

						startPowerTimer();
					}
					else{
						stopPowerTimer();
					}     	
				}
				else
				{
					if(i == 0)
						txt[i].setBackgroundResource(R.drawable.sleep_poweroff_bg_left);
					else if(i == txt.length -1)
						txt[i].setBackgroundResource(R.drawable.sleep_poweroff_bg_right);
					else
						txt[i].setBackgroundResource(R.drawable.sleep_poweroff_bg_mid);
				}
			}
		}
	};



	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}

	protected void stopPowerTimer() {
		// TODO Auto-generated method stub
		Editor editor = settings.edit().putInt("powerDownTime", -1).putInt("position", 0);
		editor.commit();
		Intent intent = new Intent("PowerDownReceiver");  
		//创建PendingIntent对象封装Intent，由于是使用广播，注意使用getBroadcast方法   
		PendingIntent pi = PendingIntent.getBroadcast(getActivity(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);  
		//获取AlarmManager对象   
		AlarmManager am = (AlarmManager)getActivity().getSystemService("alarm");  
		am.cancel(pi);   
	}

	protected void startPowerTimer() {
		// TODO Auto-generated method stub

		Editor editor = settings.edit().putInt("powerDownTime",curTime).putInt("position", position);
		editor.commit();
		Intent intent = new Intent("PowerDownReceiver");  
		//创建PendingIntent对象封装Intent，由于是使用广播，注意使用getBroadcast方法   
		PendingIntent pi = PendingIntent.getBroadcast(getActivity(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);  
		//获取AlarmManager对象   
		AlarmManager am = (AlarmManager)getActivity().getSystemService("alarm");  
		//设置闹钟从当前时间开始，每隔10分钟执行一次PendingIntent对象，注意第一个参数与第二个参数的关系   
		am.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+60*1000,60*1000,pi); 
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

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		isOnResume=false;
		position=-1;
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		getActivity().unregisterReceiver(sleepPowerDownReceiver);
	}
	
	BroadcastReceiver sleepPowerDownReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			if(arg1.getAction().equals("SleepPowerDown")) {
				int curTime = arg1.getIntExtra("resttime", -1);
				if(isOnResume){
					if(curTime==0){
						for(int i = 1; i<Core.sleepTimeItems_value.length; i++)
						{
							if(i == txt.length -1)
								txt[i].setBackgroundResource(R.drawable.sleep_poweroff_bg_right);
							else
								txt[i].setBackgroundResource(R.drawable.sleep_poweroff_bg_mid);
							txt[i].setText(Core.sleepTimeItems_value[i]+timeUnit);
						}
						txt[0].setBackgroundResource(R.drawable.sleep_poweroff_bg_click);
						txt[0].requestFocus();
						curTime=-1;
						position=-1;
					}
					else{
						for(int i = 1; i<Core.sleepTimeItems_value.length; i++)
						{
							txt[i].setText(Core.sleepTimeItems_value[i]+timeUnit);
							txt[position].setText(curTime+timeUnit);
						}
					}
				}
			}
		}
	}; 

}
