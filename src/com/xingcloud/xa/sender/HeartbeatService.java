package com.xingcloud.xa.sender;

import org.json.JSONException;
import org.json.JSONObject;

import com.xingcloud.xa.XA;
import com.xingcloud.xa.XAEvent;
import com.xingcloud.xa.XAReportCache;
import com.xingcloud.xa.custom.SignedParams;
import com.xingcloud.xa.custom.Stats;
import com.xingcloud.xa.report.CustomReport;
import com.xingcloud.xa.utils.XTimeStamp;
import com.xingcloud.xa.utils.Xutils;



import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class HeartbeatService extends Service {

	public static long delayMillis= 5 * 60 * 1000;//10 * 60 * 1000
	private Handler msgHandle =  new Handler();
	private int statusCode;   
	private ServiceBinder localBinder = new ServiceBinder();   
	private Runnable mTask = new Runnable(){
		@Override
		public void run() {
			// TODO Auto-generated method stub
	    	JSONObject item =  new JSONObject();
	    	//item = null;
	    	try {
				item.put("time_offset", delayMillis/1000);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	       
	        SignedParams param =  new SignedParams();
	        param.setAppId(Xutils.getGameAppId(XAReportCache.instance().getContext()));
			//param.setSnsUid(Xutils.generateUUID(XAReportCache.instance().getContext()));
			param.setUid(Xutils.generateUUID(XAReportCache.instance().getContext()));
	        Stats stat = new Stats();
			

			stat.setCustomData(XAEvent.USER_HEART_BEAT, item.toString(), XTimeStamp.getTimeStamp());
			
			CustomReport cusReport = new CustomReport(param,stat);
	        
	        if(Xutils.isAppNetworkPermit(XAReportCache.instance().getContext()) && Xutils.isNetworkAvailable(XAReportCache.instance().getContext()))
	        {
	        	if(XA.instance().getUid().length() >0)
	        	{
	        		//CloudAncusReporrcache.sendCustomReportNow(cusReport);
	        		XAReportCache.instance().sendCustomReportNow(cusReport);
	        	}
	        	else
	        	{
	        		XAReportCache.instance().addNoIdReports(cusReport);
	        		Log.i("XingCloud", "Uid is empty, sending later...");
	        	}
	        }
	        else
	        {
	        	Log.d("XingCloud", "Network is not available for your device");
	        }
			msgHandle.postDelayed(mTask, delayMillis);
		}
		
	};
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return localBinder;
	}

	@Override
	public void onStart(Intent intent,int startId)
	{
		
		super.onStart(intent, startId);
	}
	
	@Override
	public void onCreate()
	{
		
		super.onCreate();
		msgHandle.post(mTask);
	}
	
	@Override
	public void onDestroy()
	{
		
		super.onDestroy();
		msgHandle.removeCallbacks(mTask);
	}
	
	public class ServiceBinder extends Binder {
		
		
		public HeartbeatService getService()
		{
			return HeartbeatService.this;
		}
		 public int getStatusCode() {   
	            return statusCode;   
	      }   
	}
	
	@Override 
	public void onRebind(Intent i)
	{
		
	}
	@Override 
	 public boolean onUnbind(Intent i) {   
         //Log.i(TAG, "Service.onUnbind");   
        return false;   
     }   
}
