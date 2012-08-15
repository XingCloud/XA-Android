package com.xingcloud.analytic.report;

import android.view.Display;

public class ReportField {
	
	public static final String UserError_Message = "msg";
	public static final String UserError_Code = "code";
	
	public static final String UserLogin_Step = "step";
	public static final String UserLogin_Time = "time";
	
	public static final String UserQuit_Time = "time_duration";
	
	public static final String PayComplete_Gross = "gross";
	public static final String PayComplete_Gcurrency = "gurrency";
	public static final String PayComplete_Channel = "channel";
	public static final String PayComplete_TransID = "trans_id";
	
	public static final String Tutorial_Index = "index";
	public static final String Tutorial_Name = "name";
	public static final String Tutorial_Tutorial = "tutorial";
	
	public static final String BuyItem_Resource = "resource";
	public static final String BuyItem_Paytype = "paytype";//收入或支出（只能取值为income/payout）
	public static final String BuyItem_Amount = "amount";//消耗的货币总量
	public static final String BuyItem_Number = "number";//物品数量
	
	public static final String MileStone_Name = "milestone_name";
	
	public static final String Level1 = "level1";
	public static final String Level2 = "level2";
	public static final String Level3 = "level3";
	public static final String Level4 = "level4";	
	public static final String Level5 = "level5";
}
