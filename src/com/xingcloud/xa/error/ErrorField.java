package com.xingcloud.xa.error;

import org.json.JSONException;
import org.json.JSONObject;

import com.xingcloud.xa.utils.XTimeStamp;



public class ErrorField {
	static private final String USER_ERROR="user.error";
	
	private String appId="";
	private String uid="";
	private String errorCode="";
	private String msg="";
	private int count=0;
	private String timesamp="";
	private JSONObject errors = new JSONObject();
	public ErrorField()
	{
	}
	public ErrorField(String appId,String uid,String msg)
	{
		this.appId=appId;
		this.uid=uid;
		//this.errorCode=errorCode;
		this.msg=msg;
		//this.count=count;
		try {
			//errors.put("errorCode", errorCode);
			errors.put("msg", msg);
			//errors.put("count", count);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public ErrorField(String appId,String uid,String errorCode,String msg,int count)
	{
		this.appId=appId;
		this.uid=uid;
		this.errorCode=errorCode;
		this.msg=msg;
		this.count=count;
		try {
			errors.put("errorCode", errorCode);
			errors.put("msg", msg);
			errors.put("count", count);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setAppId(String appId)
	{
		this.appId=appId;
	}
	
	public String getAppId()
	{
		return this.appId;
	}
	
	public void setTimestamp(String tstamp)
	{
		this.timesamp=tstamp;
	}
	
	public String getTimestamp()
	{
		return this.timesamp;
	}
	public void setUid(String uid)
	{
		this.uid=uid;
	}
	
	public String getUid()
	{
		return this.uid;
	}
	
	public void setErrorCode(String errorCode)
	{
		this.errorCode=errorCode;
		try {
			errors.put("errorCode", errorCode);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getErrorCode()
	{
		return this.errorCode;
	}
	
	public void setMsg(String msg)
	{
		this.msg=msg;
		try {
			errors.put("msg", msg);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getMsg()
	{
		return this.msg;
	}
	
	public void setCount(int count)
	{
		this.count=count;
		try {
			errors.put("count", count);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int getCount()
	{
		return this.count;
	}
	
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		//builder.append("\"appid\":"+this.appId+"&");
		builder.append("\"uid\":"+"\""+this.uid+"\",");
		builder.append("\"event\":"+"\""+USER_ERROR+"\",");
		
		if(null == errors)
		{
			builder.append("\"json_var\":");
			builder.append("[]");
			
		}
		else
		{
			builder.append("\"json_var\":");
			builder.append(this.errors.toString());
		}
		if(null == timesamp || "" == timesamp)
		{
			builder.append(",");
			builder.append("\"timestamp\":");
			builder.append("\""+XTimeStamp.getTimeStamp()+"\"");
		}
		else
		{
			builder.append(",");
			builder.append("\"timestamp\":");
			builder.append("\""+this.timesamp+"\"");
		}
		return builder.toString();
	}
}
