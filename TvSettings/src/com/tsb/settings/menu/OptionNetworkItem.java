package com.tsb.settings.menu;

import com.tsb.settings.R;
import com.tsb.settings.fragment.menu.net.NetworkFragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

public class OptionNetworkItem extends MenuItem {
	private Activity mContext;
	public OptionNetworkItem(Activity context,int title) {
		super(title, 1);
		mContext=context;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void selectNext() {
		// TODO Auto-generated method stub
		Intent it = new Intent();
		it.setClassName("com.tsb.tv", "com.tsb.tv.fragment.menu.net.NetworkActivity");
		mContext.startActivity(it);
		getItemView().setBackgroundResource(R.drawable.menu_second_focus);
	}

	@Override
	public void selectPrev() {
		// TODO Auto-generated method stub
		
	}

	private TextView mTitleView;
	@Override
	protected void onBindView(LayoutInflater inflater, ViewGroup container,
			boolean inflate) {
		// TODO Auto-generated method stub
		if (inflate) {
			inflater.inflate(R.layout.layout_item_option_menu, container);
		}
		mTitleView = (TextView) container.findViewById(R.id.textview);
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
