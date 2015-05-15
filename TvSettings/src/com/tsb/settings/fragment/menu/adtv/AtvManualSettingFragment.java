package com.tsb.settings.fragment.menu.adtv;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.app.TvManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsb.settings.R;
import com.tsb.settings.TvManagerHelper;
import com.tsb.settings.TvManagerHelper.ChannelInformation;
import com.tsb.settings.broadcast.TvBroadcastDefs;
import com.tsb.settings.fragment.menu.systemSetting.OSDLanguage;
import com.tsb.settings.util.FragmentUtils;
import com.tsb.settings.util.LogUtil;

public class AtvManualSettingFragment extends Fragment {
	
	public static final String ARG_INDEX = "channel_index";
	public static Context ctx;
	public static LinearLayout layoutFineTune;
	private static int curFreq = 0;
	private static boolean focus = false;
	private static final int CHINESE = 0;
	private static final int ENGLISH = 1;	
	private static int curPos = 0;
	
	private abstract static class Item implements View.OnKeyListener, OnFocusChangeListener {
		
		public final int id;
		public final int hint;
		
		private final List<String> options = new ArrayList<String>();
		
		private int position = -1;
		
		private View bindView;
		private boolean mFocused = false;
		
		protected Context mContext;
		protected Configuration mConfigurations;
		protected ChannelInformation mChanneoInfo;
		
		Item(int id, int hint) {
			this.id = id;
			this.hint = hint;
		}
		
		abstract protected void onSetupOptions(Context context, List<String> options);
		abstract protected void onUpdateView(View view);
		abstract public int valueAt(int position);
		
		public void init(Context context, Configuration configurations) {
			mContext = context.getApplicationContext();
			mConfigurations = configurations;
			onSetupOptions(context, options);
			
			if (options.isEmpty()) {
				position = -1;
			} else if (position < 0 || position >= options.size()) {
				position = 0;
			}
		}
		
		public void bindView(View view) {
			bindView = view;
			view.setTag(this);
			view.setOnKeyListener(this);
			view.setOnFocusChangeListener(this);
			onFocusChange(view, view.isFocused());
		}
		
		public void unbindView() {
			bindView = null;
		}
		
		public int getPosition() {
			return position;
		}
		
		public boolean setPosition(int position, boolean notify) {
			if (position < 0 || position >= options.size()) {
				return false;
			}
			
			this.position = position;
			if (notify) {
				onValueChanged(position);
			}
			return true;
		}
		
		public boolean nextValue(int id, boolean notify) {
			if (options.isEmpty()) {
				position = -1;
				return false;
			}
			
			position++;
			if(id == R.id.option_fine_tune)
			{
				if(position >= options.size())
					position = options.size() - 1;
				else
					TvManagerHelper.getInstance(mContext)
						.setCurrentAtvFrequencyOffset(50000, true);
			}
			else
			{
				if (position >= options.size()) {
					position = 0;
				}
			}
			if (notify) {
				onValueChanged(position);
			}
			return true;
		}
		
		public boolean prevValue(int id, boolean notify) {
			if (options.isEmpty()) {
				position = -1;
				return false;
			}
			
			position--;			
			if(id == R.id.option_fine_tune)
			{
				if(position < 0)
					position = 0;
				else
					TvManagerHelper.getInstance(mContext)
						.setCurrentAtvFrequencyOffset(-50000, true);
			}
			else
			{
				if (position < 0) {
					position = options.size() - 1;
				}
			}
			if (notify) {
				onValueChanged(position);
			}
			return true;
		}
		
		public void updateValue(ChannelInformation channel) {
			mChanneoInfo = channel;
			onUpdateValue(channel);
			updateView();
		}
		
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				FragmentUtils.mHandler.removeMessages(0);
				FragmentUtils.mHandler.sendEmptyMessageDelayed(0, 15000);
				switch(keyCode) {
				case KeyEvent.KEYCODE_DPAD_RIGHT:
					return nextValue(v.getId(), true);
				case KeyEvent.KEYCODE_DPAD_LEFT:
					return prevValue(v.getId(), true);
				case KeyEvent.KEYCODE_ENTER:
				case KeyEvent.KEYCODE_DPAD_CENTER:
					return true;
				default:
					break;
				}
			}
			return false;
		}

		@Override
		public String toString() {
			if (position >= 0) {
				return options.get(position);
			}
			return "-";
		}
		
		public final void updateView() {
			if (bindView != null) {
				onUpdateView(bindView);
			}
		}
		
		protected void onValueChanged(int position) {
			updateView();
		}

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			mFocused = hasFocus;
			TvManagerHelper tm = TvManagerHelper.getInstance(ctx);
			if(v.getId() == R.id.option_fine_tune)
			{
				if(true == hasFocus)
				{					
//					curFreq = tm.getCurFrequency();
					focus = true;
				}
				else
				{
					setPosition(30, false);
					focus = false;
				}
			}
		}

		protected void onUpdateValue(ChannelInformation channel) {
			
		}
		
		protected static int indexOf(int[] array, int value) {
			for (int i = 0; i < array.length; i++) {
				if (array[i] == value) {
					return i;
				}
			}
			return -1;
		}

		public int getValue() {
			return valueAt(position);
		}
	}
	
	private static abstract class TextItem extends Item {

		TextItem(int id, int hint) {
			super(id, hint);
		}

		@Override
		protected void onUpdateView(View view) {
			TextView tv = (TextView) ((ViewGroup) view).getChildAt(2);
			if(view.getId() != R.id.option_fine_tune)/*频点单独处理*/
			{
				tv.setText(toString());
			}
		}
		
	}
	
	// ----------------- Items ----------------------------------------------------------
	private static class ItemPos extends TextItem {

		ItemPos() {
			super(R.id.option_position, R.string.tcl_chan_set_cur_chan);
		}

		@Override
		protected void onSetupOptions(Context context, List<String> options) {
			TvManagerHelper tm = TvManagerHelper.getInstance(mContext);
			int count = tm.getAtvScanEntryCount();
			for (int i = 1; i < count+1; i++) {
				options.add(String.valueOf(i));
			}
		}

		@Override
		public int valueAt(int position) {
			return position;
		}

		@Override
		protected void onUpdateValue(ChannelInformation channel) {
			setPosition(channel.channelIndex, false);
		}

		@Override
		protected void onValueChanged(int position) {
			super.onValueChanged(position);
			TvManagerHelper.getInstance(mContext).setCurrentChannelByIndex(position+1, null, null);
			updateItems();
		}

		private void updateItems() {
			// TODO Auto-generated method stub
			new Thread(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					mInitHandler.sendEmptyMessageDelayed(REFRESH_ITEM, 50);
				}
				
			}.start();
		}
		public static final int REFRESH_ITEM = 1;
		Handler mInitHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch(msg.what){
				case REFRESH_ITEM:
					TextView tv = (TextView) layoutFineTune.getChildAt(2);
					float freq = TvManagerHelper.getInstance(ctx).getCurFrequency()/10000/(float)100.00;
					//tv.setText(String.valueOf(freq));
					tv.setText(String.format("%.2f", freq));
					break;
				default:
					break;
				}
			}
			
		};
	}
	
	private static class ItemSoundSystem extends TextItem {

		private static final int[] VALUES = {
			TvManager.RT_ATV_SOUND_SYSTEM_DK,
			TvManager.RT_ATV_SOUND_SYSTEM_I,
			TvManager.RT_ATV_SOUND_SYSTEM_BG,
			TvManager.RT_ATV_SOUND_SYSTEM_MN//TODO: RT_ATV_SOUND_SYSTEM_M?
		};
		
		ItemSoundSystem() {
			super(R.id.option_sound_system, R.string.STRING_SOUND_SYSTEM);
		}

		@Override
		protected void onSetupOptions(Context context, List<String> options) {
			String[] opt = context.getResources().getStringArray(R.array.manual_tuning_system);
			for (int i = 0; i < opt.length; i++) {
				options.add(opt[i]);
			}
		}

		@Override
		public int valueAt(int position) {
			return VALUES[position];
		}

		@Override
		protected void onUpdateValue(ChannelInformation channel) {
			super.onUpdateValue(channel);
			int value = TvManagerHelper.getInstance(mContext).getCurrentAtvSoundStd();
			setPosition(indexOf(VALUES, value), false);
		}

		@Override
		protected void onValueChanged(int position) {
			super.onValueChanged(position);
			int value = VALUES[position];
			TvManagerHelper.getInstance(mContext).setCurrentAtvSoundStd(value);
		}
		
	}
	
	private static class ItemColor extends TextItem {

		private static final int[] VALUES = {
			TvManager.RT_COLOR_STD_AUTO,
			TvManager.RT_COLOR_STD_PAL_I,
//			TvManager.RT_COLOR_STD_NTSC_443,
			TvManager.RT_COLOR_STD_NTSC
		};
		
		String[] hints;
		
		ItemColor() {
			super(R.id.option_color, R.string.STRING_COLOR_SYSTEM);
		}

		@Override
		protected void onSetupOptions(Context context, List<String> options) {
			hints = context.getResources().getStringArray(R.array.manual_tuning_color_system_full_ext);
			String[] opt = context.getResources().getStringArray(R.array.manual_tuning_color_system_full_ext);
			for (int i = 0; i < opt.length; i++) {
				options.add(opt[i]);
			}
		}

		@Override
		public int valueAt(int position) {
			return VALUES[position];
		}

		@Override
		protected void onValueChanged(int position) {
			super.onValueChanged(position);
			TvManagerHelper tm = TvManagerHelper.getInstance(mContext);
			tm.setCurrentAtvColorStd(VALUES[position]);
		}
		
		@Override
		protected void onUpdateValue(ChannelInformation channel) {
			super.onUpdateValue(channel);
			int value = TvManagerHelper.getInstance(mContext).getCurrentAtvColorStd();
			setPosition(indexOf(VALUES, value), false);
		}
		
	}
	
	private static class ItemFineTune extends TextItem {

		private static final int MAX = 30;
		
		
		ItemFineTune() {
			super(R.id.option_fine_tune, R.string.STRING_MANUAL_FINE_TUNING);
		}
		

		@Override
		protected void onSetupOptions(Context context, List<String> options) {
			for (int i = -MAX; i <= MAX; i++ ) {
				options.add(String.valueOf(i));
			}
			setPosition(MAX + 1, false);
			curPos = 31;
		}

		@Override
		public int valueAt(int position) {
			return position - MAX;
		}
		
		
		@Override
		protected void onValueChanged(int position) {
			super.onValueChanged(position);
			TvManagerHelper tm = TvManagerHelper.getInstance(mContext);	

			
			float freq = 0;

			TextView tv = (TextView) layoutFineTune.getChildAt(2);	
			/*打印延时*/
			Log.v("AtvManualSettingFragment", "cur_freq1"+ tm.getCurFrequency() +
					"  offset1"+getValue() + " pos1"+position );	
			Log.v("AtvManualSettingFragment", "cur_freq2"+ tm.getCurFrequency() +
					"  offset2"+getValue() + " pos2"+position );	
			freq = tm.getCurFrequency() /10000/(float)100.00;	
			tv.setText(String.format("%.2f", freq));
			//tv.setText(String.valueOf(freq));
		}

		@Override
		protected void onUpdateValue(ChannelInformation channel) {
			super.onUpdateValue(channel);
			
			TvManagerHelper tm = TvManagerHelper.getInstance(ctx);
			TextView tv = (TextView) layoutFineTune.getChildAt(2);
			float freq = tm.getCurFrequency()/10000/(float)100.00;
			tv.setText(String.format("%.2f", freq));		
			//tv.setText(String.valueOf(tm.getCurFrequency()/10000/(float)100.00));
			//TvManagerHelper tvHelper = TvManagerHelper.getInstance(mContext);
			//int offset = tvHelper.getCurrentAtvFrequencyOffset();
			//setPosition(offset + MAX, false);
		}
	}
	
	// -----------------------------------------------------------------------------------
	private static class Configuration {
		
		private final Item[] items;
		Configuration() {
			items = new Item[]{
				new ItemSoundSystem(),
				new ItemPos(),
				new ItemColor(),
				new ItemFineTune(),
			};
		}
		
		void init(Context context) {
			for (Item i : items) {
				i.init(context, this);
			}
		}
		
		void bindView(View root) {
			for (Item i : items) {
				View v = root.findViewById(i.id);
				i.bindView(v);
			}
		}
		
		void unbindView() {
			for (Item i : items) {
				i.unbindView();
			}
		}

		void updateValue(ChannelInformation channel) {
			for (Item i : items) {
				i.updateValue(channel);
			}
		}
	}
	
	private String TAG = "AtvManualSettingFragment";
	// -------------------------------------------------------------------------------------
	private Configuration mConfigs = new Configuration();
	
	private TvManagerHelper mTvHelper;
	private BroadcastReceiver mBroadcastReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mTvHelper = TvManagerHelper.getInstance(getActivity());
		ctx = getActivity();
		mConfigs.init(getActivity());
		
		// TODO: Intent to update content on channel changed.
		// Not sure if this is the correct intent action and message. Jason.
		mBroadcastReceiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				final String action = intent.getAction();
				if (TvBroadcastDefs.ACTION_TV_DISPLAY_READY.equals(action) ||
					TvBroadcastDefs.ACTION_TV_NO_SIGNAL.equals(action)) {
					LogUtil.logD(TAG, "ACTION_TV_DISPLAY_READY or ACTION_TV_NO_SIGNAL,action=" + action, true);
					// on channel changed
					ChannelInformation channel = mTvHelper.getCurrentChannelInformation();
					mConfigs.updateValue(channel);
					
				} else if (TvBroadcastDefs.ACTION_TV_MEDIA_MESSAGE.equals(action)) {
					// on receive tv message
					int msg = intent.getIntExtra(TvBroadcastDefs.EXTRA_TV_MEDIA_MESSAGE, -1);
					LogUtil.logD(TAG, "ACTION_TV_MEDIA_MESSAGE,message=" + msg, true);
					if(msg == TvManager.RT_MSG_SLR_SCAN_SEEK_COMPLETE || 
						msg == TvManager.RT_MSG_SLR_SCAN_MANUAL_COMPLETE) {
						// Scan complete
						ChannelInformation channel = mTvHelper.getCurrentChannelInformation();
						mConfigs.updateValue(channel);
					}
				}
			}
		};
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(TvBroadcastDefs.ACTION_TV_DISPLAY_READY);
		intentFilter.addAction(TvBroadcastDefs.ACTION_TV_NO_SIGNAL);
		intentFilter.addAction(TvBroadcastDefs.ACTION_TV_MEDIA_MESSAGE);
		getActivity().registerReceiver(mBroadcastReceiver,  intentFilter);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(mBroadcastReceiver);
		
        /*显示无信号提示框*/	
		/*TvManagerHelper tm = TvManagerHelper.getInstance(getActivity());
	    boolean hasSignal = tm.isSignalDisplayReady();
		if (!hasSignal) {
			FragmentManager fm = getFragmentManager();
			Fragment f = fm.findFragmentByTag("no_signal");
			if (f == null) {
				f = new NoSignalFragment();
				fm.beginTransaction()
						.add(R.id.container_background, f, "no_signal")
						.commit();
			}
		}*/		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_fragment_atv_manual_setting, container, false);
		mConfigs.bindView(view);
		layoutFineTune = (LinearLayout)view.findViewById(R.id.option_fine_tune);
		
		// Get arguments
		Bundle args = getArguments();
		int channelIdx;
		if (args != null) {
			channelIdx = args.getInt(ARG_INDEX, 0);
		} else {
			channelIdx = mTvHelper.getCurrentChannelIndex();
		}
		mConfigs.updateValue(mTvHelper.getChannelAt(channelIdx));
		
		TextView tv = (TextView) layoutFineTune.getChildAt(2);
		float freq = TvManagerHelper.getInstance(ctx).getCurFrequency()/10000/(float)100.00;
		tv.setText(String.format("%.2f", freq));
		//tv.setText(String.valueOf(freq));
		
		TextView text[] = new TextView[4];
		text[0] = (TextView) view.findViewById(R.id.cur_chan);
		text[1] = (TextView) view.findViewById(R.id.sound_system);
		text[2] = (TextView) view.findViewById(R.id.color_system);
		text[3] = (TextView) view.findViewById(R.id.frequency);
		
		for(int i=0; i < 4; i++)
		{			
			if(ENGLISH == OSDLanguage.getCurOsdLanguage(getActivity()))
			{
				text[i].setTextSize(12);
			}
			else
			{
				text[i].setTextSize(16);
			}
		}

		return view;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mConfigs.unbindView();
	}

}
