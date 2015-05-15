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

import java.util.concurrent.atomic.AtomicBoolean;

import com.tsb.settings.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View.OnKeyListener;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;


public class WifiEnabler implements CompoundButton.OnCheckedChangeListener  {
	private final String TAG = "WifiEnabler======>";
    private final Context mContext;
    private Switch mSwitch;
    private AtomicBoolean mConnected = new AtomicBoolean(false);
    private static final int MSG_SHOW_DLABEL = 222;

    private final WifiManager mWifiManager;
    private final IntentFilter mIntentFilter;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
                handleWifiStateChanged(intent.getIntExtra(
                        WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN));
            } else if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {
                if (!mConnected.get()) {
                    handleStateChanged(WifiInfo.getDetailedStateOf((SupplicantState)
                            intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE)));
                }
            } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                NetworkInfo info = (NetworkInfo) intent.getParcelableExtra(
                        WifiManager.EXTRA_NETWORK_INFO);
                mConnected.set(info.isConnected());
                handleStateChanged(info.getDetailedState());
            }
        }
    };

    public WifiEnabler(Context context, Switch switch_) {
        mContext = context;
        mSwitch = switch_;

        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mIntentFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        // The order matters! We really should not depend on this. :(
        mIntentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        
    }

    public void resume() {
        // Wi-Fi state is sticky, so just let the receiver update UI
    	Log.d(TAG , "resume========================>");
        mContext.registerReceiver(mReceiver, mIntentFilter);
        mSwitch.setOnCheckedChangeListener(this);
//        mSwitch.setOnSwitchChangeListener(mSwitchChangeListener);
    }

    public void pause() {
    	Log.d(TAG , "pause========================>");
    	try {
        mContext.unregisterReceiver(mReceiver);
//        mSwitch.setOnCheckedChangeListener(null);
//        mSwitch.setOnSwitchChangeListener(null);
    	} catch (IllegalArgumentException e) {
    		e.printStackTrace();
    	}
    }

    public void setSwitch(Switch switch_) {
        if (mSwitch == switch_) return;
        mSwitch.setOnCheckedChangeListener(null);
        mSwitch = switch_;
        mSwitch.setOnCheckedChangeListener(this);

        final int wifiState = mWifiManager.getWifiState();
        boolean isEnabled = wifiState == WifiManager.WIFI_STATE_ENABLED;
        boolean isDisabled = wifiState == WifiManager.WIFI_STATE_DISABLED;
        mSwitch.setChecked(isEnabled);
        mSwitch.setEnabled(isEnabled || isDisabled);
    }
    

    
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    	Log.d(TAG, "onCheckedChanged:" + isChecked);
        //Do nothing if called as a result of a state machine event
    	Log.d(TAG, "=========================>onSwitchChangeListener");

        // Disable tethering if enabling Wifi
        int wifiApState = mWifiManager.getWifiApState();
        if (isChecked && ((wifiApState == WifiManager.WIFI_AP_STATE_ENABLING) ||
                (wifiApState == WifiManager.WIFI_AP_STATE_ENABLED))) {
            mWifiManager.setWifiApEnabled(null, false);
        }
        if(isChecked){
        	mSwitch.setTrackResource(R.drawable.switch_on);
        }
        else{
        	mSwitch.setTrackResource(R.drawable.switch_off);
        }
        Log.d("WifiEnabler======>", "3 mWifiManager.setWifiEnabled(enable):enable=" + isChecked);
        if (mWifiManager.setWifiEnabled(isChecked)) {
        	 Log.d("WifiEnabler======>", "4 success to mWifiManager.setWifiEnabled(enable):enable=" + isChecked);
            // Intent has been taken into account, disable until new state is active
            mSwitch.setEnabled(false);
        } else {
            // Error
        	Log.d("WifiEnabler======>", "5 failed to mWifiManager.setWifiEnabled(enable)");
            Toast.makeText(mContext, R.string.wifi_error, Toast.LENGTH_SHORT).show();
        }
	
    }

    private void handleWifiStateChanged(int state) {
    	Log.d(TAG, "WIFI_STATE ====>" + state);
        switch (state) {
            case WifiManager.WIFI_STATE_ENABLING:
                mSwitch.setEnabled(false);
                break;
            case WifiManager.WIFI_STATE_ENABLED:
                setSwitchChecked(true);
                mSwitch.setEnabled(true);
                break;
            case WifiManager.WIFI_STATE_DISABLING:
                mSwitch.setEnabled(false);
                break;
            case WifiManager.WIFI_STATE_DISABLED:
                setSwitchChecked(false);
                mSwitch.setEnabled(true);
                break;
            default:
                setSwitchChecked(false);
                mSwitch.setEnabled(true);
                break;
        }
    }

    private void setSwitchChecked(boolean checked) {
        if (checked != mSwitch.isChecked()) {
            mSwitch.setChecked(checked);
        }
    }

    private void handleStateChanged(DetailedState state) {
        // After the refactoring from a CheckBoxPreference to a Switch, this method is useless since
        // there is nowhere to display a summary.
        // This code is kept in case a future change re-introduces an associated text.
        /*
        // WifiInfo is valid if and only if Wi-Fi is enabled.
        // Here we use the state of the switch as an optimization.
        if (state != null && mSwitch.isChecked()) {
            WifiInfo info = mWifiManager.getConnectionInfo();
            if (info != null) {
                //setSummary(Summary.get(mContext, info.getSSID(), state));
            }
        }
        */
    }

}
