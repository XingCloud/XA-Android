package com.xingcloud.xa;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.xingcloud.xa.custom.CustomField;
import com.xingcloud.xa.custom.SignedParams;
import com.xingcloud.xa.custom.Stats;
import com.xingcloud.xa.device.DeviceInfo;
import com.xingcloud.xa.device.XLocation;
import com.xingcloud.xa.device.XNetwork;
import com.xingcloud.xa.report.CustomReport;
import com.xingcloud.xa.report.ErrorReport;
import com.xingcloud.xa.utils.DownloadTester;
import com.xingcloud.xa.utils.DownloadTester.GetHostListener;
import com.xingcloud.xa.utils.DownloadTester.NetworkListener;
import com.xingcloud.xa.utils.XCTime;
import com.xingcloud.xa.utils.XTimeStamp;
import com.xingcloud.xa.utils.Xutils;


public class XAReportCache {

	Vector emptyIdReports=null;
	private HashMap<Object,Boolean> reports;
	private  Object currentReport = null;
	private Activity mContext;
	public int time_span=10;
	public int count_span = 5;
	SignedParams param;
	private static XAReportCache _instantce = null;
	static LooperThread th;
	private  static final int APPROVAL_SEND = 1;
	public static final int REPORT_ERROR = 2;
	public static final int REPORT_SUCCESS = 3;
	private XAReportCache()
	{
		reports = new HashMap<Object,Boolean>();
		param = new SignedParams();
	}
	
	public static XAReportCache instance()
	{
		if(null == _instantce)
		{
			_instantce = new XAReportCache();
		}
		return _instantce;
	}
	
	public HashMap<Object,Boolean> getReports()
	{
		return reports;
	}
	public void setContext(Activity context)
	{
		mContext = context;
	}
	
	public Activity getContext()
	{
		return  mContext;
	}
	public void setSignedParams(SignedParams p)
	{
		param = p;
	}
	
	public Object getCurrentReport()
	{
		return currentReport;
	}
	
	public void setCurrentReport(Object report)
	{
		currentReport = report;
	}
	public synchronized void sendNoIdReports(String userId)
	{
		if(emptyIdReports == null || emptyIdReports.size() <=0)
		{
			return;
		}
		else
		{
			for(int i =0; i < emptyIdReports.size(); i++)
			{
				Object obj = emptyIdReports.get(i);
				if(obj instanceof CustomReport)
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
	
	public  void sendCustomReportNow(CustomReport report)
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
	
	public synchronized void addReport(Object report)
	{
		//Log.d("XingCloud", ""+1);
		if(null == reports|| null ==  report)
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
//			if(null == report)
//			{
//				Log.e("XingCloud","report content is null");
//				return;
//			}
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
		//	currentTime = time.getCurrentTime();
		}		
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

		Xutils.getIntance().deleteReport(mContext);
	}
	
	
	public synchronized void sendBatchReport()
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
		param.setTimestamp(XTimeStamp.getTimeStamp());
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
				}

			}
			if(cf.getStats() != null && cf.getStats().size() >0)
			{
				CustomReport cr = new CustomReport();
				cr.reportCustomAction(cf);
				currentReport = cr;
			}
			cf = null;
			//cr = null;
//			if(null == reports || reports.isEmpty())
//			{
//				return;
//			}
//			
//			String batchReport="";
//			objs = null;
//			Set<Object> objs2 =  reports.keySet();
//			//Log.d("XingCloudCount2", ""+objs2.size());
//			it = null;
//			Iterator<Object> it2 = objs2.iterator();
//			int i =0;
//			batchReport += "[";
//			//synchronized(it)
//			{
//				while(it2.hasNext())
//				{
//					
//					Object bj = it2.next();
//					if(bj == null || bj instanceof CustomReport)
//					{
//						//Log.d("XingCloud", "lost"+bj.toString());
//						continue;
//					}
//					
//					if(i > 0)
//						batchReport+= ",";
//					batchReport += "{";
//
//					if(bj instanceof ErrorReport)
//					{
//
//						batchReport+= ((ErrorReport)bj).getContent();
//
//					}
//					i++;
//					//Log.d("XingCloud-i", ""+i);
//					batchReport += "}";
//				}
//				batchReport += "]";
				reports.clear();
				reports = null;
				Xutils.getIntance().deleteReport(mContext);
//				if(batchReport.compareToIgnoreCase("[]")==0 || batchReport.compareToIgnoreCase("[ ]")==0)
//				{
//
//					return;
//				}
//				CustomReport r = new CustomReport(batchReport,0);
//				if(Xutils.isAppNetworkPermit(mContext) && Xutils.isNetworkAvailable(mContext))
//				{
//					
//					r.reportBatchAction(batchReport);
//					currentReport = r;
//					r = null;
//				}
//				else
//				{
//					addReport(r);
//					currentReport = r;
//				}

			
			
			
		}
		
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
	
	
	public void sendLastReports()
	{
//		/mContext
		File f = mContext.getFileStreamPath(Xutils.XINGCLOUD_REPORT_NAME);
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
							sendBatchReport();
							removeAllReport();
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
//				if(XAPolicy.getIntance().getReportPolicy(mContext) != XAPolicy.BATCH_AT_LAUNCH)
//				{
//					if(Xutils.isAppNetworkPermit(mContext) && Xutils.isNetworkAvailable(mContext))
//					{
//						sendBatchReport();
//						removeAllReport();
//					}
//					else
//					{
//						if(null != reports && reports.size() > 0)
//						{
//							saveReportWithoutNetwork(mContext);
//						}
//					}
//				}
				
			}
		}
	}
	public void saveReportWithoutNetwork(Context context)
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
    		//if(null != reports && reports.size() > 0)
			{
				reports.clear();
				reports = null;
			}
    	}
    	
	}
	
	public void startTimer()
	{
		XAPolicy.getIntance().setReportPolicy(XA.mPolicy,mContext);
		switch(XA.mPolicy)
		{
		case XAPolicy.DEFAULT:
			th = new LooperThread();
			th.start();
			
			 break;
		}
	}
	
	class LooperThread extends Thread { 
        public void run() { 
            super.run(); 
           // Looper.loop();
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
				break;
			}
			super.handleMessage(msg);
		}
	};
	
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
			e.printStackTrace();
		}
	}
	
	
	
	public void sendFinishReport(long ptime)
	{
		
		XCTime time = new XCTime();
		JSONObject values =  new JSONObject();
		
		try {
			values.put("finishTime", System.currentTimeMillis());
			
			
			if(ptime <0)
			{
				ptime = Math.abs(ptime);
			}
			values.put("time_duration", ptime);
			values.put("is_mobile", "true");
			//values.put("ref", Xutils.getDeliveryChanel(context));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("data", values);
		XA.instance().trackEvent(XAEvent.USER_QUIT, params, mContext);
	
	}
	public void sendUpdateReport()
	{
		//JSONObject values =  new JSONObject();
		XCTime time = new XCTime();
		JSONObject baseInfo = null;
		try {
			baseInfo = new JSONObject(generateBaseInfo(mContext));
			baseInfo.put("is_mobile", "true");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("data", baseInfo);
		XA.instance().trackEvent(XAEvent.USER_UPDATE, params, mContext);
	}
	
	public void sendVisitReport()
	{
		JSONObject values =  new JSONObject();
		//XCTime time = new XCTime();
		try {
			values.put("ref", Xutils.getDeliveryChanel(mContext));
			//values.put("time", time.getCurrentTime());
			values.put("XA_tagname",Xutils.getGameAppVersion(mContext));
			values.put("is_mobile", "true");
			//values.put("ref", Xutils.getDeliveryChanel(context));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("data", values);
		XA.instance().trackEvent(XAEvent.USER_VISIT, params, mContext);
	}
	
	public void sendCurrentReports()
	{
		if(Xutils.isAppNetworkPermit(mContext) && Xutils.isNetworkAvailable(mContext) )
		{
			sendBatchReport();
			removeAllReport();
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
			stat.setEventName(XAEvent.COUNT);
			stat.setTimestamp(XTimeStamp.getTimeStamp());
			String ds =String.valueOf(downloadSpeed*100);
			XNetwork net = new XNetwork(mContext);
			String type =ip+"_" +net.getConnectType()+"_DL";
			stat.setCunstomData(type, ""+size, "", "", "", "", Integer.valueOf(ds.substring(0, ds.indexOf("."))));
			SignedParams sparam = new SignedParams();
			sparam.setSnsUid(XA.instance().getUid());
			sparam.setAppId("chucktest@337_en_speedtest");
			CustomReport cusReport = new CustomReport(sparam,stat);
			if(XAPolicy.getIntance().getReportPolicy(mContext) == XAPolicy.REALTIME)
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
			e.printStackTrace();
			if(currentReport != null)
			{

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
			stat.setEventName(XAEvent.COUNT);
			stat.setTimestamp(XTimeStamp.getTimeStamp());
			String ds =String.valueOf(uploadSpeed*100);
			
			XNetwork net = new XNetwork(mContext);
			String type =ip+"_" +net.getConnectType()+"_UL";
			stat.setCunstomData(type, ""+size, "", "", "", "", Integer.valueOf(ds.substring(0,ds.indexOf("."))));
			SignedParams sparam = new SignedParams();
			sparam.setSnsUid(XA.instance().getUid());
			sparam.setAppId("chucktest@337_en_speedtest");
			CustomReport cusReport = new CustomReport(sparam,stat);
			if(XAPolicy.getIntance().getReportPolicy(mContext) == XAPolicy.REALTIME)
			{
				if(Xutils.isAppNetworkPermit(mContext) && Xutils.isNetworkAvailable(mContext))
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
			Log.e("Xingcloud", "No speedtest.rtf file in sdcard or network exception");
			if(currentReport != null)
			{

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
	
	public static TimerTask sendTask = new TimerTask(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message message = new Message();
			message.what = REPORT_ERROR;
			sendHandler.sendMessage(message);
		}
		
	};
	
	public static Handler sendHandler =  new Handler(){
		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
			case REPORT_ERROR:
				_instantce.addReport(_instantce.currentReport);
				sendTask.cancel();
				//send report here
				//sendBatchReport();
				//removeAllReport();
				//timer.schedule(task, 300000); 
				break;
			case REPORT_SUCCESS:
				Xutils.getIntance().deleteReport(_instantce.mContext);
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	
}
