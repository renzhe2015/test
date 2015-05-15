package com.tsb.settings;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONException;
import org.json.JSONObject;
import android.app.TvManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Debug;
import android.util.Log;
import android.widget.Toast;

public class DeviceIDTest {
	private static String   URI= "https://tvuser-tcl.cedock.com/uc/json" ;
	public static  boolean DeviceIDOK=false;
	public Context mContext=null;
	public String code="error";
	public TvManager mTvManager;
	
	
	
	public DeviceIDTest(Context mcontext) {
		mContext=mcontext;
		
		mTvManager = (TvManager)mcontext.getSystemService("tv");     //tq 20140922
		if(!isNetworkConnected(mcontext))   //tq 20140625 net is ok? 
		{
			Log.v("TestDeviceID", "Net is not ready ========"+DeviceIDOK);
			Toast.makeText(mContext, "Net is not ready!",Toast.LENGTH_SHORT).show();
			DeviceIDOK=false;
			return;
		}
		Log.v("TestDeviceID", "TestDeviceID is ========"+DeviceIDOK);
//		Toast.makeText(mContext, "DeviceID is testing...", Toast.LENGTH_SHORT);
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						code=device_test();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start(); 
	}

	public String device_test() throws Exception
	{
		Log.v("tqtest", "we are going to device_test!");
		JSONObject my_object = new JSONObject();
		JSONObject new_child_obj = new JSONObject();
		
		my_object.put("action", "TestDevice");
		
//		new_child_obj.put("deviceid", "6665d646af82dc79f1705ab1c8e7e88b6c8f9f69");
//		new_child_obj.put("devmodel","TSB-CN-MS918-U7450-3D");
//		new_child_obj.put("devmac","10.88.88.22.30.01");
		
		new_child_obj.put("deviceid", mTvManager.getDeviceID());
		new_child_obj.put("devmodel",mTvManager.getClientType());
		new_child_obj.put("devmac",mTvManager.getMacAddress());
		
		Log.v("tqtest","getDeviceID="+mTvManager.getDeviceID());
		Log.v("tqtest","getClientType="+mTvManager.getClientType());
		Log.v("tqtest","getMacAddress="+mTvManager.getMacAddress());
		
		
		my_object.put("device", new_child_obj);
		String message = new String(my_object.toString());
		Log.v("test device login send", message);
		String out_http_msg = do_https_request(URI, message);
		Log.v("test device login", out_http_msg);
		
		return parser_test_device(out_http_msg);	
	}
	
    public String do_https_request(String url, String strSendMsg) throws  Exception 
    {
    	Log.v("***  strsend ****", "url ="+url+ ";  "+strSendMsg);
    	
    	SSLContext sc = SSLContext.getInstance("TLS");
    	sc.init(null, new TrustManager[]{new TrustAnyTrustManager()}, new java.security.SecureRandom());//åˆå§‹åŒ–ä¸Šä¸‹æ–�?
    	URL pathurl = new URL(url);
    	URLConnection conntest = pathurl.openConnection();
    	
    	if(conntest != null){
    		Log.v("URLConnection","conntest is null");
    	}
    	
    	HttpsURLConnection conn = (HttpsURLConnection)conntest;
    	conn.setConnectTimeout(5000);
    	conn.setSSLSocketFactory(sc.getSocketFactory());
    	conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
    	conn.setDoInput(true);
    	conn.setDoOutput(true);
     
    	conn.setRequestMethod("POST");
    	
    	
    	conn.connect();
 
    	conn.getOutputStream().write(strSendMsg.getBytes());
    	String temp = "";
    	String result = "";
    	InputStreamReader inr = new InputStreamReader(conn
    	.getInputStream(), "utf-8");
    	BufferedReader br = new BufferedReader(inr);
    	while((temp=br.readLine())!=null){
    		Log.v("return character","temp = "+temp);
    	result += temp;
    	}
    	return result;
	}  
    public String parser_test_device(String out_string) throws JSONException
    {
    	String error_code = new String();
    	JSONObject my_object = new JSONObject(out_string);
    	int retv = 0;
    	if(my_object.has("error"))
    	{
    		String error = new String(my_object.getString("error"));
    		JSONObject error_object = new JSONObject(error);
    		if(error_object.has("code"))
    		{
    			retv = error_object.getInt("code");
    			if(0 == retv)
    				error_code = "false";
    			else
    				error_code = "error_code:" + retv + "error_info:";
    		}
    		if(error_object.has("info") && error_code != "false")
    		{
    			error_code = error_code + error_object.getString("info");
    			
    		}
    	}
    	
		return error_code;
    	
    	
    }

	public boolean getTestDeviceStatus() {
		// TODO Auto-generated method stub
		try {
			Thread.currentThread().sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(!code.equalsIgnoreCase("false")){
			try {
				Thread.currentThread().sleep(1200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(code.equalsIgnoreCase("false")||DeviceIDOK)
		{
		 Log.v("tqtest", "device id test OK");
//				Toast.makeText(mContext, "Portal Test OK!",Toast.LENGTH_SHORT).show();
		    return true;
	
		}
		else{
			Log.v("tqtest", "device id test fail");
//				Toast.makeText(mContext, "Portal Test Fail!",Toast.LENGTH_SHORT).show();
		     return false;
			
		}
	}

	public  static class TrustAnyTrustManager implements X509TrustManager
	{


		public void checkClientTrusted(X509Certificate[] chain, String authType)
		throws CertificateException {
			// TODO Auto-generated method stub

		}


		public void checkServerTrusted(X509Certificate[] chain, String authType)
		throws CertificateException 
		{
			// TODO Auto-generated method stub
		}


		public X509Certificate[] getAcceptedIssuers() 
		{
			// TODO Auto-generated method stub
			return new X509Certificate[]{};
		}

	}
	public static class TrustAnyHostnameVerifier implements HostnameVerifier
	{


		public boolean verify(String hostname, SSLSession session) 
		{
			// TODO Auto-generated method stub
			return true;
		}

	}
	 public boolean isNetworkConnected(Context context) {  
		    if (context != null) {  
		        ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
		        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();  
		          if (mNetworkInfo != null) {  
		            return mNetworkInfo.isAvailable();  
	        }  
		       }  
		    return false;  
		 } 
}
