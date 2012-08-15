package com.xingcloud.analytic;

import org.json.JSONObject;

import com.xingcloud.analytic.user.UserField;

import android.app.Activity;
import android.content.Context;

interface ICloud {
	/**
	 * 在启动时发送
	 * @param context 当前activity的实例
	 */
	void onCreate(Activity context);
	/**
	 * 在onStart中调用
	 * @param context 当前activity的上下文实例
	 */
	void onStart(Context context);
	/**
	 * 在应用时发送
	 * @param context 当前activity的上下文实例
	 */
	void onPause(Context context);
	/**
	 * 在应用resume时发送
	 * @param context 当前activity的上下文实例
	 */
	void onResume(Context context);
	/**
	 * 在用户退出时发送
	 * @param context 当前activity的实例
	 */
	public void onFinish(Activity context);
	
	/**
	 * 需要更新用户数据的时候调用，比如用户登录时，切换用户时
	 * @param context 当前activity的实例
	 */
	public void update(Activity context);
	/**
	 * 设置发送report的策略
	 * @param policy 策略，可供选择的有：
	 * 	DEFAULT：定时定量发送，默认间隔时间是1分钟，一次最大数量上10条
	 *	REALTIME：立即发送，只要有report就马上发出去
	 *	BATCH_AT_LAUNCH：在每次启动时批量发送
	 *	BATCH_AT_TERMINATE：在每次退出时批量发送
	 * @param context 当前activity的上下文实例
	 */
	
	public void setReportPolicy(int policy,Context context);
	
	/**
	 * 暂未实现功能
	 * @param context 当前activity的上下文实例
	 */
	public void setErrorHandler(Activity context);
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
	public void trackEvent(Context context,String function, String action,
			String level1,String level2,String level3,String level4,String level5,
			int count);
	/**
	 * track游戏中都的交易行为
	 * @param context	当前activity的实例
	 * @param function	交易过程中的事件
	 * @param values	交易数据
	 */
	public void trackTransaction(Activity context, int function,JSONObject values);
	/**
	 * 统计在activity上停留的时间，在destroy或者跳转时调用
	 * @param context 当前activity的实例
	 * @param current_activity_name   当前activity的名字
	 * @param next_activity_name	  要跳转到的activity的名字
	 */
	public void trackPageview(Activity context,String current_activity_name,String next_activity_name);
	/**
	 * 
	 * 发送基本事件
	 * @param context 当前activity的context实例
	 * @param user	  封装事件的数据对象
	 */
	public void trackUserEvent(Context context,UserField user);
	/**
	 * 
	 * @param gameUid
	 */
	public void updateGameUid(String gameUid);
	
	
}
