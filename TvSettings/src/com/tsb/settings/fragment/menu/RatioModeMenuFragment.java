package com.tsb.settings.fragment.menu;

import java.util.List;

import android.app.TvManager;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.tsb.settings.R;
import com.tsb.settings.TvManagerHelper;
import com.tsb.settings.menu.MenuItem;
import com.tsb.settings.menu.OptionMenuItem;

public class RatioModeMenuFragment extends BaseMenuFragment{
//	private OptionMenuItem mRatioModeItem;
	private String[] mRatioMode;
	private final int[] VALUE_PIC_RATIO = {
			TvManager.SCALER_RATIO_16_9,//16：9
			TvManager.SCALER_RATIO_4_3, //4：3
			TvManager.SCALER_RATIO_BBY_AUTO,//自动	
	};
	private TvManagerHelper tm;
	
	@Override
	public void onCreateMenuItems(List<MenuItem> items) {
		tm = TvManagerHelper.getInstance(getActivity());
		mRatioMode = getResources().getStringArray(R.array.pic_ratio);
//		mRatioModeItem = MenuItem.createOptionItem(R.string.tcl_scale_model);
//		mRatioModeItem.mClickEqualsRightKey = true;
//		items.add(mRatioModeItem);
		initSoundParameters();
	}
	
	public void initSoundParameters() {
		new Thread() {
			@Override
			public void run() {
				super.run();
				mHandler.sendEmptyMessageDelayed(REFRESH_ITEM, 0);
			}
		}.start();
	}
	public static final int REFRESH_ITEM = 1;
	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REFRESH_ITEM:
				if (!isVisible()) {
					return;
				}
//				mRatioModeItem.setOptionsByArray(mRatioMode, VALUE_PIC_RATIO, ratioListener);
				
//				mRatioModeItem.setCurrentValue(tm.mTvManager.getAspectRatio(/*tm.getInputSource()*/));
				break;

			default:
				break;
			}
		};
	};
	private OnItemClickListener ratioListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			tm.mTvManager.setAspectRatio(VALUE_PIC_RATIO[position]);
//			mRatioModeItem.setCurrentValue(VALUE_PIC_RATIO[position]);
//			mRatioModeItem.getPopView().dismiss();
		}
	};

	@Override
	public int getTitle() {
		return R.string.tcl_scale_setting;
	}

}
