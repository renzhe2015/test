package com.tsb.settings.settings.view;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.tsb.settings.R;
import com.tsb.settings.settings.view.AutoRunTextView;
import com.tsb.settings.settings.view.PopView;
import com.tsb.settings.settings.view.TopBottomPopView;
import com.tsb.settings.settings.view.PopView.OnPopViewDismissListener;
import com.tsb.settings.settings.view.TopBottomPopView.OnUpDownClickListener;
import com.tsb.settings.settings.view.VerticalSeekBar.OnSeekBarChangeListener;
import com.tsb.settings.util.FragmentUtils;

public class LayoutItem extends FrameLayout implements OnFocusChangeListener,
		OnPopViewDismissListener, OnClickListener {

	private ImageView imageView, divider_below;
	private TextView textTitle;
	private AutoRunTextView textValue;
	private Context mContext;
	private boolean needPopWindow = true; // defalut is true
	private PopView popView;
	public static final int LISTSHOW = 0;
	public static final int SEEKBARSHOW = 1;
	public static final int ARRAWSHOW = 2;
	private int show = LISTSHOW;

	private String[] list;
	private int position = 0;
	private String value;
	private OnItemClickListener itemClickListener;
	private OnSeekBarChangeListener seekBarChangeListener;
	private OnUpDownClickListener upDownClickListener;

	public LayoutItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.layout_item_option_menu, this);
		imageView = (ImageView) findViewById(R.id.imageview);
		divider_below = (ImageView) findViewById(R.id.divider_below);
		textValue = (AutoRunTextView) findViewById(R.id.textvalue);
		textTitle = (TextView) findViewById(R.id.textview);
		setOnFocusChangeListener(this);
		setOnClickListener(this);
		setClickable(true);
		setFocusable(true);
		setBackgroundResource(R.drawable.sub_menu_selector);
	}

	@Override
	public void setFocusable(boolean arg0) {
		super.setFocusable(arg0);
		if (!arg0) {
			textValue.setTextColor(getResources().getColor(R.color.gray_color));
			textTitle.setTextColor(getResources().getColor(R.color.gray_color));
			imageView.setVisibility(View.GONE);
			setEnabled(false);
		}
	}

	public void needPopWindow(boolean needPopWindow) {
		this.needPopWindow = needPopWindow;
	}

	public void needRightArraw(boolean needArraw) {
		if (!needArraw) {
			imageView.setVisibility(View.GONE);
			setClickable(false);
			setFocusable(false);
		}
	}

	public void noRightArrawWithClickable() {
		imageView.setVisibility(View.GONE);
		setClickable(true);
		setFocusable(true);
	}

	public void needBottomLine(boolean needLine) {
		if (!needLine) {
			divider_below.setVisibility(View.GONE);
		}
	}

	public void needTextMarquee(boolean needMarquee) {
		textValue.setMarquee(needMarquee);
	}

	public void initListView(final String[] list, int position,
			OnItemClickListener clickListener) {
		show = LISTSHOW;
		this.list = list;
		this.position = position;
		itemClickListener = clickListener;
		if (itemClickListener == null) {
			itemClickListener = new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					setSelectedItemPosition(position);
					if (list.length > position && position > 0) {
						setTextValue(list[position]);
						popView.dismiss();
					}
				}
			};
		}
	}

	private void setSelectedItemPosition(int position) {
		this.position = position;
	}

	public void initSeekBarView(int process,OnSeekBarChangeListener onSeekBarChangeListener) {
		show = SEEKBARSHOW;
		position = process;
		seekBarChangeListener = onSeekBarChangeListener;
	}

	public void initArrawView(String value,OnUpDownClickListener upDownClickListener) {
		show = ARRAWSHOW;
		this.value = value;
		this.upDownClickListener = upDownClickListener;
	}



	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
	}

	
	
	
	
	
	
	
	public PopView getPopView() {
		return popView;
	}
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (needPopWindow && popView != null && popView.isShowing()) {
			popView.dismiss();
			popView = null;
		}
	}

	@Override
	public boolean onKeyDown(int keycode, KeyEvent arg1) {
		if (/**
		 * keycode == KeyEvent.KEYCODE_DPAD_CENTER ||
		 **/
		keycode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			if (needPopWindow) {
				showPopView();
			}
		}
		return super.onKeyDown(keycode, arg1);
	}

	private void showPopView() {
//		setBackgroundResource(R.drawable.item_normal);
		switch (show) {
		case LISTSHOW:
			popView = new ListPopView(mContext, this, list, position);
			((ListPopView) popView).setOnItemClickListener(itemClickListener);
			break;
		case SEEKBARSHOW:
			popView = new SeekbarPopView(mContext, this, position);
			((SeekbarPopView) popView)
					.setOnSeekBarChangeListener(seekBarChangeListener);
			break;
		case ARRAWSHOW:
			popView = new TopBottomPopView(mContext, this, value,
					upDownClickListener);
			break;
		default:
			break;
		}

		popView.setOnPopViewDismissListener(this);
		popView.show();
	}

	public String getSelectedItem() {
		if (popView != null && show == LISTSHOW) {
			position = popView.getSelectedPosition(); // sync textvalue&popView
			if (list.length > position) {
				return list[position];
			}
		}
		return null;
	}
	public void setTitle(String text) {
		textTitle.setText(text);
	}

	public void setTitle(int text) {
		textTitle.setText(mContext.getString(text));
	}

	public void setTextValue(String text) {
		textValue.setText(text);
		if (popView != null && show == LISTSHOW) {
			position = popView.getSelectedPosition(text);
		} else if (popView != null && show == SEEKBARSHOW) {
			int newPos;
			try {
				newPos = Integer.parseInt(text);
				position = newPos;
			} catch (Exception e) {
			}
		} else if (popView != null && show == ARRAWSHOW) {
			value = text;
			((TopBottomPopView) popView).setValue(value);
		}
	}

	public void setTextValueWidth(int pixels) {
		android.view.ViewGroup.LayoutParams params = textValue
				.getLayoutParams();
		if (params == null) {
			params = new LayoutParams(
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		if (pixels == 0) {
			params.width = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		} else {
			params.width = pixels;
		}
		textValue.setLayoutParams(params);
		textValue.setGravity(Gravity.RIGHT);
		textValue.setPadding(0, 0, 5, 0);
	}

	public int getSelectedItemPosition() {
		return position;
	}

	public String getTextValue() {
		return (String) textValue.getText();
	}

	@Override
	public void onFocusChange(View arg0, boolean focus) {
		if (focus) {
			imageView.setBackgroundResource(R.drawable.arrow_right_focus);
			textTitle.setTextColor(getResources().getColor(R.color.text_focus));
			textValue.setTextColor(getResources().getColor(R.color.text_focus));
		} else {
			if (needPopWindow) {
				setBackgroundResource(R.drawable.sub_menu_selector);
			}
			imageView.setBackgroundResource(R.drawable.arrow_right_unfocus);
			textTitle.setTextColor(getResources()
					.getColor(R.color.text_unfocus));
			textValue.setTextColor(getResources()
					.getColor(R.color.text_unfocus));
		}
	}

	@Override
	public void OnPopViewDismiss() {
	//	setBackgroundResource(R.drawable.sub_menu_selector);
	//	LayoutItem.this.requestFocus();
		if (popView != null && show == LISTSHOW) {
			position = popView.getSelectedPosition(textValue.getText()
					.toString());
		} else {
			position = popView.getSelectedPosition();
		}
	}

	@Override
	public void onClick(View arg0) {
		FragmentUtils.mHandler.removeMessages(0);
		FragmentUtils.mHandler.sendEmptyMessageDelayed(0, 15000);
		if (needPopWindow) {
			showPopView();
		}
	}
	public String[] getList(){
		if(list!=null)
			return list;
		else 
			return null;
	}
}
