package com.xingcloud.xa.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class XCTime {
	private Date date;
	public XCTime()
	{
		date = new Date();
	}
	public String getCurrentTimeStr()
	{
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//Date date = new Date();
		Timestamp now = new Timestamp(date.getTime());	
		return now.toString();
	}
	
	public long getCurrentTime()
	{
		//Date date = new Date();
		return date.getTime();
	}
	
	public int getTimeSpan(long pretime,long curtime)
	{
		int span = (int)(curtime-pretime)/1000 ;
		if(span < 0)
		{
			span = 0;
		}
		return span;
	}
	public int getMonth()
	{
		
		return date.getMonth();
	}
	
	public int getHour()
	{
		return date.getHours();
	}
	
	public int getMinute()
	{
		return date.getMinutes();
	}
	
	public int getSecond()
	{
		return date.getSeconds();
	}
	
	public int getDay()
	{
		return date.getDay();
	}
}
