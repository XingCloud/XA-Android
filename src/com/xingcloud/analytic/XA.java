package com.xingcloud.analytic;

import android.app.Activity;

public class XA {

	private static XA _instance = null;
	private String sUid= "";
	private XA()
	{
		sUid= "";
	}
	
	public static XA getInstance()
	{
		if(null == _instance)
		{
			_instance = new XA();
		}
		return _instance;
	}
	
	public void setUid(String uid)
	{
		sUid = uid;
	}
	
	public String getUid()
	{
		return sUid;
	}
	
	public void setPolicy(int policy, Activity activity)
	{
		if(null == activity)
		{
			return;
		}
		XAPolicy.getIntance().setReportPolicy(policy, activity);
	}
	
	public int getPolicy(Activity activity)
	{
		return XAPolicy.getIntance().getReportPolicy(activity);
	}
}
