package com.xingcloud.xa.error;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import com.xingcloud.xa.XA;
import com.xingcloud.xa.XAEvent;
import com.xingcloud.xa.XAReportCache;
import com.xingcloud.xa.custom.ReportField;
import com.xingcloud.xa.custom.Stats;
import com.xingcloud.xa.report.CustomReport;
import com.xingcloud.xa.report.ErrorReport;
import com.xingcloud.xa.utils.LogTag;
import com.xingcloud.xa.utils.XTimeStamp;
import com.xingcloud.xa.utils.Xutils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;



public class CrashHandler implements UncaughtExceptionHandler{
    /** Debug Log tag*/  
    public static final String TAG = "CrashHandler";  

    /** 系统默认的UncaughtException处理类 */  
    private Thread.UncaughtExceptionHandler mDefaultHandler;  
    /** CrashHandler实例 */  
    private static CrashHandler INSTANCE;  
    /** 程序的Context对象 */  
    private Activity mContext;     
      
    /** 保证只有一个CrashHandler实例 */  
    private CrashHandler() {}  
    /** 获取CrashHandler实例 ,单例模式*/  
    public static CrashHandler getInstance() {  
        if (INSTANCE == null) {  
            INSTANCE = new CrashHandler();  
        }  
        return INSTANCE;  
    }  
  
    /** 
     * 初始化,注册Context对象, 
     * 获取系统默认的UncaughtException处理器, 
     * 设置该CrashHandler为程序的默认处理器 
     *  
     * @param ctx 
     */  
    public void init(Activity ctx) {  
        mContext = ctx;  
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();  
        Thread.setDefaultUncaughtExceptionHandler(this);  
    }  
  
    /** 
     * 当UncaughtException发生时会转入该函数来处理 
     */  
    @Override  
    public void uncaughtException(Thread thread, Throwable ex) {  
        if (!handleException(ex) && mDefaultHandler != null) {  
            //如果用户没有处理则让系统默认的异常处理器来处理  
            mDefaultHandler.uncaughtException(thread, ex);  
        } else {  
            //Sleep一会后结束程序  
            try {  
                Thread.sleep(4000);  
            } catch (InterruptedException e) {  
                Log.e(TAG, "Error : ", e);  
            }  
            android.os.Process.killProcess(android.os.Process.myPid());  
            System.exit(10);  
        }  
    }  
  
    /** 
     * 自定义错误处理,将错误信息写到文件中去
     * @param ex 
     * @return true:如果处理了该异常信息;否则返回false 
     */  
    private boolean handleException(Throwable ex) {  
        if (ex == null) {  
            return true;  
        }  
        final String msg = ex.getLocalizedMessage();    
        
        return saveCrashInfoToFile(ex);
    }    

    /** 
     * 保存错误信息到文件中 
     * @param ex 
     * @return 
     */  
    private boolean saveCrashInfoToFile(Throwable ex) {  
        Writer info = new StringWriter();  
        PrintWriter printWriter = new PrintWriter(info);  
        ex.printStackTrace(printWriter);  
        
        Throwable cause = ex.getCause();  
        while (cause != null) {  
            cause.printStackTrace(printWriter);  
            cause = cause.getCause();  
        }  
  
        String result = info.toString();  
        printWriter.close();  
        
        if(result!="" && result!=null)
        {
        	
        	JSONObject baseInfo = new JSONObject();
    		try {
    			
    			baseInfo.put(ReportField.USER_ERROR_CODE, "XCCrash");
    			baseInfo.put("is_mobile", "true");
    			baseInfo.put(ReportField.USER_ERROR_MESSAGE, result);
    		} catch (JSONException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		
    		
    		Stats stat = new Stats();
    		

    		stat.setCustomData(XAEvent.USER_ERROR, baseInfo.toString(), XTimeStamp.getTimeStamp());
    		
    		CustomReport cusReport = new CustomReport(XA.instance().getParam(),stat);
    		//XAReportCache.instance().addReport(cusReport);
    		//ErrorReport errorReport = new ErrorReport(err);
        	List<Object> reports = new ArrayList<Object>();
        	reports.add(cusReport);
        	Object  obj = XAReportCache.instance().getCurrentReport();
        	if(null != obj)
        	{
        		reports.add(obj);
        	}
        	HashMap<Object,Boolean> reps = XAReportCache.instance().getReports();
        	if(null != reps && reps.size() > 0)
        	{
        		Set<Object> objs =  reps.keySet();
        		if(!objs.isEmpty())
        		{
        			Iterator<Object> it = objs.iterator();
            		while(it.hasNext())
            		{
            			Object bj = it.next();
            			if(bj != obj)
            			reports.add(bj);
            		}
        		}
        		
        	}
    		try {
    			baseInfo = null;
    			baseInfo = new JSONObject();
    			Long finish_time = System.currentTimeMillis();
    			baseInfo.put("finishTime", finish_time);
    			long ptime = XA.instance().getDuration(finish_time);			
    			if(ptime <0)
    			{
    				ptime = Math.abs(ptime);
    			}
    			baseInfo.put("time_duration", ptime);
    			baseInfo.put("is_mobile", "true");
    		} catch (JSONException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		stat = null;
    		stat = new Stats();
    		

    		stat.setCustomData(XAEvent.USER_QUIT, baseInfo.toString(), XTimeStamp.getTimeStamp());
    		cusReport = null;
    		cusReport = new CustomReport(XA.instance().getParam(),stat);
    		//XAReportCache.instance().addReport(cusReport);
    		reports.add(cusReport);
    		Xutils.getIntance().saveReport(mContext, reports);
    		//XAReportCache.instance().saveReportWithoutNetwork(mContext);
        	return true;
        }
        else
        {
        	return false;
        }
    }  
 
}
  
 
