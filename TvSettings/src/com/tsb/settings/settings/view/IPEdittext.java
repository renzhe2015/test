package com.tsb.settings.settings.view;


import com.tsb.settings.R;
import com.tsb.settings.util.FragmentUtils;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


public class IPEdittext extends LinearLayout implements OnFocusChangeListener {

	private ViewHolder mViewHolder;
	private Context mContext;
	private EdtTextWatcher mTextWatcher;

	class ViewHolder {
		LinearLayout layout;
		TextView tv_name;
		EditText[] edts;
	}

	public IPEdittext(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.settings_editext, this);
		mViewHolder = new ViewHolder();
		mViewHolder.edts = new EditText[4];
		mViewHolder.tv_name = (TextView) layout.findViewById(R.id.tv_name);
		mViewHolder.layout = (LinearLayout) findViewById(R.id.layout);
		mViewHolder.edts[0] = (EditText) layout.findViewById(R.id.edt1);
		mViewHolder.edts[1] = (EditText) layout.findViewById(R.id.edt2);
		mViewHolder.edts[2] = (EditText) layout.findViewById(R.id.edt3);
		mViewHolder.edts[3] = (EditText) layout.findViewById(R.id.edt4);

		mTextWatcher = new EdtTextWatcher();
		for (int i = 0; i < 4; i++) {
			mViewHolder.edts[i].setOnFocusChangeListener(this);
			mViewHolder.edts[i].addTextChangedListener(mTextWatcher);
		}
		//		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.edt);
		//		mViewHolder.tv_name.setText(a.getString(R.styleable.edt_tv_name));
		//		a.recycle();
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (checkFocus()) {
			mViewHolder.layout.setBackgroundResource(R.drawable.edit_focus);
		} else {
			mViewHolder.layout
			.setBackgroundResource(R.drawable.edittext_button_selector);
		}
	}


	public void setText(String text) {		
		String[] res = split(text);
		if (res != null) {
			for (int i = 0; i < 4; i++) {
				mViewHolder.edts[i].setText(res[i]);
				mViewHolder.edts[i].setSelection(res[i].length());
			}
		}
	}

	public String getText() {
		String[] res = new String[4];
		for (int i = 0; i < 4; i++) {
			res[i] = mViewHolder.edts[i].getText().toString();
		}
		return res[0] + "." + res[1] + "." + res[2] + "." + res[3];
	}

	private String[] split(String text) {
		if (text == null || "".equals(text)) {
			return null;
		}
		String[] res = text.split("\\.");
		if (res.length == 4) {
			return res;
		}
		return null;
	}

	public int valid() {

		for (int i = 0; i < 4; i++) {
			String ip = mViewHolder.edts[i].getText().toString();
			if ("".equals(ip)) {
				return i;
			}
			Integer valueI;
			try {
				valueI = Integer.parseInt(ip);
			} catch (NumberFormatException e) {
				return i;
			}
			int value = valueI.intValue();
			if (value > 255) {
				return i;
			}
		}

		return -1;
	}

	public boolean checkFocus() {
		for (int i = 0; i < 4; i++) {
			if (mViewHolder.edts[i].isFocused()) {
				return true;
			}
		}
		return false;
	}

	private int getCurFocusPosition() {
		int current_forcus = -1;
		for (int i = 0; i < 4; i++) {
			if (mViewHolder.edts[i].hasFocus()) {
				current_forcus = i;
			}
		}
		return current_forcus;
	}

	public int checkNextFocus() {
		int current_forcus = -1;
		int next_forcus = -1;
		for (int i = 0; i < 4; i++) {
			if (mViewHolder.edts[i].isFocused()) {
				current_forcus = i;
			}
		}
		if (current_forcus != -1) {
			if (current_forcus < 3) {
				next_forcus = current_forcus + 1;
			}
		}
		return next_forcus;
	}

	public boolean isCursorAtFirst() {
		Log.d("IPEditText", "mViewHolder.edts[0].getSelectionStart()==>" + mViewHolder.edts[0].getSelectionStart());
		if (mViewHolder.edts[0].hasFocus() && mViewHolder.edts[0].getSelectionStart() == 0) {
			return true;
		}
		return false;
	}

	public void setForcus(int index) {
		mViewHolder.edts[index].requestFocus();
	}

	public boolean hasChanged() {
		return false;
	}

	class EdtTextWatcher implements TextWatcher {


		public EdtTextWatcher() {
		}

		@Override
		public void afterTextChanged(Editable s) {

			if (s.length() >= 3) {
				int next_forcus = checkNextFocus();
				if (next_forcus != -1) {
					mViewHolder.edts[next_forcus].requestFocus();
				}

			}
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub

		}		
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent keyEvent) {
		if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
			FragmentUtils.mHandler.removeMessages(0);
			FragmentUtils.mHandler.sendEmptyMessageDelayed(0, 15*1000); 
			int keyCode =  keyEvent.getKeyCode();
			switch(keyCode) {
			case KeyEvent.KEYCODE_DPAD_LEFT:
				if (getCurFocusPosition() == 0&&mViewHolder.edts[0].getSelectionStart()==0) {
					return true;
				}
				break;
			case KeyEvent.KEYCODE_DPAD_UP:
				if(this.getId()==R.id.eth_manuel_ip){
					return true;
				}
				break;
			case KeyEvent.KEYCODE_DPAD_CENTER:
				FragmentUtils.mHandler.removeMessages(0);
				return true;
			}
		}
		return super.dispatchKeyEvent(keyEvent);
	}

}
