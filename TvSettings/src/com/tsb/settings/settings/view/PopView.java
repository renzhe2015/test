package com.tsb.settings.settings.view;

import com.tsb.settings.R;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.PopupWindow;

public abstract class PopView extends PopupWindow {

	public Context mContext;
	public View parent;
	public OnPopViewDismissListener dismissListener;

	public int width, height, yoff;

	public PopView(Context mContext, View parent) {
		this.mContext = mContext;
		this.parent = parent;
		width = 180;
		setFocusable(true);
		setOutsideTouchable(true);
		setBackgroundDrawable(new BitmapDrawable());/*
													 * back key will be
													 * processed
													 */
		setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				if (dismissListener != null) {
					dismissListener.OnPopViewDismiss();
				}
			}
		});
	}

	public void initContentView(View layout) {
		setContentView(layout);
		setWidth(width);
		setHeight(height);
	}

	public void setOnPopViewDismissListener(
			OnPopViewDismissListener dismissListener) {
		this.dismissListener = dismissListener;
	}

	public void show() {
		showAsDropDown(parent, parent.getWidth() + 40, -parent.getHeight()
				- yoff);
		parent.setBackgroundResource(R.drawable.menu_second_focus);
	}
	
	public abstract int getSelectedPosition(String value);
	
	public abstract int getSelectedPosition();

	public interface OnPopViewDismissListener {
		public void OnPopViewDismiss();
	}
	
	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		super.dismiss();
        parent.setBackgroundResource(R.drawable.sub_menu_selector);
	}
}
