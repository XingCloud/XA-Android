package com.xingcloud.xa.sender;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Timer;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;


import com.xingcloud.analytic.xnative.XCNative;
import com.xingcloud.xa.XAReportCache;
import com.xingcloud.xa.custom.CustomEvent;
import com.xingcloud.xa.error.ErrorEvent;

import android.os.AsyncTask;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class ReportTask extends AsyncTask<Void, Void, Boolean>{
	
	private String baseUrl="http://analytic.337.com/";
	private String normalParam = "index.php?";
	private String customParam = "storelog.php?";
	private String pageUrl = "http://analytic.337.com/index.php?";
	private String url = "";
	private int eventId = 0;
	private String stamp;
	
	public ReportTask(String sUrl,int event)
	{
		url  = sUrl;
		eventId =event;
	}
	public ReportTask(String sUrl,String timestamp,int event)
	{
		url  = sUrl;
		stamp =timestamp;
		eventId =event;
	}
	private String encodeUrl(String param)
	{
		if(null == param || param.trim().length() <= 0)
		{
			return "";
		}
		else
		{
			
			while(param.contains(" "))
			{
				param = param.replace(" ", "-");
				if(!param.contains(" "))
				{
					break;
				}
			}
			return param;
		}
		
	}
	private  void doExecuteTask(String url)
	{
		int result = -1;
		if(null ==url || url.trim().length() <=0)
		{
			return;
		}
		url = encodeUrl(url);

		if(eventId == CustomEvent.CUSTOM_EVENT)
		{
			
			try {
				//URLEncoder.encode("UTF-8")
				
				result = XCNative.sendReport(baseUrl+normalParam+url);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}//XCNative.
		}	
		else if(eventId == CustomEvent.BATCH_EVENT_CUS)
		{
			try {
				//result = XCNative.sendPageReport((pageUrl+url));
				result = XCNative.sendPageReport((baseUrl+normalParam+url));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}//XCNative.
		}
		if(-1 == result || result == 0)
		{
			Timer timer = new Timer();
			timer.schedule(XAReportCache.sendTask, 50);
		}
//		else if(eventId == ErrorEvent.ERROR_EVENT || eventId == CustomEvent.BATCH_EVENT)
//		{
//			ReportThread reportThread = new ReportThread();
//			reportThread.start();
//		}
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	protected Boolean doInBackground(Void... params) {
		// TODO Auto-generated method stub
		try
		{
			doExecuteTask(url);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	private int sendReport(String url)
	{
		Log.d("XingCloud", url);
		HttpGet request =  new HttpGet(url);
		HttpClient client =  new DefaultHttpClient();
		HttpResponse response = null;
		try {
			response = client.execute(request);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(null != response && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
		{
			return 1;
		}
		else
		{
			return 0;
		}
		
	}
	private int sendPageReport(String url)
	{
		return sendReport(url);
	}
	
	private int sendBaseReport(String url)
	{
		return sendReport(url);
	}
	
	class ReportThread extends Thread { 
        public void run() { 
            super.run(); 
          //  Looper.prepare();
            try { 
            	Message message = new Message();
     			message.what = XAReportCache.REPORT_SUCCESS;
     			XAReportCache.sendHandler.sendMessage(message);
            } catch (Exception e) { 
                e.printStackTrace(); 
            } 
           // Looper.loop();
        } 
    }
}
