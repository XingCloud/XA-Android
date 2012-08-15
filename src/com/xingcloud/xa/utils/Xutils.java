package com.xingcloud.xa.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import com.xingcloud.xa.XA;
import com.xingcloud.xa.report.CustomReport;
import com.xingcloud.xa.report.ErrorReport;
import com.xingcloud.xa.report.ReferenceField;

//import com.xingcloud.analytic.CloudAnalytic;
//import com.xingcloud.analytic.report.CustomReport;
//import com.xingcloud.analytic.report.ErrorReport;
//import com.xingcloud.analytic.report.ReferenceField;
//import com.xingcloud.analytic.report.UserReport;
//import com.xingcloud.analytic.user.UserField;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

public class Xutils {

	public static final String XINGCLOUD_REPORT_NAME="xingcloud_report.csv";
	public static final String UUID_PREF_NAME="xcuuid";
	public static final String UUID_NAME = "uuid";
	private static final String XC_TIME = "time";
	private static final String XC_REPORT_TYPE = "type";
	private static final String XC_REPORT_FUNCTION = "event";
	private static final String XC_REPORT_CONTENT = "content";
	public static String REF = "";
	public static boolean bRef = false;
	private static Xutils _instance;
	private CSVReader reader = null;
	CSVWriter writer = null;
	static String sAppid;
	protected Xutils()
	{
		bRef = false;
		REF ="";
		sAppid = "";
	}
	public static void setAppid(String appid)
	{
		if(appid != null && appid.length() > 0)
		{
			sAppid = appid;
		}
	}
	public static Xutils getIntance()
	{
		if(_instance == null)
		{
			_instance =  new Xutils();
		}
		return _instance;
	}
	
	public  List<Object> getUnsendReports(File path)
	{
		if(null == path || !path.exists())
		{
			return null;
		}
		
		List<Object> results = null;
		try
		{
			results =  new ArrayList<Object>();
			
			
			reader =  new CSVReader(new FileReader(path));
			String[] headers= reader.readNext();
			//String[] rows= reader.readNext();
			//Object result = new Object();
			for(;;)
			{
				String[] row = reader.readNext();
				if(null ==row)
				{
					break;
				}
				if( null == row[1] )
				{
					break;
				}
				if(row[1].compareToIgnoreCase("custom") == 0)
				{
					CustomReport report = new CustomReport(row[3],1);
					results.add(report);
				}
//				else if(row[1].compareToIgnoreCase("error") == 0)
//				{
//					ErrorReport report =  new ErrorReport(row[3]);
//					results.add(report);
//				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
		}
		return results;
	}
	
	
	public  void deleteReport(Context context)
	{
		if(null == context)
		{
			Log.e("XingCloud", "context is null");
			return;
		}
		File file = context.getFileStreamPath(XINGCLOUD_REPORT_NAME);
		if(null == file)
		{
			return;
		}
		context.deleteFile(XINGCLOUD_REPORT_NAME);
	}
	
	public  void saveReport(Context activity,List<Object> reports)
	{
		if(null == activity)
		{
			Log.e("XingCloud", "context is null");
			return;
		}
		String[] row = new String[]{XC_TIME,XC_REPORT_TYPE,XC_REPORT_FUNCTION,XC_REPORT_CONTENT};
		
		
		if(null == reports || reports.size() <= 0)
		{
			return;
		}
		try
		{
			int size= reports.size();
			
	        File file = activity.getFileStreamPath(Xutils.XINGCLOUD_REPORT_NAME);
	 		if(file == null || !file.exists())
	 		{
	 			FileOutputStream outStream = activity.openFileOutput(XINGCLOUD_REPORT_NAME, Context.MODE_PRIVATE);
		        // outStream.write("传智播客".getBytes());
		        outStream.close();   
	 			writer =  new CSVWriter(new FileWriter(activity.getFilesDir()+"/"+XINGCLOUD_REPORT_NAME));
				writer.writeNext(row);
	 		}
	 		else
	 		{
	 			
//	 			reader =  new CSVReader(new FileReader(file));
//				String[] headers= reader.readNext();
//				reader = null;
				
				FileInputStream   fis   =   new   FileInputStream(file);
	 			if(fis != null && fis.available() >0)
	 			{
	 				writer =  new CSVWriter(new FileWriter(activity.getFilesDir()+"/"+XINGCLOUD_REPORT_NAME,true));
					//writer.writeNext(row);
	 				
	 			}
	 			else
	 			{
	 				writer =  new CSVWriter(new FileWriter(activity.getFilesDir()+"/"+XINGCLOUD_REPORT_NAME,true));
					writer.writeNext(row);
	 			}
	 		
	 		}
			
			Object report = null;
			for(int i = 0; i < size; i++)
			{
				report = reports.get(i);
				row[0] = XTimeStamp.getTimeStamp();
				if(report instanceof CustomReport)
				{
					row[1] = "custom";
					row[2] = "custom"; 
					row[3] = ((CustomReport)report).getContent();
				}
//					else if(report instanceof UserReport)
//				{
//					row[1] = "user";
//					UserField user = new UserField();
//					
//					row[2] = user.parseEvent(((UserReport)report).getEventId());
//					row[3] = ((UserReport)report).getData();
//				}
//				else if(report instanceof ErrorReport)
//				{
//					row[1] = "error";
//					row[2]  = "user.error";
//					row[3] = ((ErrorReport)report).getContent();
//				}
				writer.writeNext(row);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(null != writer)
				{
					writer.close();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
	}
	public static String getGameAppId(Activity ctx)
	{
		if(null == ctx)
		{
			Log.e("XingCloud", "context is null");
			return "";
		}
		if(sAppid != null && sAppid.length()>0)
		{
			return sAppid;
		}
		//ActivityInfo ai = null;
		String gkey = "";
    	try {
    		ApplicationInfo info = ctx.getPackageManager().getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
    		//info.metaData;
			//ai = ctx.getPackageManager().getActivityInfo(ctx.getComponentName(), PackageManager.GET_META_DATA);
			if(info !=null && info.metaData!=null&&info.metaData.get("XINGCLOUD_GAME_APPID") != null)
			{
				gkey = info.metaData.get("XINGCLOUD_GAME_APPID").toString();  
			}
			else
			{
				Log.e("XingCLoud", "Please provide XINGCLOUD_GAME_APPID in manifest file");
			}
			
    	} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	sAppid = gkey;
		return gkey;
	}
	
	public static String getDeliveryChanel(Activity ctx)
	{
		if(null == ctx)
		{
			Log.e("XingCloud", "context is null");
			return "";
		}
		if(REF != null &&REF.length() > 0)
		{
			return REF;
		}
	//	ActivityInfo ai = null;
		String channel = "";
    	try {
    		ApplicationInfo info = ctx.getPackageManager().getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
    		//ApplicationInfo info = ctx.getApplicationContext().getApplicationInfo();
			//ai = ctx.getPackageManager().getActivityInfo(ctx.getComponentName(), PackageManager.GET_META_DATA);
			if(null == info || info.metaData == null || null == info.metaData.get("XINGCLOUD_CHANNEL"))
			{
				return "";
			}
			channel = info.metaData.get("XINGCLOUD_CHANNEL").toString();   
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		if(bRef)
//		{
//			
//		}
		channel = ReferenceField.NORMAL_REF_FRAGMENT + channel;
		return channel;
	}
	
	public static String getMetaChanel(Activity ctx)
	{
		if(null == ctx)
		{
			Log.e("XingCloud", "context is null");
			return "";
		}
		//ActivityInfo ai = null;
		String channel = "";
    	try {
    		ApplicationInfo info = ctx.getPackageManager().getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
    		//ApplicationInfo info = ctx.getApplicationContext().getApplicationInfo();
			//ai = ctx.getPackageManager().getActivityInfo(ctx.getComponentName(), PackageManager.GET_META_DATA);
			if(null == info || info.metaData == null || null == info.metaData.get("XINGCLOUD_CHANNEL"))
			{
				return "";
			}
			channel = info.metaData.get("XINGCLOUD_CHANNEL").toString();   
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return channel;
	}
	public static String getGkey(Activity ctx)
    {
		if(null == ctx)
		{
			Log.e("XingCloud", "context is null");
			return "";
		}
    	//ActivityInfo ai = null;
    	String gkey = "";
    	try {
    		ApplicationInfo info = ctx.getPackageManager().getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
    		//ApplicationInfo info = ctx.getApplicationContext().getApplicationInfo();
			//ai = ctx.getPackageManager().getActivityInfo(ctx.getComponentName(), PackageManager.GET_META_DATA);
			gkey = info.metaData.get("XCLOUD_APPID").toString();  
    	} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return gkey;
    }
	public static String getGkeys(Activity ctx)
    {
		if(null == ctx)
		{
			Log.e("XingCloud", "context is null");
			return "";
		}
    	//ActivityInfo ai = null;
    	String gkey = "";
    	try {
    		ApplicationInfo info = ctx.getPackageManager().getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
    		//ApplicationInfo info = ctx.getApplicationContext().getApplicationInfo();
			//ai = ctx.getPackageManager().getActivityInfo(ctx.getComponentName(), PackageManager.GET_META_DATA);
			gkey = info.metaData.get("XCLOUD_APPID").toString();   
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//String gkey = ai.metaData.get("XCLOUD_APPID").toString();   
		return gkey;
    }
	public static String getGsecret(Activity ctx)
    {
		if(null == ctx)
		{
			Log.e("XingCloud", "context is null");
			return "";
		}
    	//ActivityInfo ai = null;
    	String gsecret = "";
    	try {
    		ApplicationInfo info = ctx.getPackageManager().getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
    		//ApplicationInfo info = ctx.getApplicationContext().getApplicationInfo();
			//ai = ctx.getPackageManager().getActivityInfo(ctx.getComponentName(), PackageManager.GET_META_DATA);
			gsecret = info.metaData.get("XCLOUD_SECRET_KEY").toString(); 
    	} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//String   
		return gsecret;
    }
	public static String getGsecrets(Activity ctx)
    {
		if(null == ctx)
		{
			Log.e("XingCloud", "context is null");
			return "";
		}
    	//ActivityInfo ai = null;
    	String gsecret = "";
    	try {
    		ApplicationInfo info = ctx.getPackageManager().getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
    		//ApplicationInfo info = ctx.getApplicationContext().getApplicationInfo();
			//ai = ctx.getPackageManager().getActivityInfo(ctx.getComponentName(), PackageManager.GET_META_DATA);
			gsecret = info.metaData.get("XCLOUD_SECRET_KEY").toString();   
    	} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		/String 
		return gsecret;
    }
	
	public static String generateUUID(Context context)
	{
		if(null == context)
		{
			Log.e("XingCloud", "context is null");
			return "";
		}
		SharedPreferences pref = context.getSharedPreferences(Xutils.UUID_PREF_NAME, Activity.MODE_PRIVATE);
		if(null != pref && null != pref.getString(Xutils.UUID_NAME, "") && pref.getString(Xutils.UUID_NAME, "").trim().length() > 0)
		{
			return pref.getString(Xutils.UUID_NAME, "");
		}
		if(XA.instance().isWaitForUid())
		{
			return "";
		}
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String deviceId = tm.getDeviceId();
		String deviceUUID;
		
		UUID uuid =  UUID.randomUUID();
		String uuidStr = uuid.toString().replaceAll("-", "").substring(0,15);
		String wifiMAC = getMacAddr(context);
		if(null != wifiMAC)
		{
			wifiMAC = wifiMAC.replaceAll("\\.|:", "");
		}
		if(deviceId == null || TextUtils.isEmpty(deviceId.trim()))
		{
			if(wifiMAC != null && !TextUtils.isEmpty(wifiMAC))
			{
				deviceUUID = "-"+wifiMAC;
			}
			else
			{
				deviceUUID = uuidStr;
			}
		}
		else
		{
			if(wifiMAC != null && !TextUtils.isEmpty(wifiMAC))
			{
				deviceUUID = deviceId+"-"+wifiMAC;
			}
			else
			{
				deviceUUID = deviceId+"-"+uuidStr;
			}
		}
		return deviceUUID;
	}
	
	public static String getMacAddr(Context context)
	{
		if(null == context)
		{
			Log.e("XingCloud", "context is null");
			return "";
		}
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return info.getMacAddress();
	}
	
	public static Boolean isSendCrashLogs(Context context,String permission)
	{
		if(null == context)
		{
			Log.e("XingCloud", "context is null");
			return false;
		}
		if (permission == null) {  
	        throw new IllegalArgumentException("permission is null");  
	    }  
		
		
	    int i= context.checkPermission(permission, Binder.getCallingPid(),  
	            Binder.getCallingUid());  
	    if(i == PackageManager.PERMISSION_GRANTED)
	    {
	    	return true;
	    }
	    else
	    {
	    	return false;
	    }
	    	
	}
	public static Boolean isPermit(Context context, String permission)
	{
		if(null == context)
		{
			Log.e("XingCloud", "context is null");
			return false;
		}
		if (permission == null) {  
	        throw new IllegalArgumentException("permission string is null");  
	    }  
		
		
	    int i= context.checkPermission(permission, Binder.getCallingPid(),  
	            Binder.getCallingUid());  
	    if(i == PackageManager.PERMISSION_GRANTED)
	    {
	    	return true;
	    }
	    else
	    {
	    	return false;
	    }
	}
	public static String getGameAppVersion(Context context)
	{
		if(null == context)
		{
			Log.e("XingCloud", "context is null");
			return "";
		}
		String code ="";
		try
		{
			code= context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return code;
	}
	public static Boolean isAppNetworkPermit(Context context)
	{
		if(null == context)
		{
			Log.e("XingCloud", "context is null");
			return false;
		}
		int i= context.checkPermission("android.permission.INTERNET", Binder.getCallingPid(),  
	            Binder.getCallingUid());  
		if(i == PackageManager.PERMISSION_GRANTED)
	    {
	    	return true;
	    }
	    else
	    {
	    	return false;
	    }
	}
	/*
	 * 网络是否可用
	 */
	public static Boolean isNetworkAvailable(Context context)
	{
		if(null == context)
		{
			Log.e("XingCloud", "context is null");
			return false;
		}
		//获得ConnectivityManager服务
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
		NetworkInfo networkinfo = null;
		try
		{
			//网络信息
			networkinfo = manager.getActiveNetworkInfo();  
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
        //判断网络是否可用
        if (networkinfo == null || !networkinfo.isAvailable()) {  
             return false;
        }  
        else
        {
        	return true;
        }
	}
	
	public static void saveUUID(Context activity,String s_uid)
	{

		SharedPreferences pref = activity.getSharedPreferences(UUID_PREF_NAME, Activity.MODE_PRIVATE);

		SharedPreferences.Editor editor = pref.edit();
		editor.putString(UUID_NAME, s_uid);
		
		editor.commit();
	}
	
	public static String getUUID(Context activity)
	{
		SharedPreferences pref = activity.getSharedPreferences(UUID_PREF_NAME, Activity.MODE_PRIVATE);
		try
		{
			if(null == pref || null == pref.getString(UUID_NAME, "") || pref.getString(UUID_NAME, "").trim().length() <= 0)
			{
				return null;
			}
			else
			{
				return pref.getString(UUID_NAME, "");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;		
		}
		
	}
	
	public static String generateUUID2(Context context)
	{
		if(null == context)
		{
			Log.e("XingCloud", "context is null");
			return "";
		}
		SharedPreferences pref = context.getSharedPreferences(Xutils.UUID_PREF_NAME, Activity.MODE_PRIVATE);
		if(null != pref && null != pref.getString(Xutils.UUID_NAME, "") && pref.getString(Xutils.UUID_NAME, "").trim().length() > 0)
		{
			return pref.getString(Xutils.UUID_NAME, "");
		}

		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String deviceId = tm.getDeviceId();
		String deviceUUID;
		
		UUID uuid =  UUID.randomUUID();
		String uuidStr = uuid.toString().replaceAll("-", "").substring(0,15);
		String wifiMAC = getMacAddr(context);
		if(null != wifiMAC)
		{
			wifiMAC = wifiMAC.replaceAll("\\.|:", "");
		}
		if(deviceId == null || TextUtils.isEmpty(deviceId.trim()))
		{
			if(wifiMAC != null && !TextUtils.isEmpty(wifiMAC))
			{
				deviceUUID = "-"+wifiMAC;
			}
			else
			{
				deviceUUID = uuidStr;
			}
		}
		else
		{
			if(wifiMAC != null && !TextUtils.isEmpty(wifiMAC))
			{
				deviceUUID = deviceId+"-"+wifiMAC;
			}
			else
			{
				deviceUUID = deviceId+"-"+uuidStr;
			}
		}
		return deviceUUID;
	}
}
