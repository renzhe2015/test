package com.tsb.settings.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.tsb.settings.TvManagerHelper;

public class DfbFragment extends BaseFragment {
	private final static String TAG = "DfbSurface";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Preview preview = new Preview(inflater.getContext());
		return preview;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		int action = event.getAction();
		TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());
		switch(keyCode) {
			case KeyEvent.KEYCODE_PROG_RED:
				getFragmentManager().beginTransaction().remove(this).commit();
				return true;
			case KeyEvent.KEYCODE_ESCAPE:
			case KeyEvent.KEYCODE_BACK:
			case KeyEvent.KEYCODE_DPAD_UP:
			case KeyEvent.KEYCODE_DPAD_DOWN:
			case KeyEvent.KEYCODE_DPAD_LEFT:
			case KeyEvent.KEYCODE_DPAD_RIGHT:
			case KeyEvent.KEYCODE_DPAD_CENTER:
			case KeyEvent.KEYCODE_ENTER:
			case 262://KeyEvent.KEYCODE_JUMP:
			case KeyEvent.KEYCODE_PROG_BLUE:
			case KeyEvent.KEYCODE_PROG_GREEN:
			case KeyEvent.KEYCODE_PROG_YELLOW:
			case KeyEvent.KEYCODE_0:
			case KeyEvent.KEYCODE_1:
			case KeyEvent.KEYCODE_2:
			case KeyEvent.KEYCODE_3:
			case KeyEvent.KEYCODE_4:
			case KeyEvent.KEYCODE_5:
			case KeyEvent.KEYCODE_6:
			case KeyEvent.KEYCODE_7:
			case KeyEvent.KEYCODE_8:
			case KeyEvent.KEYCODE_9:
				if (tm.isForwardKeyToGinga()) {
					if (action == KeyEvent.ACTION_DOWN) {
						tm.forwardKeyToGinga(keyCode);
					}
					Log.v(TAG, "Intercept key event: " + event.toString());
					return true;
				}
				break;
			default:
				break;
		}
		
		return false;
	}

	static class Preview extends SurfaceView implements SurfaceHolder.Callback {
//		private final String TAG = "Preview";
//		SurfaceHolder mHolder;
//		Dfb mDfb = new Dfb();
//		TvManager mTV;
//		private int screenWidth;// px
//		private int screenHeight;// py

		Preview(Context context) {
			super(context);
//
//			mTV = (TvManager) context.getSystemService("tv");
//			// Install a SurfaceHolder.Callback so we get notified when the
//			// underlying surface is created and destroyed.
//			mHolder = getHolder();
//			mHolder.addCallback(this);
//			mHolder.setFormat(5);   //HAL_PIXEL_FORMAT_BGRA_8888  
//			mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//			DisplayMetrics dm = new DisplayMetrics();
//			dm = getResources().getDisplayMetrics();
//			// this.getWindowManager().getDefaultDisplay().getMetrics(dm);
//			screenWidth = dm.widthPixels;
//			screenHeight = dm.heightPixels;
//			Log.e(TAG, "screenWidth = " + screenWidth + "screenHeight = "
//					+ screenHeight);

		}

		public void surfaceCreated(SurfaceHolder holder) {
//			try {
//				mDfb.setDfbDisplay(holder.getSurface(), screenWidth,
//						screenHeight);
//			} catch (IOException exception) {
//				Log.e(TAG, "IOException caused by setPreviewDisplay()",
//						exception);
//			}
//			mTV.AndroidDfbStart();
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
//			TvManagerHelper.getInstance(getContext()).closeGinga();
//			mDfb.surfacedestory(); // close capturethread
//			mTV.AndroidDfbStop();
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
			
		}

	}
}
