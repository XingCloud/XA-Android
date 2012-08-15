package com.xingcloud.analytic.user;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import com.xingcloud.analytic.utils.XTimeStamp;
import com.xingcloud.analytic.utils.Xutils;

public class UserField {
	
	private final  String USER_LOGIN = "user.login";
	private final  String USER_UPDATE = "user.update";
	private final  String USER_VISIT = "user.visit";
	private final  String USER_HEART_BEAT = "user.heartbeat";
	private final  String USER_QUIT = "user.quit";
	private final  String USER_INC = "user.inc";
	
	private final  String USER_ACTION_BUY_ITEM = "user.action.BuyItem";
	private final  String USER_ACTION_TUTORIAL_ACTION = "user.action.TutorialAction";
	private final  String USER_ACTION_TUTOTIAL_STEP_ACTION = "user.action.TutorialStepAction";
	
	private final  String USER_PAY_VISIT = "pay.visit";
	private final  String USER_PAY_VISITC = "pay.visitc";
	private final  String USER_PAY_COMPLETE = "pay.complete";
	private final  String USER_PAGE_VISIT = "page.visit";
	
	
	private String _appId="";
	private String _uid="";//uuid
	private String _event="";
	private JSONObject _json_var = null;
	private String _ref = null;//option
	private String timestamp="";
	
	
	public UserField()
	{}
	public void setTimestamp(String tstamp)
	{
		this.timestamp=tstamp;
	}
	
	public String getTimestamp()
	{
		return this.timestamp;
	}
	public UserField(String appId,String uid,String event,JSONObject json_var)
	{
		this._appId = appId;
		this._uid = uid;
		this._event =  parseEvent(Integer.valueOf(event));
		this._json_var = json_var;
	}
	/****************
	 * 现在用不着ref参数，请使用时设置为null
	 * @param appId
	 * @param uid
	 * @param event
	 * @param json_var
	 * @param ref
	 */
	public UserField(String appId,String uid,String event,JSONObject json_var,String ref)
	{
		this._appId = appId;
		this._uid = uid;
		this._event = parseEvent(Integer.valueOf(event));
		this._json_var = json_var;
		this._ref = ref;
	}
	
	public void setAppId(String appId)
	{
		this._appId = appId;
	}
	
	public String getAppId()
	{
		return this._appId;
	}
	
	public void setUId(String uid)
	{
		this._uid = uid;
	}
	
	public String getUId()
	{
		return this._uid;
	}
	
	
	public void setEvent(int event)
	{
		this._event = parseEvent(event);
	}
	
	public String getEvent()
	{
		return String.valueOf(deparseEvent(this._event));
	}
	
	public void setJsonVar(JSONObject json_var)
	{
		this._json_var = json_var;
	}
	
	public JSONObject getJsonVar()
	{
		return this._json_var;
	}
	
	public void setRef(String ref)
	{
		this._ref = ref;
	}
	
	public String getRef()
	{
		return this._ref;
	}
	
	public String toString()
	{
		StringBuilder builder =  new  StringBuilder();
		builder.append("appid=");
		builder.append(this._appId);
		builder.append("&");
		builder.append("uid=");
		builder.append(this._uid);
		builder.append("&");
		builder.append("event=");
		builder.append(this._event);
		builder.append("&");
		if(null == _json_var)
		{
			builder.append("json_var=");
			builder.append("[]");
		}
		else
		{
			builder.append("json_var=");
			builder.append(this._json_var.toString());
		}
		return builder.toString();
	}
	
	public String toStringEx()
	{
		StringBuilder builder =  new  StringBuilder();
		try {
			builder.append("appid=");
			builder.append(this._appId);
			builder.append("&");
			builder.append("uid=");
			
			builder.append(URLEncoder.encode(this._uid,"UTF-8"));
			
			builder.append("&");
			builder.append("event=");
			builder.append(URLEncoder.encode(this._event,"UTF-8"));
			builder.append("&");
			if(null == _json_var)
			{
				builder.append("json_var=");
				builder.append("[]");
				
			}
			else
			{
				
				builder.append("json_var=");
				_json_var.put("is_mobile", "true");
				builder.append(URLEncoder.encode(this._json_var.toString(),"UTF-8"));
			}
			if(_ref !=null)
			{
				builder.append("&");
				builder.append("ref=");
				builder.append(URLEncoder.encode(this._ref.toString(),"UTF-8"));
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return builder.toString();
	}
	
	public String toStringBa()
	{
		JSONObject ba =  new JSONObject();
		try {
			ba.put("appid", this._appId);
			ba.put("uid", this._uid);
			ba.put("event", this._event);
			if(null != _json_var)
			{
				if(!_json_var.has("is_mobile"))
				{
					_json_var.put("is_mobile", "true");
				}
					
				ba.put("json_var", _json_var);
			}
			if(_ref !=null)
			{
				ba.put("ref", this._ref);
			}
			if(null == timestamp || "" == timestamp)
			{
				ba.put("timestamp", XTimeStamp.getTimeStamp());
			}
			else
			{
				ba.put("timestamp", timestamp);
			}
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		logs=[
//		      {"timestamp":"1312341396935","uid":"hechel",
//		    	  "appid":"chucktest@sinaweibo_cn_1","event":"user.login}," +
//		    	  		"{"uid":"357341030388786-38e7d819726f"," +
//		    	  				""event":"user.error","json_var":{"count":1," +
//		    	  						""errorCode":"XCCrash"," +
//		    	  								""msg":"java.lang.NullPointerException\n\tat com.xingcloud.analytic.demo.AnalyticDemoActivity.tackBuyAction(AnalyticDemoActivity.java:83)\n\tat com.xingcloud.analytic.demo.AnalyticDemoActivity.onClick(AnalyticDemoActivity.java:104)\n\tat android.view.View.performClick(View.java:2408)\n\tat android.view.View$PerformClick.run(View.java:8817)\n\tat android.os.Handler.handleCallback(Handler.java:587)\n\tat android.os.Handler.dispatchMessage(Handler.java:92)\n\tat android.os.Looper.loop(Looper.java:143)\n\tat android.app.ActivityThread.main(ActivityThread.java:4914)\n\tat java.lang.reflect.Method.invokeNative(Native Method)\n\tat java.lang.reflect.Method.invoke(Method.java:521)\n\tat com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:868)\n\tat com.android.internal.os.ZygoteInit.main(ZygoteInit.java:626)\n\tat dalvik.system.NativeStart.main(Native Method)\n"},"timestamp":"1312341397725"}]
//		      }

//		StringBuilder builder =  new  StringBuilder();
//		builder.append("{");
//		builder.append("\"appid\""+":");
//		builder.append("\""+this._appId+"\"");
//		builder.append(",");
//		builder.append("\"uid\""+":");
//		builder.append("\""+this._uid+"\"");
//		builder.append(",");
//		builder.append("\"event\""+":");
//		builder.append("\""+this._event+"\"");
//		builder.append(",");
//		if(null == _json_var)
//		{
//			builder.append("\"json_var\":");
//			builder.append("[]");
//			
//		}
//		else
//		{
//			builder.append("\"json_var\":");
//			builder.append(this._json_var.toString());
//		}
//		if(_ref !=null)
//		{
//			builder.append(",");
//			builder.append("\"+ref\""+":");
//			builder.append("\""+this._ref+"\"");
//		}
//		if(null == timestamp || "" == timestamp)
//		{
//			builder.append(",");
//			builder.append("\"timestamp\":");
//			//builder.append("\""+XTimeStamp.getTimeStamp()+"\"");
//			builder.append("\"");//+this.timestamp+"\"");
//			builder.append(XTimeStamp.getTimeStamp());
//			builder.append("\"");
//		}
//		else
//		{
//			builder.append(",");
//			builder.append("\"timestamp\":");
//			builder.append("\"");//+this.timestamp+"\"");
//			builder.append(this.timestamp);
//			builder.append("\"");
//		}
//		
//		builder.append("}");
		return ba.toString();
	}
	
	public String parseEvent(int event)
	{
		switch(event)
		{
		case UserEvent.USER_LOGIN:
			return USER_LOGIN;
		case UserEvent.USER_ACTION_BUY_ITEM:
			return USER_ACTION_BUY_ITEM;
		case UserEvent.USER_ACTION_TUTORIAL_ACTION:
			return USER_ACTION_TUTORIAL_ACTION;
		case UserEvent.USER_ACTION_TUTOTIAL_STEP_ACTION:
			return USER_ACTION_TUTOTIAL_STEP_ACTION;
		case UserEvent.USER_HEART_BEAT:
			return USER_HEART_BEAT;
		case UserEvent.USER_PAY_COMPLETE:
			return USER_PAY_COMPLETE;
		case UserEvent.USER_PAY_VISIT:
			return USER_PAY_VISIT;
		case UserEvent.USER_PAY_VISITC:
			return USER_PAY_VISITC;
		case UserEvent.USER_QUIT:
			return USER_QUIT;
		case UserEvent.USER_UPDATE:
			return USER_UPDATE;
		case UserEvent.USER_VISIT:
			return USER_VISIT;
		case UserEvent.USER_PAGE_VISIT:
			return USER_PAGE_VISIT;
		case UserEvent.USER_INC:
			return USER_INC;
		}
		return "";
	}
	
	public int deparseEvent(String event)
	{
		if(event.compareToIgnoreCase(USER_LOGIN) == 0)
		{
			return UserEvent.USER_LOGIN;
		}
		else if(event.compareToIgnoreCase(USER_ACTION_BUY_ITEM) == 0)
		{
			return UserEvent.USER_ACTION_BUY_ITEM;
		}
		else if(event.compareToIgnoreCase(USER_ACTION_TUTORIAL_ACTION) == 0)
		{
			return UserEvent.USER_ACTION_TUTORIAL_ACTION;
		}
		else if(event.compareToIgnoreCase(USER_ACTION_TUTOTIAL_STEP_ACTION) == 0)
		{
			return UserEvent.USER_ACTION_TUTOTIAL_STEP_ACTION;
		}
		else if(event.compareToIgnoreCase(USER_HEART_BEAT) == 0)
		{
			return UserEvent.USER_HEART_BEAT;
		}
		else if(event.compareToIgnoreCase(USER_PAY_COMPLETE) == 0)
		{
			return UserEvent.USER_PAY_COMPLETE;
		}
		else if(event.compareToIgnoreCase(USER_PAY_VISIT) == 0)
		{
			return UserEvent.USER_PAY_VISIT;
		}
		else if(event.compareToIgnoreCase(USER_PAY_VISITC) == 0)
		{
			return UserEvent.USER_PAY_VISITC;
		}
		else if(event.compareToIgnoreCase(USER_QUIT) == 0)
		{
			return UserEvent.USER_QUIT;
		}
		else if(event.compareToIgnoreCase(USER_UPDATE) == 0)
		{
			return UserEvent.USER_UPDATE;
		}
		else if(event.compareToIgnoreCase(USER_VISIT) == 0)
		{
			return UserEvent.USER_VISIT;
		}
		else if(event.compareToIgnoreCase(USER_PAGE_VISIT) == 0)
		{
			return UserEvent.USER_PAGE_VISIT;
		}
		else if(event.compareToIgnoreCase(USER_INC) == 0)
		{
			return UserEvent.USER_INC;
		}
		return -1;
	}
}
