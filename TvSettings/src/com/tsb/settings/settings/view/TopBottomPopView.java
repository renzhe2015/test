package com.tsb.settings.settings.view;

import android.content.Context;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsb.settings.R;


public class TopBottomPopView extends PopView implements OnKeyListener ,OnClickListener{

	private LinearLayout layout;
	private TextView textvalue;
	private ImageView uparraw,downarraw;
	private OnUpDownClickListener upDownClickListener;

	public TopBottomPopView(Context mContext, View parent,String value,OnUpDownClickListener upDownClickListener) {
		super(mContext, parent);
		LayoutInflater layoutInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layout = (LinearLayout) layoutInflater.inflate(
				R.layout.popview_arraw, null);
		layout.setOnKeyListener(this);
		uparraw = (ImageView)layout.findViewById(R.id.uparraw);
		uparraw.setOnClickListener(this);
		downarraw = (ImageView)layout.findViewById(R.id.downarraw);
		downarraw.setOnClickListener(this);
		textvalue = (TextView) layout.findViewById(R.id.textvalue);
		textvalue.setText(value);
		width = 170;
		height = 220;
		yoff = 32;
		initContentView(layout);
		this.upDownClickListener = upDownClickListener;
	}
	
	public void setValue(String text){
		textvalue.setText(text);
	}

	@Override
	public boolean onKey(View arg0, int keycode, KeyEvent event) {
		TaskTimer.onTimer(mContext); // Re timing
		if (keycode == KeyEvent.KEYCODE_DPAD_LEFT
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			handler.sendEmptyMessageDelayed(0, 200); // use this 200 delay wait
		} else if(keycode == KeyEvent.KEYCODE_DPAD_UP
				&& event.getAction() == KeyEvent.ACTION_DOWN){
			upDownClickListener.onUp();
		} else if(keycode == KeyEvent.KEYCODE_DPAD_DOWN
				&& event.getAction() == KeyEvent.ACTION_DOWN){
			upDownClickListener.onDown();
		}
		return false;
	}
	
	public interface OnUpDownClickListener{
		void onUp();
		void onDown();
	} 

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			TopBottomPopView.this.dismiss();
		};
	};

	@Override
	public int getSelectedPosition() {
		return 0;
	}

	@Override
	public void onClick(View arg0) {
		TaskTimer.onTimer(mContext); 
		switch (arg0.getId()) {
		case R.id.uparraw:
			upDownClickListener.onUp();
			break;
		case R.id.downarraw:
			upDownClickListener.onDown();
			break;
		default:
			break;
		}
	}

	@Override
	public int getSelectedPosition(String value) {
		return 0;
	}

}
