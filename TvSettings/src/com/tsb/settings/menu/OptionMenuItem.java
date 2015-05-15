package com.tsb.settings.menu;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.tsb.settings.R;
import com.tsb.settings.settings.view.AutoRunTextView;
import com.tsb.settings.settings.view.ListPopView;
import com.tsb.settings.settings.view.PopView;
import com.tsb.settings.settings.view.PopView.OnPopViewDismissListener;
import com.tsb.settings.util.LogUtil;

@SuppressLint("ResourceAsColor")
public class OptionMenuItem extends MenuItem implements OnFocusChangeListener,OnPopViewDismissListener{//{
	private static final String TAG = "OptionMenuItem";
	boolean mRightKeyEventEqualsOnClick=false;
	public boolean mClickEqualsRightKey = false;
	private static class Option {
		
		int value;
		int titleId;
		String title;
		
		boolean skip = false;
		
		public Option(int value) {
			this.value = value;
			titleId = value;
		}
		
		public Option(int value, String title) {
			this.value = value;
			this.title = title;
		}
		
		public Option(int value, int title) {
			this.value = value;
			titleId = title;
		}

		public String getTitle() {
			return title;
		}
		
		public String getTitle(Context context) {
			if (title != null) {
				return title.toString();
			}
			return context.getResources().getString(titleId);
		}
		
		public Option setSkipped(boolean skipped) {
			skip = skipped;
			return this;
		}
	}
	
	// Data and status
	private final List<Option> options;
	private int selection = -1;

	private ImageView mImageView, mDivider_below;
	public AutoRunTextView mTextValue;
	private TextView mTitle;
	private boolean needPopWindow = true; // defalut is true
	private ViewGroup container;
	private static PopView popView;
	
	public OptionMenuItem(int title) {
		super(title, ITEM_TYPE_OPTION);
		options = new ArrayList<Option>();
	}
	
	/**
	 * Add an option to the spinner.
	 * Note that the value of each item is set to its title resource id.
	 * @param title The string resource id for the option title.
	 * @return
	 */
	public OptionMenuItem addOption(int title) {
		options.add(new Option(title));
		if (selection < 0) {
			selection = 0;
		}
		return this;
	}
	
	/**
	 * Add an option to the spinner.
	 * @param title The string resource id for the option title.
	 * @param value The corresponding value of this option.
	 * @return
	 */
	public OptionMenuItem addOption(int title, int value) {
		return addOption(title, value, false);
	}
	
	public OptionMenuItem addOption(int title, int value, boolean skipped) {
		options.add(new Option(value, title).setSkipped(skipped));
		if (selection < 0) {
			selection = 0;
		}
		return this;
	}
	
	public OptionMenuItem addOption(String title, int value) {
		options.add(new Option(value, title));
		if (selection < 0) {
			selection = 0;
		}
		return this;
	}
	
	public OptionMenuItem setOptionsByArray(String[] languages, int[] values, 
			OnItemClickListener clickListener) {
		if (languages.length != values.length) {
			throw new IllegalArgumentException();
		}
		
		for (int i = 0; i < languages.length; i++) {
			options.add(new Option(values[i], languages[i]));
		}
		if (selection < 0) {
			selection = 0;
		}
		
		initListView(languages, selection, clickListener);
		return this;
	}
	private static boolean listPopShow = false;
	private String[] mPopListTitle;
	private OnItemClickListener popItemClickListener = null;
	private void initListView(final String[] list, int position,
			OnItemClickListener clickListener) {
		
		mPopListTitle = list;
		this.selection = position;
		popItemClickListener = clickListener;
		if (popItemClickListener == null) {
			popItemClickListener = new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					setSelectedItemPosition(position);
					if (list.length > position && position > 0) {
						setTextValue(mPopListTitle[position]);
						popView.dismiss();
					}
				}
			};
		}
	}
	
	public OptionMenuItem setCurrentValue(int value) {
		return setCurrentValue(value, false);
	}
	
	public OptionMenuItem setCurrentValue(int value, boolean notify) {
		for (int i = 0; i < options.size(); i++) {
			Option o = options.get(i);
			if (o.value == value) {
				selection = i;
				if (notify) {
					notifyValueChange(value);
				}
				break;
			}
		}
		updateContent();
		return this;
	}
	
	public int getCurrentValue() {
		return options.get(selection).value;
	}
	
	public String[] getPopListTitle() {
		return mPopListTitle;
	}

	public int getSelection() {
		return selection;
	}

	public void setSelection(int position) {
		this.selection = position;
	}

	/**设置值的索引*/
	protected void setSelectedItemPosition(int position2) {
		
	}
	
	private void updateContent() {
		if (mTextValue == null) {
			if(container == null)
				return;
			mTextValue = (AutoRunTextView) container.findViewById(R.id.textvalue);
		}
		LogUtil.logD(TAG, "Start updateContent mTextValue=" + mTextValue, false);
		String value = options.get(selection).getTitle(mTextValue.getContext());
		if (selection >= 0 && selection < options.size()) {
			LogUtil.logD(TAG, "update selection="+ selection +" value=" + value, false);
			mTextValue.setText(value);
		} else {
			mTextValue.setText("");
		}

	}
	public void updateContent(String txt) {
		if (mTextValue == null) {
			mTextValue = (AutoRunTextView) container.findViewById(R.id.textvalue);
		}
		mTextValue.setText(txt);
	}
	
	@Override
	protected void onBindView(LayoutInflater inflater, ViewGroup container, boolean inflate) {
		LogUtil.logD(TAG, "Start onBindView", false);
		if (inflate) {
			inflater.inflate(R.layout.layout_item_option_menu, container);
		}
		this.container = container;
		mImageView = (ImageView) container.findViewById(R.id.imageview);
		mDivider_below = (ImageView) container.findViewById(R.id.divider_below);
		mTextValue = (AutoRunTextView) container.findViewById(R.id.textvalue);
		LogUtil.logD(TAG, "onBindView mTextValue=" + mTextValue, false);
		mTitle = (TextView) container.findViewById(R.id.textview);
		mTitle.setText(title);

		needPopWindow(needPopWindow);
		this.container.setOnFocusChangeListener(this);
	}
	

	
	public void updateEnable(boolean enable) {
		// TODO Auto-generated method stub
		if(!enable){
			if(mTextValue!=null){
				mTextValue.setEnabled(false);
				mTextValue.setFocusable(false);
			}
			if(mTitle!=null){
				mTitle.setEnabled(false);
				mTitle.setFocusable(false);
			}		
		}else {
			if(mTextValue!=null){
				mTextValue.setEnabled(true);
				mTextValue.setFocusable(true);
			}
			if(mTitle!=null){
				mTitle.setEnabled(true);
				mTitle.setFocusable(true);
			}		
		}
	}

	@Override
	protected void onUnbindView() {
		mImageView = null;
		mDivider_below = null;
		mTextValue = null;
		mTitle = null;
	}
	public void needPopWindow(boolean needPopWindow) {
		this.needPopWindow = needPopWindow;
	}
	public OptionMenuItem needRightArraw(boolean needArraw) {
		if (!needArraw) {
			mImageView.setVisibility(View.INVISIBLE);
			this.container.setClickable(false);
			this.container.setFocusable(false);
		}
		return this;
	}
	public OptionMenuItem noRightArrawWithClickable() {
		mImageView.setVisibility(View.GONE);
		this.container.setClickable(true);
		this.container.setFocusable(true);
		return this;
	}
	public OptionMenuItem needBottomLine(boolean needLine) {
		if (!needLine) {
			mDivider_below.setVisibility(View.GONE);
		}
		return this;
	}
	public void needTextMarquee(boolean needMarquee) {
		mTextValue.setMarquee(needMarquee);
	}
	public void onFocusChange(View arg0, boolean focus) {
		if (!focus && needPopWindow) {
			container.setBackgroundResource(R.drawable.sub_menu_selector);
		}  
	}
	public PopView getPopView() {
		return popView;
	}
	
	public static boolean ExsitPopView() {
		try {
			if(popView!=null){
				popView.dismiss();
				return listPopShow;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return false;		
	}	
	
	private void showPopView() {
		listPopShow = true;
		View view = getItemView();
		
		LogUtil.logD(TAG, "popview parent view=" + view, true);
		popView = new ListPopView(getItemView().getContext(), view, mPopListTitle, selection);
		((ListPopView) popView).setOnItemClickListener(popItemClickListener);
			
		popView.setOnPopViewDismissListener(this);
		popView.show();
	}
//	@Override
//	protected void onDetachedFromWindow() {
//		super.onDetachedFromWindow();
//		if (needPopWindow && popView != null && popView.isShowing()) {
//			popView.dismiss();
//			popView = null;
//		}
//	}
//	public boolean onKeyDown(int keycode, KeyEvent arg1) {
//		if (needPopWindow && !options.isEmpty()) {
//			showPopView();
//		}
//		else if(mRightKeyEventEqualsOnClick){
//			performClick();
//		}
//		return true;
//	}

	public String getSelectedItem() {
		/*if (popView != null && show == LISTSHOW) {
			position = popView.getSelectedPosition(); // sync textvalue&popView
			if (list.length > position) {
				return list[position];
			}
		}*/
		return null;
	}
	public void setTextValue(String text) {
		if (mTextValue != null) {
			mTextValue.setText(text);
		}
	}
	public void updateValue(int value) {
		if (mTextValue == null){
			return;
		}
	}
//	@Override
//	public void onClick(View arg0) {
//		if (needPopWindow) {
//			showPopView();
//		}
//	}
	@Override
	public void OnPopViewDismiss() {
/*		setBackgroundResource(R.drawable.sub_menu_selector);
		LayoutItem.this.requestFocus();
		if (popView != null && show == LISTSHOW) {
			position = popView.getSelectedPosition(textValue.getText()
					.toString());
		} else {
			position = popView.getSelectedPosition();
		}*/
	}
	/**
	 * 设置为true的话，右方向按钮事件将等同于确认按钮事件
	 * @param b
	 */
	public void setRightKeyEventEqualsOnClick(boolean b){
		mRightKeyEventEqualsOnClick=b;
	}
	@Override
	public void selectNext() {
		LogUtil.logD("Start to Show PopView", true);
		if (needPopWindow && !options.isEmpty()) {
			showPopView();
		}
		else if(mRightKeyEventEqualsOnClick){
			performClick();
		}
		
	}
	@Override
	public void selectPrev() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void showView() {
		if(mClickEqualsRightKey) {
			if (needPopWindow && !options.isEmpty()) {
				showPopView();
			}
		}
	}
	
	public void setAutoTextViewGravity(int direction) {
		if(direction == 0)
			mTextValue.setGravity(Gravity.LEFT);
		else
			mTextValue.setGravity(Gravity.RIGHT);
	}
}
