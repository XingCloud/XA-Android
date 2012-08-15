package com.xingcloud.xa;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class XAPolicy {

	/*
	 * 立即发送，只要有report就马上发出去
	 */
	 public static final int REALTIME = 0;
	 /*
	  * 在每次启动时批量发送
	  */
	 public static final int BATCH_AT_LAUNCH = 1;
	 /*
	  * 在每次退出时批量发送
	  */
	 public static final int BATCH_AT_TERMINATE = 2;
	 
	 //定时定量发送，默认间隔时间是1分钟，一次最大数量上10条
	 public static final int DEFAULT =3;
	 
	 private static XAPolicy _instance;
	 private int report_policy;
	 private final static String POLICY_PREFERENCE_NAME = "report_policy";
	 private final static String POLICY = "policy";
	 
	 
	 private  XAPolicy()
	 {
		 
	 }
		/*******************
		 *  获得ReportPolicy的实例
		 * @return
		 */
		public static XAPolicy getIntance()
		{
			if(null == _instance)
			{
				_instance = new XAPolicy();
			}
			return _instance;
		}
		/*************
		 * 设置report的策略
		 * @param policy 策略
		 * @param activity
		 */
		public  void setReportPolicy(int policy,Context activity)
		{
			this.report_policy = policy;
			
			SharedPreferences pref = activity.getSharedPreferences(POLICY_PREFERENCE_NAME, Activity.MODE_PRIVATE);

			SharedPreferences.Editor editor = pref.edit();
			
			editor.putInt(POLICY, policy);
			
			editor.commit();
			
		}
		/***************
		 * 获得reprt的策略
		 * @param activity  当前activity的实例
		 * @return
		 */
		public  int getReportPolicy(Context activity)
		{
			SharedPreferences pref = activity.getSharedPreferences(POLICY_PREFERENCE_NAME, Activity.MODE_PRIVATE);
			if(null == pref)
			{
				return 6;
			}
			return pref.getInt(POLICY, Activity.MODE_PRIVATE);
		}
}
