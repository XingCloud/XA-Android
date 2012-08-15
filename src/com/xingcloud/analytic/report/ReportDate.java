package com.xingcloud.analytic.report;

import java.util.Date;

import com.xingcloud.analytic.utils.XCTime;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;


public class ReportDate {
	private Date date;
	private String REPORT_TIME_PREF = "report";
	public String REPORT_TIME = "report_time";
	public String REPORT_FLAG = "reported";
	public String REPORT_MONTH = "month";
	public String REPORT_DAY = "day";
	public String REPORT_HOUR = "hour";
	public String REPORT_MINUTE="minute";
	public String REPORT_SECOND = "second";
	XCTime time;
	public  ReportDate()
	{
		 date = new Date();
		 time =  new XCTime();
	}
	
	public void setReportTime(Context activity)
	{
		SharedPreferences pref = activity.getSharedPreferences(REPORT_TIME_PREF, Activity.MODE_PRIVATE);

		SharedPreferences.Editor editor = pref.edit();
		editor.putLong(REPORT_TIME, time.getCurrentTime());
		editor.putInt(REPORT_MONTH, time.getMonth());
		editor.putInt(REPORT_DAY, time.getDay());
		editor.putInt(REPORT_HOUR, time.getHour());
		editor.putInt(REPORT_MINUTE, time.getMinute());
		editor.putInt(REPORT_SECOND, time.getSecond());
		editor.putBoolean(REPORT_FLAG, true);
		editor.commit();
	}
	
	public long getLastReportTime(Context activity)
	{
		SharedPreferences pref = activity.getSharedPreferences(REPORT_TIME_PREF, Activity.MODE_PRIVATE);
		//SharedPreferences.Editor editor = pref.edit();
		//editor.//
		if(null == pref)
		{
			return 0;
		}
		return pref.getLong(REPORT_TIME, Activity.MODE_PRIVATE);
	}
	
	public int getLastReportMonth(Context activity)
	{
		SharedPreferences pref = activity.getSharedPreferences(REPORT_TIME_PREF, Activity.MODE_PRIVATE);
		if(null == pref)
		{
			return 0;
		}
		return pref.getInt(REPORT_MONTH, Activity.MODE_PRIVATE);
	}
	 
	public int getLastReportDay(Context activity)
	{
		SharedPreferences pref = activity.getSharedPreferences(REPORT_TIME_PREF, Activity.MODE_PRIVATE);
		if(null == pref)
		{
			return 0;
		}
		return pref.getInt(REPORT_DAY, Activity.MODE_PRIVATE);
	}
	
	public int getLastReportHour(Context activity)
	{
		SharedPreferences pref = activity.getSharedPreferences(REPORT_TIME_PREF, Activity.MODE_PRIVATE);
		if(null == pref)
		{
			return 0;
		}
		return pref.getInt(REPORT_HOUR, Activity.MODE_PRIVATE);
	}
	
	public int getLastReportMinute(Context activity)
	{
		SharedPreferences pref = activity.getSharedPreferences(REPORT_TIME_PREF, Activity.MODE_PRIVATE);
		if(null == pref)
		{
			return 0;
		}
		return pref.getInt(REPORT_MINUTE, Activity.MODE_PRIVATE);
	}
	
	public int getLastReportSecond(Context activity)
	{
		SharedPreferences pref = activity.getSharedPreferences(REPORT_TIME_PREF, Activity.MODE_PRIVATE);
		if(null == pref)
		{
			return 0;
		}
		return pref.getInt(REPORT_SECOND, Activity.MODE_PRIVATE);
	}
	//public void getMonth()
}
