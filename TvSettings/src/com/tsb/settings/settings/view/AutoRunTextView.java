package com.tsb.settings.settings.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

public class AutoRunTextView extends TextView{
	
	private boolean marguee = false;

	public AutoRunTextView(Context context) {
		super(context);
	}

	public AutoRunTextView(Context context,AttributeSet set) {
		super(context,set);
	}
	
	public void setMarquee(boolean marguee){
		this.marguee = marguee;
	}
	
	@Override
	public boolean isFocused() {
		if(marguee){
			return true;
		}
		return super.isFocused();
	}
	
	@Override  
	protected void onFocusChanged(boolean focused, int direction,  
			Rect previouslyFocusedRect) {  
	if (isFocused())  
		super.onFocusChanged(isFocused(), direction, previouslyFocusedRect);  
	}  
	
	@Override  
	public void onWindowFocusChanged(boolean focused) {  
	if (isFocused())  
		super.onWindowFocusChanged(isFocused());  
	} 
}
