package com.tsb.settings.fragment;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsb.settings.R;

public class CustomProgessBar extends LinearLayout {
	static final public int STYLE_SIGNAL_INTENSITY = 1;
	static final public int STYLE_NORMAL = 2;
	static final public int STYLE_UNKNOWN = 3;

	private int style = STYLE_UNKNOWN;
	private Context mContext;
	private TextView innerProgressDrawable;

	public CustomProgessBar(Context context) {
		super(context);
		mContext = context;
	}

	public CustomProgessBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
	}

	public CustomProgessBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	/**
	 * 设置进度条类型，根据类型不同显示不同的样式
	 * @param style 要设置的类型，分STYLE_SIGNAL_INTENSITY和STYLE_NORMAL两种。
	 * STYLE_SIGNAL_INTENSITY类型是信号强度进度条。
	 * STYLE_NORMAL是普通样式的进度条。
	 */
	public void setStyle(int style) {
		//如果设置的style和当前style相同，则不作动作。
		if (this.style == style){
			return;
		}
		this.style = style;
		if (style == STYLE_SIGNAL_INTENSITY) {
			setProgressOrIntensity(0);

		} else if (style == STYLE_NORMAL) {
			setPadding(0, 2, 2, 0);
			setBackgroundResource(R.drawable.dtv_infobar_progress_background);
			innerProgressDrawable = new TextView(mContext);
			innerProgressDrawable.setBackgroundResource(R.drawable.dtv_infobar_progress_foreground);
			addView(innerProgressDrawable, new LayoutParams(20,
					LayoutParams.MATCH_PARENT));
		}
	}

	/**
	 * 设置进度条的信号强度值或者当前进度值
	 * @param percentageOrIntensity
	 * 		当前进度条类型为STYLE_SIGNAL_INTENSITY时，设置进度条显示的信号强度，有可以为0-100
	 * 		当前进度条类型为STYLE_NORMAL时，设置进度条进度，有可以为0-100
	 */
	public void setProgressOrIntensity(int percentageOrIntensity) {
		if (percentageOrIntensity > 100){
			percentageOrIntensity = 100;
		}
		else if (percentageOrIntensity < 0){
			percentageOrIntensity = 0;
		}
		if (style == STYLE_SIGNAL_INTENSITY) {
			Log.v("CustomProgessBar", "set  signal  Intensity : " + percentageOrIntensity);
			if(percentageOrIntensity < 10){
				percentageOrIntensity = percentageOrIntensity*10;
			}
			percentageOrIntensity = percentageOrIntensity / 25;
			if (percentageOrIntensity == 4){
				percentageOrIntensity = 3;
			}
			if (percentageOrIntensity >= 0 && percentageOrIntensity < 4) {
				int[] intensityDrawables = { R.drawable.signal_intensity_01,
						R.drawable.signal_intensity_02,
						R.drawable.signal_intensity_03,
						R.drawable.signal_intensity_04};
				setBackgroundResource(intensityDrawables[percentageOrIntensity]);
			}
		} else if (style == STYLE_NORMAL) {
			Log.v("FH", "setProgress : " + percentageOrIntensity);
			android.view.ViewGroup.LayoutParams tempLayoutParams = innerProgressDrawable.getLayoutParams();
			tempLayoutParams.width = getLayoutParams().width * percentageOrIntensity / 100;
			innerProgressDrawable.setLayoutParams(tempLayoutParams);
		}
	}
}
