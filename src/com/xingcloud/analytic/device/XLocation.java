package com.xingcloud.analytic.device;

import org.json.JSONException;
import org.json.JSONObject;

import com.xingcloud.analytic.utils.Xutils;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public class XLocation {
	private Context contex;
	public XLocation(Context ctx)
	{
		if(null == ctx)
		{
			throw new Error("XLocation init error");
		}
		if(!Xutils.isPermit(ctx, "android.permission.INTERNET") || !Xutils.isPermit(ctx, "android.permission.ACCESS_FINE_LOCATION") || !Xutils.isPermit(ctx, "android.permission.ACCESS_MOCK_LOCATION"))
		{
//			throw new Error("please provide android.permission.INTERNET,android.permission.ACCESS_FINE_LOCATION and android.permission.ACCESS_MOCK_LOCATION");
			Log.e("XingCloud", "please provide android.permission.INTERNET,android.permission.ACCESS_FINE_LOCATION and android.permission.ACCESS_MOCK_LOCATION");
		}
		contex = ctx;
	}
	public Location getLocationByGPS()
	{
		LocationManager mLM =  (LocationManager)contex.getSystemService(Context.LOCATION_SERVICE);
		Criteria crit = new Criteria();
		crit.setAccuracy(Criteria.ACCURACY_FINE);
		crit.setAltitudeRequired(false);
		crit.setBearingRequired(false);
		crit.setCostAllowed(true);
		crit.setPowerRequirement(Criteria.POWER_LOW);
		String provider = mLM.getBestProvider(crit, true);
		
		 Location location = mLM  
         .getLastKnownLocation(LocationManager.GPS_PROVIDER);  
		 if (location == null) {  
			 location = mLM  
             .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);  
		 }  
		 return location;  
		
	}
	
	public JSONObject getLocation()
	{
		JSONObject location = new JSONObject();
		try {
			location.put("location", getLocationByGPS());
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return location;
	}
	
	public String getLocationEx()
	{
		if(!Xutils.isPermit(contex, "android.permission.INTERNET") || !Xutils.isPermit(contex, "android.permission.ACCESS_FINE_LOCATION") || !Xutils.isPermit(contex, "android.permission.ACCESS_MOCK_LOCATION"))
		{
			//throw new Error("please provide android.permission.INTERNET,android.permission.ACCESS_FINE_LOCATION and android.permission.ACCESS_MOCK_LOCATION");
			return "";
		}
		Location lo = getLocationByGPS();
		if(null == lo)
		{
			return "";
		}
		StringBuilder location= new StringBuilder();
		try {
//			location.append("\"accuracy\":");
//			location.append("\""+String.valueOf(lo.getAccuracy())+"\"");
//			location.append(",");
			
			location.append("\"altitude\":");
			location.append("\""+String.valueOf(lo.getAltitude())+"\"");
			location.append(",");
			
			location.append("\"longitude\":");
			location.append("\""+String.valueOf(lo.getLongitude())+"\"");
//			location.append(",");
//			
//			location.append("\"locationprovider\":");
//			location.append("\""+String.valueOf(lo.getProvider())+"\"");
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return location.toString();
	}
}
