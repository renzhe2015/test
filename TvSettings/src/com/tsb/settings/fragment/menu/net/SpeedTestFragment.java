package com.tsb.settings.fragment.menu.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import com.tsb.settings.R;
import com.tsb.settings.fragment.MainMenuFragment;
import com.tsb.settings.util.Util;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class SpeedTestFragment extends BaseNetMenuFragment implements OnClickListener{
	private static final String TAG = "SpeedTestActivity";
	private ImageView mImageMinute;
	//private TCLProgressBar mProgressTest;
//	private TextView tvProgressTest;
	private TextView tvAverageSpeed;
	private TextView tvBandWidth;
	private TextView tvVideoRecommend;
	private Button btnStartTest;
	
	private LinearLayout mLayoutSpeedResult;
	//private LinearLayout mLayoutSpeedProgress;
	
	
	private final int LOAD_PAGE_FINISH = 1;
	private final int RENDER_PAGE_FINISH = 3;
	private final int GET_SPEED_FINISH = 4;
	private final int NET_ERROR = 5;
	private final int PAGE_LOAD_ERROR = 7;
	private final int PAGE_LOAD_TIME_OUT = 8;
	private final int SET_BUTTON_TRUE = 9;
	private final int SET_NET_STOP = 10;
	private final int REFRESH_PROGRESS = 11;
	public final static int READ_BLOCK_SIZE = 16;
	public final static int READ_BLOCK_TIME = 1; //每个网页读取次数
	private final int REQUEST_TIME_OUT = 5000;
	
	private int[] levelsDegree = {-90, -45, 0, 45, 90};
	
	private float mOldDegree = levelsDegree[0];
	private boolean secondMessage = false;
	private boolean startTest = false;
	private HttpURLConnection conn = null;
	public static final String DOWNLOAD_URL = "http://d.app.huan.tv/appstore/resources/2013/11/15/tcltestspeed/file_1384506246118.jpg";
	public static double speed = 0;
	private long runTime = 0;
	
	private int mCurState = 0;
	private long curAverageSpeed = 0;
	private String mCurAvSpString = "";
	private TrafficData dataTraffic = new TrafficData();
	private Timer mSystemInfoRefreshTimer = new Timer();
	private final int mTotalSize = 2172709;
	private int downLoadFileSize = 0;
	private int curdownLoadFileSize = 0;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.speed_test_layout, container,
				false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mView=view;
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onResume() {
		ininUI();
		mOldDegree = levelsDegree[0];
		refreshclock(0);
		mSystemInfoRefreshTimer.schedule(mTimerTask, 0, 1000);
		super.onResume();
	}
	//******add by huanghfh@tcl.com 09-16
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mSystemInfoRefreshTimer.cancel();
//		this.finish();
	}
	//********end
	private void ininUI() {
		tvAverageSpeed = (TextView)mView.findViewById(R.id.tv_speed_test_average);
		tvBandWidth = (TextView)mView.findViewById(R.id.tv_speed_test_bandwidth);
		tvVideoRecommend = (TextView)mView.findViewById(R.id.tv_speed_test_video);

		mImageMinute = (ImageView)mView.findViewById(R.id.firstminute);
		btnStartTest = (Button)mView.findViewById(R.id.btn_speed_test_start);

		mLayoutSpeedResult = (LinearLayout)mView.findViewById(R.id.layout_speed_test_result);
		btnStartTest.requestFocus();
		btnStartTest.setOnClickListener(this);
		btnStartTest.setOnKeyListener(this);
	}
	
	private static final long[] bandWidthStandards = {128, 256, 512, 1024};
	private static final String[] bandWidths = {"1M", "2M", "4M", "8M"};
	/**对应角度：蜗牛到自行车（-90~0）;自行车到汽车（0~45）;汽车到飞机（45～90）**/
	private static final int speedStandars[] = {0, 128, 256, 512,}; //对应速度(蜗牛,自行车,汽车,飞机)
	public void refreshclock(float data) {
		float degree = 0;
		Log.d("0888data", data + "");
		if (data <= speedStandars[0]) {
			degree = levelsDegree[0];
		} else if (data > speedStandars[0] && data <= speedStandars[1]) {
			degree = levelsDegree[0] + (data - speedStandars[0]) * 90 / (speedStandars[1] - speedStandars[0]);
		} else if (speedStandars[1] < data && data <= speedStandars[2]) {
			degree = (data - speedStandars[1]) * 45 / (speedStandars[2] - speedStandars[1]);
		} else if (data > speedStandars[2] && data <= speedStandars[3]) {
			degree = (data - speedStandars[2]) * 45 / (speedStandars[3] - speedStandars[2]) + 45;
		} else if (data > speedStandars[3]) {
			degree = levelsDegree[4];
		}		
		runclock(mImageMinute, degree, 800, false);		
		if (data <= 0) {
			tvAverageSpeed.setText("0KB/S");
			tvBandWidth.setText("0M");
		}
	}
	
	private void runclock(ImageView iv, float degree, long duration,
			boolean circle) {
		ImageView imageView = iv;
		float rundegree = degree;

		Log.d("0888mOldDegree+rundegree", mOldDegree + "-----------------"
				+ rundegree);
		float runolddegree = mOldDegree;
		long runduration = duration;
		if (degree > mOldDegree) {
			imageView.setImageDrawable(getResources().getDrawable(R.drawable.downloadminute_up));
		} else if (degree < mOldDegree){
			imageView.setImageDrawable(getResources().getDrawable(R.drawable.downloadminute_down));
		} else {
			imageView.setImageDrawable(getResources().getDrawable(R.drawable.downloadminute));
		}
		mOldDegree = degree;
		
		RotateAnimation anim = new RotateAnimation(runolddegree, rundegree,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.95f);
		anim.setDuration(runduration);
		anim.setInterpolator(new LinearInterpolator());
		anim.setFillAfter(true);
		if (circle) {
			anim.setRepeatMode(1);
			anim.setRepeatCount(-1);
		}
		imageView.startAnimation(anim);
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
		case R.id.btn_speed_test_start:		
			String starttest = getString(R.string.speed_test_start);
			String cancletest = getString(R.string.speed_test_stop);
			String startagain = getString(R.string.speed_test_again);
			if (btnStartTest.getText().equals(starttest) || btnStartTest.getText().equals(startagain)) {
				secondMessage = false;
//				mLayoutSpeedProgress.setVisibility(View.VISIBLE);
				mLayoutSpeedResult.setVisibility(View.GONE);
//				mProgressTest.setProgress(0);
//				tvProgressTest.setText("0%");
				speedThread = new SpeedThread();
				speedThread.isRunning = true;
				
				speedThread.start();
				btnStartTest.setText(cancletest);
			} else {
				destoryRunningThread();
				destoryConnection();
				if (startTest) {
					btnStartTest.setEnabled(false);
					btnStartTest.setClickable(false);
				}
				mOldDegree = levelsDegree[0];
				refreshclock(0);
				btnStartTest.setText(starttest);

			}
			break;
		}
		
	}
	
	private TimerTask mTimerTask = new TimerTask() {

		public void run() {
			Log.d("0888runTime+mTotalSize", "mTotalSize"
					+ mTotalSize);
			Log.d("0888runTime+mTotalSize", "speedThread.isRunning"
					+ speedThread.isRunning);
			if (speedThread.isRunning && mTotalSize > 0) {
//				int progress = (downLoadFileSize * 100) / mTotalSize;
//				mProgressTest.setProgress(progress);			
				
				if (downLoadFileSize > 0 && runTime == 0) {
					speed = (downLoadFileSize - curdownLoadFileSize) / 1024;
					curdownLoadFileSize = downLoadFileSize ;
					handler.sendEmptyMessage(REFRESH_PROGRESS);	
					
					Log.d("0888runTime+downLoadFileSize", "speed "
							+ speed);
				} 				
			}
			/*dataTraffic.readFile(SpeedTestActivity.this);
			curAverageSpeed = dataTraffic.diffReceive();
			mCurAvSpString = dataTraffic.diffReceive(SpeedTestActivity.this);
			Log.d("speedtest", "" + curAverageSpeed);*/
		}
	};
	
	private String getBandWidth() {
		for (int i = 0; i < bandWidthStandards.length; i++) {
			if (speed < bandWidthStandards[i]) {
				return bandWidths[i];
			}
		}
		return bandWidths[bandWidths.length - 1];
	}
	
	public SpeedThread speedThread = new SpeedThread();
	class SpeedThread extends Thread {
		volatile boolean isRunning = false;

		public void run() {
			try {
				startTest = true;
				speed = getAverageSpeed();
				startTest = false;				
			} catch (Exception e) {
				Log.d("xieweifeng", "in exception");
				e.printStackTrace();
			}
		}

	}
	
	private long startTime = 0;
	private double getAverageSpeed() {
		downLoadFileSize = 0;
		runTime = 0;
		speed = 0;
		byte[] bs = new byte[READ_BLOCK_SIZE];
		
		curdownLoadFileSize = 0;
		mCurState = 0;
		startTime = System.currentTimeMillis();
		int time = 0;
		
		try {
			URL url = new URL(DOWNLOAD_URL);
			for (time = 0; time < READ_BLOCK_TIME; time++) {				
				conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(REQUEST_TIME_OUT);
				int connectstate = -1;
				try {
					connectstate = conn.getResponseCode();
				} catch (IOException e) {
					e.printStackTrace();
				}
				Log.d("0888connectstate", connectstate + "");
				if (connectstate == HttpURLConnection.HTTP_OK) {
	
					InputStream is = null;
					if (conn != null) {						
						is = conn.getInputStream();
						int line = 0;
						line = is.read(bs);
						while (speedThread.isRunning) {
							line = is.read(bs);							
							if (line == -1) {								
								break;
							}
							downLoadFileSize += line;
						}
						is.close();
						conn.disconnect();
						Log.d("0888runTime+downLoadFileSize", "downLoadFileSize"
								+ downLoadFileSize);						
						line = 0;
					}
				} else {
					if (connectstate == HttpURLConnection.HTTP_CLIENT_TIMEOUT) {
						mCurState = NET_ERROR;
					} else {
						mCurState = SET_NET_STOP;
					}
					break;
				}
				if (!speedThread.isRunning)
					break;
			}
		} catch (MalformedURLException e) {
			Log.d("0888runTime+MalformedURLException", "SET_NET_STOP" );
			e.printStackTrace();
			mCurState = PAGE_LOAD_ERROR;			
		} catch (IOException e) {
			Log.d("0888runTime+IOException", "SET_NET_STOP" );
			mCurState = SET_NET_STOP;
			e.printStackTrace();
		} catch (IllegalStateException e) {
			Log.d("0888runTime+IllegalStateException", "SET_NET_STOP" );
			mCurState = SET_NET_STOP;
			e.printStackTrace();
		} catch (Exception e) {
			Log.d("0888runTime+Exception", "SET_NET_STOP" );			
			mCurState = SET_NET_STOP;
			e.printStackTrace();
		} finally {
			if (mCurState != 0) {
				handler.sendEmptyMessage(mCurState);
			} else {
				long endTime = System.currentTimeMillis();
				Log.d("0888endTime", endTime + "");
				runTime = (endTime - startTime);
				Log.d("0888runTime", runTime + "");	
				downLoadFileSize = downLoadFileSize / 1024;
				Log.d("0888runTime+downLoadFileSize", "downLoadFileSize"
						+ downLoadFileSize);
				if (runTime != 0 && downLoadFileSize > 0 && speedThread.isRunning) {
					speed = (downLoadFileSize * 1000) / runTime;
					mCurState = GET_SPEED_FINISH;
					handler.sendEmptyMessage(mCurState);
				} else {
					mCurState = SET_NET_STOP;
					handler.sendEmptyMessage(mCurState);
				}	
			}
			try {
				mCurState = SET_BUTTON_TRUE;
				handler.sendEmptyMessage(mCurState);
				destoryConnection();
			} catch (Exception e) {
			}
		}
	
		return speed;
	}

	private void destoryRunningThread() {
		try {
			if (speedThread != null) {
				speedThread.isRunning = false;
				SpeedThread.interrupted();
				speedThread.interrupt();
			}
		} catch (Exception e) {
		}
	}
	
	private void destoryConnection() {
		try {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		} catch (Exception e) {
		}
	}

	private void updateRecommendVideo() {
		if (speed < bandWidthStandards[1]) {
			tvVideoRecommend.setText(R.string.speed_test_video_normal);
		} else if (speed < bandWidthStandards[2]) {
			tvVideoRecommend.setText(R.string.speed_test_video_high);
		} else if (speed >= bandWidthStandards[2]) {
			tvVideoRecommend.setText(R.string.speed_test_video_super);
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			Log.d(TAG, "handleMessage(Message msg)===>" + msg.what);
			if (!Thread.currentThread().isInterrupted()) {
				switch (msg.what) {

				case GET_SPEED_FINISH:
					Log.i("*************************", "speed" + speed);
					refreshclock((float) speed);
					if (runTime >= 0) {
						mLayoutSpeedResult.setVisibility(View.VISIBLE);
						tvAverageSpeed.setText(speed * 8 + "kb/s");
						tvBandWidth.setText(getBandWidth());
						updateRecommendVideo();
//						mProgressTest.setProgress(100);
//						tvProgressTest.setText(100 + "%");
					}
					destoryConnection();
					destoryRunningThread();
					btnStartTest.setText(getString(R.string.speed_test_again));
					mImageMinute.setImageDrawable(getResources().getDrawable(R.drawable.downloadminute));
					break;

				case NET_ERROR:
					Log.d(TAG, "handleMessage(Message msg)===>NET_ERROR");
					mOldDegree = levelsDegree[0];
					refreshclock(0);
//					Util.showToast(getApplicationContext(), getString(R.string.neterror));
					btnStartTest.setText(getString(R.string.speed_test_start));
					mLayoutSpeedResult.setVisibility(View.GONE);
					mImageMinute.setImageDrawable(getResources().getDrawable(R.drawable.downloadminute));
//					mLayoutSpeedProgress.setVisibility(View.GONE);
					break;

				case SET_BUTTON_TRUE:
					Log.d(TAG, "handleMessage(Message msg)===>SET_BUTTON_TRUE");
					btnStartTest.setEnabled(true);
					btnStartTest.setClickable(true);
					btnStartTest.setFocusable(true);
					btnStartTest.setText(getString(R.string.speed_test_start));
					destoryRunningThread();
					mImageMinute.setImageDrawable(getResources().getDrawable(R.drawable.downloadminute));
					break;

				case SET_NET_STOP:
					Log.d(TAG, "handleMessage(Message msg)===>SET_NET_STOP");
					mOldDegree = levelsDegree[0];
					refreshclock(0);
					btnStartTest.setText(getString(R.string.speed_test_start));
					mLayoutSpeedResult.setVisibility(View.GONE);
					mImageMinute.setImageDrawable(getResources().getDrawable(R.drawable.downloadminute));
//					mLayoutSpeedProgress.setVisibility(View.GONE);
					break;
				case REFRESH_PROGRESS:
					//tvProgressTest.setText(mProgressTest.getProgress() + "%");
					refreshclock((float) speed);
					break;
					default:
						break;
				}

			}
			super.handleMessage(msg);
		}
	};
	
}
