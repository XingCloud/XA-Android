package com.xingcloud.analytic.report;

import com.xingcloud.analytic.custom.SignedParams;
import com.xingcloud.analytic.custom.Stats;

public class CustomBatchReport extends CustomReport {

	public CustomBatchReport()
	{
		super();
	}
	
	public CustomBatchReport(String content)
	{
		super(content);
	}
	
	public CustomBatchReport(SignedParams param,Stats stats)
	{
		super(param,stats);
	}
}
