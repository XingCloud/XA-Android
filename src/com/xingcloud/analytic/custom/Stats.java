package com.xingcloud.analytic.custom;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Stats {
	String timeStamp;
	String statFunction;
	String _data;
	
	public Stats()
	{}
	public Stats(String timestamp,String statfunction)
	{
		this.timeStamp = timestamp;
		this.statFunction = statfunction;
	}
	public Stats(String timestamp,String statfunction,String data)
	{
		this.timeStamp = timestamp;
		this.statFunction = statfunction;
		this._data =  data;
	}
	public void setCustomData(String data)
	{
		_data =  data;
	}
	
	public void setStatFunction(String function)
	{
		this.statFunction = function;
	}
	
	public void setTimestamp(String timestamp)
	{
		this.timeStamp = timestamp;
	}
	public void setCunstomData(String type,String level1,String level2,String level3,String level4,String level5,int amount)
	{
		
		StringBuilder build = new StringBuilder();
		build.append("[");
		
		if(null ==  type)
		{
			type = "";
		}
		build.append("\"");
		build.append(type);
		build.append("\",");
		
		if(null ==  level1)
		{
			level1 = "";
		}
		build.append("\"");
		build.append(level1);
		build.append("\",");
		
		if(null ==  level2)
		{
			level2 = "";
		}
		build.append("\"");
		build.append(level2);
		build.append("\",");
		
		
		if(null ==  level3)
		{
			level3 = "";
		}
		build.append("\"");
		build.append(level3);
		build.append("\",");
		
		if(null ==  level4)
		{
			level4 = "";
		}
		build.append("\"");
		build.append(level4);
		build.append("\",");
		
		if(null ==  level5)
		{
			level5 = "";
		}
		build.append("\"");
		build.append(level5);
		build.append("\",");
		String count = "";
		if(amount < 0)
		{
			count = "";
		}
		count  = String.valueOf(amount);
		build.append("\"");
		build.append(count);
		build.append("\"");
		
		build.append("]");
		_data = build.toString();
//		try {
//			_data = new JSONArray(build.toString());
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	public void setCunstomData(String type,String level1,String level2,String level3,String level4,String level5,String amount)
	{
		StringBuilder build = new StringBuilder();
		build.append("[");
		
		if(null ==  type)
		{
			type = "";
		}
		build.append("\"");
		build.append(type);
		build.append("\",");
		
		if(null ==  level1)
		{
			level1 = "";
		}
		build.append("\"");
		build.append(level1);
		build.append("\",");
		
		if(null ==  level2)
		{
			level2 = "";
		}
		build.append("\"");
		build.append(level2);
		build.append("\",");
		
		
		if(null ==  level3)
		{
			level3 = "";
		}
		build.append("\"");
		build.append(level3);
		build.append("\",");
		
		if(null ==  level4)
		{
			level4 = "";
		}
		build.append("\"");
		build.append(level4);
		build.append("\",");
		
		if(null ==  level5)
		{
			level5 = "";
		}
		build.append("\"");
		build.append(level5);
		build.append("\",");
		
		if(amount == null)
		{
			amount = "";
		}
		
		build.append("\"");
		build.append(amount);
		
		build.append("]");
		_data = build.toString();
//		try {
//			_data = new JSONArray(build.toString());
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		build = null;
	}
	
	public String toString()
	{
		StringBuilder build = new StringBuilder();
		build.append("{");
		
		build.append("\"timestamp\":");
		build.append("\""+this.timeStamp+"\"");
		build.append(",");
		
		build.append("\"statfunction\":");
		build.append("\""+this.statFunction+"\"");
		build.append(",");
		
		build.append("\"data\":");
		build.append(this._data);
		
		build.append("}");
		
		return build.toString();
	}
}
