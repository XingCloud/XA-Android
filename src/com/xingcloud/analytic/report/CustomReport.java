package com.xingcloud.analytic.report;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.os.AsyncTask.Status;

import com.xingcloud.analytic.CloudAnalytic;
import com.xingcloud.analytic.custom.CustomEvent;
import com.xingcloud.analytic.custom.CustomField;
import com.xingcloud.analytic.custom.ICustomAction;
import com.xingcloud.analytic.custom.SignedParams;
import com.xingcloud.analytic.custom.Stats;
import com.xingcloud.analytic.sender.ReportTask;
import com.xingcloud.analytic.utils.XTimeStamp;
import com.xingcloud.analytic.utils.Xutils;
import com.xingcloud.analytic.xnative.XCNative;

public class CustomReport implements ICustomAction{

//private static CustomReport _instance;
	String custom_resource = "";
	SignedParams param;
	Stats stats;
	ReportTask ctask = null;
	ReportTask btask = null;
	int normarl_bat=0;
	public CustomReport(String content)
	{
		custom_resource = content;
	}
	public CustomReport(String content,int bat)
	{
		custom_resource = content;
		normarl_bat =bat;
	}
	public CustomReport()
	{
		
	}
	public CustomReport(SignedParams param,Stats stats)
	{
		this.param = param;
		this.stats = stats;
	}
	
	public Stats getStat()
	{
		return (stats == null)?null:stats;
	}
	
	public SignedParams getSignedParams()
	{
		return param;
	}
//	public static CustomReport instance()
//	{
//		if(null == _instance)
//		{
//			_instance = new CustomReport();
//		}
//		return _instance;
//	}
	private void sendCustomReport(String params)
	{
		if(null == params )
		{
			throw new Error("report params is null");
		}
		if(null == ctask || ctask.isCancelled() || ctask.getStatus() == Status.FINISHED || ctask.getStatus() == Status.PENDING)
		{
			try
			{
				ctask =  new ReportTask(params,CustomEvent.CUSTOM_EVENT);
				ctask.execute();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		if(null != ctask && ctask.getStatus() == Status.FINISHED)
		{
			try
			{
				ctask.cancel(true);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
		}
		//XCNative.sendBaseReport(user_data.toStringEx(), XTimeStamp.getTimeStamp());
		
		//XCNative.sendReport(params);
	}
	private void sendBatchReport(String params, int from)
	{
		if(null == params )
		{
			throw new Error("report params is null");
		}
		if(null == btask || btask.isCancelled() || btask.getStatus() == Status.FINISHED || btask.getStatus() == Status.PENDING)
		{
			try
			{
				if(from == 1)
				{
					//BATCH_EVENT_CUS
					btask =  new ReportTask(params,CustomEvent.BATCH_EVENT_CUS);
				}
				else
				{
					btask =  new ReportTask(params,CustomEvent.BATCH_EVENT);
				}
				//btask =  new ReportTask(params,CustomEvent.BATCH_EVENT);
				//XCNative.sendBaseReport(user_data.toStringEx(), XTimeStamp.getTimeStamp());
				btask.execute();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		if(null != btask && btask.getStatus() == Status.FINISHED)
		{
			try
			{
				btask.cancel(true);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
		}
		//task.
		//XCNative.sendReport(params);
	}
	@Override
	public void reportCustomAction(CustomField custom_data) {
		// TODO Auto-generated method stub
		if(null == custom_data || null == custom_data.getSignedParams() || null == custom_data.getStats())
		{
			throw new Error("CustomField data is not provided");
		}
		try {
			StringBuilder params = new StringBuilder();
			params.append("appid=");
			
			params.append(URLEncoder.encode(custom_data.getAppid(),"UTF-8"));
			
			params.append("&");
			params.append("log=");
			params.append(custom_data.toString());//URLEncoder.encode(custom_data.toString(),"UTF-8")
			try
			{
				sendCustomReport(params.toString());
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	public void reportCustomAction(SignedParams param, Stats stats) {
		// TODO Auto-generated method stub
		if(null == param || null == stats )
		{
			throw new Error("report data is not provided");
		}
		try {
			CustomField custom = new CustomField(param,stats);
			
			StringBuilder params = new StringBuilder();
			params.append("appid=");
			
			params.append(URLEncoder.encode(custom.getAppid(),"UTF-8"));
			
			params.append("&");
			params.append("log=");
			params.append(custom.toString());//URLEncoder.encode(,"UTF-8")
			try
			{
				sendCustomReport(params.toString());
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			custom = null;
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public String getAppid()
	{
		if(param != null)
		{
			return param.getAppId();
		}
		return "";
	}
	public String getContent()
	{
		if( param== null && stats == null)
		{
			if(custom_resource != null)
			{
				return custom_resource;
			}
			else
			{
				return "";
			}
		}
		else
		{
			CustomField custom = new CustomField(param,stats);
			StringBuilder params = new StringBuilder();
//			params.append("appid=");
//			params.append(custom.getAppid());
//			params.append("&");
//			params.append("log=");
			params.append(custom.toString());
			return params.toString();
		}
	}
	@Override
	public void reportCustomAction(String custom_data) {
		// TODO Auto-generated method stub
		if(null == custom_data )
		{
			throw new Error("report data is not provided");
		}
		
		try
		{
			sendCustomReport(custom_data);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void reportCustomAction() {
		// TODO Auto-generated method stub
		//custom_resource
		if( param== null && stats == null)
		{
			if(custom_resource != null &&custom_resource.compareToIgnoreCase("[]") != 0 && custom_resource.compareToIgnoreCase("") != 0&& custom_resource.compareToIgnoreCase(" ") != 0)
			{
				if( normarl_bat == 0)
				{
					reportBatchAction(custom_resource);
				}
				else
				{
					reportrealAction(custom_resource);
				}
			}
			//
			
		}
		else
		{
			reportCustomAction(this.param,this.stats);
		}
		
	}
	
	public void reportrealAction(String custom_data) {
		// TODO Auto-generated method stub
		if(null == custom_data)
		{
			throw new Error("report data is not provided");
		}
		StringBuilder params = new StringBuilder();
//		if(!custom_data.contains("appid="))
//		{
//			
//		}
		try {
			params.append("appid=");
			
			params.append(URLEncoder.encode(CloudAnalytic.instance().getGameId(),"UTF-8"));
			
			params.append("&");
			params.append("log=");
			params.append(URLEncoder.encode(custom_data,"UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try
		{
			sendBatchReport(params.toString(),1);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
	@Override
	public void reportBatchAction(String custom_data) {
		// TODO Auto-generated method stub
		if(null == custom_data)
		{
			throw new Error("report data is not provided");
		}
		StringBuilder params = new StringBuilder();
//		if(!custom_data.contains("appid="))
//		{
//			
//		}
		try {
			params.append("appid=");
			
			params.append(URLEncoder.encode(CloudAnalytic.instance().getGameId(),"UTF-8"));
			
			params.append("&");
			params.append("logs=");
			params.append(URLEncoder.encode(custom_data,"UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try
		{
			sendBatchReport(params.toString(),0);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
	

}
