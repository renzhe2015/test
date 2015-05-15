package com.tsb.settings.fragment;

import java.util.Calendar;

import com.tsb.settings.R;
import com.tsb.settings.TvManagerHelper;

import android.util.Log;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.widget.TextView;

public class AdvDigitalClock extends TextView {

	Calendar mCalendar;
	private final static String m12 = "h:mm:ss aa";
	private final static String m24 = "k:mm:ss";

	private Runnable mTicker;
	private Handler mHandler;

	private boolean mTickerStopped = false;

	String mFormat;
   
    private TvManagerHelper mTvManager;
    
	public AdvDigitalClock(Context context) {
	
		this(context, null);
		
		// TODO Auto-generated constructor stub
	}

	public AdvDigitalClock(Context context, AttributeSet attrs) {
	    
		this(context, attrs, 0);
		
		// TODO Auto-generated constructor stub
	}

	public AdvDigitalClock(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		mTvManager = TvManagerHelper.getInstance(context);
		
		// TODO Auto-generated constructor stub
		if (attrs != null) {
			TypedArray a = context.obtainStyledAttributes(attrs,
					R.styleable.AdvDigitalClock);
			String advDateFormat = a
					.getString(R.styleable.AdvDigitalClock_adv_date_format);
			if (advDateFormat == null || "".equals(advDateFormat)) {
				// 没有输入自定义的时间输出格式，采用默认格式显示。
				if (get24HourMode()) {
					mFormat = m24;
				} else {
					mFormat = m12;
				}
			} else {
				mFormat = advDateFormat;
			}
			a.recycle();
		}

		initClock(context);
	}

	private void initClock(Context context) {
	
		Resources r = context.getResources();

		if (mCalendar == null) {
			mCalendar = Calendar.getInstance();
		}

		setFormat(mFormat);
	}

	@Override
	protected void onAttachedToWindow() {
	
		mTickerStopped = false;
		super.onAttachedToWindow();
		mHandler = new Handler();

		/**
		 * requests a tick on the next hard-second boundary
		 */
		mTicker = new Runnable() {
			public void run() {
				if (mTickerStopped)
					return;
				//mCalendar.setTimeInMillis(System.currentTimeMillis());
				
				mCalendar.setTimeInMillis(mTvManager.currentTvTimeMillis());
				
				setText(DateFormat.format(mFormat, mCalendar));
				invalidate();
				long now = SystemClock.uptimeMillis();
				long next = now + (1000 - now % 1000);
				mHandler.postAtTime(mTicker, next);
			}
		};
		mTicker.run();
	}

	@Override
	protected void onDetachedFromWindow() {
	
	
		super.onDetachedFromWindow();
		mTickerStopped = true;
	}

	/**
	 * Pulls 12/24 mode from system settings
	 */
	private boolean get24HourMode() {
	
		return android.text.format.DateFormat.is24HourFormat(getContext());
	}

	private void setFormat(String advDateFormat) {
	
		mFormat = advDateFormat;
	}
}
