package com.xingcloud.analytic.user;



public class UserEvent {
	
	private final static int first = 0;
	/*
	 * 记录用户加载游戏的时间
	 */
	public final static int USER_LOGIN = 1;
	/*
	 *用于更新这个用户在系统上的对于统计项有影响的信息
	 */
	public final static int USER_UPDATE = 2;
	/*
	 * 
	 * 记录用户登录游戏的行为
	 */
	public final static int USER_VISIT = 3;
	/*
	 * 心跳事件
	 */
	public final static int USER_HEART_BEAT = 4;
	/*
	 * 记录用户玩游戏的在线时长
	 */
	public final static int USER_QUIT = 5;
	
	/*
	 * 记录游戏中商城买卖的状况
	 */
	public final static int USER_ACTION_BUY_ITEM = 6;
	/*
	 * 记录游戏中向导行为的执行状况,在向导成功完成之后发送
	 */
	public final static int USER_ACTION_TUTORIAL_ACTION = 7;
	/*
	 * 录游戏中向导行为的每一步的执行状况，在向导每个步骤结束的时候发送
	 */
	public final static int USER_ACTION_TUTOTIAL_STEP_ACTION = 8;
	/*
	 * 记录用户打开支付页面
	 */
	public final static int USER_PAY_VISIT = 9;
	/*
	 * 记录用户打开支付渠道页面
	 */
	public final static int USER_PAY_VISITC = 10;
	/*
	 * 记录用户付费情况
	 */
	public final static int USER_PAY_COMPLETE = 11;
	/**
	 * 统计用户在页面停留的时间
	 */
	public final static int USER_PAGE_VISIT = 12;
	
	/*
	 * 累加事件
	 */
	public final static int USER_INC = 13;
	

	
	private final static int last = 14;
	
	public static Boolean isEventSupported(int event)
	{
		if(event <= first || (event >= last))
		{
			return false;
		}
		return true;
	}
}
