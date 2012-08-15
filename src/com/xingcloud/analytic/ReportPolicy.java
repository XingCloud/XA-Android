package com.xingcloud.analytic;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;

public class ReportPolicy {

	/*
	 * 立即发送，只要有report就马上发出去
	 */
	 public static final int REALTIME = 0;
	 /*
	  * 在每次启动时批量发送
	  */
	 public static final int BATCH_AT_LAUNCH = 1;
	 /*
	  * 在每次退出时批量发送
	  */
	 public static final int BATCH_AT_TERMINATE = 2;
	// public static final int PUSH = 3;
	// public static final int DAILY = 4;
	 /*
	  * 只在有wifi的环境中才发，实时发送
	  */
	 public static final int WIFIONLY = 5;
	 //定时定量发送，默认间隔时间是1分钟，一次最大数量上10条
	 public static final int DEFAULT =6;
	
	private int report_time_span;
	private int report_policy;
	private static ReportPolicy _instance;
	
	
	private final static String POLICY_PREFERENCE_NAME = "report_policy";
	private final static String POLICY = "policy";
	
	/*******************
	 *  获得ReportPolicy的实例
	 * @return
	 */
	public static ReportPolicy getIntance()
	{
		if(null == _instance)
		{
			_instance = new ReportPolicy();
		}
		return _instance;
	}
	/*************
	 * 设置report的策略
	 * @param policy 策略
	 * @param activity
	 */
	public  void setReportPolicy(int policy,Context activity)
	{
		this.report_policy = policy;
		
		SharedPreferences pref = activity.getSharedPreferences(POLICY_PREFERENCE_NAME, Activity.MODE_PRIVATE);

		SharedPreferences.Editor editor = pref.edit();
		
		editor.putInt(POLICY, policy);
		
		editor.commit();
		
	}
	/***************
	 * 获得reprt的策略
	 * @param activity  当前activity的实例
	 * @return
	 */
	public  int getReportPolicy(Context activity)
	{
		SharedPreferences pref = activity.getSharedPreferences(POLICY_PREFERENCE_NAME, Activity.MODE_PRIVATE);
		if(null == pref)
		{
			return 6;
		}
		return pref.getInt(POLICY, Activity.MODE_PRIVATE);
	}
	/*****************
	 * 设置发送report的间隔时间
	 * @param timeSpan
	 */
	public void setTimeSpan(int timeSpan)
	{
		this.report_time_span = timeSpan;
	}
	
	
	
}
