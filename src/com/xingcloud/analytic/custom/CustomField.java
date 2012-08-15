package com.xingcloud.analytic.custom;

import java.util.ArrayList;

import android.provider.Browser;

public class CustomField {
	SignedParams sParams;
	ArrayList<String> stats;
	
	public CustomField()
	{
		sParams = new SignedParams();
		stats = new ArrayList<String>();
	}
	
	public CustomField(SignedParams signedParams,Stats stat)
	{
		sParams  = signedParams;
		if(stats == null)
		{
			stats =  new ArrayList<String>();
		}
		stats.add(stat.toString());
	}
	
	public CustomField(SignedParams signedParams,ArrayList<Stats> stat)
	{
		sParams  = signedParams;
		if(stats == null)
		{
			stats =  new ArrayList<String>();
		}
		for(int i=0; i < stat.size(); i++)
		{
			stats.add(stat.get(i).toString());
		}
	}
	
	
	public void setSignedParams(SignedParams param)
	{
		sParams = param;
	}
	
	public SignedParams getSignedParams()
	{
		return sParams;
	}
	public void setStats(Stats stat)
	{
		if(stats == null)
		{
			stats =  new ArrayList<String>();
		}
		stats.add(stat.toString());
	}
	
	public void setStats(ArrayList<Stats> stat)
	{
		if(stats == null)
		{
			stats =  new ArrayList<String>();
		}
		for(int i=0; i < stat.size(); i++)
		{
			stats.add(stat.get(i).toString());
		}
	}
	
	public ArrayList<String> getStats()
	{
		return stats;
	}
	public String getAppid()
	{
		return sParams.getAppId();
	}
	
	public String toString()
	{
		StringBuilder build = new StringBuilder();
	
		build.append("{");
		build.append("\"signedParams\":");
		if(null == sParams)
		{
			sParams = new SignedParams();
		}
		build.append(sParams.toString());
		build.append(",");
		if(stats == null)
		{
			stats =  new ArrayList<String>();
		}
		build.append("\"stats\":");
		build.append("[");
		for(int i = 0; i < stats.size(); i++)
		{
			if(i != 0)
			{
				build.append(",");
			}
			build.append(stats.get(i));
		}
		build.append("]");
		
		build.append("}");
		
		return build.toString();
	}
}
