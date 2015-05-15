/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tsb.settings.fragment.menu.net;


import com.tsb.settings.BuildConfig;
import com.tsb.settings.R;
import com.tsb.settings.TvClientTypeList;
import com.tsb.settings.TvManagerHelper;
import com.tsb.settings.fragment.BaseFragment;
import com.tsb.settings.fragment.MainMenuFragment;
import com.tsb.settings.settings.view.LayoutItem;
import com.tsb.settings.settings.view.TaskTimer;
import com.tsb.settings.util.FragmentUtils;
import com.tsb.settings.util.Util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.LinkProperties;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.IpAssignment;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiConfiguration.ProxySettings;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.security.Credentials;
import android.security.KeyStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;



public class WifiDialog extends Fragment implements View.OnClickListener,OnKeyListener,
TextWatcher, AdapterView.OnItemClickListener, CompoundButton.OnCheckedChangeListener{
	private static final String KEYSTORE_SPACE = "keystore://";
	private final String TAG = "WifiDialog";
	private WifiSettingsImpl mWifiSettings;
	private IntentFilter mFilter;
	private boolean edit = false;
	private  AccessPoint mAccessPoint;

	private TextView mSsid;
	private int mSecurity;
	private TextView mPassword;
	private Switch mShowPassword;
	private int mWepTxKeyidx;

	private LayoutItem layoutWifiStatus;
	private LayoutItem layoutWifiSecurity;
	private LayoutItem layoutWifiIpAddress;
	private LayoutItem layoutWifiMacAddress;

	private LayoutItem layoutEapMethod;
	private LayoutItem layoutEapCaCert;
	private LayoutItem layoutPhase2;
	private LayoutItem layoutEapUserCert;

	private LayoutItem layoutWepKey;

	private LayoutItem layoutSecurity;

	private TextView mEapIdentity;
	private TextView mEapAnonymous;


	private Button btSubmit = null;
	private Button btForget= null;

	private LinearLayout llButtonLayout;
	private String clientType;
	private IpAssignment mIpAssignment = IpAssignment.UNASSIGNED;
	private ProxySettings mProxySettings = ProxySettings.UNASSIGNED;
	@SuppressLint("NewApi")
	private LinkProperties mLinkProperties = new LinkProperties();
	private View mView;
	static boolean requireKeyStore(WifiConfiguration config) {
		if (config == null) {
			return false;
		}
		return false;
	}

	WifiConfiguration getConfig() {
		if (mAccessPoint != null && mAccessPoint.networkId != -1 && !edit) {
			return null;
		}
		WifiConfiguration config = new WifiConfiguration();

		if (mAccessPoint == null) {
			config.SSID = AccessPoint.convertToQuotedString(
					mSsid.getText().toString());
			// If the user adds a network manually, assume that it is hidden.
			config.hiddenSSID = true;
		} else if (mAccessPoint.networkId == -1) {
			config.SSID = AccessPoint.convertToQuotedString(
					mAccessPoint.ssid);
		} else {
			config.networkId = mAccessPoint.networkId;
		}

		if (mAccessPoint != null) {
			config.BSSID = mAccessPoint.bssid;
		}

		switch (mSecurity) {
		case AccessPoint.SECURITY_NONE:
			config.allowedKeyManagement.set(KeyMgmt.NONE);
			break;

		case AccessPoint.SECURITY_WEP:
			config.allowedKeyManagement.set(KeyMgmt.NONE);
			config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
			config.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);
			if (mPassword.length() != 0) {
				int length = mPassword.length();
				String password = mPassword.getText().toString();
				// WEP-40, WEP-104, and 256-bit WEP (WEP-232?)
				if ((length == 10 || length == 26 || length == 58) &&
						password.matches("[0-9A-Fa-f]*")) {
					config.wepKeys[mWepTxKeyidx] = password;
				} else {
					config.wepKeys[mWepTxKeyidx] = '"' + password + '"';
				}
				config.wepTxKeyIndex = mWepTxKeyidx;
			}
			break;

		case AccessPoint.SECURITY_PSK:
			config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
			if (mPassword.length() != 0) {
				String password = mPassword.getText().toString();
				if (password.matches("[0-9A-Fa-f]{64}")) {
					config.preSharedKey = password;
				} else {
					config.preSharedKey = '"' + password + '"';
				}
			}
			break;

		case AccessPoint.SECURITY_EAP:
			config.allowedKeyManagement.set(KeyMgmt.WPA_EAP);
			config.allowedKeyManagement.set(KeyMgmt.IEEE8021X);
			break;
		default:
			return null;	
		}

		config.proxySettings = mProxySettings;
		config.ipAssignment = mIpAssignment;
		config.linkProperties = new LinkProperties(mLinkProperties);

		return config;

	}


	@Override
	public void onResume() {
		getActivity().registerReceiver(mReceiver, mFilter);
		super.onResume();
	}

	@Override
	public void onPause() {
		getActivity().unregisterReceiver(mReceiver);
		super.onPause();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		return inflater.inflate(R.layout.wifi_dialog, container,
				false);
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d("WifiDialog", "onCreate===============================>");
		Bundle args = getArguments();
		if (args != null) {
			edit=args.getInt("edit")==0?false:true;
		}
		clientType=TvManagerHelper.getInstance(getActivity()).mTvManager.getClientType();
		Log.d("WifiDialog", "onCreate===============================>edit:" + edit);

		mWifiSettings = WifiSettingsImpl.getInstance(getActivity());
		if (edit) {
			mAccessPoint = null;
		} else {
			mAccessPoint = mWifiSettings.getSelectedAp();
		}
		mSecurity = (mAccessPoint == null) ? AccessPoint.SECURITY_NONE : mAccessPoint.security;
		Log.d("WifiDialog", "mAccessPoint===============================>" + mAccessPoint);

		mFilter = new IntentFilter();
		mFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		mFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		mFilter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
		mFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
		mFilter.addAction(WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION);
		mFilter.addAction(WifiManager.LINK_CONFIGURATION_CHANGED_ACTION);
		mFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		mFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
		super.onCreate(savedInstanceState);
	}
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub    	
		mView=view;   

		layoutWifiStatus  = (LayoutItem) mView.findViewById(R.id.layout_wifistatus);
		layoutWifiStatus.setTitle(R.string.wifi_status);
		if(clientType!=null&&clientType.equals(TvClientTypeList.RowaClient_82))
			layoutWifiStatus.setTextValueWidth(281);
		else
			layoutWifiStatus.setTextValueWidth(420);
		layoutWifiStatus.needPopWindow(false);
		layoutWifiStatus.needRightArraw(false);

		layoutWifiSecurity  = (LayoutItem) mView.findViewById(R.id.layout_wifi_security);
		layoutWifiSecurity.setTitle(R.string.wifi_security);
		if(clientType!=null&&clientType.equals(TvClientTypeList.RowaClient_82))
			layoutWifiSecurity.setTextValueWidth(281);
		else
			layoutWifiSecurity.setTextValueWidth(420);
		layoutWifiSecurity.needPopWindow(false);
		layoutWifiSecurity.needRightArraw(false);

		layoutWifiIpAddress  = (LayoutItem) mView.findViewById(R.id.layout_wifi_ip_address);
		layoutWifiIpAddress.setTitle(R.string.wifi_ip_address);
		if(clientType!=null&&clientType.equals(TvClientTypeList.RowaClient_82))
			layoutWifiIpAddress.setTextValueWidth(281);
		else
			layoutWifiIpAddress.setTextValueWidth(420);
		layoutWifiIpAddress.needPopWindow(false);
		layoutWifiIpAddress.needRightArraw(false);
		layoutWifiIpAddress.setVisibility(View.GONE);
		//add mac address fix question 0002532
		layoutWifiMacAddress  = (LayoutItem) mView.findViewById(R.id.layout_wifi_mac_address);
		layoutWifiMacAddress.setTitle(R.string.wifi_mac_address);
		if(clientType!=null&&clientType.equals(TvClientTypeList.RowaClient_82))
			layoutWifiMacAddress.setTextValueWidth(281);
		else
			layoutWifiMacAddress.setTextValueWidth(420);
		layoutWifiMacAddress.needPopWindow(false);
		layoutWifiMacAddress.needRightArraw(false); 
		layoutWifiMacAddress.setVisibility(View.GONE);
		//
		llButtonLayout = (LinearLayout)mView.findViewById(R.id.ll_button_type1);
		llButtonLayout.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean hasfocus) {
				Log.d("WifiDialog", "onFocusChange=" + hasfocus);
				if (hasfocus) {
					Log.d("WifiDialog", "btSubmit.isEnabled()=" + btSubmit.isEnabled());
					if (btSubmit == null)
						btSubmit = (Button)mView.findViewById(R.id.btn_connect);
					if (btSubmit != null && btSubmit.isEnabled()) {
						btSubmit.requestFocus();
					} 
				}

			}
		});


		if (mAccessPoint == null) {
			Log.d("WifiDialog", "mAccessPoint == null" );
			TextView tvTitle = (TextView)mView.findViewById(R.id.tv_alertdialog_title);        	
			tvTitle.setText(R.string.wifi_add_network);

			mView.findViewById(R.id.type).setVisibility(View.VISIBLE);
			mSsid = (TextView) mView.findViewById(R.id.ssid);
			mSsid.setOnKeyListener(this);
			Util.setEditTextEnd((EditText)mSsid);
			mSsid.addTextChangedListener(this);
			mSsid.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					// TODO Auto-generated method stub
					if (!hasFocus) {
						Log.d("WifiDialog", "mSsid.onFocusChange():validate()");
						validate();
					}
				}
			});

			layoutSecurity = ((LayoutItem) mView.findViewById(R.id.security));  
			layoutSecurity.setOnKeyListener(this);
			layoutSecurity.initListView(getResources().getStringArray(R.array.wifi_security), 0, listenerSecurityClick);
			layoutSecurity.setTitle(R.string.wifi_security);
			if(clientType!=null&&clientType.equals(TvClientTypeList.RowaClient_82))
				layoutSecurity.setTextValueWidth(201);
			else
				layoutSecurity.setTextValueWidth(300);
			LinearLayout ll = (LinearLayout)mView.findViewById(R.id.ll_button_type2);
			ll.setVisibility(View.VISIBLE);

			btSubmit = (Button)mView.findViewById(R.id.btn_save_connect);
			btSubmit.setText(R.string.wifi_save);            
			btSubmit.setOnClickListener(mListener);  
			mSsid.requestFocus();
		} else {
			Log.d("WifiDialog", "mAccessPoint != null" );
			TextView tvTitle = (TextView)mView.findViewById(R.id.tv_alertdialog_title);        	
			tvTitle.setText(mAccessPoint.ssid);

			LinearLayout infolayout = (LinearLayout)mView.findViewById(R.id.infolayout);
			infolayout.setVisibility(View.VISIBLE);
			ViewGroup group = (ViewGroup) mView.findViewById(R.id.info);


			DetailedState state = mAccessPoint.getState();
			if (state != null) {
				//addRow(group, R.string.wifi_status, Summary.get(this, state));
				layoutWifiStatus.setVisibility(View.VISIBLE);
				layoutWifiStatus.setTextValue(Summary.get(getActivity(), state));
			}

			String[] type = getResources().getStringArray(R.array.wifi_security);
			if (mAccessPoint.security == 3) {
				layoutWifiSecurity.setVisibility(View.VISIBLE);
				layoutWifiSecurity.setTextValue(this.getString(R.string.wifi_security_eap));
				//addRow(group, R.string.wifi_security, this.getString(R.string.wifi_security_eap));
			} else {
				//addRow(group, R.string.wifi_security, type[mAccessPoint.security]);
				layoutWifiSecurity.setVisibility(View.VISIBLE);
				layoutWifiSecurity.setTextValue( type[mAccessPoint.security]);
			}

			int level = mAccessPoint.getLevel();


			WifiInfo info = mAccessPoint.getInfo();
			if (info != null) {
				// TODO: fix the ip address for IPv6.
				int address = info.getIpAddress();
				if (address != 0) {
					// addRow(group, R.string.wifi_ip_address, Formatter.formatIpAddress(address));
					layoutWifiIpAddress.setVisibility(View.VISIBLE);
					layoutWifiIpAddress.setTextValue( Formatter.formatIpAddress(address));
				}

				//add mac address fix question 0002532
				String mac = info.getMacAddress();
				if(mac!=null && !mac.equals(""))
				{
					layoutWifiMacAddress.setVisibility(View.VISIBLE); 
					layoutWifiMacAddress.setTextValue(mac);  
				}
				//

				//if state is null , use supplicant state as wifi status fix question 0002532
				if (state == null && info.getSupplicantState()!=null) {
					switch(info.getSupplicantState())
					{
					case COMPLETED:
						layoutWifiStatus.setVisibility(View.VISIBLE);
						layoutWifiStatus.setTextValue(getResources().getStringArray(R.array.wifi_status)[5]);	
						break;
					case DISCONNECTED:
						layoutWifiStatus.setVisibility(View.VISIBLE);
						layoutWifiStatus.setTextValue(getResources().getStringArray(R.array.wifi_status)[8]);
						break;
					}
				}
				//
			}

			if (mAccessPoint.networkId == -1 || edit) {
				showSecurityFields();
			}

			if (edit) {
				LinearLayout ll = (LinearLayout)mView.findViewById(R.id.ll_button_type2);
				ll.setVisibility(View.VISIBLE);

				btSubmit = (Button)mView.findViewById(R.id.btn_save_connect);
				btSubmit.setText(R.string.wifi_save);
				btSubmit.setOnClickListener(mListener);
				btSubmit.requestFocus();
			} else {

				boolean isConnect = false;
				boolean isForget = false;
				if (state == null && level != -1) {                    
					isConnect = true;
				}

				if (mAccessPoint.networkId != -1) {
					isForget = true;
				}
				//根据链接状态，展示不同形态
				if(isConnect){
					if(isForget){

						LinearLayout ll = (LinearLayout)mView.findViewById(R.id.ll_button_type1);
						ll.setVisibility(View.VISIBLE);

						btSubmit = (Button)mView.findViewById(R.id.btn_connect);
						btSubmit.setText(R.string.wifi_connect);
						btSubmit.setOnClickListener(mListener);

						btForget = (Button)mView.findViewById(R.id.btn_forget);
						btForget.setText(R.string.wifi_forget);
						btForget.setOnClickListener(mListener);

						btSubmit.requestFocus();
					}
					else{
						LinearLayout ll = (LinearLayout)mView.findViewById(R.id.ll_button_type2);
						ll.setVisibility(View.VISIBLE);

						btSubmit = (Button)mView.findViewById(R.id.btn_save_connect);
						btSubmit.setText(R.string.wifi_save);
						btSubmit.setOnClickListener(mListener);


						if (mAccessPoint.security == 0) {
							btSubmit.requestFocus();
						}
					}                    
				}
				else{
					if(isForget){
						LinearLayout ll = (LinearLayout)mView.findViewById(R.id.ll_button_type1);
						ll.setVisibility(View.VISIBLE);

						btForget = (Button)mView.findViewById(R.id.btn_forget);
						btForget.setText(R.string.wifi_forget);
						btForget.setOnClickListener(mListener);

						Button btnConnect = (Button)mView.findViewById(R.id.btn_connect);
						btnConnect.setText(R.string.wifi_connect);
						btnConnect.setEnabled(false);
						btnConnect.setFocusable(false);

						btForget.requestFocus();
					}
				}                
			}
		}       
		if(btForget!=null) btForget.setOnKeyListener(this);
		if(btSubmit != null){
			validate();
		}
		super.onViewCreated(view, savedInstanceState);

	}

	/*    private void addRow(ViewGroup group, int name, String value) {
        View row = getLayoutInflater().inflate(R.layout.wifi_dialog_row, group, false);
        ((TextView) row.findViewById(R.id.name)).setText(name);
        ((TextView) row.findViewById(R.id.value)).setText(value);
        group.addView(row);
    }*/


	private void validate() {
		// TODO: make sure this is complete.
		btSubmit.setOnKeyListener(this);
		if ((mSsid != null && mSsid.length() == 0) ||
				((mAccessPoint == null || mAccessPoint.networkId == -1) &&
						((mSecurity == AccessPoint.SECURITY_WEP && mPassword.length() == 0) ||
								(mSecurity == AccessPoint.SECURITY_PSK && (mPassword.length() < 8 || mPassword.length() > 63))))) {
			//getButton(BUTTON_SUBMIT).setEnabled(false);
			btSubmit.setEnabled(false);
			btSubmit.setFocusable(false);
			//            btSubmit.setBackgroundResource(R.drawable.tcl_button_gray);
		} else if (mSecurity == AccessPoint.SECURITY_WEP 
				&& (mAccessPoint == null || mAccessPoint.networkId == -1)) {
			String password = mPassword.getText().toString();
			if (mPassword.length() != 5 && mPassword.length() != 10
					&& mPassword.length() != 13 && mPassword.length() != 26) {  			
				btSubmit.setEnabled(false);
				btSubmit.setFocusable(false);
			} else if ((mPassword.length() == 10 || mPassword.length() == 26) &&
					!password.matches("[0-9A-Fa-f]*")) {
				Log.d("aaaaaaaaaaaaaaaaaaa======>", "mPassword.toString()" + password);
				btSubmit.setEnabled(false);
				btSubmit.setFocusable(false);
			}
			else {
				btSubmit.setEnabled(true);
				btSubmit.setFocusable(true);
			}
		}  else {
			//getButton(BUTTON_SUBMIT).setEnabled(true);
			btSubmit.setEnabled(true);
			btSubmit.setFocusable(true);
			//            btSubmit.setBackgroundResource(R.drawable.tcl_button_normal);
		}
	}

	public void onClick(View view) {
		mPassword.setInputType(
				InputType.TYPE_CLASS_TEXT | (((Switch) view).isChecked() ?
						InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
							InputType.TYPE_TEXT_VARIATION_PASSWORD));
	}

	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		Log.d(TAG, "onCheckedChanged:" + isChecked);
		//Do nothing if called as a result of a state machine event
		mPassword.setInputType(
				InputType.TYPE_CLASS_TEXT | (isChecked ?
						InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
							InputType.TYPE_TEXT_VARIATION_PASSWORD));
		if(isChecked){
			mShowPassword.setTrackResource(R.drawable.switch_on);
		}
		else{
			mShowPassword.setTrackResource(R.drawable.switch_off);
		}
	}

	public void onTextChanged(CharSequence s, int start, int before, int count) {
		TaskTimer.onTimer(getActivity());
	}

	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	public void afterTextChanged(Editable editable) {
		TaskTimer.onTimer(getActivity());
		validate();
	}



	private void showSecurityFields() {
		if (mSecurity == AccessPoint.SECURITY_NONE) {
			mView.findViewById(R.id.fields).setVisibility(View.GONE);
			return;
		}
		mView.findViewById(R.id.fields).setVisibility(View.VISIBLE);

		if (mPassword == null) {
			mPassword = (TextView) mView.findViewById(R.id.password);
			mPassword.setOnKeyListener(this);
			mPassword.addTextChangedListener(this);
			mPassword.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					// TODO Auto-generated method stub
					if (!hasFocus) {
						Log.d("WifiDialog", "mSsid.onFocusChange():validate()");
						if (mSecurity == AccessPoint.SECURITY_WEP 
								&& (mAccessPoint == null || mAccessPoint.networkId == -1)) {
							String password = mPassword.getText().toString();
							if (mPassword.length() != 5 && mPassword.length() != 10
									&& mPassword.length() != 13 && mPassword.length() != 26) {
								Util.showToast(getActivity(), getString(R.string.wep_password_length_hint));
							} else if ((mPassword.length() == 10 || mPassword.length() == 26) &&
									!password.matches("[0-9A-Fa-f]*")) {
								Util.showToast(getActivity(), getString(R.string.wep_password_word_hint));

							}
						}
					}
				}
			});



			mShowPassword= (Switch)mView.findViewById(R.id.show_password);					
			mShowPassword.setOnKeyListener(this);
			mShowPassword.setText(R.string.wifi_show_password);
			mShowPassword.setTextSize(TypedValue.COMPLEX_UNIT_PX, 22);
			mShowPassword.setOnCheckedChangeListener(this);
			mPassword.setHint("");
			Util.setEditTextEnd((EditText)mPassword);
			if (mAccessPoint != null && mAccessPoint.networkId != -1) {
				mPassword.setHint(R.string.wifi_unchanged);
			}
		}

		if (mSsid == null) {
			mPassword.requestFocus();
		}

		if (mSecurity == AccessPoint.SECURITY_PSK) {
			mPassword.setHint(R.string.wifi_password_length_hint);
		}

		if (mSecurity == AccessPoint.SECURITY_WEP) {
			mView.findViewById(R.id.linear_keychange).setVisibility(View.VISIBLE);
			mWepTxKeyidx=0;
			layoutWepKey = (LayoutItem) mView.findViewById(R.id.wepkeycontent);
			layoutWepKey.setOnKeyListener(this);
			layoutWepKey.setTitle(R.string.wep_key_change);
			layoutWepKey.initListView(getResources().getStringArray(R.array.wifi_wep_keys), mWepTxKeyidx, WifiDialog.this);
			layoutWepKey.setTextValue(getResources().getStringArray(R.array.wifi_wep_keys)[mWepTxKeyidx]);

			layoutWepKey.setNextFocusDownId(R.id.btn_save_connect);
			mPassword.setHint("");

		} else {
			mView.findViewById(R.id.linear_keychange).setVisibility(View.GONE);
		}

		if (mSecurity != AccessPoint.SECURITY_EAP) {
			mView.findViewById(R.id.eap).setVisibility(View.GONE);
			return;
		}
		mView.findViewById(R.id.eap).setVisibility(View.VISIBLE);

		if (layoutEapMethod == null) {
			layoutEapMethod = (LayoutItem) mView.findViewById(R.id.layout_wifi_eap_method);
			layoutEapMethod.setTitle(R.string.wifi_eap_method);
			layoutEapMethod.setTextValueWidth(201);

			layoutPhase2 = (LayoutItem) mView.findViewById(R.id.phase2);
			layoutPhase2.setTitle(R.string.please_select_phase2);
			layoutPhase2.setTextValueWidth(201);

			layoutEapCaCert = (LayoutItem) mView.findViewById(R.id.ca_cert);
			layoutEapCaCert.setTitle(R.string.wifi_eap_ca_cert);
			layoutEapCaCert.setTextValueWidth(201);

			layoutEapUserCert = (LayoutItem) mView.findViewById(R.id.user_cert);
			layoutEapUserCert.setTitle(R.string.wifi_eap_user_cert);
			layoutEapUserCert.setTextValueWidth(201);

			mEapIdentity = (TextView) mView.findViewById(R.id.identity);
			mEapAnonymous = (TextView) mView.findViewById(R.id.anonymous);
			mEapIdentity.addTextChangedListener(this);
			mEapAnonymous.addTextChangedListener(this);

			layoutEapMethod.initListView(this.getResources()
					.getStringArray(R.array.wifi_eap_method), 0, null);
			layoutPhase2.initListView(this.getResources()
					.getStringArray(R.array.wifi_phase2_entries), 0, null);

			loadCertificates(layoutEapCaCert, Credentials.CA_CERTIFICATE);
			loadCertificates(layoutEapUserCert, Credentials.USER_PRIVATE_KEY);

			//            if (mAccessPoint != null && mAccessPoint.networkId != -1) {
			//                WifiConfiguration config = mAccessPoint.getConfig();
			//                setSelection(layoutEapMethod, config.eap.value());
			//                setSelection(layoutPhase2, config.phase2.value());
			//                setCertificate(layoutEapCaCert, Credentials.CA_CERTIFICATE,
			//                        config.ca_cert.value());
			//                setCertificate(layoutEapUserCert, Credentials.USER_PRIVATE_KEY,
			//                		config.key_id.value());
			//                mEapIdentity.setText(config.identity.value());
			//                mEapAnonymous.setText(config.anonymous_identity.value());
			//            }
		}
	}

	private void loadCertificates(LayoutItem layoutitem, String prefix) {
		String[] certs = KeyStore.getInstance().saw(prefix);
		Context context = getActivity();
		String unspecified = context.getString(R.string.wifi_unspecified);

		if (certs == null || certs.length == 0) {
			certs = new String[] {unspecified};
		} else {
			String[] array = new String[certs.length + 1];
			array[0] = unspecified;
			System.arraycopy(certs, 0, array, 1, certs.length);
			certs = array;
		}

		layoutitem.initListView(certs, 0, null);
	}

	private void setCertificate(LayoutItem layout, String prefix, String cert) {
		prefix = KEYSTORE_SPACE + prefix;
		if (cert != null && cert.startsWith(prefix)) {
			// setSelection(spinner, cert.substring(prefix.length()));
			layout.setTextValue(cert.substring(prefix.length()));
		}
	}

	private void setSelection(LayoutItem layout, String value) {
		if (value != null) {
			/*ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
            for (int i = adapter.getCount() - 1; i >= 0; --i) {
                if (value.equals(adapter.getItem(i))) {
                    spinner.setSelection(i);
                    break;
                }
            }*/
			layout.setTextValue(value);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {   	
		if (position < 4 && position >=0 ) {
			layoutWepKey.getPopView().dismiss();
			mWepTxKeyidx = position;
			layoutWepKey.setTextValue(getResources().getStringArray(R.array.wifi_wep_keys)[mWepTxKeyidx]);
		}   	
	}

	private AdapterView.OnItemClickListener listenerSecurityClick = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			layoutSecurity.setTextValue(layoutSecurity.getList()[position]);
			mSecurity = position;
			showSecurityFields();
			validate();
			layoutSecurity.getPopView().dismiss();
		}
	};


	private  View.OnClickListener mListener = new View.OnClickListener() {
		public void onClick(View v) {
			FragmentUtils.mHandler.removeMessages(0);
			FragmentUtils.mHandler.sendEmptyMessageDelayed(0, 15000);
			switch(v.getId()) {
			case R.id.btn_forget:
				if (mAccessPoint != null) {
					mWifiSettings.forget();
				}
				break;
			case R.id.btn_connect:
			case R.id.btn_save_connect:
				Log.d(TAG, "==========>onClick() bt_submit 0");
				WifiConfiguration config = getConfig();
				if (config == null) {
					Log.d(TAG, "==========>onClick() bt_submit 1: config == null");
					if (mWifiSettings.getSelectedAp() != null
							&& mWifiSettings.getSelectedAp().networkId != -1) {
						mWifiSettings.connect(mWifiSettings.getSelectedAp().networkId);
						//						showWaitDialog();

					}
				} else if (config.networkId != -1) {
					Log.d(TAG, "==========>onClick() bt_submit 2: config.networkId != -1");
					if (mWifiSettings.getSelectedAp() != null) {
						mWifiSettings.getWifiManager().save(config, null);
					}
				} else {
					int networkId = mWifiSettings.getWifiManager().addNetwork(config);
					Log.d(TAG, "==========>onClick() bt_submit 3: config.networkId == " + networkId);
					if (networkId != -1) {
						Log.d(TAG, "enableNetwork() networkId = " + networkId);						
						config.networkId = networkId;
						if (edit) {
							mWifiSettings.getWifiManager().save(config, null);

							mWifiSettings.connect(networkId);
							//							showWaitDialog();


						} else {
							mWifiSettings.connect(networkId);
							//							showWaitDialog();

						}
					}
				}
				break;
			}
			MainMenuFragment.fromNetSetting=false;
			FragmentManager fm = MainMenuFragment.fm;
			FragmentTransaction ft = fm.beginTransaction();
			ft.detach(WifiDialog.this);
			ft.commit();
			mView.getRootView().findViewById(R.id.layout_third_menu).setVisibility(View.GONE);
			mView.getRootView().findViewById(MainMenuFragment.mLastSelectionId2).requestFocus();
		};
	};



	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(BuildConfig.DEBUG)Log.d(TAG , "WifiDialog_onReceive===>" + intent.getAction());
			handleEvent(context, intent);
		}
	};

	private void handleEvent(Context context, Intent intent) {
		String action = intent.getAction();
		if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {
			if (!mWifiSettings.mConnected.get()) {
				updateConnectionState(WifiInfo
						.getDetailedStateOf((SupplicantState) intent
								.getParcelableExtra(WifiManager.EXTRA_NEW_STATE)));
			}

			Bundle bundle = intent.getExtras();
			int EXTRA_SUPPLICANT_ERROR = bundle
					.getInt(WifiManager.EXTRA_SUPPLICANT_ERROR);
			if(BuildConfig.DEBUG)Log.d(TAG, "WifiManager.SUPPLICANT_STATE_CHANGED_ACTION:EXTRA_SUPPLICANT_ERROR==" + EXTRA_SUPPLICANT_ERROR);
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

		} else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
			NetworkInfo info = (NetworkInfo) intent
					.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			mWifiSettings.mConnected.set(info.isConnected());
			updateConnectionState(info.getDetailedState());
		} 
	}



	/*private TCLDLabel mConnectWaitDialog;
	private void showWaitDialog() {
		if (mConnectWaitDialog == null) {
			mConnectWaitDialog = TCLDLabel.makeTCLDLabel(MainActivity.mContext);
		}
		mConnectWaitDialog.show();
		TaskTimer.cancel();
	}

	private void dismissWaitDialog() {
		if (mConnectWaitDialog != null && mConnectWaitDialog.isShowing()) {
			mConnectWaitDialog.dismiss();
		}
		TaskTimer.onTimer(this);
	}*/

	static String removeDoubleQuotes(String string) {
		int length = string.length();
		if ((length > 1) && (string.charAt(0) == '"')
				&& (string.charAt(length - 1) == '"')) {
			return string.substring(1, length - 1);
		}
		return string;
	}
	//refresh the WifiDialog UI fix question 0003455
	private void updateConnectionState(DetailedState state) {	
		WifiInfo info = mWifiSettings.getWifiManager().getConnectionInfo();
		if (info == null || info.getSSID() == null || "0x".equals(removeDoubleQuotes(info.getSSID()))
				|| "".equals(removeDoubleQuotes(info.getSSID()))) {
			if(BuildConfig.DEBUG)Log.d(TAG, "updateConnectionState===>0:" + state);
			if (state != null && layoutWifiStatus != null) {
				if (layoutWifiStatus.getVisibility() == View.VISIBLE) {

					state = DetailedState.DISCONNECTED;

					layoutWifiStatus.setTextValue(Summary.get(getActivity(), state));
					return ;
				}
			}
		}

		if (info != null && info.getSSID() != null && mAccessPoint != null) {
			if (removeDoubleQuotes(info.getSSID()).equals(mAccessPoint.ssid)) {

				if (state != null && layoutWifiStatus != null) {
					if (layoutWifiStatus.getVisibility() == View.VISIBLE) {        				
						if (state == DetailedState.SCANNING){
							state = DetailedState.FAILED;
						}
						layoutWifiStatus.setTextValue(Summary.get(getActivity(), state));
					}
				}

				if(state != null && state==DetailedState.CONNECTED)
				{
					layoutWifiSecurity.setVisibility(View.VISIBLE);
					if (mAccessPoint.security == 3) {
						layoutWifiSecurity.setTextValue(this.getString(R.string.wifi_security_eap));
					} else {
						layoutWifiSecurity.setTextValue( getResources().getStringArray(R.array.wifi_security)[mAccessPoint.security]);
					}

					int address = info.getIpAddress();
					if (address != 0) {
						layoutWifiIpAddress.setVisibility(View.VISIBLE);
						layoutWifiIpAddress.setTextValue( Formatter.formatIpAddress(address));
					}

					String mac = info.getMacAddress();
					if(mac!=null && !mac.equals(""))
					{
						layoutWifiMacAddress.setVisibility(View.VISIBLE); 
						layoutWifiMacAddress.setTextValue(mac);  
					}
				}
			}
		}	
	}


	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			FragmentUtils.mHandler.removeMessages(0);
			FragmentUtils.mHandler.sendEmptyMessageDelayed(0, 15*1000); 
			switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_LEFT:
				if(!(btForget!=null && btForget.hasFocus() && btSubmit!=null && btSubmit.isEnabled()))
				{//forget button not null and have focus ,submit button not enabled ,than focus turn left

					MainMenuFragment.fromNetSetting=false;
					FragmentManager fm = MainMenuFragment.fm;
					FragmentTransaction ft = fm.beginTransaction();
					ft.detach(this);
					ft.commit();
					mView.getRootView().findViewById(R.id.layout_third_menu).setVisibility(View.GONE);
					mView.getRootView().findViewById(MainMenuFragment.mLastSelectionId2).requestFocus();
					return true;
				}

				break;
			case KeyEvent.KEYCODE_BACK:
				MainMenuFragment.fromNetSetting=false;
				FragmentManager fm = MainMenuFragment.fm;
				FragmentTransaction ft = fm.beginTransaction();
				ft.detach(this);
				ft.commit();
				mView.getRootView().findViewById(R.id.layout_third_menu).setVisibility(View.GONE);
				mView.getRootView().findViewById(MainMenuFragment.mLastSelectionId2).requestFocus();
				return true;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				//			if((btSubmit!=null&&(!btSubmit.isFocusable()||btSubmit.isFocused()))||(btForget!=null&&btForget.isFocused()))

				if((btSubmit!=null&&(!btSubmit.isFocusable()&&(mSecurity == AccessPoint.SECURITY_NONE&&layoutSecurity.hasFocus()||mSecurity==AccessPoint.SECURITY_WEP&&layoutWepKey.hasFocus()||mSecurity==AccessPoint.SECURITY_PSK&&mShowPassword.hasFocus())||btSubmit.hasFocus()))||(btForget!=null&&btForget.hasFocus()))
					return true;
				break;
			case KeyEvent.KEYCODE_DPAD_UP:
				if(edit&&mSsid!=null&&mSsid.hasFocus()||!edit&&mPassword!=null&&mPassword.hasFocus()||btForget!=null)
					return true;
				break;
			case KeyEvent.KEYCODE_DPAD_CENTER:
				if(v.getId()==R.id.password||v.getId()==R.id.ssid){
					FragmentUtils.mHandler.removeMessages(0);
					return true;
				}
			default:
				break;
			}
		}
		return false;
	}
}

