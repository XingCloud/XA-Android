package com.xingcloud.analytic.utils;


import java.util.Date;


public class XTimeStamp {

	public static String getTimeStamp()
	{
		Date date = new Date();
		//System.nanoTime();
		return String.valueOf(date.getTime());
	}
}
