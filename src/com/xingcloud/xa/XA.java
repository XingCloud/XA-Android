package com.xingcloud.xa;



import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;


import com.xingcloud.analytic.xnative.XCNative;
import com.xingcloud.xa.custom.SignedParams;
import com.xingcloud.xa.custom.Stats;
import com.xingcloud.xa.device.DeviceInfo;
import com.xingcloud.xa.device.XLocation;
import com.xingcloud.xa.device.XNetwork;
import com.xingcloud.xa.error.CrashHandler;
import com.xingcloud.xa.log.Level;
import com.xingcloud.xa.report.CustomReport;
import com.xingcloud.xa.report.ReferenceField;
import com.xingcloud.xa.sender.HeartbeatService;

import com.xingcloud.xa.utils.XCTime;
import com.xingcloud.xa.utils.XTimeStamp;
import com.xingcloud.xa.utils.Xutils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;



public class XA {

	
	static String sUid = "";
	static String sAppid = "";
	private String gameUid= "";
	private static XA _instance =  null;
	static int mPolicy = 3;
	boolean bWaitForUid = false;
	public static int nlogLevel = Level.DEBUG;
	static SignedParams param = null;
	//XAReportCache rcache = null;
	long start_time = 0;
	long finish_time = 0;

	private XA()
	{
		//rcache = XAReportCache.instance();
		param = new SignedParams();
	}
	
	public static XA instance()
	{
		if(null == _instance)
		{
			_instance = new XA();
		}
		return _instance;
	}
	/*
	 * 设置或者修改应用ID
	 */
	public void setGameAppid(Activity activity,String appid)
	{
		if(null != appid && appid.length() >0 && activity != null)
		{
			Xutils.setAppid(appid);		
			updateGameUid(appid);
		}
	}
	
	public SignedParams getParam()
	{
		return param;
	}
	/**
	 * 更新游戏用户ID
	 * @param gameUid 游戏中的用户ID
	 */
	private void updateGameUid(String gameUid) {
		// TODO Auto-generated method stub
		if(null ==gameUid)
		{
			this.gameUid = "";
			return;
		}
		this.gameUid = gameUid;
	}
	
	private void setSignedParams(Activity ctx)
	{
		if(null == ctx)
		{
			Log.e("XingCloud", "context is null");
			return ;
		}
		param.setAppId(Xutils.getGameAppId(ctx));
		//param.setSnsUid(sUid);
		param.setUid(sUid);
		XAReportCache.instance().setSignedParams(param);
	}
	
	/**
	 * 设置心跳服务得间隔时间,请在onCreate之前调用
	 */
	public void setHeartbeatTimeOffset(int time_offset)
	{
		HeartbeatService.delayMillis = time_offset * 1000;
	}
	
	/**
	 * 获取游戏用户ID
	 * @return 返回用户ID
	 */
	public String getGameId()
	{
		return gameUid;
	}
	
	public void setUid(Context context,String uid)
	{
		sUid = uid;
		Xutils.saveUUID(context,sUid);
	}
	
	public String getUid()
	{
		return sUid;
	}
	
	
	public void setPolicy(int policy, Activity activity)
	{
		if(null == activity)
		{
			return;
		}
		mPolicy = policy;
		XAPolicy.getIntance().setReportPolicy(policy, activity);
	}
	
	public int getPolicy(Activity activity)
	{
		return XAPolicy.getIntance().getReportPolicy(activity);
	}
	
	public void setAppid(String appid)
	{
		sAppid = appid;
	}
	
	public String getAppid()
	{
		return sAppid;
	}
	
	/*
	 * 设置apk来源或者发布渠道
	 */
	public void setReference(String reference,Activity context) 
	{
		if(null ==  reference || reference.length() <=0)
		{
			return;
		}
	  //  Xutils.REF = reference;
	    Xutils.REF = ReferenceField.parseReference(reference, context);//(reference);
	    Xutils.bRef = true;
	}
	
	
	public String getReference()
	{
		return Xutils.REF;
	}
	
	
	public void startHeartBeat()
	{
		trackHeartBeat();
	}
	
	public void stopHeartBeat()
	{
		try
		{
			XAReportCache.instance().getContext().unbindService(conn);
		}
		catch(Exception e)
		{
			Log.e("XingCloud","No service to unbind");
		}
	}
	
	public void waitForUid()
	{
		bWaitForUid = true;
	}
	
	public boolean isWaitForUid()
	{
		return bWaitForUid;
	}
	
	public void setLogLevel(int logLevel)
	{
		nlogLevel = logLevel;
	}
	private void sendGameStartReport(Activity context)
	{
		if(null ==context)
		{
			throw new  Error("The context param is null");
		}
		JSONObject values =  new JSONObject();
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("data", values);
		Stats stat = new Stats();	
		stat.setCustomData(XAEvent.PAGE_VIEW, params.get("data").toString(), XTimeStamp.getTimeStamp());
		SignedParams param1 = new SignedParams();
		param1.setAppId(Xutils.getGameAppId(context));
		param1.setUid(Xutils.generateUUID2(context));
		CustomReport cusReport = new CustomReport(param1,stat);
		if(XAPolicy.getIntance().getReportPolicy(context) == XAPolicy.REALTIME)
		{
			if(Xutils.isAppNetworkPermit(context) && Xutils.isNetworkAvailable(context) )
			{
				if((sUid==null||sUid.length() <=0 )&& Xutils.generateUUID(context).length() <=0)
				{
					XAReportCache.instance().addNoIdReports(cusReport);
				}
				else
				{
					if(sUid==null||sUid.length() <=0 )
					{
						sUid =  Xutils.generateUUID2(context);
					}
					XAReportCache.instance().sendCustomReportNow(cusReport);
					//currentReport = cusReport;
					XAReportCache.instance().setCurrentReport(cusReport);
				}
				
			}
			else
			{
				if((sUid==null||sUid.length() <=0 )&& Xutils.generateUUID(context).length() <=0)
				{
					XAReportCache.instance().addNoIdReports(cusReport);
				}
				else
				{
					XAReportCache.instance().addReport(cusReport);
				}			
			}
			
		}
		else 
		{
			if((sUid==null||sUid.length() <=0 )&& Xutils.generateUUID(context).length() <=0)
			{
				XAReportCache.instance().addNoIdReports(cusReport);
			}
			else
			{
				XAReportCache.instance().addReport(cusReport);
			}	
		}
	}
	public void trackEvent(String eventName, Map<String,Object> params, Activity context)
	{
		if(null ==context)
		{
			throw new  Error("The context param is null");
		}
		
		Stats stat = new Stats();
		

		stat.setCustomData(eventName, params.get("data").toString(), XTimeStamp.getTimeStamp());
		
		CustomReport cusReport = new CustomReport(param,stat);
		if(XAPolicy.getIntance().getReportPolicy(context) == XAPolicy.REALTIME)
		{
			if(Xutils.isAppNetworkPermit(context) && Xutils.isNetworkAvailable(context) )
			{
				if((sUid==null||sUid.length() <=0 )&& Xutils.generateUUID(context).length() <=0)
				{
					XAReportCache.instance().addNoIdReports(cusReport);
				}
				else
				{
					if(sUid==null||sUid.length() <=0 )
					{
						sUid =  Xutils.generateUUID(context);
					}
					XAReportCache.instance().sendCustomReportNow(cusReport);
					//currentReport = cusReport;
					XAReportCache.instance().setCurrentReport(cusReport);
				}
				
			}
			else
			{
				if((sUid==null||sUid.length() <=0 )&& Xutils.generateUUID(context).length() <=0)
				{
					XAReportCache.instance().addNoIdReports(cusReport);
				}
				else
				{
					XAReportCache.instance().addReport(cusReport);
				}			
			}
			
		}
		else 
		{
			if((sUid==null||sUid.length() <=0 )&& Xutils.generateUUID(context).length() <=0)
			{
				XAReportCache.instance().addNoIdReports(cusReport);
			}
			else
			{
				XAReportCache.instance().addReport(cusReport);
			}	
		}
	}
	
	public void setReportUid(Context context,String userId)
	{
		if(null != context && null != userId)
		{
			Xutils.saveUUID(context,userId);
			sUid = userId;
			XAReportCache.instance().sendNoIdReports(sUid);
		}
		
	}
	
	
	public void onCreate(Activity context) {
		// TODO Auto-generated method stub
		
		if(null ==context)
		{
			throw new  Error("GDP Analytic initilize failure, the Activity param is null");
		}
		XAReportCache.instance().setContext(context);
		XCNative.initCurl("init");
		//start_time = time.getCurrentTime();
		start_time = System.currentTimeMillis();
		try
		{
			if(null == sUid || sUid.length() <=0)
			{
				sUid = Xutils.generateUUID(context);
			}
			
			gameUid = Xutils.getGameAppId(context);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		setSignedParams(context);
		
		if(Xutils.isSendCrashLogs(context, "android.permission.READ_LOGS"))
		{
			setErrorHandler(context);
		}
		trackHeartBeat();
		String uuid = Xutils.getUUID(context);
		if( uuid == null || uuid.length() <= 0)
		{
			Xutils.saveUUID(context,sUid);
			XAReportCache.instance().sendUpdateReport();
		}
		else
		{
			sUid = uuid;
		}
		XAReportCache.instance().sendLastReports();
		
		XAReportCache.instance().startTimer();
		if(!bWaitForUid)
		{
			XAReportCache.instance().sendVisitReport();
		}
		
		
		if(XAPolicy.getIntance().getReportPolicy(context) == XAPolicy.BATCH_AT_LAUNCH)
		{
			XAReportCache.instance().sendCurrentReports();
		}
		
	}
	/**
	 * 在用户退出时发送
	 * @param context 当前activity的实例
	 */
	public void onFinish(Activity context) {
		// TODO Auto-generated method stub
		if(null ==context)
		{
			throw new  Error("The Activity param is null");
		}
		finish_time = System.currentTimeMillis();
		
		if(!bWaitForUid)
		{
			long ptime = ((finish_time - start_time) )/1000;
			XAReportCache.instance().sendFinishReport(ptime);
		}

		try
		{
			context.unbindService(conn);
		}
		catch(Exception e)
		{
			Log.e("XingCloud","No service to unbind");
		}
		
		if(XAPolicy.getIntance().getReportPolicy(context) == XAPolicy.BATCH_AT_TERMINATE)
		{
			XAReportCache.instance().sendCurrentReports();
		}
		else
		{
			XAReportCache.instance().saveReportWithoutNetwork(context);
		}
		
	}
	
	
	/**
	 * 设置错误处理函数,处理未知异常
	 * @param context 前activity的上下文实例
	 */
	public void setErrorHandler(Activity context) {
		if(null ==context)
		{
			throw new  Error("GDP error handler initilize failure, the Activity param is null");
		}
		CrashHandler handler = CrashHandler.getInstance();
		handler.init(context);
	}
	
	public void trackHeartBeat()
	{
	//	Intent i = new Intent(this, HeartbeatService.class);
		if(null == XAReportCache.instance().getContext())
		{
			Log.d("XingCloud", "context is null");
			return;
		}
		Intent service=new Intent(XAReportCache.instance().getContext(),HeartbeatService.class);//显示意图    
		//mContext.startService(service);    
		XAReportCache.instance().getContext().bindService(service, conn, Context.BIND_AUTO_CREATE);
		
		
	}
	
	/**
	 * 测试网络速度，并且把网络的下载和上传得网速log到服务器
	 */
	public void trackNetworkSpeed()
	{
		XAReportCache.instance().trackNetworkSpeed();
	}
	
	private HeartbeatService mService;
	
	
	private ServiceConnection conn = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			// TODO Auto-generated method stub
			mService= ((HeartbeatService.ServiceBinder)arg1).getService();
			//mService.onCreate();
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			// TODO Auto-generated method stub
			mService =null;
		}
		
	};
	public long getDuration(long finishtime)
	{
		if(finishtime <= 0 )
		{
			finishtime =  System.currentTimeMillis();
		}
		return ((finishtime - start_time) )/1000;
	}
	
}
