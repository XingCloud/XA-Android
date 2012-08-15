package com.xingcloud.analytic.sender;

import org.json.JSONException;
import org.json.JSONObject;

import com.xingcloud.analytic.CloudAnalytic;
import com.xingcloud.analytic.report.UserReport;
import com.xingcloud.analytic.user.UserEvent;
import com.xingcloud.analytic.user.UserField;
import com.xingcloud.analytic.utils.XTimeStamp;
import com.xingcloud.analytic.utils.Xutils;

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
				item.put("time_offset", "10min");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	UserField user = new UserField();
	        user.setAppId(CloudAnalytic.instance().getGameId());
	        user.setUId(CloudAnalytic.instance().getUid());
	        
	        user.setEvent(UserEvent.USER_HEART_BEAT);
	        user.setJsonVar(item);
	        user.setRef("");
	        user.setTimestamp(XTimeStamp.getTimeStamp());
	        UserReport report = new UserReport(user.toStringBa(),Integer.valueOf(user.getEvent()));
	        if(Xutils.isAppNetworkPermit(CloudAnalytic.instance().getContxt()) && Xutils.isNetworkAvailable(CloudAnalytic.instance().getContxt()))
	        {
	        	if(CloudAnalytic.instance().getUid().length() >0)
	        	{
	        		//CloudAnalytic.instance().trackUserEvent(CloudAnalytic.instance().getContxt(),  user);
	        		
	        		report.reportUserAction(user, Integer.valueOf(user.getEvent()));
	        	}
	        	else
	        	{
	        		CloudAnalytic.instance().addNoIdReports(user);
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
		super.onRebind(i);
	}
	@Override 
	 public boolean onUnbind(Intent i) {   
         //Log.i(TAG, "Service.onUnbind");   
        return false;   
     }   
}
