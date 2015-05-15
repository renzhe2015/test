package com.tsb.settings.fragment.menu.net;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.TvManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ethernet.EthernetDevInfo;
import android.net.ethernet.EthernetManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.tsb.settings.R;
import com.tsb.settings.fragment.MainMenuFragment;
import com.tsb.settings.settings.view.IPEdittext;
import com.tsb.settings.util.Util;



public class EthernetManualFragment extends BaseNetMenuFragment implements 
View.OnClickListener {
	private static final String TAG = "ManualEthernetActivity===>";

	private IPEdittext etIp, etSubnetMask, etGateway, etDns;

	private Button btConfirm;

	private TextView tvMac;
	private static int  EVENT_INTERFACE_CONFIGUREATION_SUCCEEDED = 1;
	private static int  EVENT_INTERFACE_CONFIGUREATION_FAILED = 2;
	private boolean isFirstIntent = true;
	private String[] mSettingIds;
	private TvManager mTv;
	private EthernetManager mEthManager;
	private EthernetDevInfo mEthInfo;
	private WifiManager mWifiManager;
	private EthernetImpl mEthernetImpl;
	private final BroadcastReceiver mEthStateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			Log.d(TAG, "onReceive==>" + intent.getAction());
			if (intent.getAction().equals(
					EthernetManager.ETHERNET_STATE_CHANGED_ACTION)) {
				if (isFirstIntent) {
					isFirstIntent = false;
					return;
				}
				HandleEthStateChanged(intent);
			} else if (intent.getAction().equals(
					NetworkFragment.ETHERNET_INTERFACE_CONF_CHANGED)) {
				HandleEthStateChanged(intent);
			}
		}
	};

	protected boolean response() {
		Log.i(TAG,"response return true");
		if (etIp.isCursorAtFirst() || etSubnetMask.isCursorAtFirst()
				|| etDns.isCursorAtFirst() || etGateway.isCursorAtFirst() || btConfirm.hasFocus()) {
			return true;
		}
		return false;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.ethernet_manual_layout, container,
				false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mView=view;
		mEthManager = (EthernetManager) getActivity().getSystemService(Context.ETHERNET_SERVICE);
		mTv = (TvManager) getActivity().getSystemService("tv");
		mWifiManager = (WifiManager)getActivity().getSystemService(Context.WIFI_SERVICE);
		mEthernetImpl = EthernetImpl.getInstance(mEthManager,mTv);	

		tvMac = (TextView) mView.findViewById(R.id.tv_eth_manual_mac);

		etIp = (IPEdittext) mView.findViewById(R.id.eth_manuel_ip);
		etSubnetMask = (IPEdittext) mView.findViewById(R.id.eth_manuel_mask);
		etGateway = (IPEdittext) mView.findViewById(R.id.eth_manuel_gateway);
		etDns = (IPEdittext) mView.findViewById(R.id.eth_manuel_dns);
		btConfirm = (Button) mView.findViewById(R.id.btn_save_ip_config);
		btConfirm.setOnKeyListener(this);
		btConfirm.setOnClickListener(EthernetManualFragment.this);
		IntentFilter mIntentFilter = new IntentFilter(
				EthernetManager.ETHERNET_STATE_CHANGED_ACTION);
		mIntentFilter.addAction(NetworkFragment.ETHERNET_INTERFACE_CONF_CHANGED);

		try {
			getActivity().registerReceiver(mEthStateReceiver, mIntentFilter);
		} catch (Exception e) {

		}
		mSettingIds = new String[] { getString(R.string.tcl_ip_address),
				getString(R.string.tcl_subnet_mask),
				getString(R.string.tcl_gateway),
				getString(R.string.tcl_dns_address) };								
		updateView();

		super.onViewCreated(view, savedInstanceState);
	}
	
	private void updateView() {
		// TODO Auto-generated method stub
		if (mEthernetImpl.mEthManager.isConfigured()) {
			mEthernetImpl.updateEthernetDevInfo();
		}

		tvMac.setText(mEthernetImpl.getMac());
		etIp.setText(mEthernetImpl.getIp());
		etSubnetMask.setText(mEthernetImpl.getSubnetMask());
		etGateway.setText(mEthernetImpl.getGetway());
		etDns.setText(mEthernetImpl.getDns());
		etIp.requestFocus();
	}

	@Override
	public void onPause() {
		if (mEthStateReceiver == null)
			return;
		try {
			getActivity().unregisterReceiver(mEthStateReceiver);
		} catch (Exception e) {

		}
		super.onPause();
	}

	public void onDestroy() {
		Log.i(TAG, "onDestroy()");


		super.onDestroy();

	}
	@Override
	public void onClick(View view) {		
		switch(view.getId()) {
		case R.id.btn_save_ip_config:
			confirmFun();
			break;
		}
	}
	private boolean confirmFun() {		

		Log.d(TAG, "IPAddress=" + etIp.getText().toString());

		String strIp = null, strGateway = null, strNetmask = null, strDNS1 = null;


		if (mEthernetImpl.isIpAddress(etIp.getText().toString())) {
			strIp = etIp.getText().toString().trim();
		} else {
			showToast(0);
			return false;
		}

		if (mEthernetImpl.isSubnetMask(etSubnetMask.getText().toString())) {
			strNetmask = etSubnetMask.getText().toString().trim();
		} else {
			showToast(1);
			return false;
		}

		if (mEthernetImpl.isIpAddress(etGateway.getText().toString())) {
			strGateway = etGateway.getText().toString().trim();
		} else {
			showToast(2);
			return false;
		}

		if (mEthernetImpl.isIpAddress(etDns.getText().toString())
				|| etDns.getText().toString().equals("")) {
			strDNS1 = etDns.getText().toString().trim();
		} else {
			showToast(3);
			return false;
		}
		mEthernetImpl.configEthernet(strIp, strNetmask, strGateway, strDNS1);
		MainMenuFragment.fromNetSetting=false;
		FragmentManager fm = MainMenuFragment.fm;
		FragmentTransaction ft = fm.beginTransaction();
		ft.detach(this);
		ft.commit();
		mView.getRootView().findViewById(R.id.layout_third_menu).setVisibility(View.GONE);
		mView.getRootView().findViewById(MainMenuFragment.mLastSelectionId2).requestFocus();
		return true;

	}




	private void showToast(int i) {
		// TODO Auto-generated method stub
		String info = Util.replaceBlank(mSettingIds[i])
				+ getString(R.string.ip_settings_invalid_address);

		Util.showToast(getActivity(), info);
	}



	private void HandleEthStateChanged(Intent intent) {
		/*	mEthernetImpl.isConfiguring = false;
		boolean isConfigured = false;//mEthernetImpl.getWiredNetworkStates(this);

		isConfigured = intent.getBooleanExtra(EthernetManager.EXTRA_ETHERNET_STATE,false);

		Log.i(TAG, "HandleEthStateChanged isConfigured:" + isConfigured);
		int state = intent.getIntExtra(EthernetManager.EXTRA_ETHERNET_STATE, -1);
		Log.i(TAG, "HandleEthStateChanged state:" + state);

		if (state == EVENT_INTERFACE_CONFIGUREATION_SUCCEEDED || isConfigured) {
			TCLToast toast = TCLToast.makePrompt(EthernetManualActivity.this, R.string.ip_config_success, TCLToast.INFO_IMAGE, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			updateView();
		} else if (state == EVENT_INTERFACE_CONFIGUREATION_FAILED || !isConfigured) {
			TCLToast toast = TCLToast.makePrompt(EthernetManualActivity.this, R.string.ip_config_failed, TCLToast.INFO_IMAGE, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}	*/	
	}
}
