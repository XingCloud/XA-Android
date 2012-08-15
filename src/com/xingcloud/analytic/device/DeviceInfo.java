package com.xingcloud.analytic.device;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.xingcloud.analytic.utils.Xutils;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;

public class DeviceInfo {

	TelephonyManager tm = null;
	Context inContext;
	public DeviceInfo(Context ctx)
	{
		if(null == ctx)
		{
			throw new Error("DeviceInfo init error");
		}
		//|| !Xutils.isPermit(ctx, "android.permission.ACCESS_DEVICE_STATS")
		if(!Xutils.isPermit(ctx, "android.permission.READ_PHONE_STATE") )
		{
			//throw new Error("please provide android.permission.READ_PHONE_STATE and android.permission.ACCESS_DEVICE_STATS");
			Log.e("XingCloud", "please provide android.permission.READ_PHONE_STATE and android.permission.ACCESS_DEVICE_STATS");
		}
		if(null == tm)
		{
			tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
		}
		inContext = ctx;
	}
	
	public String getDisplayMetrics()
	{
		if(!Xutils.isPermit(inContext, "android.permission.READ_PHONE_STATE") )
		{
			return "";
		}
		String str = "";
		DisplayMetrics dm = new DisplayMetrics();
		dm = inContext.getApplicationContext().getResources().getDisplayMetrics();
		int screenWidth = dm.widthPixels;
		int screenHeight = dm.heightPixels;
		float density = dm.density;
		float xdpi = dm.xdpi;
		float ydpi = dm.ydpi;
//		str += "\"Awidth\":" + "\""+String.valueOf(screenWidth)+"\""+",";
//		str += "\"Aheight\":" + "\""+String.valueOf(screenHeight)+"\""+",";
//		str += "\"density\":" +"\""+ String.valueOf(density)+"\""+",";
//		str += "\"Xdimension\":" + "\""+String.valueOf(xdpi)+"\""+",";
//		str += "\"Ydimension\":" + "\""+String.valueOf(ydpi)+"\"";
		
		str += "\"resolution\":"+"\""+String.valueOf(screenWidth)+"*"+String.valueOf(screenHeight)+"\"";
		return str;
	}
	/*
	 * 获得设备的ID
	 */
	public String getDeviceId()
	{
		if(!Xutils.isPermit(inContext, "android.permission.READ_PHONE_STATE") )
		{
			return "";
		}
		return tm.getDeviceId();
	}
	
	public String getDeviceSoftwareVersion()
	{
		if(!Xutils.isPermit(inContext, "android.permission.READ_PHONE_STATE") )
		{
			return "";
		}
		return tm.getDeviceSoftwareVersion();
	}
	
	public String getTelephoneNumber()
	{
		if(!Xutils.isPermit(inContext, "android.permission.READ_PHONE_STATE") )
		{
			return "";
		}
		return tm.getLine1Number();
	}
	
	public int getSimState()
	{
		if(!Xutils.isPermit(inContext, "android.permission.READ_PHONE_STATE") )
		{
			return TelephonyManager.SIM_STATE_UNKNOWN;
		}
		return tm.getSimState();
	}
	
	public String getSimStateDescription()
	{
		String sexist = null;
		int iState = tm.getSimState();
		switch(iState)
		{
		case TelephonyManager.SIM_STATE_ABSENT:
			sexist = SimState.NO_SIM_CARD;
			break;
		case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
			sexist = SimState.SIM_STATE_NETWORK_LOCKED;
			break;
		case TelephonyManager.SIM_STATE_PIN_REQUIRED:
			sexist = SimState.SIM_STATE_PIN_REQUIRED;
			break;
		case TelephonyManager.SIM_STATE_PUK_REQUIRED:
			sexist = SimState.SIM_STATE_PUK_REQUIRED;
			break;
		case TelephonyManager.SIM_STATE_READY:
			sexist = SimState.SIM_STATE_READY;
			break;
		default:
			sexist = SimState.SIM_STATE_UNKNOWN;
			break;
		}
		return sexist;
	}
	
	public String getSimOperator()
	{
		return tm.getSimOperator();
	}
	
	public String getSimOperatorName()
	{
		if(getSimState() == TelephonyManager.SIM_STATE_READY)
		{
			return tm.getSimOperatorName();
		}
		return "";
	}
	
//	public String getSimSerialNumber()
//	{
//		return tm.getSimSerialNumber();
//	}
//	
//	public Boolean isNetworkRpaming()
//	{
//		return tm.isNetworkRoaming();
//	}
	/*
	 * 获得设备的型号，比如HTC Sensation, Nexus S等
	 */
	public String getDeviceModel()
	{
		return Build.MODEL;
	}
	
	public String getSdkVersion()
	{
		return Build.VERSION.SDK;
	}
	
//	public String getManufacture()
//	{
//		return Build.MANUFACTURER;
//	}
	public String getPhoneType()
	{
		int iType = tm.getPhoneType();
		String sType;
		switch(iType)
		{
		case TelephonyManager.PHONE_TYPE_NONE:
			sType = TelephoneType.PHONE_TYPE_NONE;
			break;
		case TelephonyManager.PHONE_TYPE_CDMA:
			sType = TelephoneType.PHONE_TYPE_CDMA;
			break;
		case TelephonyManager.PHONE_TYPE_GSM:
			sType = TelephoneType.PHONE_TYPE_GSM;
			break;
		default:
			sType = TelephoneType.PHONE_TYPE_UNKNOWN;
			break;
		}
		return sType;
	}
//	
//	public String getSubscriberId()
//	{
//		return tm.getSubscriberId();
//	}
//	
//	public Boolean hasIccCard()
//	{
//		return tm.hasIccCard();
//	}
	
//	public String getVoiceMailNumber()
//	{
//		return tm.getVoiceMailNumber();
//	}
//	
//	public String getVoiceMailAlphaTag()
//	{
//		return tm.getVoiceMailAlphaTag();
//	}
	/*
	 * 获得设备的cpu信息
	 */
	public String getDeviceCPUInfo()
	{
		String cpu = null;
		try
        {
			//通过fileReader来读取设备cpu的信息
            FileReader filereader = new FileReader("/proc/cpuinfo");
            if(filereader != null)
                try
                {
                    BufferedReader bufferedreader = new BufferedReader(filereader, 1024);
                    cpu = bufferedreader.readLine();
                    bufferedreader.close();
                    filereader.close();
                }
                catch(IOException ioexception)
                {
                    Log.e("Xingcloud", "Could not read from file /proc/cpuinfo", ioexception);
                }
        }
        catch(FileNotFoundException filenotfoundexception)
        {
            Log.e("Xingcloud", "Could not open file /proc/cpuinfo", filenotfoundexception);
        }
        //分解出cpu信息
        if(cpu != null)
        {
            int i = cpu.indexOf(':') + 1;
            cpu = cpu.substring(i);
        }
        return cpu;

	}
	
	public JSONObject getDeviceInfo()
	{
		JSONObject device = new JSONObject();
		try {
			
			device.put("deviceId", this.getDeviceId());
			device.put("deviceModel", this.getDeviceModel());
			device.put("osVersion", this.getDeviceSoftwareVersion());
			device.put("sdkVersion", this.getSdkVersion());
			device.put("cpuInfo", this.getDeviceCPUInfo());
//			device.put("manufacture", this.getManufacture());
			device.put("phoneType", this.getPhoneType());
			device.put("display", this.getDisplayMetrics());
			device.put("simState", this.getSimState());
			String t = this.getSimOperatorName();
			if(t != null && t.length() > 0 && t.contains("\u0000"))
			{
				t = t.replace("\u0000", "");
			}
			device.put("simOperator", t);
			device.put("telephoneNumber", this.getTelephoneNumber());
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return device;
		
	}
	
	
	public String getDeviceInfoEx()
	{
		StringBuilder device = new StringBuilder();
		try {
//			device.append("\"deviceId\":");
//			device.append("\""+getDeviceId()+"\"");
//			device.append(",");
			
			device.append("\"deviceModel\":");
			device.append("\""+getDeviceModel()+"\"");
			device.append(",");
			
//			device.append("\"osVersion\":");
//			device.append("\""+getDeviceSoftwareVersion()+"\"");
//			device.append(",");
			
			device.append("\"sdkVersion\":");
			device.append("\""+getSdkVersion()+"\"");
			device.append(",");
			
			device.append("\"cpuInfo\":");
			device.append("\""+getDeviceCPUInfo()+"\"");
			device.append(",");
			
//			device.append("\"manufacture\":");
//	//		device.append("\""+getManufacture()+"\"");
//			device.append(",");
			
			device.append("\"phoneType\":");
			device.append("\""+getPhoneType()+"\"");
			device.append(",");
			
			
			device.append(getDisplayMetrics());
			device.append(",");
			
//			device.append("\"simState\":");
//			device.append("\""+getSimState()+"\"");
//			device.append(",");
			
			device.append("\"simOperator\":");
			String t = this.getSimOperatorName();
			if(t != null && t.length() > 0)
			{
				int k = t.indexOf("\u0000");
				t = t.replace("\u0000", "");
			}
			
			device.append("\""+t+"\"");
	//		device.append(",");
			
//			device.append("\"telephoneNumber\":");
//			device.append("\""+getTelephoneNumber()+"\"");
		
		
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return device.toString();
		
	}
}
