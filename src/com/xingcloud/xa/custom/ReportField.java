package com.xingcloud.xa.custom;

public class ReportField {

	public static final String USER_ERROR_MESSAGE = "message";
	public static final String USER_ERROR_CODE="code";
	
	public static final String USER_LOGIN_INDEX="index";
	public static final String USER_LOGIN_TIME="time";
	public static final String USER_QUIT_DURATION_TIME="duration_time";
	
	public static final String PAY_COMPLETE_GROSS="gross";
	public static final String PAY_COMPLETE_GCURRENCY="gcurrency"; //与vcurrency对应
	public static final String PAY_COMPLETE_CHANNEL="channel";
	public static final String PAY_COMPLETE_TRANS_ID = "trans_id";
	
	public static final String TUTORIAL_INDEX="index";
	public static final String TUTORIAL_ID="tid";
	public static final String TUTORIAL_STEP_NAME="step_name";

	
	public static final String BUY_ITEM_RESOURCE="resource";
	public static final String BUY_ITEM_PAY_TYPE="pay_type";// :收入或支出（只能取值为income/payout）
	public static final String BUY_ITEM_AMOUNT="amount";//：消耗的货币总量
	public static final String BUY_ITEM_NUMBER="number";//:物品数量
	
	public static final String MILESTONE_NAME="name";
	public static final String LEVEL_1="level_1";
	public static final String LEVEL_2="level_2";
	public static final String LEVEL_3="level_3";
	public static final String LEVEL_4="level_4";
	public static final String LEVEL_5="level_5";
}
