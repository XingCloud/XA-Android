package com.xingcloud.analytic.report;

public class SocialReportField extends ReportField {

	private String platform_id;
	private String platform_name;
	
	public SocialReportField(String pid,String pname)
	{
		this.platform_id = pid;
		this.platform_name = pname;
	}
	
	public void setPlatformId(String pid)
	{
		platform_id = pid;
	}
	public String getPlatformId()
	{
		return platform_id;
	}
	
	public void setPlatformName(String pname)
	{
		platform_name = pname;
	}
	public String getPlatformName()
	{
		return platform_name;
	}
}
