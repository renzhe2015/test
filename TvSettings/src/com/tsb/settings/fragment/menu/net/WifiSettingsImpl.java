package com.tsb.settings.fragment.menu.net;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiConfiguration.Status;
import android.util.Log;

public class WifiSettingsImpl {
	private String TAG = "WifiSettings";
	private static WifiSettingsImpl mWifiSettingsImpl;
	private static Context mContext;
	private WifiManager mWifiManager;
	private Collection<AccessPoint> accessPoints = null;
	private boolean mResetNetworks = false;
	private int mLastPriority;
	private AccessPoint mSelected;
	private int mCountPasswordError = 0;
	public static AtomicBoolean mConnected = new AtomicBoolean(false);
	
	public static WifiSettingsImpl getInstance(Context context) {
		mContext = context;
		if (mWifiSettingsImpl == null) {
			mWifiSettingsImpl = new WifiSettingsImpl();
		}
		return mWifiSettingsImpl;
	}
	
	public void clearWifiSettings() {
		mSelected = null;
		mCountPasswordError = 0;
		mResetNetworks = false;
	}
	
	public void clearSelectedAP() {
		mSelected = null;
	}
	
	public WifiManager getWifiManager() {
		if (mWifiManager == null) {
			mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		}
		return mWifiManager;
	}
	
	public void forget() {
		new Thread(new Runnable() {
			final int networkId = mSelected.networkId;
			@Override
			public void run() {
				if (networkId == -1) {
		            // Should not happen, but a monkey seems to triger it
		            return;
		        }
		        mWifiManager.forget(networkId, null);
			}
		}).start();
	}
	
	
	public void enableNetworks() {
		for (AccessPoint ap : accessPoints) {
			WifiConfiguration config = ap.getConfig();
			if (config != null && config.status != Status.ENABLED) {
				if (!(config.status == Status.DISABLED && config.disableReason == WifiConfiguration.DISABLED_AUTH_FAILURE)) {
					mWifiManager.enableNetwork(config.networkId, false);
				}
			}
		}

		mResetNetworks = false;
	}
	
	public void connect(int networkId) {
		if (networkId == -1) {
			return;
		}
		// Reset the priority of each network if it goes too high.
		if (mLastPriority > 1000000) {
			for (AccessPoint accessPoint : accessPoints) {
				if (accessPoint.networkId != -1) {
					WifiConfiguration config = new WifiConfiguration();
					config.networkId = accessPoint.networkId;
					config.priority = 0;
					mWifiManager.updateNetwork(config);
				}
			}
			mLastPriority = 0;
		}

		// Set to the highest priority and save the configuration.
		WifiConfiguration config = new WifiConfiguration();
		config.networkId = networkId;
		config.priority = ++mLastPriority;
		mWifiManager.updateNetwork(config);
		mWifiManager.saveConfiguration();

		Log.d(TAG, "Connect to network by disabling others.networkId = "
				+ networkId);

		// Connect to network by disconnect current connection by chengct.		
		mWifiManager.disconnect();
		mWifiManager.connect(networkId, null);
		
		mResetNetworks = true;
		mCountPasswordError = 0;
	}
	
	public void updateAccessPoints(Collection<AccessPoint> accesspoints) {
		accessPoints = accesspoints;
	}
	
	public Collection<AccessPoint> getAccessPoints() {
		return accessPoints;
	}
	
	public boolean getResetNetworksFlag() {
		return mResetNetworks;
	}
	
	public void setResetNetworksFlag(boolean reset) {
		mResetNetworks = reset;
	}
	
	public void setLastPriorityValue(int value) {
		mLastPriority = value;
	}
	
	public int getLastPriority() {
		return mLastPriority;
	}
	
	public void setSelectedAp(AccessPoint ap) {
		mSelected = null;
		mSelected = ap;
	}
	
	public AccessPoint getSelectedAp() {
		return mSelected;
	}
	
	public void setPasswordErrorCount(int count) {
		mCountPasswordError = count;
	}
	
	public int getPasswordErrorCount() {
		return mCountPasswordError;
	}
}
