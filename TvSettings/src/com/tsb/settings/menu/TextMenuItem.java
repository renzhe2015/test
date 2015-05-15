package com.tsb.settings.menu;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tsb.settings.R;

public class TextMenuItem extends MenuItem{

	protected TextMenuItem(int title, int type) {
		super(title, type);
	}

	@Override
	public void selectNext() {
		
	}

	@Override
	public void selectPrev() {
		
	}

	private TextView mTitleView;
	@Override
	protected void onBindView(LayoutInflater inflater, ViewGroup container,
			boolean inflate) {
		if (inflate) {
			inflater.inflate(R.layout.layout_item_setting_text, container);
		}
		
		mTitleView = (TextView) container.findViewById(R.id.text_control);
		mTitleView.setText(title);
	}

	@Override
	protected void onUnbindView() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showView() {
		// TODO Auto-generated method stub
		
	}

}
