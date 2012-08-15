package com.xingcloud.analytic;

import java.io.File;
import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.xingcloud.analytic.custom.AnalyticFunction;
import com.xingcloud.analytic.custom.CustomField;
import com.xingcloud.analytic.custom.SignedParams;
import com.xingcloud.analytic.custom.Stats;
import com.xingcloud.analytic.device.DeviceInfo;
import com.xingcloud.analytic.device.XLocation;
import com.xingcloud.analytic.device.XNetwork;
import com.xingcloud.analytic.error.CrashHandler;
import com.xingcloud.analytic.report.CustomReport;
import com.xingcloud.analytic.report.ErrorReport;
import com.xingcloud.analytic.report.ReferenceField;
import com.xingcloud.analytic.report.UserReport;
import com.xingcloud.analytic.sender.HeartbeatService;
import com.xingcloud.analytic.user.UserEvent;
import com.xingcloud.analytic.user.UserField;
import com.xingcloud.analytic.utils.DownloadTester;
import com.xingcloud.analytic.utils.DownloadTester.GetHostListener;
import com.xingcloud.analytic.utils.DownloadTester.NetworkListener;
import com.xingcloud.analytic.utils.FileHelper;
import com.xingcloud.analytic.utils.LogTag;
import com.xingcloud.analytic.utils.XCTime;
import com.xingcloud.analytic.utils.XTimeStamp;
import com.xingcloud.analytic.utils.Xutils;
import com.xingcloud.analytic.xnative.XCNative;

public class CloudAnalytic implements ICloud{
	public static final String UUID_PREF_NAME="xcuuid";
	public static final String UUID_NAME = "uuid";
	
	private  static final int APPROVAL_SEND = 1;
	public static final int REPORT_ERROR = 2;
	public static final int REPORT_SUCCESS = 3;
	private HashMap<Object,Boolean> reports;
//	private Context context;
	private SignedParams param = null;
	XCTime time = new XCTime();
	private long start_time = 0;
	private long pause_time = 0;
	private long resume_time = 0;
	private long finish_time = 0;
	private static CloudAnalytic _instance;
	private String sUid = "";
	private String gameUid= "";
	private UserField userInfo;
	public int time_span=10;
	public int count_span = 5;
	Context mContext = null;
	static LooperThread th;
	private  Object currentReport = null;
	long lastTime = 0;
	long currentTime =0;
	private static int mPolicy = 6;
	private Boolean sendByCustomer =  false;
	//customRask t;
	Vector emptyIdReports=null;
	
	
	public long getDuration(long finishtime)
	{
		return ((finishtime - start_time) )/1000;
	}
	
	
	public Context getContxt()
	{
		return mContext;
	}
	
	protected CloudAnalytic()
	{
		reports = new HashMap<Object,Boolean>();
		param = new SignedParams();
	}
	
	/**************************
	 * 获得CloudAnalytic的实例
	 * @return
	 */
	public static CloudAnalytic instance()
	{
		if(_instance == null)
		{
			_instance =  new CloudAnalytic();
		}
		
		return _instance;
	}
	
	private synchronized void addReport(Object report)
	{
		//Log.d("XingCloud", ""+1);
		if(null == reports)
		{
			reports = new HashMap<Object,Boolean>();
		}
		if(null == report)
		{
			Log.e("XingCloud","report content is null");
			return;
		}
		
		synchronized (reports) 
		{
			if(null == reports)
			{
				reports = new HashMap<Object,Boolean>();
			}
			if(null == report)
			{
				Log.e("XingCloud","report content is null");
				return;
			}
		//	int p = ReportPolicy.getIntance().getReportPolicy(mContext);
			if((count_span > 0 && reports != null && reports.size() >= count_span) )
			{
				if(Xutils.isAppNetworkPermit(mContext) && Xutils.isNetworkAvailable(mContext) )
				{
					
//							//if(ReportPolicy.getIntance().getReportPolicy(context) == ReportPolicy.REALTIME)
//							{
					sendBatchReport();
					removeAllReport();
							//}
						//}
				}
				else
				{
					List<Object> reportList = new ArrayList<Object>();
					if(null != reports && reports.size() > 0)
		        	{
		        		Set<Object> objs =  reports.keySet();
		        		if(!objs.isEmpty())
		        		{
		        			Iterator<Object> it = objs.iterator();
		            		while(it.hasNext())
		            		{
		            			Object bj = it.next();
		            			if(!reportList.contains(bj))
		            			{
		            				reportList.add(bj);
		            			}
		            		}
		        		}
		        		
		        	}
					Xutils.getIntance().saveReport(mContext, reportList);
					reportList = null;
					removeAllReport();
				}
				
				reports = new HashMap<Object,Boolean>();
				//}
				reports.put(report, false);
			}
			else
			{
				reports.put(report, false);
			}
			
			XCTime time = new XCTime();
			currentTime = time.getCurrentTime();
			
			
//			else if(mPolicy == ReportPolicy.REALTIME)
//			{
//				sendBatchReport();
//				removeAllReport();
//			}
		}
		
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
			update(activity);
		}
	}
	/*
	 * 设置或修改应用的ID
	 */
	public void setGameAppid(String appid)
	{
		if(null != appid && appid.length() >0)
		{
			Xutils.setAppid(appid);		
			updateGameUid(appid);
		}
	}
	
	public boolean isSendOutside()
	{
		return sendByCustomer;
	}
	public void sendVisitAndQuitOutside(Boolean flag)
	{
		sendByCustomer = flag;
	}
	private synchronized void removeAllReport()
	{
		//
		if(reports == null|| reports.size() <=0 )
		{
			return;
		}
		synchronized (reports)
		{
			if(reports == null|| reports.size() <=0 )
			{
				return;
			}
			if(reports != null)
				reports.clear();
				reports = null;
		}
//		if(ReportPolicy.getIntance().getReportPolicy(mContext) == ReportPolicy.DEFAULT)
//		{
//			XCTime time = new XCTime();
//			lastTime = time.getCurrentTime();
//		}
		Xutils.getIntance().deleteReport(mContext);
	}
	private void removeReport(Object report)
	{
		Set<Object> objs = reports.keySet();
		if(null == objs)
		{
			return;
		}
		if(!objs.isEmpty())
		{
			objs.remove(report);
		}
	}
	public  HashMap<Object,Boolean> getReports()
	{
		return reports;
	}
	public  Object getCurrentReport()
	{
		return currentReport;
	}
	public synchronized void addNoIdReports(Object report)
	{
		if(emptyIdReports == null)
		{
			emptyIdReports =  new Vector<Object>();
		}
		synchronized(emptyIdReports)
		{
			if(emptyIdReports == null)
			{
				emptyIdReports =  new Vector<Object>();
			}
			if(report instanceof CustomReport)
			{
				((CustomReport)report).getSignedParams().setSnsUid(Xutils.generateUUID2(mContext));
				sendCustomReportNow(((CustomReport)report));
			}
			else
			{
				emptyIdReports.add(report);
			}
			
		}
	}
	
//	@Override
//	public void onCreate(Activity context) {
//		// TODO Auto-generated method stub
//		start_time = time.getCurrentTime();
//		if(ReportPolicy.getIntance().getReportPolicy(context) == ReportPolicy.BATCH_AT_LAUNCH)
//		{
//			sendBatchReport();
//		}
//	}
	/**
	 * 设置心跳服务得间隔时间,请在onCreate之前调用
	 */
	public void setHeartbeatTimeOffset(int time_offset)
	{
		HeartbeatService.delayMillis = time_offset * 1000;
	}
	/*
	 * 停止心跳服务
	 */
	public void stopHeartbeatSrvice()
	{
		try
		{
			this.getContxt().unbindService(conn);
		}
		catch(Exception e)
		{
			Log.e("XingCloud","No service to unbind");
		}
	}
	/**
	 * 在应用时发送
	 * @param context 当前activity的上下文实例
	 */
	@Override
	public void onPause(Context context) {
		// TODO Auto-generated method stub
		if(null ==context)
		{
			throw new  Error("The context param is null");
		}
		pause_time  = System.currentTimeMillis();
	}
	/**
	 * 在应用resume时发送
	 * @param context 当前activity的上下文实例
	 */
	@Override
	public void onResume(Context context) {
		// TODO Auto-generated method stub
		if(null ==context)
		{
			throw new  Error("The context param is null");
		}
		resume_time  = System.currentTimeMillis();
	}
	/**
	 * 在用户退出时发送
	 * @param context 当前activity的实例
	 */
	@Override
	public void onFinish(Activity context) {
		// TODO Auto-generated method stub
		if(null ==context)
		{
			throw new  Error("The Activity param is null");
		}
		finish_time = System.currentTimeMillis();
		if(!sendByCustomer)
		{
			JSONObject baseInfo = new JSONObject();
			try {
				baseInfo.put("finishTime", finish_time);
				//、、- (resume_time-pause_time)
				long ptime = ((finish_time - start_time) )/1000;
				if(ptime <0)
				{
					ptime = Math.abs(ptime);
				}
				baseInfo.put("time_duration", ptime);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			userInfo =  new UserField();
			userInfo.setAppId(Xutils.getGameAppId(context));
			userInfo.setEvent(UserEvent.USER_QUIT);
			userInfo.setJsonVar(baseInfo);
			userInfo.setUId(Xutils.generateUUID(context));
			userInfo.setTimestamp(XTimeStamp.getTimeStamp());
			UserReport report = new UserReport(userInfo.toStringBa(),UserEvent.USER_QUIT);
			if(Xutils.isAppNetworkPermit(mContext) && Xutils.isNetworkAvailable(mContext) && (ReportPolicy.getIntance().getReportPolicy(context) == ReportPolicy.REALTIME))
			{
				currentReport = report;
				
				//report.reportUserAction(UserEvent.USER_QUIT);
				report.reportUserAction(userInfo, UserEvent.USER_QUIT);
			}
			else
			{
				addReport(report);
				currentReport = report;
				
				
			}
		}
		//Intent service=new Intent(mContext,HeartbeatService.class);//显示意图    
	//	mContext.stopService(service);
		try
		{
			this.getContxt().unbindService(conn);
		}
		catch(Exception e)
		{
			Log.e("XingCloud","No service to unbind");
		}
		
		if(ReportPolicy.getIntance().getReportPolicy(context) == ReportPolicy.BATCH_AT_TERMINATE)
		{
			if(Xutils.isAppNetworkPermit(mContext) && Xutils.isNetworkAvailable(mContext))
			{
				sendBatchReport();
				removeAllReport();
			}
			else
			{
				saveReportWithoutNetwork(context);
			}
		}
		else
		{
			saveReportWithoutNetwork(context);
		}
		
		
	}
	
	private void saveReportWithoutNetwork(Context context)
	{
		
		if(null != reports && reports.size() > 0)
    	{
			List<Object> reportN = new ArrayList<Object>();
    		Set<Object> objs =  reports.keySet();
    		if(!objs.isEmpty())
    		{
    			Iterator<Object> it = objs.iterator();
        		while(it.hasNext())
        		{
        			Object bj = it.next();
        			if(!reportN.contains(bj))
        			{
        				reportN.add(bj);
        			}
        			
        		}
    		}
    		Xutils.getIntance().saveReport(context, reportN);
    	}
    	
	}
	/**
	 * 需要更新用户数据的时候调用，比如用户登录时，切换用户时
	 * @param context 当前activity的实例
	 */
	@Override
	public void update(Activity context) {
		// TODO Auto-generated method stub
		if(null ==context)
		{
			throw new  Error("The Activity param is null");
		}
		setSignedParams(context);
	}
	private void setSignedParams(Activity ctx)
	{
		if(null == ctx)
		{
			Log.e("XingCloud", "context is null");
			return ;
		}
		param.setAppId(Xutils.getGameAppId(ctx));
		param.setSnsUid(sUid);

	}
	/**
	 * 设置发送report的策略
	 * @param policy 策略，可供选择的有：
	 * 	DEFAULT：定时定量发送，默认间隔时间是1分钟，一次最大数量上10条
	 *	REALTIME：立即发送，只要有report就马上发出去
	 *	BATCH_AT_LAUNCH：在每次启动时批量发送
	 *	BATCH_AT_TERMINATE：在每次退出时批量发送
	 * @param context 当前activity的上下文实例
	 */
	@Override
	public void setReportPolicy(int policy,Context context) {
		// TODO Auto-generated method stub
		if(null ==context)
		{
			throw new  Error("The context param is null");
		}
		mPolicy = policy;
		
	}
	
	private void startTimer()
	{
		ReportPolicy.getIntance().setReportPolicy(mPolicy,mContext);
		switch(mPolicy)
		{
		case ReportPolicy.DEFAULT:
			th = new LooperThread();
			th.start();
			
			 break;
		}
	}
	/**
	 * 选择默认发送report策略时的，report cache的数量上限
	 * @param defaultCountCache
	 */
	public void setDefaultCount(int defaultCountCache)
	{
		if(defaultCountCache >0)
		{
			count_span = defaultCountCache;
		}
		
	}
	/**
	 * 选择默认发送report策略时的时间间隔，单位秒
	 * @param second
	 */
	public void setDefaultTimeCache(int second)
	{
		if(second>0)
		{
			time_span = second;
		}
	}

	/**
	 * 设置错误处理函数,处理未知异常
	 * @param context 前activity的上下文实例
	 */
	@Override
	public void setErrorHandler(Activity context) {
		if(null ==context)
		{
			throw new  Error("GDP error handler initilize failure, the Activity param is null");
		}
		CrashHandler handler = CrashHandler.getInstance();
		handler.init(context);
	}
	/**
	 * 游戏中教程服务请用这个接口
	 * @param context	当前activity的上下文实例
	 * @param function	AnalyticFunction.TUTORAL_SERVICE
	 * @param index	    教程步骤数
	 * @param name		教程名称
	 * @param tutorial	教程内容
	 */
	public void trackTutorialService(Context context,String function, int index,String name,
			String tutorial)
	{
		if(null ==context)
		{
			throw new  Error("The context param is null");
		}
		JSONObject datas = new JSONObject();
		
		try {
//			if(null == index)
//			{
//				index = "";
//			}
			datas.put("index", index);
			if(null == name)
			{
				name = "";
			}
			datas.put("name", name);
			
			if(null == tutorial)
			{
				tutorial = "";
			}
			datas.put("tutorial", tutorial);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Stats stat = new Stats();
		if(function == null || function == "")
		{
			function = AnalyticFunction.TUTORAL_SERVICE;
		}
		stat.setStatFunction(function);
		stat.setTimestamp(XTimeStamp.getTimeStamp());
		stat.setCustomData(datas.toString());
		
		CustomReport cusReport = new CustomReport(param,stat);
		if(ReportPolicy.getIntance().getReportPolicy(context) == ReportPolicy.REALTIME)
		{
			if(Xutils.isAppNetworkPermit(mContext) && Xutils.isNetworkAvailable(mContext) )
			{
				sendCustomReportNow(cusReport);
				currentReport = cusReport;
			}
			else
			{
				addReport(cusReport);
			}
			
		}
		else 
		{
			addReport(cusReport);
		}
	}
	/**
	 * 游戏中的交易行为，请用这个新接口
	 * @param context	当前activity的上下文实例
	 * @param function	AnalyticFunction.BUY_SERVICE
	 * @param resource	resource类型
	 * @param paytype 支付类型
	 * @param level1	分类1
	 * @param level2	分类2
	 * @param level3	分类3
	 * @param level4	分类4
	 * @param level5	分类5
	 * @param amount	交易数量(货币消耗的数量)
	 * @param number	交易物品的数量
	 */
	public void trackBuyService(Context context,String function, String resource,String paytype,
			String level1,String level2,String level3,String level4,String level5,
			int amount,int number)
	{
		if(null ==context)
		{
			throw new  Error("The context param is null");
		}
		JSONObject datas = new JSONObject();
		
		try {
			if(null == resource)
			{
				resource = "";
			}
			datas.put("resource", resource);
			if(null == paytype)
			{
				paytype = "";
			}
			datas.put("paytype", paytype);
			
			if(null == level1)
			{
				level1 = "";
			}
			datas.put("level1", level1);
			
			if(null == level2)
			{
				level2 = "";
			}
			datas.put("level2", level2);
			
			if(null == level3)
			{
				level3 = "";
			}
			datas.put("level3", level3);
			
			if(null == level4)
			{
				level4 = "";
			}
			datas.put("level4", level4);
			
			if(null == level5)
			{
				level5 = "";
			}
			datas.put("level5", level5);
			if(amount < 0)
			{
				amount = 0;
			}
			if(number < 0)
			{
				number = 0;
			}
			datas.put("amount", amount);
			datas.put("nubmer", number);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Stats stat = new Stats();
		if(function == null || function == "")
		{
			function = AnalyticFunction.BUY_SERVICE;
		}
		stat.setStatFunction(function);
		stat.setTimestamp(XTimeStamp.getTimeStamp());
		stat.setCustomData(datas.toString());
		
		CustomReport cusReport = new CustomReport(param,stat);
		if(ReportPolicy.getIntance().getReportPolicy(context) == ReportPolicy.REALTIME)
		{
			if(Xutils.isAppNetworkPermit(mContext) && Xutils.isNetworkAvailable(mContext) )
			{
				sendCustomReportNow(cusReport);
				currentReport = cusReport;
			}
			else
			{
				addReport(cusReport);
			}
			
		}
		else 
		{
			addReport(cusReport);
		}
	}

	public void trackHeartBeat()
	{
	//	Intent i = new Intent(this, HeartbeatService.class);
		if(null == mContext)
		{
			Log.d("XingCloud", "context is null");
			return;
		}
		Intent service=new Intent(mContext,HeartbeatService.class);//显示意图    
		//mContext.startService(service);    
		this.getContxt().bindService(service, conn, Context.BIND_AUTO_CREATE);
		
		
	}
	/**
	 * track自定义事件
	 * @param context	当前activity的上下文实例
	 * @param function	指的是统计方法，count,milestone等,可以通过AnalyticFunction.COUNT和
	 * AnalyticFunction.MILESTONE获得
	 * @param action	自定义的游戏中的action名称，如buy，sell等
	 * @param level1	分类1
	 * @param level2	分类2
	 * @param level3	分类3
	 * @param level4	分类4
	 * @param level5	分类5
	 * @param count		影响的数值
	 */
	@Override
	public void trackEvent(Context context,String function, String action,
			String level1,String level2,String level3,String level4,String level5,
			int count) {
		// TODO Auto-generated method stub
		if(null ==context)
		{
			throw new  Error("The context param is null");
		}
		
		Stats stat = new Stats();
		stat.setStatFunction(function);
		stat.setTimestamp(XTimeStamp.getTimeStamp());
		stat.setCunstomData(action, level1, level2, level3, level4, level5, count);
		

		CustomReport cusReport = new CustomReport(param,stat);
		if(ReportPolicy.getIntance().getReportPolicy(context) == ReportPolicy.REALTIME)
		{
			if(Xutils.isAppNetworkPermit(mContext) && Xutils.isNetworkAvailable(mContext) )
			{
				if((sUid==null||sUid.length() <=0 )&& Xutils.generateUUID(context).length() <=0)
				{
					addNoIdReports(cusReport);
				}
				else
				{
					if(sUid==null||sUid.length() <=0 )
					{
						sUid =  Xutils.generateUUID(context);
					}
					sendCustomReportNow(cusReport);
					currentReport = cusReport;
				}
				
			}
			else
			{
				if((sUid==null||sUid.length() <=0 )&& Xutils.generateUUID(context).length() <=0)
				{
					addNoIdReports(cusReport);
				}
				else
				{
					addReport(cusReport);
				}			
			}
			
		}
		else 
		{
			if((sUid==null||sUid.length() <=0 )&& Xutils.generateUUID(context).length() <=0)
			{
				addNoIdReports(cusReport);
			}
			else
			{
				addReport(cusReport);
			}	
		}
		
		
	}
	/**
	 * track游戏中都的交易行为
	 * @param context	当前activity的实例
	 * @param function	交易过程中的事件
	 * @param values	交易数据
	 */
	@Override
	public void trackTransaction(Activity context, int function,JSONObject values) {
		// TODO Auto-generated method stub
		if(null ==context)
		{
			throw new  Error("The Activity param is null");
		}
		UserField user = new UserField();
		user.setAppId(Xutils.getGameAppId(context));
		user.setEvent(function);
		user.setJsonVar(values);
		user.setUId(Xutils.generateUUID(context));
		user.setTimestamp(XTimeStamp.getTimeStamp());
		UserReport report = new UserReport(user.toStringBa(),function);
		
		if(ReportPolicy.getIntance().getReportPolicy(context) == ReportPolicy.REALTIME)
		{
			if(Xutils.isAppNetworkPermit(mContext) && Xutils.isNetworkAvailable(mContext))
			{
				report.reportUserAction(user, function);
				currentReport = report;
			}
			else
			{
				addReport(report);
				currentReport = report;
			}
			
		}
		else 
		{
			//.d("XingCloud", ""+3);
			addReport(report);
		}
	}

	/**
	 * 统计在activity上停留的时间，在destroy或者跳转时调用
	 * @param context 当前activity的实例
	 * @param current_activity_name   当前activity的名字
	 * @param next_activity_name	  要跳转到的activity的名字
	 */
	@Override
	public void trackPageview(Activity context,String current_activity_name,String next_activity_name) {
		// TODO Auto-generated method stub
		if(null ==context)
		{
			throw new  Error("The Activity param is null");
		}
		long now = System.currentTimeMillis();
		String sec = String.valueOf((now - start_time-resume_time+pause_time)/1000);
		JSONObject params = new JSONObject();
		try {
			params.put("current_page", current_activity_name);
			params.put("time_duration", sec);
			params.put("next_page", next_activity_name);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		UserField user = new UserField();
		user.setAppId(Xutils.getGameAppId(context));
		user.setEvent(UserEvent.USER_PAGE_VISIT);
		user.setJsonVar(params);
		user.setUId(Xutils.generateUUID(context));
		user.setTimestamp(XTimeStamp.getTimeStamp());
		UserReport report = new UserReport(user.toStringBa(),UserEvent.USER_PAGE_VISIT);
		
		if(ReportPolicy.getIntance().getReportPolicy(context) == ReportPolicy.REALTIME)
		{
			if(Xutils.isAppNetworkPermit(mContext) && Xutils.isNetworkAvailable(mContext))
			{
				report.reportUserAction(user, UserEvent.USER_PAGE_VISIT);
				currentReport = report;
			}
			else
			{
				addReport(report);
				currentReport = report;
			}
		}
		else 
		{
			addReport(report);
		}
	}
	
	
//	http://analytic.337.com/index.php?appid=test&logs=[{"uid":"001","event":"user.update",
//		"json_var":{"grade":"77","country":"2","region":"823005","union":"189103122884db383660f9d6"},
//		"ref":"xafrom=google_ad_35 "},{"uid":"002","event":"user.visit"},{"uid":"003","event":"user.login",
//			"json_var":{"step":1,"time":2000},"timestamp":"111111111"}]
	
	private synchronized void sendBatchReport()
	{
		if(null == reports || reports.isEmpty())
		{
			return;
		}
		Set<Object> objs =  reports.keySet();
		if(objs.isEmpty())
		{
			return;
		}
		Iterator<Object> it = objs.iterator();
		CustomField cf =  new CustomField();
		cf.setSignedParams(param);
		synchronized (reports) 
		{
			//Log.d("XingCloudCount", ""+reports.size());
			while(it.hasNext())
			{
				
				Object bj = it.next();
				if(bj instanceof CustomReport)
				{
					if(((CustomReport)bj).getStat()!=null)
					{
						if(((CustomReport)bj).getStat()!=null && ((CustomReport)bj).getAppid() != "" && ((CustomReport)bj).getAppid().compareToIgnoreCase("chucktest@337_en_speedtest")==0)
						{
							sendCustomReportNow((CustomReport)bj);
							currentReport = ((CustomReport)bj);
						}
						else
						{
							cf.setStats(((CustomReport)bj).getStat());
						}
						
					}
					else
					{
						sendCustomReportNow((CustomReport)bj);
						currentReport = ((CustomReport)bj);
					}
					
					//sendCustomReportNow((CustomReport)bj);
					//currentReport = ((CustomReport)bj);
					//(CustomReport)bj.get
					//reports.remove(bj);
				}
				
//				else if(bj instanceof ErrorReport)
//				{
//					((ErrorReport)bj).reportErrorAction();
//					currentReport = ((ErrorReport)bj);
//					reports.remove(bj);
//				}
			}
			if(cf.getStats() != null && cf.getStats().size() >0)
			{
				CustomReport cr = new CustomReport();
				cr.reportCustomAction(cf);
				currentReport = cr;
			}
			cf = null;
			//cr = null;
			if(null == reports || reports.isEmpty())
			{
				return;
			}
			
			String batchReport="";
			objs = null;
			Set<Object> objs2 =  reports.keySet();
			//Log.d("XingCloudCount2", ""+objs2.size());
			it = null;
			Iterator<Object> it2 = objs2.iterator();
			int i =0;
			batchReport += "[";
			//synchronized(it)
			{
				while(it2.hasNext())
				{
					
					Object bj = it2.next();
					if(bj == null || bj instanceof CustomReport)
					{
						//Log.d("XingCloud", "lost"+bj.toString());
						continue;
					}
					
					if(i > 0)
						batchReport+= ",";
					batchReport += "{";
					if(bj instanceof UserReport)
					{
					//	Log.d("XingCloud", ""+4);
						String dat = ((UserReport)bj).getData();
						if(dat.startsWith("{") && dat.endsWith("}"))
						{
							batchReport+= dat.substring(1, dat.length()-1);
						}
						
						//batchReport+= ((UserReport)bj).
						//reports.remove(bj);
					}
					else if(bj instanceof ErrorReport)
					{
					//	Log.d("XingCloud", ""+4);
						//batchReport += "{";
						batchReport+= ((ErrorReport)bj).getContent();
						//batchReport += "}";
						//reports.remove(bj);
					}
					i++;
					//Log.d("XingCloud-i", ""+i);
					batchReport += "}";
				}
				batchReport += "]";
				reports.clear();
				reports = null;
				Xutils.getIntance().deleteReport(mContext);
				if(batchReport.compareToIgnoreCase("[]")==0 || batchReport.compareToIgnoreCase("[ ]")==0)
				{
					//Log.d("XingCloud", ""+40);
					return;
				}
				CustomReport r = new CustomReport(batchReport,0);
				if(Xutils.isAppNetworkPermit(mContext) && Xutils.isNetworkAvailable(mContext))
				{
					//Log.d("XingCloud", ""+41);
					r.reportBatchAction(batchReport);
					currentReport = r;
					r = null;
				}
				else
				{
					addReport(r);
					currentReport = r;
				}
//				CustomReport r = new CustomReport(batchReport);
//				r.reportBatchAction(batchReport);
//				currentReport = r;
//				r = null;
			}
			
			
		}
		
	}
	
	private void sendCustomReportNow(CustomReport report)
	{
		if(Xutils.isAppNetworkPermit(mContext) && Xutils.isNetworkAvailable(mContext))
		{
			report.reportCustomAction();
			currentReport = report;
		}
		else
		{
			addReport(report);
			currentReport = report;
		}
		
	}
	
	
	
	private static Timer timer = new Timer();
	private  Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
			case APPROVAL_SEND:
				//send report here
				if(Xutils.isAppNetworkPermit(mContext) && Xutils.isNetworkAvailable(mContext))
				{
					sendBatchReport();
					removeAllReport();
				}
				
//				timer.
//				timer.schedule(task, 300000); 
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	private TimerTask task =  new TimerTask(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message message = new Message();
			message.what = APPROVAL_SEND;
			handler.sendMessage(message);
		}
		
	};;
	
	public static Handler sendHandler =  new Handler(){
		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
			case REPORT_ERROR:
				_instance.addReport(_instance.currentReport);
				sendTask.cancel();
				//send report here
				//sendBatchReport();
				//removeAllReport();
				//timer.schedule(task, 300000); 
				break;
			case REPORT_SUCCESS:
				Xutils.getIntance().deleteReport(_instance.mContext);
				break;
			}
			super.handleMessage(msg);
		}
	};
	public static TimerTask sendTask = new TimerTask(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message message = new Message();
			message.what = REPORT_ERROR;
			sendHandler.sendMessage(message);
		}
		
	};

	/**
	 * 发送基本事件
	 * @param context 当前activity的context实例
	 * @param user	  封装事件的数据对象
	 */
	@Override
	public void trackUserEvent(Context context, UserField user) {
		// TODO Auto-generated method stub
//		UserField user = new UserField();
//		user.setAppId(sUid);
//		user.setEvent(function);
//		user.setJsonVar(values);
//		user.setUId(Xutils.generateUUID(context));
		if(null ==context)
		{
			throw new  Error("The context param is null");
		}
		if(null == user)
		{
			throw new Error("please provide user data");
		}
		try
		{
			UserReport report = new UserReport(user.toStringBa(),Integer.valueOf(user.getEvent()));
			
			if(ReportPolicy.getIntance().getReportPolicy(context) == ReportPolicy.REALTIME)
			{
				if(null == user.getEvent() || user.getEvent() == "")
				{
					throw new Error("please specify the event id");
				}
				if(Xutils.isAppNetworkPermit(mContext) && Xutils.isNetworkAvailable(mContext))
				{
					currentReport = report;
					if((user.getUId()==null||user.getUId().length() <=0 )&& Xutils.generateUUID(context).length() <=0)
					{
						addNoIdReports(user);
					}
					else
					{
						if((user.getUId()==null||user.getUId().length() <=0 ))
						{
							user.setUId(Xutils.generateUUID(context));
						}
						report.reportUserAction(user, Integer.valueOf(user.getEvent()));
					}
				}
				else
				{
					if((user.getUId()==null||user.getUId().length() <=0 )&& Xutils.generateUUID(context).length() <=0)
					{
						addNoIdReports(user);
					}
					else
					{
						addReport(report);
					}
					
					currentReport = report;
				}
			}
			else 
			{
				if((user.getUId()==null||user.getUId().length() <=0 )&& Xutils.generateUUID(context).length() <=0)
				{
					addNoIdReports(user);
				}
				else
				{
					addReport(report);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}


	public void setReportUid(Context context,String userId)
	{
		if(null != context && null != userId)
		{
			Xutils.saveUUID(context,userId);
			sUid = userId;
			//addReport
			if(emptyIdReports == null || emptyIdReports.size() <=0)
			{
				return;
			}
			else
			{
				for(int i =0; i < emptyIdReports.size(); i++)
				{
					Object obj = emptyIdReports.get(i);
					if(obj instanceof UserField)
					{
						((UserField) obj).setUId(userId);
						UserReport report = new UserReport(((UserField) obj).toStringBa(),Integer.valueOf(((UserField) obj).getEvent()));
						//addReport(report);
						report.reportUserAction(((UserField) obj), Integer.valueOf(((UserField) obj).getEvent()));
						report=null;
					}
					else if(obj instanceof CustomReport)
					{
						((CustomReport)obj).getSignedParams().setSnsUid(userId);
						sendCustomReportNow(((CustomReport)obj));
						//addReport(((CustomReport)obj));
					}
				}
				emptyIdReports.clear();
				emptyIdReports = null;
			}
		}
		
	}
	/*
	 * 设置apk来源或者发布渠道
	 */
	public void setChannelReference(String reference,Activity context) 
	{
		if(null ==  reference || reference.length() <=0)
		{
			return;
		}
	  //  Xutils.REF = reference;
	    Xutils.REF = ReferenceField.parseReference(reference, context);//(reference);
	    Xutils.bRef = true;
	}
	
	
	/**
	 * 更新游戏用户ID
	 * @param gameUid 游戏中的用户ID
	 */
	@Override
	public void updateGameUid(String gameUid) {
		// TODO Auto-generated method stub
		if(null ==gameUid)
		{
			this.gameUid = "";
			return;
		}
		this.gameUid = gameUid;
	}
	/**
	 * 获取游戏用户ID
	 * @return 返回用户ID
	 */
	public String getGameId()
	{
		return gameUid;
	}
	
	public String getUid()
	{
		return sUid;
	}
	/**
	 * 在启动时发送
	 * @param context 当前activity的实例
	 */
	@Override
	public void onCreate(Activity context) {
		// TODO Auto-generated method stub
		reports =  new HashMap<Object,Boolean>();
		if(null ==context)
		{
			throw new  Error("GDP Analytic initilize failure, the Activity param is null");
		}
		mContext = context;
		XCNative.initCurl("init");
		//start_time = time.getCurrentTime();
		start_time = System.currentTimeMillis();
		boolean bFirstTime = false;
		try
		{
			if(null == sUid || sUid.length() <=0)
			{
				sUid = Xutils.loadUUID(context);
				if(null == sUid || sUid.trim().length()==0)
				{
					bFirstTime = true;
					sUid = Xutils.generateUUID(context);
				}
			}
			Xutils.saveUUID(context,sUid);
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
		if(bFirstTime)
		{
			reportBaseInfo(context);
		}
		File f = context.getFileStreamPath(Xutils.XINGCLOUD_REPORT_NAME);
		if(f != null && f.exists())
		{
			List<Object> lastRepots = (Xutils.getIntance().getUnsendReports(f));//context.getFileStreamPath(Xutils.XINGCLOUD_REPORT_NAME)
			if(lastRepots != null)
			{
				for(int i =0; i < lastRepots.size();i ++)
				{
					if(null == reports)
					{
						reports =  new HashMap<Object,Boolean>();
					}
					
					reports.put(lastRepots.get(i), false);
					if(i == count_span)
					{
						if(Xutils.isAppNetworkPermit(mContext) && Xutils.isNetworkAvailable(mContext) )
						{
							
//									//if(ReportPolicy.getIntance().getReportPolicy(context) == ReportPolicy.REALTIME)
//									{
							sendBatchReport();
							removeAllReport();
									//}
								//}
						}
						else
						{
							List<Object> reportList = new ArrayList<Object>();
							if(null != reports && reports.size() > 0)
				        	{
				        		Set<Object> objs =  reports.keySet();
				        		if(!objs.isEmpty())
				        		{
				        			Iterator<Object> it = objs.iterator();
				            		while(it.hasNext())
				            		{
				            			Object bj = it.next();
				            			//if(!reportList.contains(bj))
				            			{
				            				reportList.add(bj);
				            			}
				            		}
				        		}
				        		
				        	}
							Xutils.getIntance().saveReport(mContext, reportList);
							reportList = null;
							if(null != reports && reports.size() > 0)
							{
								reports.clear();
								reports = null;
							}
						}
					}
					
					
				}
				if(ReportPolicy.getIntance().getReportPolicy(context) != ReportPolicy.BATCH_AT_LAUNCH)
				{
					if(Xutils.isAppNetworkPermit(mContext) && Xutils.isNetworkAvailable(mContext))
					{
						sendBatchReport();
						removeAllReport();
					}
					else
					{
						if(null != reports && reports.size() > 0)
						{
							saveReportWithoutNetwork(mContext);
						}
					}
				}
				
			}
		}
		
		startTimer();
		if(!sendByCustomer)
		{
			if(ReportPolicy.getIntance().getReportPolicy(context) != ReportPolicy.REALTIME)
			{
				JSONObject values =  new JSONObject();
				XCTime time = new XCTime();
				try {
					//values.put("name", Xutils.generateUUID(context));
					//values.put("time", time.getCurrentTime());
					values.put("XA_tagname",Xutils.getGameAppVersion(context));
					//values.put("ref", Xutils.getDeliveryChanel(context));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				UserField userInfo =  new UserField();
				userInfo.setAppId(Xutils.getGameAppId( context));
				userInfo.setEvent(UserEvent.USER_VISIT);
				userInfo.setJsonVar(values);
				userInfo.setRef(Xutils.getDeliveryChanel(context));
				userInfo.setUId(Xutils.generateUUID(context));
				userInfo.setTimestamp(XTimeStamp.getTimeStamp());
				UserReport report = new UserReport(userInfo.toStringBa(),UserEvent.USER_VISIT);
				//.e("XingCloud", ""+5);
				currentReport = report;
				addReport(report);
				report = null;
			}
			else
			{
				reportVisit(context);
			}
		}
		
		
		if(ReportPolicy.getIntance().getReportPolicy(context) == ReportPolicy.BATCH_AT_LAUNCH)
		{
			if(Xutils.isAppNetworkPermit(mContext) && Xutils.isNetworkAvailable(mContext) )
			{
				
//						//if(ReportPolicy.getIntance().getReportPolicy(context) == ReportPolicy.REALTIME)
//						{
				sendBatchReport();
				removeAllReport();
						//}
					//}
			}
			else
			{
				if(null == reports )
				{
					return;
				}
				else
				{
					//if(reports.size() >= 5)
					{
						List<Object> reportList = new ArrayList<Object>();
						if(null != reports && reports.size() > 0)
			        	{
			        		Set<Object> objs =  reports.keySet();
			        		if(!objs.isEmpty())
			        		{
			        			Iterator<Object> it = objs.iterator();
			            		while(it.hasNext())
			            		{
			            			Object bj = it.next();
			            			//if(!reportList.contains(bj))
			            			{
			            				reportList.add(bj);
			            			}
			            			
			            		}
			        		}
			        		
			        	}
						Xutils.getIntance().saveReport(mContext, reportList);
						reportList = null;
						if(null != reports && reports.size() > 0)
						{
							reports.clear();
							reports = null;
						}
					}
				}
				
			}
		}
		
	}
	
	
	
	private void reportBaseInfo(Activity context)
	{
		JSONObject baseInfo = null;
		try {
			baseInfo = new JSONObject(generateBaseInfo(context));
			baseInfo.put("is_mobile", "true");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(ReportPolicy.getIntance().getReportPolicy(context) == ReportPolicy.REALTIME)
		{
			userInfo =  new UserField();
			userInfo.setAppId(Xutils.getGameAppId(context));
			userInfo.setEvent(UserEvent.USER_UPDATE);
			userInfo.setRef(Xutils.getDeliveryChanel(context));
			userInfo.setJsonVar(baseInfo);
			userInfo.setUId(Xutils.generateUUID(context));
			UserReport report = new UserReport(userInfo,UserEvent.USER_UPDATE);
			if(Xutils.isAppNetworkPermit(mContext) && Xutils.isNetworkAvailable(mContext) )
			{
				//addNoIdReports
				currentReport = report;
				if(Xutils.generateUUID(context).length() <=0)
				{
					addNoIdReports(userInfo);
				}
				else
				{
					
					report.reportUserAction(UserEvent.USER_UPDATE);
				}
				
			}
			else
			{
				if(Xutils.generateUUID(context).length() <=0)
				{
					addNoIdReports(userInfo);
				}
				else
				{
					currentReport = report;
					addReport(report);
				}
				
			}
		}
		else
		{
			userInfo =  new UserField();
			userInfo.setAppId(Xutils.getGameAppId(context));
			userInfo.setEvent(UserEvent.USER_UPDATE);
			userInfo.setRef("xafrom="+Xutils.getDeliveryChanel(context));
			userInfo.setJsonVar(baseInfo);
			userInfo.setUId(Xutils.generateUUID(context));
			UserReport report = new UserReport(userInfo.toStringBa(),UserEvent.USER_UPDATE);
			
			
			currentReport = report;
			if(Xutils.generateUUID(context).length() <=0)
			{
				addNoIdReports(userInfo);
			}
			else
			{
				addReport(report);
			}
			
		}
	}
	
	protected String generateBaseInfo(Activity context)
	{
		DeviceInfo device = new DeviceInfo(context);
		XLocation location =  new XLocation(context);
		XNetwork net = new XNetwork(context);
		StringBuilder info =  new StringBuilder();
		info.append("{");
		try
		{
			String dv = device.getDeviceInfoEx();
			if(null != dv && dv.trim().length() >0)
			{
				info.append(dv);
				info.append(",");
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		try
		{
			String lo = location.getLocationEx();
			if(null != lo && lo.trim().length() >0)
			{
				info.append(lo);
				info.append(",");
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		try
		{
			String ni = net.getNetworkInfoEx();
			if(null != ni && ni.trim().length() >0)
			{
				info.append(ni);
				info.append(",");
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		String ver = " ";
		try
		{
			ver = Xutils.getGameAppVersion(context);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		info.append("\"XA_tagname\":"+"\""+ver+"\"");
		info.append("}");
		return info.toString();
	}
	protected void reportVisit(Activity context)
	{
		JSONObject values =  new JSONObject();
		//XCTime time = new XCTime();
		try {
			//values.put("name", Xutils.generateUUID(context));
			//values.put("time", time.getCurrentTime());
			values.put("XA_tagname",Xutils.getGameAppVersion(context));
			values.put("is_mobile", "true");
			//values.put("ref", Xutils.getDeliveryChanel(context));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		UserField userInfo =  new UserField();
		userInfo.setAppId(Xutils.getGameAppId( context));
		userInfo.setEvent(UserEvent.USER_VISIT);
		userInfo.setJsonVar(values);
		userInfo.setTimestamp(XTimeStamp.getTimeStamp());
		userInfo.setRef(Xutils.getDeliveryChanel(context));
		userInfo.setUId(Xutils.generateUUID(context));
		UserReport report = new UserReport(userInfo,UserEvent.USER_VISIT);
		if(Xutils.isAppNetworkPermit(mContext) && Xutils.isNetworkAvailable(mContext) )
		{
			if(Xutils.generateUUID(context).length() <=0)
			{
				addNoIdReports(userInfo);
			}
			else
			{
				currentReport = report;
				report.reportUserAction(UserEvent.USER_VISIT);
			}
			
		}
		else
		{
			if(Xutils.generateUUID(context).length() <=0)
			{
				addNoIdReports(userInfo);
			}
			else
			{
				currentReport = report;
				addReport(report);
			}
			
		}
	}

//	@Override
//	public void setReportPolicy(int policy, Context context, int time_offset,
//			int count_offset) {
//		// TODO Auto-generated method stub
//		time_span = time_offset;
//		count_span = count_offset;
//		ReportPolicy.getIntance().setReportPolicy(policy,context);
//		mContext = context;
//		switch(policy)
//		{
//		case ReportPolicy.DEFAULT:
//			th =  new LooperThread();
//			th.start();
//			break;
//		}
//	}
	/**
	 * 测试网络速度，并且把网络的下载和上传得网速log到服务器
	 */
	public void trackNetworkSpeed()
	{
		try
		{
			if(Xutils.isAppNetworkPermit(mContext) && Xutils.isNetworkAvailable(mContext) )
			{
				DownloadTester.getInstance().downloadHostList(new getHostListener());	
			}	
			
		}
		catch(Exception e)
		{
			Log.e(LogTag.XC_TAG, e.getMessage());
		}
	}
	
	class customRask extends Thread{
		SignedParams sparam;
		Stats stat;
		public customRask(SignedParams param,Stats stats)
		{
			sparam = param;
			stat = stats;
		}
		public customRask(String function, String action,
				String level1,String level2,String level3,String level4,String level5,
				int count)
		{
			stat = new Stats();
			stat.setStatFunction(function);
			stat.setTimestamp(XTimeStamp.getTimeStamp());
			stat.setCunstomData(action, level1, level2, level3, level4, level5, count);
			sparam =  param;
		}
		 public void run() { 
	            super.run(); 
	            Looper.prepare();
	            CustomReport cusReport = new CustomReport(param,stat);
	    		if(ReportPolicy.getIntance().getReportPolicy(mContext) == ReportPolicy.REALTIME)
	    		{
	    			
	    			sendCustomReportNow(cusReport);
	    			currentReport = cusReport;
	    		}
	    		else 
	    		{
	    			addReport(cusReport);
	    		}
	    		try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		 }
	}
	class LooperThread extends Thread { 
        public void run() { 
            super.run(); 
            Looper.loop();
            try { 
                do {               
                	Thread.sleep(time_span*1000); 
                	Message message = new Message();
         			message.what = APPROVAL_SEND;
         			handler.sendMessage(message);

                } while (!LooperThread.interrupted());
            } catch (Exception e) { 
                e.printStackTrace(); 
            } 
        } 
    }


	@Override
	public void onStart(Context context) {
		// TODO Auto-generated method stub
		start_time = time.getCurrentTime();
	} 
	class uUploadListener implements NetworkListener
	{

		@Override
		public void onComplete(double uploadSpeed,long size,String ip) {
			// TODO Auto-generated method stub
			if(uploadSpeed < 0 || mContext ==null || ip == null)
			{
				return;
			}
			Stats stat = new Stats();
			stat.setStatFunction("count");
			stat.setTimestamp(XTimeStamp.getTimeStamp());
			String ds =String.valueOf(uploadSpeed*100);
			
			XNetwork net = new XNetwork(mContext);
			String type =ip+"_" +net.getConnectType()+"_UL";
			stat.setCunstomData(type, ""+size, "", "", "", "", Integer.valueOf(ds.substring(0,ds.indexOf("."))));
			SignedParams sparam = new SignedParams();
			sparam.setSnsUid(sUid);
			sparam.setAppId("chucktest@337_en_speedtest");
			CustomReport cusReport = new CustomReport(sparam,stat);
			if(ReportPolicy.getIntance().getReportPolicy(mContext) == ReportPolicy.REALTIME)
			{
				if(ReportPolicy.getIntance().getReportPolicy(mContext) == ReportPolicy.REALTIME)
				{
					
					sendCustomReportNow(cusReport);
					currentReport = cusReport;
				}
				else 
				{
					addReport(cusReport);
				}
				
				
			}
			else 
			{
				addReport(cusReport);
			}
		}

		@Override
		public void onException(Exception e) {
			// TODO Auto-generated method stub
			Log.e(LogTag.XC_TAG, "No speedtest.rtf file in sdcard or network exception");
			if(currentReport != null)
			{
//				if(ReportPolicy.getIntance().getReportPolicy(mContext) == ReportPolicy.REALTIME)
//				{
//					
//					sendCustomReportNow((CustomReport)currentReport);
//					//currentReport = cusReport;
//				}
//				else 
				{
					addReport(currentReport);
				}
			}
		}

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub
			
		}
		
	}
	class getHostListener implements GetHostListener
	{

		@Override
		public void onComplete() {
			// TODO Auto-generated method stub
			if(Xutils.isAppNetworkPermit(mContext) && Xutils.isNetworkAvailable(mContext) )
			{
				DownloadTester.getInstance().getDownloadSpeed(new uDownloadListener());
				
			}
		}

		@Override
		public void onException(Exception e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub
			
		}
		
	}
	class uDownloadListener implements NetworkListener
	{

		@Override
		public void onComplete(double downloadSpeed,long size,String ip) {
			// TODO Auto-generated method stub
			if(downloadSpeed < 0 || mContext ==null|| ip == null )
			{
				return;
			}
			Stats stat = new Stats();
			stat.setStatFunction("count");
			stat.setTimestamp(XTimeStamp.getTimeStamp());
			String ds =String.valueOf(downloadSpeed*100);
			XNetwork net = new XNetwork(mContext);
			String type =ip+"_" +net.getConnectType()+"_DL";
			stat.setCunstomData(type, ""+size, "", "", "", "", Integer.valueOf(ds.substring(0, ds.indexOf("."))));
			SignedParams sparam = new SignedParams();
			sparam.setSnsUid(sUid);
			sparam.setAppId("chucktest@337_en_speedtest");
			CustomReport cusReport = new CustomReport(sparam,stat);
			if(ReportPolicy.getIntance().getReportPolicy(mContext) == ReportPolicy.REALTIME)
			{
				
				if(Xutils.isAppNetworkPermit(mContext) && Xutils.isNetworkAvailable(mContext) )
				{
					sendCustomReportNow(cusReport);
					currentReport = cusReport;
				}
				else
				{
					addReport(currentReport);
				}
				
			}
			else 
			{
				addReport(cusReport);
			}
			
			DownloadTester.getInstance().getUploadSpeed(new uUploadListener());
		}

		@Override
		public void onException(Exception e) {
			// TODO Auto-generated method stub
			Log.e(LogTag.XC_TAG, "Network exception or else");
			if(currentReport != null)
			{
//				if(ReportPolicy.getIntance().getReportPolicy(mContext) == ReportPolicy.REALTIME)
//				{
//					if(Xutils.isAppNetworkPermit(mContext) && Xutils.isNetworkAvailable(mContext) )
//					{
//						sendCustomReportNow((CustomReport)currentReport);
//					}
//					else
//					{
//						addReport(currentReport);
//					}
//					//currentReport = cusReport;
//				}
//				else 
				{
					addReport(currentReport);
				}
			}
			
			DownloadTester.getInstance().getUploadSpeed(new uUploadListener());
		}

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub
			
		}
		
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
}
