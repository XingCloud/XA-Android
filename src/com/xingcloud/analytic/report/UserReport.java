package com.xingcloud.analytic.report;

import android.util.Log;

import com.xingcloud.analytic.sender.ReportTask;
import com.xingcloud.analytic.user.IUserAction;
import com.xingcloud.analytic.user.UserEvent;
import com.xingcloud.analytic.user.UserField;
import com.xingcloud.analytic.utils.LogTag;
import com.xingcloud.analytic.utils.XTimeStamp;
import com.xingcloud.analytic.xnative.XCNative;

public class UserReport implements IUserAction{
	
	
	private int eventId;
	private String content;
	//private static UserReport _instance;
	private UserField user;
	public UserReport()
	{
		//user = user_data;
	}
	public UserReport(UserField user_data,int event)
	{
		user = user_data;
		eventId = event;
	}
	public UserReport(String data,int event)
	{
		content = data;
		eventId = event;
	}
	
	public void setData(String data)
	{
		content = data;
	}
	public int getEventId()
	{
		return eventId;
	}
	public String getData()
	{
		if(null != content)
		{
			return content;
		}
		else
		{
			return user.toStringBa();
		}
		
	}
//	public static UserReport instance()
//	{
//		if(null == _instance)
//		{
//			_instance = new UserReport();
//		}
//		return _instance;
//	}
	
	protected void userLogin(UserField user_data)
	{
		if(null == user_data)
		{
			throw new Error("Please support full user params");
		}
		
		ReportTask task =  new ReportTask(user_data.toStringEx(), XTimeStamp.getTimeStamp(),UserEvent.USER_LOGIN);
		//XCNative.sendBaseReport(user_data.toStringEx(), XTimeStamp.getTimeStamp());
		task.execute();
	}
	protected void userQuit(UserField user_data)
	{
		if(null == user_data)
		{
			throw new Error("Please support full user params");
		}
		
		ReportTask task =  new ReportTask(user_data.toStringEx(), XTimeStamp.getTimeStamp(),UserEvent.USER_QUIT);
		//XCNative.sendBaseReport(user_data.toStringEx(), XTimeStamp.getTimeStamp());
		task.execute();
	}
	protected void userVisit(UserField user_data)
	{
		if(null == user_data)
		{
			throw new Error("Please support full user params");
		}
		ReportTask task =  new ReportTask(user_data.toStringEx(), XTimeStamp.getTimeStamp(),UserEvent.USER_VISIT);
		//XCNative.sendBaseReport(user_data.toStringEx(), XTimeStamp.getTimeStamp());
		task.execute();
		//XCNative.sendBaseReport(user_data.toStringEx(), XTimeStamp.getTimeStamp());
	}
	
	protected void userUpdate(UserField user_data)
	{
		if(null == user_data)
		{
			throw new Error("Please support full user params");
		}
		ReportTask task =  new ReportTask(user_data.toStringEx(), XTimeStamp.getTimeStamp(),UserEvent.USER_UPDATE);
		//XCNative.sendBaseReport(user_data.toStringEx(), XTimeStamp.getTimeStamp());
		task.execute();
		//XCNative.sendBaseReport(user_data.toStringEx(), XTimeStamp.getTimeStamp());
	}

	protected void userHeartBeat(UserField user_data)
	{
		if(null == user_data)
		{
			throw new Error("Please support full user params");
		}
		ReportTask task =  new ReportTask(user_data.toStringEx(), XTimeStamp.getTimeStamp(),UserEvent.USER_HEART_BEAT);
		//XCNative.sendBaseReport(user_data.toStringEx(), XTimeStamp.getTimeStamp());
		task.execute();
		//XCNative.sendBaseReport(user_data.toStringEx(), XTimeStamp.getTimeStamp());
	}
	
	protected void userBuyItem(UserField user_data)
	{
		if(null == user_data)
		{
			throw new Error("Please support full user params");
		}
		ReportTask task =  new ReportTask(user_data.toStringEx(), XTimeStamp.getTimeStamp(),UserEvent.USER_ACTION_BUY_ITEM);
		//XCNative.sendBaseReport(user_data.toStringEx(), XTimeStamp.getTimeStamp());
		task.execute();
		//XCNative.sendBaseReport(user_data.toStringEx(), XTimeStamp.getTimeStamp());
	}
	
	protected void userTutorial(UserField user_data)
	{
		if(null == user_data)
		{
			throw new Error("Please support full user params");
		}
		ReportTask task =  new ReportTask(user_data.toStringEx(), XTimeStamp.getTimeStamp(),UserEvent.USER_ACTION_TUTORIAL_ACTION);
		//XCNative.sendBaseReport(user_data.toStringEx(), XTimeStamp.getTimeStamp());
		task.execute();
		//XCNative.sendBaseReport(user_data.toStringEx(), XTimeStamp.getTimeStamp());
	}
	
	protected void userTutorialStep(UserField user_data)
	{
		if(null == user_data)
		{
			throw new Error("Please support full user params");
		}
		ReportTask task =  new ReportTask(user_data.toStringEx(), XTimeStamp.getTimeStamp(),UserEvent.USER_ACTION_TUTOTIAL_STEP_ACTION);
		//XCNative.sendBaseReport(user_data.toStringEx(), XTimeStamp.getTimeStamp());
		task.execute();
		//XCNative.sendBaseReport(user_data.toStringEx(), XTimeStamp.getTimeStamp());
	}
	
	protected void userPayVisit(UserField user_data)
	{
		if(null == user_data)
		{
			throw new Error("Please support full user params");
		}
		ReportTask task =  new ReportTask(user_data.toStringEx(), XTimeStamp.getTimeStamp(),UserEvent.USER_PAY_VISIT);
		//XCNative.sendBaseReport(user_data.toStringEx(), XTimeStamp.getTimeStamp());
		task.execute();
		//XCNative.sendBaseReport(user_data.toStringEx(), XTimeStamp.getTimeStamp());
	}
	
	protected void userPayVisitc(UserField user_data)
	{
		if(null == user_data)
		{
			throw new Error("Please support full user params");
		}
		ReportTask task =  new ReportTask(user_data.toStringEx(), XTimeStamp.getTimeStamp(),UserEvent.USER_PAY_VISITC);
		//XCNative.sendBaseReport(user_data.toStringEx(), XTimeStamp.getTimeStamp());
		task.execute();
		//XCNative.sendBaseReport(user_data.toStringEx(), XTimeStamp.getTimeStamp());
	}

	protected void userInc(UserField user_data)
	{
		if(null == user_data)
		{
			throw new Error("Please support full user params");
		}
		ReportTask task =  new ReportTask(user_data.toStringEx(), XTimeStamp.getTimeStamp(),UserEvent.USER_INC);
		//XCNative.sendBaseReport(user_data.toStringEx(), XTimeStamp.getTimeStamp());
		task.execute();
		//XCNative.sendBaseReport(user_data.toStringEx(), XTimeStamp.getTimeStamp());
	}
	protected void userPayComplete(UserField user_data)
	{
		if(null == user_data)
		{
			throw new Error("Please support full user params");
		}
		ReportTask task =  new ReportTask(user_data.toStringEx(), XTimeStamp.getTimeStamp(),UserEvent.USER_PAY_COMPLETE);
		//XCNative.sendBaseReport(user_data.toStringEx(), XTimeStamp.getTimeStamp());
		task.execute();
		//XCNative.sendBaseReport(user_data.toStringEx(), XTimeStamp.getTimeStamp());
	}
	
	protected void userPageVisit(UserField user_data)
	{
		if(null == user_data)
		{
			throw new Error("Please support full user params");
		}
		ReportTask task =  new ReportTask(user_data.toStringEx(), XTimeStamp.getTimeStamp(),UserEvent.USER_PAGE_VISIT);
		//XCNative.sendPageReport(user_data.toStringEx());
		//XCNative.sendBaseReport(user_data.toStringEx(), XTimeStamp.getTimeStamp());
		task.execute();
		//XCNative.sendBaseReport(user_data.toStringEx(), XTimeStamp.getTimeStamp());
	}
	
	@Override
	public void reportUserAction(UserField user_data, int event) {
		// TODO Auto-generated method stub
		if(null == user_data)
		{
			throw new Error("please provide userdata before you send report");
		}
		if(!UserEvent.isEventSupported(event))
		{
			Log.e(LogTag.USER_TAG, "the event "+event+" is not supported");
		}
		eventId = event;
		switch(event)
		{
		case UserEvent.USER_LOGIN:
			userLogin(user_data);
			break;
		case UserEvent.USER_VISIT:
			userVisit(user_data);
			break;
		case UserEvent.USER_UPDATE:
			userUpdate(user_data);
			break;
		case UserEvent.USER_HEART_BEAT:
			userHeartBeat(user_data);
			break;
		case UserEvent.USER_ACTION_BUY_ITEM:
			userBuyItem(user_data);
			break;
		case UserEvent.USER_ACTION_TUTORIAL_ACTION:
			userTutorial(user_data);
			break;
		case UserEvent.USER_ACTION_TUTOTIAL_STEP_ACTION:
			userTutorialStep(user_data);
			break;
		case UserEvent.USER_PAY_VISIT:
			userPayVisit(user_data);
			break;
		case UserEvent.USER_PAY_VISITC:
			userPayVisitc(user_data);
			break;
		case UserEvent.USER_PAY_COMPLETE:
			userPayComplete(user_data);
			break;
		case UserEvent.USER_QUIT:
			userQuit(user_data);
			break;
		case UserEvent.USER_PAGE_VISIT:
			userPageVisit(user_data);
			break;
		case UserEvent.USER_INC:
			userInc(user_data);
			break;
		}
	}
	@Override
	public void reportUserAction(int event) {
		// TODO Auto-generated method stub
		reportUserAction(user,event);
	}
}
