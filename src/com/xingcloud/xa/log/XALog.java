package com.xingcloud.xa.log;

import android.util.Log;

public class XALog {

	private static int loglevel = 0;
	public static final String TAG = "XingCloud";
	public static void setLoglevel(int level)
	{
		loglevel = level;
	}
	
	public static void debug(String tag, String message)
	{
		if(tag == null || tag.length() <= 0)
		{
			tag = TAG;
		}
		if(null == message)
		{
			message = "";
		}
		Log.d(tag, message);
	}
	
	public static void info(String tag, String message)
	{
		if(tag == null || tag.length() <= 0)
		{
			tag = TAG;
		}
		if(null == message)
		{
			message = "";
		}
		Log.i(tag, message);
	}
	
	public static void error(String tag, String message)
	{
		if(tag == null || tag.length() <= 0)
		{
			tag = TAG;
		}
		if(null == message)
		{
			message = "";
		}
//		String errMsg = "error_code:"+String.valueOf(errCode);
//		errMsg += ",";
//		errMsg += "message:"+message;
		Log.e(tag, message);
	}
	
	public static void Log(String tag, String message)
	{
		switch(loglevel)
		{
		case Level.DEBUG:
			debug(tag,message);
			break;
		case Level.ERROR:
			error(tag,message);
			break;
		case Level.INFO:
			info(tag,message);
			break;
		case Level.OFF:
			return;
		}
	}
}
