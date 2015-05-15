package com.tsb.settings.fragment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

/**
 * 
 * If the text wider than the control width, will roll around
 * 
 * 
 */
public class RollTextView extends TextView {
	private String text;
	private float rollSpeed = 1.0f;
	private Paint paint;
	private Paint paintBg;
	private float x, y;
	private int w, h;
	private float txtLength = 0;
	private long delayRoll = 30;
	private int deley = 1000;
	private boolean flat;
	private boolean printSecondText;
	private String TAG = "RollTextView";
	private final int START = 10;
	private final int ROLL = 20;
	private float topOff = 0;
	private Handler mHandler;

	public RollTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		paintBg = new Paint();
		paintBg.setColor(Color.TRANSPARENT);
		paint = new Paint();
		paint.setTextSize(this.getTextSize());
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(this.getCurrentTextColor());

	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (flat) {
			canvas.drawRect(0, 0, w, h, paintBg);
			canvas.drawText(text, x, y, paint);
			if (x + txtLength < w * 2 / 3 && printSecondText) {
				canvas.drawText(text, x + txtLength + w * 1 / 3, y, paint);
			}
		}
		super.onDraw(canvas);
	}

	/**
	 * up offset
	 * 
	 * 
	 * 
	 * 
	 * @param off
	 */
	public void setOffTop(float off) {
		this.topOff = off;
	}

	private boolean userFlag = false;

	public void userDefauleOffSetTop() {
		userFlag = true;
	}

	private void start() {

		if (text == null) {
			return;
		}
		this.w = this.getWidth();
		this.h = this.getHeight();
		if (userFlag) {
			this.y = 21f;
		} else {
			this.y = (this.h + this.getTop() + topOff - paint.getTextSize()) / 2.0f;
		}
		txtLength = paint.measureText(text);

		Log.i(TAG, "x=" + x + " y=" + y + "   w=" + w + "  h=" + h);
		flat = true;
		if ((w - txtLength) / 2f < 0) {
			printSecondText = true;
			this.x = 0;
			RollTextView.this.invalidate();
			mHandler.sendEmptyMessageDelayed(ROLL, deley);
		} else {
			printSecondText = false;
			this.x = 0;
			RollTextView.this.invalidate();
		}
	}

	public float getRollSpeed() {
		return rollSpeed;
	}

	public void setRollSpeed(float rollSpeed) {
		this.rollSpeed = rollSpeed;
	}

	@Override
	public void setTextColor(int color) {
		paint.setColor(color);
		Log.i(TAG, "color" + color);
		super.setTextColor(color);
	}

	@Override
	public void setTextSize(float size) {
		paint.setTextSize(size);
		Log.i(TAG, "size" + size);
		super.setTextSize(size);
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		Log.i(TAG, "text" + text);
		if (text != null) {
			this.text = text.toString();
			if (mHandler == null) {
				mHandler = new Handler() {
					public void handleMessage(android.os.Message msg) {
						switch (msg.what) {
						case START:
							start();
							break;
						case ROLL:
							if (x + txtLength + w * 1 / 3.0f <= 0) {
								mHandler.removeMessages(ROLL);
								mHandler.sendEmptyMessageDelayed(ROLL, deley);
								x = 0;
								break;
							}
							RollTextView.this.invalidate();
							x -= rollSpeed;
							mHandler.sendEmptyMessageDelayed(ROLL, delayRoll);
							break;
						default:
							break;
						}

					}
				};
			}
			mHandler.removeMessages(START);
			mHandler.sendEmptyMessageDelayed(START, 100);

		}
		super.setText(null, type);
	}
}
