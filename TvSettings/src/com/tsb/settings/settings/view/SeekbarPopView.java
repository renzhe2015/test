package com.tsb.settings.settings.view;

import android.content.Context;
import android.os.Handler;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.View.OnKeyListener;
import android.widget.LinearLayout;

import com.tsb.settings.R;
import com.tsb.settings.settings.view.VerticalSeekBar.OnSeekBarChangeListener;

public class SeekbarPopView extends PopView implements OnKeyListener,
		OnDragListener {

	private VerticalSeekBar v_seekbar;
	private LinearLayout layout;

	public SeekbarPopView(Context mContext, View parent, int initpos) {
		super(mContext, parent);
		LayoutInflater layoutInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layout = (LinearLayout) layoutInflater.inflate(
				R.layout.popview_seekbar, null);
		v_seekbar = (VerticalSeekBar) layout.findViewById(R.id.v_seekbar);
		v_seekbar.setOnKeyListener(this);
		v_seekbar.setProgress(initpos);
		v_seekbar.setOnDragListener(this);
		width = 150;
		height = 250;
		yoff = 32;
		initContentView(layout);
	}

	public SeekbarPopView(Context mContext, View parent, int max, int initpos) {
		super(mContext, parent);
		LayoutInflater layoutInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layout = (LinearLayout) layoutInflater.inflate(
				R.layout.popview_seekbar, null);
		v_seekbar = (VerticalSeekBar) layout.findViewById(R.id.v_seekbar);
		v_seekbar.setOnKeyListener(this);
		v_seekbar.setProgress(initpos);
		v_seekbar.setMax(max);
		width = 150;
		height = 250;
		yoff = 32;
		initContentView(layout);
	}

	public void setOnSeekBarChangeListener(
			OnSeekBarChangeListener seekBarChangeListener) {
		v_seekbar.setOnSeekBarChangeListener(seekBarChangeListener);
	}

	@Override
	public boolean onKey(View arg0, int keycode, KeyEvent event) {
		TaskTimer.onTimer(mContext); // Re timing
		if (keycode == KeyEvent.KEYCODE_DPAD_UP
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			handler.sendEmptyMessageDelayed(0, 200); // use this 200 delay wait
														// for key up
		}
		return false;
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			SeekbarPopView.this.dismiss();
		};
	};

	@Override
	public int getSelectedPosition() {
		return v_seekbar.getProgress();
	}

	@Override
	public boolean onDrag(View arg0, DragEvent arg1) {
		TaskTimer.onTimer(mContext); // Re timing
		return false;
	}

	@Override
	public int getSelectedPosition(String value) {
		return 0;
	}

}
