package com.tsb.settings.menu;

import com.tsb.settings.R;
import com.tsb.settings.util.FragmentUtils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

public class OptionChildrenItem extends MenuItem{

	private Activity mContext;
	public OptionChildrenItem(Activity context,int title) {
		super(title, 0);
		mContext = context;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void selectNext() {
		// TODO Auto-generated method stub
//		FragmentUtils.showSubFragment(ChildModeMenuFragment.getBaseFragment(), CodeModify.class);
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
