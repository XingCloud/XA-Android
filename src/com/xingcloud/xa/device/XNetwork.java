package com.xingcloud.xa.device;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.json.JSONException;
import org.json.JSONObject;

import com.xingcloud.xa.utils.Xutils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class XNetwork {
	String netType;
	String nci;
	TelephonyManager tm = null;
	Context inContext;
	/*
	 * 获得网络相关信息前需要获得权限
	 */
	public XNetwork(Context ctx)
	{
		if(null == ctx)
		{
			throw new Error("XNetwork init error");
		}
		//|| !Xutils.isPermit(ctx, "android.permission.ACCESS_WIFI_STATE")
		if(!Xutils.isPermit(ctx, "android.permission.INTERNET") )
		{
			//throw new Error("please provide android.permission.INTERNET and android.permission.ACCESS_WIFI_STATE");
			Log.e("XingCloud", "please provide android.permission.INTERNET and android.permission.ACCESS_WIFI_STATE");
		}
		if(null == tm)
		{
			tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
		}
		inContext = ctx;
	}
	/*
	 * 获得是网络类型是wifi还是运营商网络
	 */
	public String getConnectType()
	{
		try
		{
			//获得ConnectivityManager服务
			ConnectivityManager mConn = (ConnectivityManager) inContext.getSystemService(Context.CONNECTIVITY_SERVICE);
			//获得网络信息
			NetworkInfo info = mConn.getActiveNetworkInfo();
			if(null == info || !mConn.getBackgroundDataSetting())
			{
				return null;
			}
			int nettype = info.getType();
			int netSubtype = info.getSubtype();
			//返回网络类型
			if(nettype == ConnectivityManager.TYPE_WIFI)
			{
				return info.getTypeName();
			}
			else if(nettype == ConnectivityManager.TYPE_MOBILE)
			{
				return getNetworkType2(netSubtype);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return "";
		
	}
	/*
	 * 获得运营商网络类型
	 */
	public String getNetworkType2(int type)
	{
		String netType;
		
		switch(type)
		{
		case 0:
			netType =  XNetworkType.NETWORK_TYPE_UNKNOWN;
			break;
		case 1:
			netType =  XNetworkType.NETWORK_TYPE_GPRS;
			break;
		case 2:
			netType =  XNetworkType.NETWORK_TYPE_EDGE;
			break;
		case 3:
			netType =  XNetworkType.NETWORK_TYPE_UMTS;
			break;
		case 4:
			netType =  XNetworkType.NETWORK_TYPE_CDMA;
			break;
		case 5:
			netType =  XNetworkType.NETWORK_TYPE_EVDO_0;
			break;
		case 6:
			netType =  XNetworkType.NETWORK_TYPE_EVDO_A;
			break;
		case 7:
			netType =  XNetworkType.NETWORK_TYPE_1xRTT;
			break;
		case 8:
			netType =  XNetworkType.NETWORK_TYPE_HSDPA;
			break;
		case 9:
			netType =  XNetworkType.NETWORK_TYPE_HSUPA;
			break;
		case 10:
			netType =  XNetworkType.NETWORK_TYPE_HSPA;
			break;
		default:
			netType =  XNetworkType.NETWORK_TYPE_UNKNOWN;
			break;
		}
		
		return netType;
	}
	public String getNetworkType()
	{
		String netType;
		
		switch(tm.getNetworkType())
		{
		case 0:
			netType =  XNetworkType.NETWORK_TYPE_UNKNOWN;
			break;
		case 1:
			netType =  XNetworkType.NETWORK_TYPE_GPRS;
			break;
		case 2:
			netType =  XNetworkType.NETWORK_TYPE_EDGE;
			break;
		case 3:
			netType =  XNetworkType.NETWORK_TYPE_UMTS;
			break;
		case 4:
			netType =  XNetworkType.NETWORK_TYPE_CDMA;
			break;
		case 5:
			netType =  XNetworkType.NETWORK_TYPE_EVDO_0;
			break;
		case 6:
			netType =  XNetworkType.NETWORK_TYPE_EVDO_A;
			break;
		case 7:
			netType =  XNetworkType.NETWORK_TYPE_1xRTT;
			break;
		case 8:
			netType =  XNetworkType.NETWORK_TYPE_HSDPA;
			break;
		case 9:
			netType =  XNetworkType.NETWORK_TYPE_HSUPA;
			break;
		case 10:
			netType =  XNetworkType.NETWORK_TYPE_HSPA;
			break;
		default:
			netType =  XNetworkType.NETWORK_TYPE_UNKNOWN;
			break;
		}
		
		return netType;
	}
	
	public String getNetworkContryIso()
	{
		String iso = tm.getNetworkCountryIso();
		return iso==null?"":iso;
	}
	/*****************
	 * 获得网络运营商的名称
	 * @return
	 */
	public String getNetworkOperatorName()
	{
		String name = tm.getNetworkOperatorName();
		return name==null?"":name;
	}
	
	/*
	 * wifi是否可用
	 */
	public  boolean isWiFiActive() {
		if(!Xutils.isPermit(inContext, "android.permission.INTERNET") )
		{
			//throw new Error("please provide android.permission.INTERNET and android.permission.ACCESS_WIFI_STATE");
			return false;
		}
		Context context = inContext.getApplicationContext();
		//获得ConnectivityManager服务
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					//判断wifi是否可用
					if (info[i].getTypeName().equals("WIFI") && info[i].isConnected()) {
						return true;
					}
				}
			}
		}
		return false;
	}
	/*
	 * 获得当前设备的mac地址
	 */
	public String getLocalMacAddress()
	{
		if(!Xutils.isPermit(inContext, "android.permission.INTERNET") )
		{
			//throw new Error("please provide android.permission.INTERNET and android.permission.ACCESS_WIFI_STATE");
			return "";
		}
		//获得WifiManager服务
		WifiManager wifi = (WifiManager) inContext.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		//返回mac地址
		return info.getMacAddress();
	}
	
	/*
	 * 获得网络的IP地址
	 */
	public String getLocalIpAddress()
	{
		if(!Xutils.isPermit(inContext, "android.permission.INTERNET"))
		{
			//throw new Error("please provide android.permission.INTERNET and android.permission.ACCESS_WIFI_STATE");
			return "";
		}
		try {   
            for (Enumeration<NetworkInterface> en = NetworkInterface   
                    .getNetworkInterfaces(); en.hasMoreElements();) {   
                NetworkInterface intf = en.nextElement();   
                for (Enumeration<InetAddress> enumIpAddr = intf   
                       .getInetAddresses(); enumIpAddr.hasMoreElements();) {   
                    InetAddress inetAddress = enumIpAddr.nextElement();   
                    if (!inetAddress.isLoopbackAddress()) {   
                        return inetAddress.getHostAddress().toString();   
                    }   
                }   
            }   
        } catch (SocketException ex) {   
            Log.e("WifiPreference IpAddress", ex.toString());   
        }   
        return null;   
	}
	
	public JSONObject getNetworkInfo()
	{
		
		JSONObject network =  new JSONObject();
		if(!Xutils.isPermit(inContext, "android.permission.INTERNET") )
		{
			//throw new Error("please provide android.permission.INTERNET and android.permission.ACCESS_WIFI_STATE");
			return network;
		}
		try {
			network.put("netType", getNetworkType());
			network.put("countryIso", getNetworkContryIso());
			network.put("netOperator", getNetworkOperatorName());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return network;
	}
	
	public String getNetworkInfoEx()
	{
		StringBuilder network =  new StringBuilder();
		try {
			network.append("\"netType\":");
			network.append("\""+getConnectType()+"\"");
			network.append(",");
			
			network.append("\"countryIso\":");
			network.append("\""+getNetworkContryIso()+"\"");
			network.append(",");
			
			network.append("\"netOperator\":");
			network.append("\""+getNetworkOperatorName()+"\"");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return network.toString();
	}
}
