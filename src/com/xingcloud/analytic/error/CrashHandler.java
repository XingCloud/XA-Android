package com.xingcloud.analytic.error;
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

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.xingcloud.analytic.CloudAnalytic;
import com.xingcloud.analytic.report.CustomReport;
import com.xingcloud.analytic.report.ErrorReport;
import com.xingcloud.analytic.report.UserReport;
import com.xingcloud.analytic.user.UserEvent;
import com.xingcloud.analytic.user.UserField;
import com.xingcloud.analytic.utils.LogTag;
import com.xingcloud.analytic.utils.XCTime;
import com.xingcloud.analytic.utils.XTimeStamp;
import com.xingcloud.analytic.utils.Xutils;

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
                Thread.sleep(3000);  
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
        	Log.e(LogTag.XC_TAG, result);
        	//public ErrorField(String appId,String uid,String errorCode,String msg,int count)
        	ErrorField err  = new ErrorField(Xutils.getGameAppId(mContext),Xutils.generateUUID(mContext),"XCCrash",result,1);
        	ErrorReport errorReport = new ErrorReport(err);
        	List<Object> reports = new ArrayList<Object>();
        	reports.add(errorReport);
        	Object  obj = CloudAnalytic.instance().getCurrentReport();
        	if(null != obj)
        	{
        		reports.add(obj);
        	}
        	HashMap<Object,Boolean> reps = CloudAnalytic.instance().getReports();
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
        	JSONObject baseInfo = new JSONObject();
    		try {
    			Long finish_time = System.currentTimeMillis();
    			baseInfo.put("finishTime", finish_time);
    			long ptime = CloudAnalytic.instance().getDuration(finish_time);
    			baseInfo.put("time_duration", ptime);
    		} catch (JSONException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		UserField userInfo =  new UserField();
    		userInfo.setAppId(Xutils.getGameAppId(mContext));
    		userInfo.setEvent(UserEvent.USER_QUIT);
    		userInfo.setJsonVar(baseInfo);
    		userInfo.setUId(Xutils.generateUUID(mContext));
    		userInfo.setTimestamp(XTimeStamp.getTimeStamp());
    		UserReport report = new UserReport(userInfo.toStringBa(),UserEvent.USER_QUIT);
    		reports.add(report);
        	Xutils.getIntance().saveReport(mContext, reports);
        	return true;
        }
        else
        {
        	return false;
        }
    }  
 
}
  
 
