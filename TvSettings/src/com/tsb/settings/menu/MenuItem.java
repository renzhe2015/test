package com.tsb.settings.menu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.tsb.settings.R;
import com.tsb.settings.util.LogUtil;

abstract public class MenuItem{
	private static final String TAG = "MenuItem";
	
	public interface OnValueChangeListener{
		public void onValueChanged(MenuItem item, int value);
	}
	
	public static final int ITEM_TYPE_TEXT_ONLY = 0;
	public static final int ITEM_TYPE_OPTION = 1;
	public static final int ITEM_TYPE_SPINNER = 2;
	public static final int ITEM_TYPE_SEEK_BAR = 3;
	
	// Basic properties
	public final int title;
	public final int type;
	// Callbacks
	private OnClickListener mClickListener;
	private OnValueChangeListener mValueChangeListener;
	
	// Data and status
	private boolean enable;
	
	private View mView;
	public static TextMenuItem createTextItem(int title) {
		return new TextMenuItem(title, ITEM_TYPE_TEXT_ONLY);
	}
	
	public static SpinnerMenuItem createSpinnerItem(int title) {
		return new SpinnerMenuItem(title);
	}
	
	public static SpinnerMenuItemExt createSpinnerItemExt(int title) {
		return new SpinnerMenuItemExt(title);
	}
	
	public static SeekBarMenuItem createSeekBarItem(int title) {
		return new SeekBarMenuItem(title);
	}
	
	public static OptionMenuItem createOptionItem(int title){
		return new OptionMenuItem(title);
	}
	
	
	
	protected MenuItem(int title, int type) {
		this.title = title;
		this.type = type;
		this.enable = true;
	}
	
	public MenuItem setEnable(boolean enable) {
		this.enable = enable;
		return this;
	}
	
	public MenuItem setOnClickListener(View.OnClickListener listener) {
		mClickListener = listener;
		return this;
	}
	
	public MenuItem setOnValueChangeListener(OnValueChangeListener listener) {
		mValueChangeListener = listener;
		return this;
	}
	
	public boolean isEnable() {
		return enable;
	}
	
	abstract public void selectNext();

	abstract public void selectPrev();
	
	abstract public void showView();
	
	public void performClick() {
		if (mClickListener != null) {
			mClickListener.onClick(mView);
		}
	}
	
	public View getItemView() {
		return mView;
	}
	
	public void setItemView(View v){
		mView = v;
	}
	
	protected void notifyValueChange(int value) {
		if (mValueChangeListener != null) {
			mValueChangeListener.onValueChanged(this, value);
		}
	}
	
	public View getView(View convertView, ViewGroup parent) {
		// Unbind previous view
//		onUnbindView();
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		ViewGroup container = null;
		LogUtil.logD(TAG, "-->getView,MenuItem convertView=" + convertView + ";mView=" + mView, true);
			//1
		if (mView == null) {
			mView = inflater.inflate(R.layout.layout_item_setting, parent, false);
			container = (ViewGroup) mView.findViewById(R.id.container_value_control);
			mView.setTag(this);
			LogUtil.logD(TAG, "1<--getView,MenuItem convertView=" + convertView + ";mView=" + mView, true);
		} else {//4
			LogUtil.logD(TAG, "4<--getView,MenuItem convertView=" + convertView + ";mView=" + mView, true);
			return mView;
		}
			//2
		container = (ViewGroup) mView.findViewById(R.id.container_value_control);
		onBindView(inflater, container, true);
		return mView;
	}
	
	abstract protected void onBindView(LayoutInflater inflater, ViewGroup container, boolean inflate);
	
	abstract protected void onUnbindView();
	
}
