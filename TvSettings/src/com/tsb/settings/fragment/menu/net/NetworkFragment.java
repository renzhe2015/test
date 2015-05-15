package com.tsb.settings.fragment.menu.net;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.TvManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.ethernet.EthernetManager;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.tsb.settings.BuildConfig;
import com.tsb.settings.R;
import com.tsb.settings.fragment.MainMenuFragment;
import com.tsb.settings.fragment.menu.BaseMenuFragment;
import com.tsb.settings.menu.DeenListView;
import com.tsb.settings.settings.view.LayoutItem;
import com.tsb.settings.settings.view.TaskTimer;
import com.tsb.settings.util.FragmentUtils;
import com.tsb.settings.util.LogUtil;
import com.tsb.settings.util.Util;

@SuppressLint("ValidFragment")
public class NetworkFragment extends Fragment implements
View.OnClickListener,OnKeyListener{
	public  static final String ETHERNET_INTERFACE_CONF_CHANGED = "android.net.ethernet.ETHERNET_INTERFACE_CONF_CHANGED";
	private final String TAG = "NetworkSettingsActivity";
	public static final String ARG_NETWORKTYPE = "networktype";
	private static int mCurNetworkType = 0;
	private LayoutItem tsNetworkMode;
	/*****************网络测速*****************/
	private LayoutItem mLayoutSpeed;
	private static final int MODE_ETHERNET = 0;
	private static final int MODE_WIFI = 1;
	View focusedView;
	/*****************有线网络*****************/
	private EthernetManager mEthManager;
	private LayoutItem btnUpdateEthernet;
	private TextView tvMac;
	private TextView tvIp;
	private TextView tvSubnet;
	private TextView tvGateway;
	private TextView tvDns;
	private LayoutItem mLayoutManual;
	//private TCLProgressBar mProgressBar;
	private LinearLayout mLayoutEthDevInfo;
	private LinearLayout mLayoutEthernet;

	private boolean bEthFirstReceive = true;

	private IntentFilter mIntentFilter;
	private EthernetHandler mEthHandler = new EthernetHandler();
	private static final int MSG_REFRESH_UI = 102;
	private static final int MSG_REFRESH_UI_ADAPTER = 103;
	private static final int MSG_REFRESH_WIFILIST = 104;
	private static final int MSG_DHCP_TIMEOUT = 101;
	private static final int WIFI_RESCAN_INTERVAL_MS = 10 * 1000;
	private boolean bIsEthInited = false;
	private TvManager mTv;
	/***************** 无线网络 *****************/
	private final Scanner mScanner;
	private WifiEnabler mWifiEnabler;
	private WifiSettingsImpl mWifiSettings;
	private Button mWifiMoreIcon;
	private boolean mIsWifiMore=false;
	private LinearLayout mLayoutWifi;
	private TextView tvWifiListTitle;
	private DeenListView mLayoutWifiList;
	public WifiAdapter wifiAdapter;
	private Switch mLayoutWifiSwitch;
	public static LayoutItem mLayoutWifiAdd;
	private WifiManager mWifiManager;
	private Handler mConnectStatusHandler = null;
	private boolean isAddWifi=false;
	private boolean bIsWifiInited = false;
	private DetailedState mLastState;
	private WifiInfo mLastInfo;
	public List<AccessPoint> mWifiList = new ArrayList<AccessPoint>();
	public List<AccessPoint> mWifiListtemp = new ArrayList<AccessPoint>();
	private EthernetImpl mEthernetImpl;

	private Fragment mCurrentFragment;
	public final BroadcastReceiver mNetStateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(mCurNetworkType==1&&bIsWifiInited){
				if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
					if (tsNetworkMode == null || mCurNetworkType != MODE_WIFI)
						return;
					updateWifiState(intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
							WifiManager.WIFI_STATE_UNKNOWN));
				}
				else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())
						|| WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION
						.equals(intent.getAction())
						|| WifiManager.LINK_CONFIGURATION_CHANGED_ACTION.equals(intent.getAction())) {
					updateWifiLayout(View.VISIBLE);
				}
				else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
					NetworkInfo info = (NetworkInfo) intent
							.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
					mWifiSettings.mConnected.set(info.isConnected());
					updateConnectionState(info.getDetailedState());
					if(info.getDetailedState().ordinal()==5)
						Util.showToast(getActivity(), Summary.get(getActivity(), info.getDetailedState()));
				} 
				else if (WifiManager.RSSI_CHANGED_ACTION.equals(intent.getAction())) {
					updateConnectionState(null);
				}
				else if(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(intent.getAction())){
					HandleWifiStateEvent(context, intent);

				}
			}
			else if (EthernetManager.ETHERNET_STATE_CHANGED_ACTION.equals(intent.getAction())&&bIsEthInited) {

				if (bEthFirstReceive) {
					bEthFirstReceive = false;
					return;
				} else if (tsNetworkMode == null || mCurNetworkType != MODE_ETHERNET) {
					return;
				}
				if (mEthernetImpl == null) {
					return;
				}

				if (!mEthernetImpl.isEth0Connect()) {
					updateEthernetInfoView(false);
				}
				HandleEthStateChanged(intent);
			} 

		}

	};
	private MainMenuFragment mParentFragment;

	@SuppressLint("ValidFragment")
	public NetworkFragment(){
		mScanner = new Scanner();

	}
	private View mView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		return inflater.inflate(R.layout.network_settings_layout, container,
				false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mView=view;
		mConnectStatusHandler = new Handler();
		mIntentFilter = new IntentFilter(EthernetManager.ETHERNET_STATE_CHANGED_ACTION);
		mIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		mIntentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		mIntentFilter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
		mIntentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
		mIntentFilter.addAction(WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION);
		mIntentFilter.addAction(WifiManager.LINK_CONFIGURATION_CHANGED_ACTION);
		mIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		mIntentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
		initValues();
		initResources();
		Thread thread = new Thread(new Runnable() {			
			@Override
			public void run() {	
				mEthHandler.sendEmptyMessage(MSG_REFRESH_UI);
				mEthHandler.sendEmptyMessageDelayed(MSG_REFRESH_UI_ADAPTER, 100);
			}
		});
		thread.start();
		super.onViewCreated(view, savedInstanceState);
	}
	protected void initValues() {
		// TODO Auto-generated method stub	
		mTv = (TvManager) getActivity().getSystemService("tv");
		mEthManager = (EthernetManager)  getActivity().getSystemService(Context.ETHERNET_SERVICE);
		mEthernetImpl = EthernetImpl.getInstance(mEthManager,mTv);
		mWifiSettings = WifiSettingsImpl.getInstance( getActivity());
		mWifiManager=mWifiSettings.getWifiManager();
		if(mWifiManager.isWifiEnabled())
			mCurNetworkType=1;
		else if(getArguments().getInt(ARG_NETWORKTYPE,-1)>=0){
			mCurNetworkType=getArguments().getInt(ARG_NETWORKTYPE,-1);
		}
		Log.d("liluyuan", "mCurNetworkType1= "+mCurNetworkType);
	}

	private void initResources() {		
		tsNetworkMode = (LayoutItem) mView.findViewById(R.id.spinner_network_choice);
		if(isAdded())
			tsNetworkMode.setTextValue(getResources()
					.getStringArray(R.array.network_mode)[mCurNetworkType]);
		Log.d("liluyuan", "mCurNetworkType2= "+mCurNetworkType);
		tsNetworkMode.needTextMarquee(true);
		tsNetworkMode.setOnKeyListener(this);
		mLayoutSpeed = (LayoutItem) mView.findViewById(R.id.layout_network_speed_test);		
		mLayoutSpeed.setOnClickListener(this);
		mLayoutSpeed.setOnKeyListener(this);
		mLayoutSpeed.needPopWindow(false);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.layout_ethernet_auto:
			if (!mEthernetImpl.isEth0Connect()) {// detect eth0 is insert
				Util.showToast2S(getActivity(), getString(R.string.network_error));
				break;
			} 			
			Log.d(TAG, "startDhcp" );
			mEthernetImpl.startDhcp();
			mEthHandler.sendEmptyMessageDelayed(MSG_DHCP_TIMEOUT, 5 * 1000);
			break;
		case R.id.layout_network_speed_test:
			mIsWifiMore=false;
			startNetSpeed();
			FragmentUtils.mHandler.removeMessages(0);
			break;
		case R.id.layout_ethernet_manual:
			if (!mEthernetImpl.isEth0Connect()) {// detect eth0 is insert
				Util.showToast2S(getActivity(), getString(R.string.network_error));
				break;
			}
			mIsWifiMore=false;
			startManuel();
			break;
		case R.id.layout_wifi_more_icon:
			mIsWifiMore=!mIsWifiMore;
			if(mIsWifiMore){
				mWifiMoreIcon.setText(R.string.tcl_wifi_network_list_part);
				if(isAdded())
					mWifiMoreIcon.setCompoundDrawables(null, null, null, getResources().getDrawable(R.drawable.wifi_list_less));				
			}
			else{
				mWifiMoreIcon.setText(R.string.tcl_wifi_network_list_more);
				if(isAdded())
					mWifiMoreIcon.setCompoundDrawables(null, null, null, getResources().getDrawable(R.drawable.wifi_list_more));
			}
			mEthHandler.sendEmptyMessageDelayed(MSG_REFRESH_WIFILIST, 500);
			break;
		case R.id.layout_wifi_add:
			mIsWifiMore=false;
			isAddWifi=true;
			startWifiAdd();
			break;
		}
	}

	private void startWifiAdd() {
		// TODO Auto-generated method stub
		creatNetChildFragment(R.id.layout_wifi_add);
	}

	private void startManuel() {
		// TODO Auto-generated method stub
		creatNetChildFragment(R.id.layout_ethernet_manual);
	}

	private void startNetSpeed(){
		creatNetChildFragment(R.id.layout_network_speed_test);
	}

	private void creatNetChildFragment(int id) {
		// TODO Auto-generated method stub
		MainMenuFragment.fromNetSetting=true;
		mView.getRootView().findViewById(R.id.layout_third_menu).setVisibility(View.VISIBLE);
		Fragment f=getParentFragment();
		FragmentManager fm = getParentFragment().getChildFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		String tag = getFragmentTag(id);
		ft.detach(this);
		Fragment fragment=null;
		switch(id){
		case R.id.layout_network_speed_test:
			fragment=new SpeedTestFragment();
			break;
		case R.id.layout_ethernet_manual:
			fragment=new EthernetManualFragment();
			break;
		case R.id.layout_wifi_add:
			fragment=new WifiDialog();
			Bundle arg = new Bundle();
			if(isAddWifi)
				arg.putInt("edit",1);
			else 
				arg.putInt("edit",0);

			fragment.setArguments(arg);
		default:
			break;
		}
		ft.add(R.id.container_menu, fragment, tag);

		ft.commitAllowingStateLoss();
		FragmentUtils.setCurFragment(fragment);
		//		MainMenuFragment.setmCurrentFragment(fragment);
	}

	private static final String getFragmentTag(int id) {
		return String.format(Locale.US, "menu::%d", id);
	}

	class EthernetHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case MSG_DHCP_TIMEOUT: {
				Log.d(TAG, "MSG_DHCP_TIMEOUT" );
				mEthernetImpl.isConfiguring = false;
				if (mEthernetImpl.getWiredNetworkStates(getActivity())) {
					if (mEthernetImpl.mEthManager.isConfigured()) {
						mEthernetImpl.updateEthernetDevInfo();
					}
					updateEthernetInfoView(true);
					Util.showToast(getActivity(),
							getString(R.string.tcl_connect_ethernet_success));
				} else {
					updateEthernetInfoView(false);
					Util.showToast(getActivity(),
							getString(R.string.tcl_connect_ethernet_fail));
				}
			}
			break;
			case MSG_REFRESH_UI:	
				mLayoutSpeed.setTitle(R.string.tcl_network_speed_test);	
				tsNetworkMode.setTitle(R.string.network_mode_change);
				break;
			case MSG_REFRESH_UI_ADAPTER:
				if(isAdded()){/*Fragment not attached to Activity*/
					tsNetworkMode.initListView(getResources()
							.getStringArray(R.array.network_mode), mCurNetworkType, listenerNetworkMode);
					onNetworkTypeChanged();
				}
				break;
			case MSG_REFRESH_WIFILIST:
				mWifiListtemp.clear();
				mWifiList.clear();
				for (AccessPoint accessPoint : mWifiSettings.getAccessPoints()) {
					mWifiListtemp.add(accessPoint);
				}
				if(mWifiListtemp.size()>3){
					mWifiMoreIcon.setVisibility(View.VISIBLE);
					if(!mIsWifiMore){
						for (int i = 0; i < 3; i++) {
							mWifiList.add(mWifiListtemp.get(i));
						}
					}		
					else{
						for (int i = 0; i < mWifiListtemp.size() ; i++) {
							mWifiList.add(mWifiListtemp.get(i));
						}
					}
				}
				else {
					mWifiMoreIcon.setVisibility(View.GONE);
					for (int i = 0; i < mWifiListtemp.size(); i++) {
						mWifiList.add(mWifiListtemp.get(i));
					}			
				}
				wifiAdapter.notifyDataSetChanged();
				autoSetListViewHeight(mLayoutWifiList);
				break;
			}
			super.handleMessage(msg);

		}

	}
	OnItemClickListener listenerNetworkMode = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			Log.d(TAG, "onItemSelected===>" + position);
			mCurNetworkType = position;
			if(isAdded())
				tsNetworkMode.setTextValue(getResources()
						.getStringArray(R.array.network_mode)[mCurNetworkType]);
			onNetworkTypeChanged();
			tsNetworkMode.getPopView().dismiss();
		}
	};
	private void onNetworkTypeChanged() {	
		switch (mCurNetworkType) {
		case MODE_ETHERNET:
			TaskTimer.cancel();
			TaskTimer.lock();
			if (!bIsEthInited) {
				initEthernetResources();
			}
			mLayoutEthernet.setVisibility(View.VISIBLE);	
			if (!mEthernetImpl.isEth0Connect()) {
				updateEthernetInfoView(false);
			} else {					
				mEthernetImpl.updateEthernetDevInfo();
				if (mEthernetImpl.getIp() == null || !mEthernetImpl.getWiredNetworkStates(getActivity())) {
					updateEthernetInfoView(false);
				} else {
					updateEthernetInfoView(true);
				}
			}
			if (mLayoutWifi != null) {
				mLayoutWifi.setVisibility(View.GONE);
			}
			if (mWifiEnabler != null) {
				mWifiEnabler.pause();
			}
			break;
		case MODE_WIFI:
			TaskTimer.cancel();
			TaskTimer.lock();
			if (!bIsWifiInited) {
				initWifiResources();
			}
			mLayoutWifi.setVisibility(View.VISIBLE);
			if (mWifiManager.isWifiEnabled()) {	
				mLayoutWifiSwitch.setChecked(true);
				mLayoutWifiSwitch.setTrackResource(R.drawable.switch_on);
				updateLastConnectionState();
				updateWifiLayout(View.VISIBLE);
			} else {
				mLayoutWifiSwitch.setChecked(false);
				updateWifiLayout(View.GONE);
			}
			if (mLayoutEthernet != null) {
				mLayoutEthernet.setVisibility(View.GONE);
			}
			if (mWifiEnabler != null) {
				mWifiEnabler.resume();
			}
			break;
		default:
			break;
		}
	}
	private void updateWifiLayout(int visible) {
		// TODO Auto-generated method stub
		if(visible==View.VISIBLE){
			mLayoutWifiSwitch.setNextFocusDownId(R.id.layout_wifi_networks_list);
		}
		else{
			mLayoutWifiSwitch.setNextFocusDownId(R.id.spinner_network_choice);
		}
		if (tsNetworkMode == null || mCurNetworkType != MODE_WIFI
				|| mLayoutWifiList == null) {
			return;
		}
		mLayoutWifiAdd.setVisibility(visible);
		mLayoutWifiList.setVisibility(visible);
		tvWifiListTitle.setVisibility(visible);
		mLayoutSpeed.setVisibility(visible);
		if(visible==View.VISIBLE)
		{
			tsNetworkMode.setNextFocusUpId(R.id.layout_network_speed_test);
		}
		else//visible==View.GONE
		{
			tsNetworkMode.setNextFocusUpId(R.id.switch_wifi);
			mWifiMoreIcon.setVisibility(visible);
		}

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				mWifiSettings.updateAccessPoints(constructAccessPoints());
				mEthHandler.sendEmptyMessageDelayed(MSG_REFRESH_WIFILIST, 500);
			}

		}).start();		

	}

	private void updateEthernetInfoView(boolean isVisible) {
		try {
			if (isVisible) {
				mLayoutEthDevInfo.setVisibility(View.VISIBLE);
				mEthernetImpl.updateEthernetDevInfo();
				tvMac.setText(mEthernetImpl.getMac());
				tvIp.setText(mEthernetImpl.getIp());
				tvSubnet.setText(mEthernetImpl.getSubnetMask());
				tvGateway.setText(mEthernetImpl.getGetway());
				tvDns.setText(mEthernetImpl.getDns());
				mLayoutSpeed.setVisibility(View.VISIBLE);
				mLayoutManual.setNextFocusDownId(R.id.layout_network_speed_test);
				tsNetworkMode.setNextFocusUpId(R.id.layout_network_speed_test);

			} else {
				mLayoutEthDevInfo.setVisibility(View.GONE);
				mLayoutSpeed.setVisibility(View.INVISIBLE);
				mLayoutManual.setNextFocusDownId(R.id.spinner_network_choice);
				tsNetworkMode.setNextFocusUpId(R.id.layout_ethernet_manual);
			}
		} catch (NullPointerException e) { //空指针则不刷新
			e.printStackTrace();
		}
	}

	private void initEthernetResources() {
		bIsEthInited = true;
		btnUpdateEthernet = (LayoutItem) mView.findViewById(R.id.layout_ethernet_auto);
		btnUpdateEthernet.setOnKeyListener(this);
		mLayoutEthernet = (LinearLayout) mView.findViewById(R.id.layout_ethernet);
		tvMac = (TextView) mView.findViewById(R.id.tv_ethernet_mac);
		tvIp = (TextView) mView.findViewById(R.id.tv_ethernet_ip);
		tvSubnet = (TextView) mView.findViewById(R.id.tv_ethernet_subnet);
		tvGateway = (TextView) mView.findViewById(R.id.tv_ethernet_gateway);
		tvDns = (TextView) mView.findViewById(R.id.tv_ethernet_dns);
		mLayoutManual = (LayoutItem) mView.findViewById(R.id.layout_ethernet_manual);

		mLayoutManual.setTitle(R.string.tcl_manual_setting);
		btnUpdateEthernet.setTitle(R.string.tcl_ip_update);

		mLayoutEthDevInfo = (LinearLayout) mView.findViewById(R.id.layout_ethernet_info);

		btnUpdateEthernet.setOnClickListener(this);
		mLayoutManual.setOnClickListener(this);
		mLayoutManual.setOnKeyListener(this);
		btnUpdateEthernet.needPopWindow(false);
		btnUpdateEthernet.noRightArrawWithClickable();
		mLayoutManual.needPopWindow(false);
	}
	private void initWifiResources() {
		bIsWifiInited = true;
		mLayoutWifi = (LinearLayout) mView.findViewById(R.id.layout_network_wifi);
		tvWifiListTitle = (TextView) mView.findViewById(R.id.tv_wifi_list_title);
		mLayoutWifiList = (DeenListView) mView.findViewById(R.id.layout_wifi_networks_list);
		mLayoutWifiSwitch = (Switch) mView.findViewById(R.id.switch_wifi);
		mLayoutWifiSwitch.setOnKeyListener(this);
		
		mLayoutWifiSwitch.setText(R.string.tcl_network_wifi);
		mLayoutWifiSwitch.setTextSize(TypedValue.COMPLEX_UNIT_PX, 22);
		mLayoutWifiSwitch.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(arg1) {
					mLayoutWifiSwitch.setTextColor(getResources().getColor(R.color.text_focus));
				}else {
					mLayoutWifiSwitch.setTextColor(getResources().getColor(R.color.text_unfocus));
				}
			}
		});
		
		mLayoutWifiAdd = (LayoutItem) mView.findViewById(R.id.layout_wifi_add);		
		mLayoutWifiAdd.setTitle(R.string.tcl_wifi_network_add);		
		mLayoutWifiAdd.setOnClickListener(this);
		mLayoutWifiAdd.setOnKeyListener(this);
		mLayoutWifiAdd.needPopWindow(false);
		mWifiMoreIcon=(Button)mView.findViewById(R.id.layout_wifi_more_icon);
		mWifiMoreIcon.setOnClickListener(this);
		mWifiMoreIcon.setOnKeyListener(this);
		wifiAdapter = new WifiAdapter(getActivity());
		mLayoutWifiList.setAdapter(wifiAdapter);
		((DeenListView) mLayoutWifiList).setSelection(-1, true);
		mLayoutWifiList.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				
				if (hasFocus) {
					((DeenListView) mLayoutWifiList).enableInvalidPosition(false);
				} else {
					wifiAdapter.notifyDataSetInvalidated();
					((DeenListView) mLayoutWifiList).setSelection(-1, true);
				}
			}
		});
		mLayoutWifiList.setOnKeyListener(this);
		mWifiEnabler = new WifiEnabler(getActivity(),
				mLayoutWifiSwitch);
		mLayoutWifiList.setOnItemClickListener(new AdapterView.OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Log.d(TAG, "showDialog=============================>0");
				mWifiSettings.setSelectedAp(mWifiList.get(position) );
				Log.d(TAG, "showDialog=============================>");
				mIsWifiMore=false;
				isAddWifi=false;
				startWifiAdd();
			}
		}
				);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		getActivity().registerReceiver(mNetStateReceiver, mIntentFilter);
		super.onResume();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		bIsEthInited=false;
		bIsWifiInited=false;
		mIsWifiMore=false;
		isAddWifi=false;
		mCurNetworkType = 0;
		if (mEthHandler.hasMessages(MSG_DHCP_TIMEOUT))
			mEthHandler.removeMessages(MSG_DHCP_TIMEOUT);
		try {
			if (mNetStateReceiver != null) {
				getActivity().unregisterReceiver(mNetStateReceiver);
			}	
		} catch (Exception e) {

		}	
		mScanner.pause();
		super.onPause();
	}
	public class WifiAdapter extends BaseAdapter {
		private String connection_wifi_ssid ;
		private SupplicantState connection_wifi_state;
		private LayoutInflater inflater;
		public WifiAdapter(Context context)
		{
			inflater = LayoutInflater.from(context);
			connection_wifi_ssid = mWifiManager.getConnectionInfo().getSSID();
			connection_wifi_state = mWifiManager.getConnectionInfo().getSupplicantState();	
		}

		@Override
		public void notifyDataSetChanged() {
			// TODO Auto-generated method stub
			connection_wifi_ssid = mWifiManager.getConnectionInfo().getSSID();
			connection_wifi_state = mWifiManager.getConnectionInfo().getSupplicantState();
			super.notifyDataSetChanged();
		}

		@Override
		public int getCount() {			
			return mWifiList.size();
		}

		@Override
		public Object getItem(int position) {
			return mWifiList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			final ChatViewHolder vh;

			if (convertView == null) {
				convertView = inflater.inflate(R.layout.wifi_list, null);	
				vh = new ChatViewHolder();
				vh.wifi_name      = (TextView)  convertView.findViewById(R.id.wifi_name);
				vh.wifi_connected = (ImageView) convertView.findViewById(R.id.wifi_connectedIcon);
				vh.wifi_level     = (ImageView) convertView.findViewById(R.id.wifi_level);
				vh.wifi_locked    = (ImageView) convertView.findViewById(R.id.wifi_lockedIcon);
				convertView.setTag(vh);
			} else {
				vh = (ChatViewHolder) convertView.getTag();
			}
			int signal_level;
			String wifi_name="\""+mWifiList.get(position).ssid+"\"";
			//add the level and state to make sure the connection state fix question 3105
			int wifi_level = mWifiList.get(position).getLevel();
			//			Log.d(TAG, "wifi_name="+wifi_name+"-----wifi_level="+wifi_level+"wifi_state="+connection_wifi_state);
			if(connection_wifi_ssid.equals(wifi_name)  && wifi_level != -1 && connection_wifi_state==SupplicantState.COMPLETED){
				//
				vh.wifi_connected.setImageResource(R.drawable.duigou_focus);
			}
			else{
				vh.wifi_connected.setImageResource(R.drawable.duigou_unfocus);				
			}

			if(mWifiList.get(position).security!=0)
				vh.wifi_locked.setImageResource(R.drawable.tcl_wifi_signal_lock);
			else
				vh.wifi_locked.setImageDrawable(null);

			wifi_name=wifi_name.split("\"")[1];
			vh.wifi_name.setText(wifi_name);
			signal_level =mWifiList.get(position).getLevel();
			switch(signal_level){
			case -1: vh.wifi_level.setImageDrawable(null);
			break;
			case 1: vh.wifi_level.setImageResource(R.drawable.tcl_wifi_signal_0);
			break;
			case 2: vh.wifi_level.setImageResource(R.drawable.tcl_wifi_signal_1);
			break;
			case 3: vh.wifi_level.setImageResource(R.drawable.tcl_wifi_signal_2);
			break;
			case 4: vh.wifi_level.setImageResource(R.drawable.tcl_wifi_signal_3);
			break;
			default:
				break;
			}
			return convertView;

		}
	}

	public class ChatViewHolder {
		ImageView wifi_connected;
		TextView wifi_name;// 网络的名字，唯一区别WIFI网络的名字
		ImageView wifi_locked;
		ImageView wifi_level;

	}

	/** Returns sorted list of access points */
	private List<AccessPoint> constructAccessPoints() {
		ArrayList<AccessPoint> accessPoints = new ArrayList<AccessPoint>();
		if(accessPoints != null)
			accessPoints.clear();
		/** Lookup table to more quickly update AccessPoints by only considering objects with the
		 * correct SSID.  Maps SSID -> List of AccessPoints with the given SSID.  */
		Multimap<String, AccessPoint> apMap = new Multimap<String, AccessPoint>();

		List<WifiConfiguration> configs = mWifiManager.getConfiguredNetworks();
		if (configs == null) {
			Log.d(TAG, "mWifiManager.getConfiguredNetworks(): null");
		}

		if (configs != null) {
			Log.d(TAG, "mWifiManager.getConfiguredNetworks():"
					+ configs.size());
			mWifiSettings.setLastPriorityValue(0);
			for (WifiConfiguration config : configs) {
				if (config.priority > mWifiSettings.getLastPriority()) {
					mWifiSettings.setLastPriorityValue(config.priority);
				}

				AccessPoint accessPoint = new AccessPoint(getActivity(), config);
				if(accessPoint.security!=3){
					accessPoint.setLayoutResource(R.layout.tcl_preference);
					accessPoint.update(mLastInfo, mLastState);
					//				accessPoint.update(mWifiAdmin.getmWifiInfo(), WifiInfo
					//						.getDetailedStateOf((SupplicantState) intent
					//								.getParcelableExtra(WifiManager.EXTRA_NEW_STATE)));

					Log.d(TAG, "accessPoint.ssid:" + accessPoint.ssid);
					Log.d(TAG, "accessPoint.getState:" + accessPoint.getState());

					/*******add by chengct for delete AP which state is DISABLED_AUTH_FAILURE*****************/
					WifiConfiguration mConfig = accessPoint.getConfig();
					if (mConfig != null && mConfig.status == WifiConfiguration.Status.DISABLED) {
						if (mConfig.disableReason == WifiConfiguration.DISABLED_AUTH_FAILURE) {
							mWifiManager.removeNetwork(accessPoint.networkId);
							mWifiManager.saveConfiguration();
							continue;
						}	           
					}
					/************************/
					accessPoints.add(accessPoint);
					apMap.put(accessPoint.ssid, accessPoint);
				}
			}
		}

		List<ScanResult> results = mWifiManager.getScanResults();
		if (results == null) {
			Log.d(TAG, "mWifiManager.getScanResults(): null");
		}
		if (results != null) {
			for (ScanResult result : results) {
				if (result.SSID == null || result.SSID.length() == 0
						|| result.capabilities.contains("[IBSS]")) {
					continue;
				}

				boolean found = false;
				for (AccessPoint accessPoint : apMap.getAll(result.SSID)) {
					if (accessPoint.update(result)) {
						found = true;
					}
				}

				// Log.d(TAG, "found in config :" + found);
				if (!found) {

					AccessPoint accessPointScan = new AccessPoint(getActivity(), result);
					if(accessPointScan.security!=3){
						accessPointScan.setLayoutResource(R.layout.tcl_preference);
						accessPoints.add(accessPointScan);
						apMap.put(accessPointScan.ssid, accessPointScan);
					}
				}
			}
		}
		// Pre-sort accessPoints to speed preference insertion
		Collections.sort(accessPoints);
		return accessPoints;
	}

	/** A restricted multimap for use in constructAccessPoints */
	private class Multimap<K,V> {
		private HashMap<K,List<V>> store = new HashMap<K,List<V>>();
		/** retrieve a non-null list of values with key K */
		List<V> getAll(K key) {
			List<V> values = store.get(key);
			return values != null ? values : Collections.<V>emptyList();
		}

		void put(K key, V val) {
			List<V> curVals = store.get(key);
			if (curVals == null) {
				curVals = new ArrayList<V>(3);
				store.put(key, curVals);
			}
			curVals.add(val);
		}
	}
	protected void HandleEthStateChanged(Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		final boolean isConfigured = intent.getBooleanExtra(
				EthernetManager.EXTRA_ETHERNET_STATE, false);
		mEthernetImpl.isConfiguring = false;
		Log.i(TAG, "~~HandleEthStateChanged(Intent intent):" + action
				+ " isConfigured:" + isConfigured);
		if (mEthernetImpl.getWiredNetworkStates(getActivity()) && isConfigured) {

			if (mEthHandler != null
					&& mEthHandler.hasMessages(MSG_DHCP_TIMEOUT)) {
				mEthHandler.removeMessages(MSG_DHCP_TIMEOUT);
			}
			//			mProgressBar.setVisibility(View.GONE);
			//			dismissDLabel();

			Util.showToast(getActivity(), getString(R.string.ip_config_success));

			if (mEthernetImpl.mEthManager.isConfigured()) {
				mEthernetImpl.updateEthernetDevInfo();		           
			}

			updateEthernetInfoView(true);
		} else if (!isConfigured ){
			//			dismissDLabel();
			Util.showToast(getActivity(), getString(R.string.ip_config_failed));
			updateEthernetInfoView(false);
		}
	}
	private void HandleWifiStateEvent(Context context, Intent intent) {
		if (!mWifiSettings.mConnected.get() ) {
			DetailedState state=WifiInfo
					.getDetailedStateOf((SupplicantState) intent
							.getParcelableExtra(WifiManager.EXTRA_NEW_STATE));
			if(state.ordinal()>0 && state.ordinal()!=8){
				Util.showToast(getActivity(), Summary.get(getActivity(), state));
			}
			updateConnectionState(state);			

		}
		Bundle bundle = intent.getExtras();
		int EXTRA_SUPPLICANT_ERROR = bundle
				.getInt(WifiManager.EXTRA_SUPPLICANT_ERROR);
		Log.d(TAG, "WifiManager.SUPPLICANT_STATE_CHANGED_ACTION:EXTRA_SUPPLICANT_ERROR==" + EXTRA_SUPPLICANT_ERROR);
		if (EXTRA_SUPPLICANT_ERROR == WifiManager.ERROR) {
			//NetworkSettingsActivity.dismissWifiAddDlable();
		} else if (EXTRA_SUPPLICANT_ERROR == WifiManager.ERROR_AUTHENTICATING) {
			//WifiSettings.dismissWaitDialog();
			if (mWifiSettings.getPasswordErrorCount() == 0) {
				Util.showToast(getActivity(), getString(R.string.wifi_pwd_error));	
			}								
			if (mWifiSettings.getPasswordErrorCount() > 0) {
				mWifiSettings.setPasswordErrorCount(0);
				return;
			}

			mWifiSettings.setPasswordErrorCount(1);
		}

	}
	private void updateConnectionState(DetailedState state) {
		// TODO Auto-generated method stub
		//when AP is connected ,Rescan the WIFI AP fix question 0002685
		if (!mWifiManager.isWifiEnabled()) {
			mScanner.pause();
			return;
		}

		if (state == DetailedState.OBTAINING_IPADDR) {
			mScanner.pause();
		} else {
			mScanner.resume();
		}
		//
		mLastInfo = mWifiManager.getConnectionInfo();
		Log.d(TAG, "mLastInfo======>" + mLastInfo);
		Log.d(TAG, "mLastState======>" + mLastState);
		if (state != null) {
			mLastState = state;
		}
		for (int i = mWifiList.size() - 1; i >= 0; --i) {
			mWifiList.get(i).update(mLastInfo, mLastState);
		}
		//		updateWifiLayout(View.VISIBLE);
	}

	//get supplicant state from ConnectionInfo fix question 0002532
	private void updateLastConnectionState() {
		// TODO Auto-generated method stub
		//when AP is connected ,Rescan the WIFI AP fix question 0002685
		if (mWifiManager.isWifiEnabled()) {
			mScanner.resume();
		}
		//
		mLastInfo = mWifiManager.getConnectionInfo();
		if(BuildConfig.DEBUG)Log.d(TAG, "mLastInfo======>" + mLastInfo);
		for (int i = mWifiList.size() - 1; i >= 0; --i) {
			mWifiList.get(i).update(mLastInfo, mLastState);
		}
	}
	//

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(event.getAction()==KeyEvent.ACTION_DOWN){
			FragmentUtils.mHandler.removeMessages(0);
			FragmentUtils.mHandler.sendEmptyMessageDelayed(0, 15000);
			switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				if(tsNetworkMode!=null&&tsNetworkMode.isFocused()){
					break;
				}else if(mLayoutSpeed != null && mLayoutSpeed.isFocused()){
					startNetSpeed();
					FragmentUtils.mHandler.removeMessages(0);
				}else if(mLayoutWifiAdd != null && mLayoutWifiAdd.isFocused()){
					isAddWifi=true;
					startWifiAdd();
				}else if(mLayoutManual != null && mLayoutManual.isFocused()){
					if (!mEthernetImpl.isEth0Connect()) {// detect eth0 is insert
						Util.showToast2S(getActivity(), getString(R.string.network_error));
						return true;
					}
					startManuel();
				}
				return true;
			case KeyEvent.KEYCODE_BACK:
			case KeyEvent.KEYCODE_DPAD_LEFT:
				MainMenuFragment.fromNetSetting=false;
				v.getRootView().findViewById(MainMenuFragment.mLastSelectionId2).requestFocus();
				return true;
			default:
				return false;
			}

		}
		return false;
	}

	// when AP is connected ,Rescan the WIFI AP fix question 0002685
	private class Scanner extends Handler {
		private int mRetry = 0;

		void resume() {
			// if(BuildConfig.DEBUG)Log.d(TAG, "Scanner onResume====>");
			if (!hasMessages(0)) {
				sendEmptyMessage(0);
			}
		}

		void forceScan() {
			removeMessages(0);
			sendEmptyMessage(0);
		}

		void pause() {
			mRetry = 0;
			removeMessages(0);
		}

		@Override
		public void handleMessage(Message message) {
			// if (WifiPowerSetting.getWlanNum() < 1) {
			// mWifiManager.setWifiEnabled(false);
			// return;
			// }
			if (mWifiManager.startScan()) {
				if (BuildConfig.DEBUG)
					Log.d(TAG, "mWifiManager.startScan()");
				mRetry = 0;
			} else if (++mRetry >= 3) {
				mRetry = 0;
				Util.showToast(getActivity(),
						getString(R.string.wifi_fail_to_scan));
				return;
			}
			sendEmptyMessageDelayed(0, WIFI_RESCAN_INTERVAL_MS);
		}
	}

	private void updateWifiState(int state) {
		if (BuildConfig.DEBUG)
			Log.d(TAG, "WIFI_STATE ====>" + state);
		switch (state) {
		case WifiManager.WIFI_STATE_ENABLED:
			mScanner.resume();
			updateWifiLayout(View.VISIBLE);
			return; // not break, to avoid the call to pause() below

			// case WifiManager.WIFI_STATE_ENABLING:
			// addMessagePreference(R.string.wifi_starting);
			// break;

		case WifiManager.WIFI_STATE_DISABLED:
			updateWifiLayout(View.GONE);
			break;
		}

		mLastInfo = null;
		mLastState = null;
		mScanner.pause();
	}
	//set the list height 
	private void autoSetListViewHeight(ListView listView) {  
		int totalHeight = 0;  
		int itemCount   = 0;
		ListAdapter listAdapter = listView.getAdapter(); 
		if (listAdapter == null) {  
			return;  
		}  
		itemCount = listAdapter.getCount() > 6 ? 6 : listAdapter.getCount();//if count > 6 then count = 6
		ViewGroup.LayoutParams params = listView.getLayoutParams(); 
		if(itemCount==6)
		{
			params.height = 294;
		}
		else
		{
			for (int i = 0; i < itemCount; i++) {  
				View listItem = listAdapter.getView(i, null, listView);  
				if(listItem!=null)
				{
					listItem.measure(0, 0);  
					totalHeight += listItem.getMeasuredHeight(); 
				}
			} 
			params.height = totalHeight
					+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));  
		}
		listView.setLayoutParams(params);  
		//			mLayoutWifiList.requestLayout();
		mLayoutWifiList.invalidate();
	}
	//
}
