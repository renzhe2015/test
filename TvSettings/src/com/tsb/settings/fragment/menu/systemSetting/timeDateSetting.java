package com.tsb.settings.fragment.menu.systemSetting;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.util.AbstractMessageParser.Resources;
import com.tsb.settings.R;
import com.tsb.settings.fragment.menu.BaseMenuFragment;
import com.tsb.settings.menu.MenuItem;
import com.tsb.settings.util.FragmentUtils;

public class timeDateSetting extends BaseMenuFragment implements OnClickListener{
	Button timeConfirm, timeCancel;
	LinearLayout Date,Time;
	EditText yearEditText, monthEditText,dayEditText,hourEditText,minuteEditText;
	Time t = new Time();
	int YEAR,MONTH,DAY,HOUR,MINUTE;
	String stringDate, stringTime;
	public Context mContext;

	//add by huangfh@tcl.com
	private TextFilter textFilter=new TextFilter();
	//end add by huangfh@tcl.com
	@Override
	protected View onInflateLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.layout_activity_timedate_setting, container, false);
		timeConfirm = (Button)v.findViewById(R.id.timeConfirm);
		timeCancel = (Button)v.findViewById(R.id.timeCancel);
		yearEditText = (EditText)v.findViewById(R.id.year);
		monthEditText = (EditText)v.findViewById(R.id.month);
		dayEditText = (EditText)v.findViewById(R.id.day);
		hourEditText = (EditText)v.findViewById(R.id.hour);
		minuteEditText = (EditText)v.findViewById(R.id.minute);
		Date=(LinearLayout)v.findViewById(R.id.date);
		Time=(LinearLayout)v.findViewById(R.id.time);
		
		timeConfirm.setOnClickListener(onClick);
		timeCancel.setOnClickListener(onClick);
		
		timeConfirm.setOnFocusChangeListener(onFocus);
		timeCancel.setOnFocusChangeListener(onFocus);
		yearEditText.setOnFocusChangeListener(onFocus);
		monthEditText.setOnFocusChangeListener(onFocus);
		dayEditText.setOnFocusChangeListener(onFocus);
		hourEditText.setOnFocusChangeListener(onFocus);
		minuteEditText.setOnFocusChangeListener(onFocus);
		
		mContext = getActivity();
		
		t.setToNow();
		
		yearEditText.setText(""+t.year);
		monthEditText.setText(t.month+1+"");
		dayEditText.setText(""+t.monthDay);
		hourEditText.setText(""+t.hour);
		if(t.minute < 10){
			minuteEditText.setText("0"+t.minute);
		}else{
			minuteEditText.setText(""+t.minute);
		}
		
		//add by huangfh@tcl.com	
		yearEditText.requestFocus();
		yearEditText.addTextChangedListener(textFilter);
		monthEditText.addTextChangedListener(textFilter);
		dayEditText.addTextChangedListener(textFilter);
		hourEditText.addTextChangedListener(textFilter);
		minuteEditText.addTextChangedListener(textFilter);
		//end add by huangfh@tcl.com
//		yearEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() 
//		{  		      
//		    @Override  
//		    public void onFocusChange(View v, boolean hasFocus) {  
//		        if(hasFocus){	
//		        	Date.setBackgroundResource(R.drawable.edit_focus);
//		        }else{
//		        	Date.setBackgroundResource(R.drawable.edit_normal);
//		        }  
//		    }             
//		});  ;
//		
//		monthEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() 
//		{  		      
//		    @Override  
//		    public void onFocusChange(View v, boolean hasFocus) {  
//		        if(hasFocus){	
//		        	Date.setBackgroundResource(R.drawable.edit_focus);
//		        }else{
//		        	Date.setBackgroundResource(R.drawable.edit_normal);
//		        }  
//		    }             
//		});  ;
//		
//		dayEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() 
//		{  		      
//		    @Override  
//		    public void onFocusChange(View v, boolean hasFocus) {  
//		        if(hasFocus){	
//		        	Date.setBackgroundResource(R.drawable.edit_focus);
//		        }else{
//		        	Date.setBackgroundResource(R.drawable.edit_normal);
//		        }  
//		    }             
//		});  ;
//		
//		hourEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() 
//		{  		      
//		    @Override  
//		    public void onFocusChange(View v, boolean hasFocus) {  
//		        if(hasFocus){	
//		        	Time.setBackgroundResource(R.drawable.edit_focus);
//		        }else{
//		        	Time.setBackgroundResource(R.drawable.edit_normal);
//		        }  
//		    }             
//		});  ;
//		
//		minuteEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() 
//		{  		      
//		    @Override  
//		    public void onFocusChange(View v, boolean hasFocus) {  
//		        if(hasFocus){	
//		        	Time.setBackgroundResource(R.drawable.edit_focus);
//		        }else{
//		        	Time.setBackgroundResource(R.drawable.edit_normal);
//		        }  
//		    }             
//		});  ;
		
		return v;
	}
	
	View.OnFocusChangeListener  onFocus=new  View.OnFocusChangeListener()
	{
		@Override
		public void onFocusChange(View arg0, boolean arg1) {
			// TODO Auto-generated method stub
			switch(arg0.getId())
			{
				case R.id.year:
				case R.id.month:
				case R.id.day:
					Log.v("tq test", "it is going to the DATE!");
					Time.setBackgroundResource(R.drawable.edit_normal);
					Date.setBackgroundResource(R.drawable.edit_focus);
					break;
				case R.id.hour:
				case R.id.minute:
					Log.v("tq test", "it is going to the TIME!");
					Date.setBackgroundResource(R.drawable.edit_normal);
					Time.setBackgroundResource(R.drawable.edit_focus);
					break;
				case R.id.timeConfirm:
				case R.id.timeCancel:	
					Date.setBackgroundResource(R.drawable.edit_normal);
					Time.setBackgroundResource(R.drawable.edit_normal);
					
			}			
		}
		
	};
	
	View.OnClickListener onClick = new View.OnClickListener()
    {
        public void onClick(View v)
        {        	
            switch (v.getId())
            {
            case R.id.timeConfirm:
            	 if(valid()){
            		 mContext = getActivity();
						Calendar c = Calendar.getInstance();  
						c.set(YEAR, MONTH-1, DAY, HOUR, MINUTE);
						long when = c.getTimeInMillis();  							   
			            SystemClock.setCurrentTimeMillis(when);   
						Log.v("tq test", " "+YEAR+" "+MONTH+" "+DAY+" "+HOUR+" "+MINUTE+" ");
						Intent intent=new Intent();  
			            intent.setAction("UPDATE TIME");  
			            intent.putExtra("timeDateSetting", "update time");
			            mContext.sendBroadcast(intent); 
						FragmentUtils.popBackSubFragment(timeDateSetting.this);
            	 }
            	 break;           	
            case R.id.timeCancel:
            	FragmentUtils.popBackSubFragment(timeDateSetting.this);
            	break;
            }
        }
    };
	
    
	public boolean valid()
	{		
		if(yearEditText.getText().toString().equals("")
			||monthEditText.getText().toString().equals("")
			||dayEditText.getText().toString().equals("")
			||hourEditText.getText().toString().equals("")
			||minuteEditText.getText().toString().equals(""))
		{
			Toast.makeText(mContext,  getResources().getString(R.string.null_input),Toast.LENGTH_SHORT).show();
		}
		else
		{
			YEAR= Integer.parseInt(yearEditText.getText().toString());
			MONTH= Integer.parseInt(monthEditText.getText().toString());
			DAY= Integer.parseInt(dayEditText.getText().toString());
			HOUR= Integer.parseInt(hourEditText.getText().toString());
			MINUTE= Integer.parseInt(minuteEditText.getText().toString());
			
		if(YEAR<1970||YEAR>2030)
				Toast.makeText(mContext, getResources().getString(R.string.year_false),Toast.LENGTH_SHORT).show();
		
		else	
			if(MONTH==0||MONTH>12)
				Toast.makeText(mContext, getResources().getString(R.string.month_false),Toast.LENGTH_SHORT).show();
			else 
				if(!day_valid(YEAR,MONTH,DAY))
					Toast.makeText(mContext, getResources().getString(R.string.day_false),Toast.LENGTH_SHORT).show();
				else
					if(HOUR>24)
						Toast.makeText(mContext,  getResources().getString(R.string.hour_false),Toast.LENGTH_SHORT).show();
					else
						if(MINUTE>59)
							Toast.makeText(mContext,  getResources().getString(R.string.minute_false),Toast.LENGTH_SHORT).show();
						else
						{
							return true;
//							mContext = getActivity();
//							Calendar c = Calendar.getInstance();  
//							c.set(YEAR, MONTH-1, DAY, HOUR, MINUTE);
//							long when = c.getTimeInMillis();  							   
//				            SystemClock.setCurrentTimeMillis(when);   
//							Log.v("tq test", " "+YEAR+" "+MONTH+" "+DAY+" "+HOUR+" "+MINUTE+" ");
//							Intent intent=new Intent();  
//				            intent.setAction("UPDATE TIME");  
//				            intent.putExtra("timeDateSetting", "update time");
//				            mContext.sendBroadcast(intent); 
//							FragmentUtils.popBackSubFragment(timeDateSetting.this);
						}
		}
		return false;
						
	}
	

	public boolean day_valid(int YEAR,int MONTH,int DAY)
	{
		switch(MONTH)
		{
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			if(DAY>31||DAY==0)
				return false;
			else
				return true;
		case 4:
		case 6:
		case 9:
		case 11:
			if(DAY>30||DAY==0)
				return false;
			else
				return true;
		case 2:
			if(YEAR % 400 == 0 || ((YEAR % 4 == 0) && (YEAR % 100 != 0)))
			{
				if(DAY>29||DAY==0)
					return false;
				else
					return true;
			}
			else
			{
				if(DAY>28||DAY==0)
					return false;
				else
					return true;
			}
				
		}
		return false;			
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
	//add by huangfh@tcl.com
	class TextFilter implements TextWatcher{

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			valid();
		}
		
	}
	//end add by huangfh@tcl.com
	
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(KeyEvent.KEYCODE_MENU == keyCode){
			Intent intent = new Intent("hideMainMenuNow");
			getActivity().sendBroadcast(intent);
			return true;
		}else{
			return super.onKey(v, keyCode, event);
		}
	}
}
