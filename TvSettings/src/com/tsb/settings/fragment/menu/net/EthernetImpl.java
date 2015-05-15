package com.tsb.settings.fragment.menu.net;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.TvManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.ethernet.EthernetDevInfo;
import android.net.ethernet.EthernetManager;
import android.util.Log;



public class EthernetImpl {
	private String TAG = "EthernetImpl";
	public static EthernetImpl mEthernetImpl;
	public EthernetManager mEthManager;
	public EthernetDevInfo info;
	private DhcpInfo mDhcpInfo;
	public boolean isConfiguring = false;
	private TvManager mTv;
	/** check ip */
	private static final Pattern IP_ADDRESS = Pattern
			.compile("((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
					+ "[0-9]|1[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|1"
					+ "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-4]|2[0-4][0-9]|1[0-9]{2}"
					+ "|[1-9][0-9]|[1-9]))");
	/** check mask */
	private static final Pattern MASK_ADDRESS = Pattern
			.compile("((255)\\.(255|254|252|248|240|224|192|128|0)\\.(255|254|252|248|240|224|192|128|0)\\.(254|252|248|240|224|192|128|0))");

	
	public EthernetImpl(EthernetManager ethernetManager,TvManager tv) {
		mEthManager = ethernetManager;
		info = new EthernetDevInfo();
		mTv=tv;
	}
	
	public static EthernetImpl getInstance(EthernetManager ethernetManager,TvManager mTv) {
		if (mEthernetImpl == null) {
			mEthernetImpl = new EthernetImpl(ethernetManager,mTv);
		}
		return mEthernetImpl;
	}
	
	public void updateEthernetDevInfo() {
		this.info = mEthManager.getSavedConfig();
	}
	
	public void setEthernetDevInfo (EthernetDevInfo info) {
		this.info = info;
	}
	
	public String getMac() { 
		
    	return mTv.getMacAddress();
    }
	
	public String getIp() {
		return info != null ? info.getIpAddress() : null ;
	}
	
	public String getSubnetMask() {
		return info != null ? info.getNetMask() : null ;
	}
	
	public String getGetway() {
		return info != null ? info.getRouteAddr() : null ;
	}
	
	public String getDns() {
		return info != null ? info.getDnsAddr() : null ;
	}
	
	public void startDhcp() {
		if (isConfiguring) 
			return;
		new Thread() {
			public void run() {
				try {
					info = new EthernetDevInfo();
					mDhcpInfo = mEthManager.getDhcpInfo();
					info.setIfName("eth0");
					info.setConnectMode(EthernetDevInfo.ETHERNET_CONN_MODE_DHCP);
					info.setIpAddress(intToIp(mDhcpInfo.ipAddress));
					info.setRouteAddr(intToIp(mDhcpInfo.gateway));
					info.setDnsAddr(intToIp(mDhcpInfo.dns1));
					info.setNetMask(intToIp(mDhcpInfo.netmask));
					Log.i("ethernet--->", "startDhcp()");
					mEthManager.updateDevInfo(info);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();		
	}
	
	protected String intToIp(int i) {
		// TODO Auto-generated method stub
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
				+ "." + ((i >> 24) & 0xFF);
	}

	public void configEthernet(final String ip, final String mask, final String gateway, final String dns) {
		if (isConfiguring) 
			return;
		new Thread() {
			public void run() {
				try {
					info = new EthernetDevInfo();
					info.setIfName("eth0");
					info.setConnectMode(EthernetDevInfo.ETHERNET_CONN_MODE_MANUAL);
					info.setIpAddress(ip);
					info.setRouteAddr(gateway);
					info.setDnsAddr(dns);
					info.setNetMask(mask);
					mEthManager.updateDevInfo(info);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();		
	}
	
	public  boolean getWiredNetworkStates(Context mContext) {
		ConnectivityManager connec = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connec == null)
			return false;

		NetworkInfo allinfo = connec.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);

		if (allinfo != null) {
			if (allinfo.isConnected()) {
				return true;
			} else
				return false;
		} else {
			return false;
		}
	}
	
	public boolean isIpAddress(String value) {
		Matcher matcher = IP_ADDRESS.matcher(value);
		if (matcher.matches()) {
			return true;
		}
		return false;
	}

	public boolean isSubnetMask(String value) {
		Matcher matcher = MASK_ADDRESS.matcher(value);
		if (matcher.matches()) {
			String[] blocks = value.split("\\.");
			if (blocks.length == 4) {
				          String binaryVal = "";  
				          for (int i = 0; i < blocks.length; i++)  
				           {  
				               String binaryStr = Integer.toBinaryString(Integer.parseInt(blocks[i]));  
				                  
				                  
				               Integer times = 8 - binaryStr.length();  
				                  
				               for(int j = 0; j < times; j++)  
				               {  
				                   binaryStr = "0" +  binaryStr;  
				               }  
				               binaryVal += binaryStr;  
				          }  
				          // String regx = "^[1]*[0]*$";  
				           if(!binaryVal.contains("01"))  
				           {  
				               return true;  
				           }  
			}
		}
		return false;
	}
	
	
	/**
	 * 
	 * @return 49 connect 48 disconnect
	 */
	public boolean isEth0Connect() {

		int flag = -1;
		File file = new File("/sys/class/net/eth0/carrier");
		try {
			FileInputStream fo = new FileInputStream(file);
			flag = fo.read();
			fo.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (flag == 49) {
			return true;
		}
		return false;
	}
	
	
	
}
