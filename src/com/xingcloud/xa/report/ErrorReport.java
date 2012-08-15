package com.xingcloud.xa.report;

import com.xingcloud.xa.error.ErrorEvent;
import com.xingcloud.xa.error.ErrorField;
import com.xingcloud.xa.error.IErrorAction;
import com.xingcloud.xa.sender.ReportTask;
import com.xingcloud.xa.utils.XTimeStamp;



public class ErrorReport implements IErrorAction{

	private String content=null;

	public ErrorReport()
	{
	}


	public ErrorReport(String params)
	{
		content = params;
	}

	public ErrorReport(ErrorField errorField)
	{
		this.content = errorField.toString();
	}
	
	private void sendErrorReport(String params)
	{
		if(null == params )
		{
			throw new Error("report params is null");
		}
		ReportTask task =  new ReportTask(params,XTimeStamp.getTimeStamp(),ErrorEvent.ERROR_EVENT);
		task.execute();
	}
	public String getContent()
	{
	
		if(null == content)
		{
			return "";
		}
		else
		{
			return content;
		}
		
	}
	@Override
	public void reportErrorAction(ErrorField errorField) {
		if(errorField==null)
		{
			throw new Error("error data is null");
		}
		
		sendErrorReport(errorField.toString());
	}
	

	@Override
	public void reportErrorAction(String content) {
		// TODO Auto-generated method stub
		if(content==null || content=="")
		{
			throw new Error("error data is null");
		}
		
		sendErrorReport(content);
	}

	@Override
	public void reportErrorAction() {
		reportErrorAction(this.content);
	}

}
