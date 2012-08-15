package com.xingcloud.xa.custom;

public class AnalyticFunction {
	/**
	 * 本方法使用了data结构里面的7个字段,使用count方法就是树形统计。
	 * 系统将在level1到level5的每个结点都统计一个数值。
	 * 系统将在level1到level5的每个结点都统计一个数值。
	 * 每个父结点都是子结点的数值的总和。
	 * 叶子结点去data结构中amount作为数值，如果，amount字符串不能转化为整形，当作1处理。
	 * 实际上，count的每个事件就是在这颗统计树上的叶子结点增加一个数值。
	 * 叶子结点去data结构中amount作为数值，如果，amount字符串不能转化为整形，当作1处理。
	 * 实际上，count的每个事件就是在这颗统计树上的叶子结点增加一个数值。
	 */
	public final static String COUNT = "count";
	/**
	 * 本方法是记录在某段时间注册的用户，完成指定任务的数量百分比。从而查看用户成长状况。
	 * 本方法仅使用data中的type字段。
	 */
	public final static String MILESTONE = "milestone";
	/**
	 * 
	 */
	public final static String TUTORAL_SERVICE = "TutorialService.doStep";
	
	public final static String BUY_SERVICE = "buyitem";
}
