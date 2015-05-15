package com.tsb.settings.fragment.menu;


import java.util.List;

import android.app.TvManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.tsb.settings.R;
import com.tsb.settings.TvManagerHelper;
import com.tsb.settings.broadcast.TvBroadcastDefs;
import com.tsb.settings.fragment.menu.adtv.AtvManualSettingFragment;
import com.tsb.settings.fragment.menu.adtv.AtvManualTuningFragment;
import com.tsb.settings.fragment.menu.adtv.AutoTuningFragment;
import com.tsb.settings.fragment.menu.adtv.ConfirmDialogFragment;
import com.tsb.settings.fragment.menu.adtv.DtvManualTuningFragment;
import com.tsb.settings.menu.DeenListView;
import com.tsb.settings.menu.MenuItem;
import com.tsb.settings.menu.OptionMenuItem;
import com.tsb.settings.util.FragmentUtils;

public class ChannelSearchMenuFragment extends SecondLevelMenuFragment{	
	private OptionMenuItem mSourceInPut;
	private OptionMenuItem mAutoSearch;
	private OptionMenuItem mManualSearch;
	private OptionMenuItem mChanSetting;
	private static final int ITEM_SOURCE = 0;
	private static final int ITEM_AUTO_SEARCH = 1;
	private static final int ITEM_MANUAL_SEARCH = 2;
	private static final int ITEM_SEARCH_SETTING = 3;
	
	private static final int[] VALUE_SOURCE_MODE = {
		TvManager.SOURCE_ATV1, //模拟信号
		TvManager.SOURCE_DTV1  //数字信号
	};
	
	
	private static TvManagerHelper tm;
	private static SharedPreferences sharedPref;
	//**********add by huangfh@tcl.com 09-03
	private OptionMenuItem  subtitle;
	private int[] mSubtitle;
	private int curTitle=-1;
	private String[] mSubtitleItems;
	private int sourceID;
//	private OptionMenuItem  voicelanguage;
//	private int[] mVoicelangs;
//	private int curLang=0;
//	private String[] mVoiceLangItems;
	private TvManager tvmm;
	private BroadcastReceiver mBroadcastReceiver;
	private IntentFilter mIntentFilter;
	private List<MenuItem> mItems;
//	private boolean hasLanguage=false;
	private boolean hasSubtitle=false;
	private boolean firstInit=true;
	private int mPosition=-1;
	private boolean keyDelay=true;
	//*******end
	@Override
	public void onCreateMenuItems(List<MenuItem> items) {
		mItems=items;
		tm = TvManagerHelper.getInstance(getActivity());
		sourceID=tm.getInputSource();
		if(sourceID==1) mPosition=0;
		else if(sourceID==3) mPosition=1;
		// source Input 信源输入
		mSourceInPut = MenuItem.createOptionItem(R.string.tcl_source_input);
		mSourceInPut.mClickEqualsRightKey = true;
//		mSourceInPut.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				FragmentUtils.showSubFragment(getBaseFragment(), SourceSelectorFragment.class);
//			}
//		});	
		items.add(mSourceInPut);		
		
		// source Input 自动搜索
		mAutoSearch = MenuItem.createOptionItem(R.string.tcl_auto_search);
		mAutoSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int strId = 0;				
				if (tm.getInputSource() == TvManager.SOURCE_DTV1)
					strId = R.string.STRING_AUTO_TUNING_DTV;
				else if(tm.getInputSource() == TvManager.SOURCE_ATV1)
					strId = R.string.STRING_AUTO_TUNING_ATV;				

				ConfirmDialogFragment.show(getBaseFragment(),
						AutoTuningFragment.class, null, strId, R.string.STRING_QUICKSETUP_INFO);
			}
		});
		mAutoSearch.setRightKeyEventEqualsOnClick(true);
		items.add(mAutoSearch);
		
		// Manual Search 手动搜索
		mManualSearch = MenuItem.createOptionItem(R.string.tcl_manual_search);
		mManualSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {			
				if (tm.getInputSource() == TvManager.SOURCE_ATV1)
					FragmentUtils.showSubFragmentWithTag(getBaseFragment(), AtvManualTuningFragment.class, "atvManualSearch");
				else if(tm.getInputSource() == TvManager.SOURCE_DTV1)
					FragmentUtils.showSubFragmentWithTag(getBaseFragment(), DtvManualTuningFragment.class, "dtvManualSearch");				
			}
		});
		mManualSearch.setRightKeyEventEqualsOnClick(true);
		items.add(mManualSearch);
		
		// Atv Chan Setting 手动微调		
		mChanSetting = MenuItem.createOptionItem(R.string.tcl_chan_set);
		mChanSetting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {	
				if(0 != tm.getChannelCount())
					FragmentUtils.showSubFragmentWithTag(getBaseFragment(), AtvManualSettingFragment.class, "atvManualSetting");			
				else
				{
					Toast.makeText(getActivity(), R.string.tcl_atv_no_channel, Toast.LENGTH_SHORT).show();	
				}
			}
		});
		mChanSetting.setRightKeyEventEqualsOnClick(true);
		//********add by huangfh@tcl.com 09-19
		if(TvManagerHelper.isAtvSource(sourceID)&&tm.isSignalDisplayReady())
		items.add(mChanSetting);	
//		voicelanguage=MenuItem.createOptionItem(R.string.voice_language);
//		voicelanguage.mClickEqualsRightKey=true;
//		if(updateVoiceLanguage()){
//			items.add(voicelanguage);
//		}
		subtitle=MenuItem.createOptionItem(R.string.subtitle);
		subtitle.mClickEqualsRightKey=true;
		if(updateSubtitle()){
			items.add(subtitle);
		}		
//		updateVoiceLanguageAndSubtitle();
		//*******end
		initSoundParameters();		
	}
	//*******add by huangfh@tcl.com 09-03
		private boolean updateSubtitle(){
			if(tm.isSignalDisplayReady()&&TvManagerHelper.isDtvSource(TvManagerHelper.getInstance(getActivity()).getInputSource())){
				tvmm=tm.mTvManager;
				String Subtitles[]=tvmm.getDtvSubtitleIndexList().split("\n");
				curTitle=tvmm.getCurDtvSubtitleIndex();				
				int titleNum=tvmm.getDtvSubtitleIndexListCount();
				mSubtitle=new int[titleNum+1];
				mSubtitleItems=new String[titleNum+1];
				if(titleNum>0){
					for(int i=0;i<=titleNum;i++){
						mSubtitle[i]=i;
						if(i==0) mSubtitleItems[i]=getResources().getString(R.string.STRING_OFF);							
						else mSubtitleItems[i]=Subtitles[i-1];
					}				
					return true;
				}
			}
			return false;
		}
//		private boolean updateVoiceLanguage(){
//			if(tm.isSignalDisplayReady()&&TvManagerHelper.isDtvSource(TvManagerHelper.getInstance(getActivity()).getInputSource())){
//				tvmm=tm.mTvManager;
//				String languages[]=tvmm.getCurDtvSoundSelectList().split("\n");
//				String tempLanguages=tvmm.getCurrentAudioLang();
//				for(int j=0;j<languages.length;j++){
//					if(tempLanguages.equals(languages[j])){
//						curLang=j;
//						break;
//					}
//				}
//				int langNum=tvmm.getCurDtvSoundSelectCount();
//				mVoicelangs=new int[langNum];
//				mVoiceLangItems=new String[langNum];
//				if(langNum>0){
//					for(int i=0;i<langNum;i++){
//						mVoicelangs[i]=i;
//						mVoiceLangItems[i]=languages[i];
//					}				
//					return true;
//				}
//			}
//			return false;
//		}
	private void updateVoiceLanguageAndSubtitle(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
//				hasLanguage=false;
				hasSubtitle=false;
				if(tm.isSignalDisplayReady()&&TvManagerHelper.isDtvSource(TvManagerHelper.getInstance(getActivity()).getInputSource())){
					tvmm=tm.mTvManager;
//					String languages[]=tvmm.getCurDtvSoundSelectList().split("\n");
//					String tempLanguages=tvmm.getCurrentAudioLang();
//					for(int j=0;j<languages.length;j++){
//						if(tempLanguages.equals(languages[j])){
//							curLang=j;
//							break;
//						}
//					}
//					int langNum=tvmm.getCurDtvSoundSelectCount();
//					mVoicelangs=new int[langNum];
//					mVoiceLangItems=new String[langNum];
//					if(langNum>0){
//						for(int i=0;i<langNum;i++){
//							mVoicelangs[i]=i;
//							mVoiceLangItems[i]=languages[i];
//						}				
//						hasLanguage=true;
//					}
//					else{
//						hasLanguage=false;
//					}
					String Subtitles[]=tvmm.getDtvSubtitleIndexList().split("\n");
					curTitle=tvmm.getCurDtvSubtitleIndex();				
					int titleNum=tvmm.getDtvSubtitleIndexListCount();
					mSubtitle=new int[titleNum+1];
					mSubtitleItems=new String[titleNum+1];
					if(titleNum>0){
						for(int i=0;i<=titleNum;i++){
							mSubtitle[i]=i;
							if(i==0) mSubtitleItems[i]=getResources().getString(R.string.STRING_OFF);							
							else mSubtitleItems[i]=Subtitles[i-1];
						}
						hasSubtitle=true;						
					}
					else{
						hasSubtitle=false;
					}
				}
//				Log.d("hfh","haslanguage"+hasLanguage);
				Log.d("hfh","hassubtitle"+hasSubtitle);
				mHandler.sendEmptyMessageDelayed(1,0);
			}
		}).start();
	}
	private OnItemClickListener titleItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			if(mSubtitle.length>0){		
				if(position==0){
					tvmm.setSubtitleEnable(false);
					subtitle.setTextValue(mSubtitleItems[position]);
				}
				else{
					tvmm.setSubtitleEnable(true);
					tvmm.setDtvSubtitleByIndex(position-1);
					subtitle.setCurrentValue(position);
				}
				subtitle.getPopView().dismiss();
				subtitle.setSelection(position);
			}
		}
	};
//	private OnItemClickListener languageItemClickListener = new OnItemClickListener() {
//		@Override
//		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
//				long arg3) {
//			if(mVoicelangs.length>0){			
//				tvmm.setCurDtvSoundSelectByIndex(position);
//				voicelanguage.setCurrentValue(position);
//				voicelanguage.getPopView().dismiss();
//				voicelanguage.setSelection(position);
//			}
//		}
//	};
	//******end
	public void initSoundParameters() {
		new Thread() {
			@Override
			public void run() {
				mHandler.sendEmptyMessageDelayed(REFRESH_ITEM, SecondLevelMenuFragment.DATA_INIT_DELAY);
			}
		}.start();
	}
	
	OnItemClickListener sourceSelectClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1,
				int position, long arg3) {
			if (mSourceInPut.getPopListTitle().length > position && position >= 0) {
				mSourceInPut.setTextValue(mSourceInPut.getPopListTitle()[position]);
				mSourceInPut.getPopView().dismiss();
				mSourceInPut.setSelection(position);
				mPosition=position;
				firstInit=false;
				mLastSelection = 0;
				if(mPosition==1&&sourceID!=TvManager.SOURCE_DTV1)
				{	
					keyDelay=false;
					getActivity().sendBroadcast(new Intent("com.tcl.changesource"));
					//********add by hfh 09-19
					if(mItems.contains(mChanSetting)) {
						mChanSetting.setTextValue(" ");
						mItems.remove(mChanSetting);	
					}
				}
				if(mPosition==0&&sourceID!=TvManager.SOURCE_ATV1)
				{	
					keyDelay=false;
					getActivity().sendBroadcast(new Intent("com.tcl.changesource"));
					if(mItems.contains(subtitle)) { 
						subtitle.setTextValue(" ");
						mItems.remove(subtitle);
					}
//					if(mItems.contains(voicelanguage)) 
//					{
//						voicelanguage.setTextValue(" ");
//						mItems.remove(voicelanguage);
//					}
				}				
				tm.setInputSource(VALUE_SOURCE_MODE[position]);
			}
		}
	};	
			
	public static final int REFRESH_ITEM = 0;
	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REFRESH_ITEM:
				if (!isVisible()) {
					break;
				}
				
				/*鼠标不显示焦点问题*/
				int focusId = sharedPref.getInt("focusId", -1);
				if(focusId > 0)
				{
					mListView.requestFocusFromTouch();
					mListView.requestFocus();					
					mListView.setSelection(focusId);				
					mListView.setFocusableInTouchMode(true);
				}
				
				mSourceInPut.setOptionsByArray(getResources().getStringArray(R.array.tcl_select_adtv), VALUE_SOURCE_MODE
						, sourceSelectClickListener);				
//				mSourceInPut.setCurrentValue(VALUE_SOURCE_MODE[mSourceInPut.getSelection()]);
				mSourceInPut.needRightArraw(mSourceInPut.isEnable());
//				View tmpView = mListView.getChildAt(mListView.getCount() - 1);
				if(tm.getInputSource() == TvManager.SOURCE_DTV1)
				{
//					tmpView.setEnabled(false);				
					mSourceInPut.setCurrentValue(VALUE_SOURCE_MODE[1]);
//					mChanSetting.getItemView().setVisibility(View.GONE);
				}
				else
				{
					mSourceInPut.setCurrentValue(VALUE_SOURCE_MODE[0]);
//					mChanSetting.getItemView().setVisibility(View.VISIBLE);
//					tmpView.setEnabled(true);
				}
				//********add by huangfh@tcl.com 09-03
				if(mItems.contains(subtitle)){
					subtitle.setOptionsByArray(mSubtitleItems, mSubtitle, titleItemClickListener);
					if(tvmm.getSubtitleEnable()==false){
						subtitle.setTextValue(mSubtitleItems[0]);
					}
					else{
						subtitle.setCurrentValue(curTitle+1);
					}
				}
//				if(mItems.contains(voicelanguage)){
//					voicelanguage.setOptionsByArray(mVoiceLangItems, mVoicelangs, languageItemClickListener);
//					voicelanguage.setCurrentValue(curLang);
//				}
				if(mItems.contains(mChanSetting)) mChanSetting.setTextValue(" ");
				mListView.setSelection(mLastSelection);
				keyDelay=true;
				updateContent();
				//******end 
				break;
			case 1:
				Log.d("hfh","mPosition"+mPosition+" sourceId"+sourceID+" firstInit"+firstInit);
				if(mPosition==1||firstInit==true&&sourceID==TvManager.SOURCE_DTV1)
				{	
					//********add by hfh 09-19					
//					if(hasLanguage)	mItems.add(voicelanguage);
					if(hasSubtitle) mItems.add(subtitle);
				}
				if(mPosition==0||firstInit==true&&sourceID==TvManager.SOURCE_ATV1)
				{	
					if(tm.getInputSource()==1&&tm.isSignalDisplayReady()){
						mChanSetting.setTextValue(" ");
						mItems.add(mChanSetting);
					}
				}				
				sourceID=tm.getInputSource();
				initSoundParameters(); 
				//*****end
				break;
			case 2:
				keyDelay=true;
			default:
				break;
			}
		};
	};
		
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
    	sharedPref = getActivity().getSharedPreferences("searchMenuFocus", Context.MODE_PRIVATE);    						
		if(null != sharedPref)
		{
			Editor editor = sharedPref.edit();
			editor.putInt("focusId", -1);
			editor.commit();
		}
		
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stu
		super.onResume();	
		
		mListView.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				//解决跳到三级菜单和一级菜单时返回，字体样式切换和焦点丢失问题。
				if (hasFocus) {
					((DeenListView) mListView).enableInvalidPosition(false);
				} else {
					mAdapter.notifyDataSetInvalidated();
					((DeenListView) mListView).setSelection(-1, true);
				}
				
				sharedPref = getActivity().getSharedPreferences("searchMenuFocus", Context.MODE_PRIVATE); 
				Editor editor = sharedPref.edit();
				if (hasFocus)
				{
					editor.putInt("focusId", mListView.getSelectedItemPosition());						
				}else{
					editor.putInt("focusId", -1);
				}
				editor.commit();
			}
		});		
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();	
	}	
	
	@Override
	public int getTitle() {
		return R.string.STRING_PICTURE;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mBroadcastReceiver = new BroadcastReceiver() {			
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.d("hfh", "11111111111111");
				String action = intent.getAction();
				if(action.equals(TvBroadcastDefs.ACTION_TV_DISPLAY_READY)){
					if(mItems.contains(mChanSetting)) mItems.remove(mChanSetting);
//					if(mItems.contains(voicelanguage)) mItems.remove(voicelanguage);
					if(mItems.contains(subtitle)) mItems.remove(subtitle);
					updateVoiceLanguageAndSubtitle();						
				}else if(action.equals(TvBroadcastDefs.ACTION_TV_NO_SIGNAL)){
					mHandler.sendEmptyMessageDelayed(2,0);
				}
			}
		};
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(TvBroadcastDefs.ACTION_TV_DISPLAY_READY);
		mIntentFilter.addAction(TvBroadcastDefs.ACTION_TV_NO_SIGNAL);
		getActivity().registerReceiver(mBroadcastReceiver, mIntentFilter);
		Log.d("hfh", "registerReceiver");
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		getActivity().unregisterReceiver(mBroadcastReceiver);
		firstInit=true;
		keyDelay=true;
		Log.d("hfh", "unregisterReceiver");
	}
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyDelay){
			return super.onKey(v, keyCode, event);
		}else return true;
	}
	
}
