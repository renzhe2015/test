package com.tsb.settings.fragment;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class RollText extends TextView {

	public RollText(Context arg0, AttributeSet arg1, int arg2) {
		super(arg0, arg1, arg2);
		// TODO Auto-generated constructor stub
	}

	public RollText(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public RollText(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean isFocused() {
		// TODO Auto-generated method stub
		//return super.isFocused();
		return true;
	}
	
	@Override
	public boolean hasFocus() {
		// TODO Auto-generated method stub
		//return super.hasFocus();
		return true;
	}

}
