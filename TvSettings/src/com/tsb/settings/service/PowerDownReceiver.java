package com.tsb.settings.service;

import java.util.Timer;
import java.util.TimerTask;

import com.tsb.settings.R;
import com.tsb.settings.fragment.menu.SystemSettingMenuFragment;
import com.tsb.settings.fragment.menu.systemSetting.SleepPowerOffSettingFragment;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TvManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;



public class PowerDownReceiver extends BroadcastReceiver{
	private AlertDialog alertDialog;
	private Timer mTimer;
    private MyTimerTask mTimerTask;
    private TextView textView;
    private final int MSG_UPDATE_SLEEP_TIME = 20;
    private  int countTime = 60;
    private int count=30;
    private final int MSG_SHOW_DIALOG=21;
    private Context mContext;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		mContext=context;
		if(intent.getAction().equals("PowerDownReceiver")){
			SharedPreferences  settings = context.getSharedPreferences("powerDownTimeFile",Context.MODE_PRIVATE);
			int resttime=settings.getInt("powerDownTime", -1);
			resttime--;
			if(resttime>0){
				Editor editor = settings.edit().putInt("powerDownTime", resttime);
				editor.commit();
//				SleepPowerOffSettingFragment.handler.sendEmptyMessage(resttime);
//				SystemSettingMenuFragment.handler.sendEmptyMessage(resttime);
				mContext.sendBroadcast(new Intent("SleepPowerDown").putExtra("resttime", resttime));
//				Toast.makeText(context, "你该打酱油了"+resttime, Toast.LENGTH_SHORT).show();
			}else if(resttime==0){
				Intent intent1 = new Intent("PowerDownReceiver");  
				//创建PendingIntent对象封装Intent，由于是使用广播，注意使用getBroadcast方法   
				PendingIntent pi = PendingIntent.getBroadcast(context,0,intent1,PendingIntent.FLAG_UPDATE_CURRENT);  
				//获取AlarmManager对象   
				AlarmManager am = (AlarmManager)context.getSystemService("alarm");  
				am.cancel(pi);   
				Editor editor = settings.edit().putInt("powerDownTime", -1).putInt("position", 0);
				editor.commit();
				mHandler.sendEmptyMessage(MSG_SHOW_DIALOG);
//				Toast.makeText(context, "你别打酱油了", Toast.LENGTH_SHORT).show();
			}
		} 
	}
	 
		private Handler mHandler = new Handler() {

			public void handleMessage(Message message) {
				if (message.what == MSG_UPDATE_SLEEP_TIME) {
					if(countTime > 0)
					{
						textView.setText(String.format(mContext.getString(R.string.tcl_sleepRestTime), countTime));
					}
					else
					{
						if (mTimer != null) {
							if (mTimerTask != null) {
								mTimerTask.cancel();
							}
						}	
											
						if(alertDialog.getCurrentFocus().getId() 
								== alertDialog.getButton(-1).getId())
						{
							sleepPowerOff(true);
						}
						else
						{
							sleepPowerOff(false);
						}
						alertDialog.dismiss();
					}				
				}
				else if(message.what ==MSG_SHOW_DIALOG){
					showAlterDialog();
				}
			}
		};
		 private void StartLockWindowTimer(int delay){    	
				if (mTimer == null) {
					mTimer = new Timer(true);
				}

				if (mTimer != null) {
					if (mTimerTask != null) {
						mTimerTask.cancel();
					}

					mTimerTask = new MyTimerTask();
					mTimer.schedule(mTimerTask, delay * 1000, 1000);
				}
		      }
		    
		    class MyTimerTask extends TimerTask{
		    	  @Override
		    	  public void run() {
		    	   // TODO Auto-generated method stub
		    	   Message msg = new Message();
		    	   msg.what = MSG_UPDATE_SLEEP_TIME;
		    	   countTime--;
		    	   mHandler.sendMessage(msg);
		    	  }    	     
		    }
		         
		private void sleepPowerOff(Boolean powerOff){
			if (mTimer != null) {
				if (mTimerTask != null) {
					mTimerTask.cancel();
				}
			}
//			SleepPowerOffSettingFragment.handler.sendEmptyMessage(0);
			mContext.sendBroadcast(new Intent("SleepPowerDown").putExtra("resttime", 0));
			if(powerOff)
			{
				TvManager tm = (TvManager) mContext.getSystemService("tv");					
				tm.stopRpcServer();
			}
		}
		//*******add by hfh09-27
		private void showAlterDialog(){
			 textView = new TextView(mContext); 
		        textView.setHeight(130);
		        textView.setGravity(Gravity.CENTER);
		        countTime = 60; 
		        
		        alertDialog =  new AlertDialog.Builder(new ContextThemeWrapper(mContext,R.style.Theme_Transparent))
				.setView(textView)
				.setTitle(mContext.getResources().getString(R.string.tcl_kindly_reminder))
				.setPositiveButton(mContext.getResources().getString(R.string.children_confirm),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {						
								alertDialog.dismiss();
								sleepPowerOff(true);
							}
						})
				.setNegativeButton(mContext.getResources().getString(R.string.children_cancel), 
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0,
									int arg1) {	
//								if(!if_mute)tvmm.mTvManager.setMute(false);
								alertDialog.dismiss();	
								sleepPowerOff(false);
							}
						})
				.setOnDismissListener(new OnDismissListener() {
							
					    @Override
						public void onDismiss(DialogInterface arg0) {
							// TODO Auto-generated method stub
							if (mTimer != null) {
								if (mTimerTask != null) {
									mTimerTask.cancel();
								}
							}
							sleepPowerOff(false);
							alertDialog.dismiss();
						}
					}).create();
				alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
				alertDialog.show(); 
				StartLockWindowTimer(0);
		}
		//******end
}
