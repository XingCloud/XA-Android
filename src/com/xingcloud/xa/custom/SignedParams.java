package com.xingcloud.xa.custom;

public class SignedParams {
	private String sign = "";
	
	private String sns_uid="";
	
	
	private String sns_id="";
	
	private String appid = "";
	private String uid="";//uuid
	private String timestamp = "";
	public SignedParams()
	{}
	
	public SignedParams(String _sign,String _sns_uid,String _appid,String _sns_id,String _uid)
	{
		this.appid = _appid;
		this.sign = _sign;
		this.sns_id = _sns_id;
		this.sns_uid = _sns_uid;
		this.uid = _uid;
	}
	
	public void setTimestamp(String timestamp)
	{
		if(null == timestamp)
		{
			this.timestamp = "";
		}
		this.timestamp = timestamp;
	}
	
	public String getTimestamp()
	{
		return this.timestamp;
	}
	public void setUid(String _uid)
	{
		if(null == _uid)
		{
			_uid = "";
		}
		this.uid = _uid;
	}
	
	public String getUid()
	{
		return this.uid;
	}
	public void setSign(String _sign)
	{
		if(null == _sign)
		{
			_sign = "";
		}
		this.sign = _sign;
	}
	
	public String getSign()
	{
		return this.sign;
	}
	
	public void setSnsUid(String _sns_uid)
	{
		if(null == _sns_uid)
		{
			_sns_uid = "";
		}
		this.sns_uid = _sns_uid;
	}
	
	public String getSnsUid()
	{
		return this.sns_uid;
	}
	
	public void setSnsId(String _sns_id)
	{
		if(null == _sns_id)
		{
			_sns_id = "";
		}
		this.sns_id = _sns_id;
	}
	
	public String getSnsId()
	{
		return this.sns_id;
	}
	
	public void setAppId(String _app_id)
	{
		if(null == _app_id)
		{
			_app_id = "";
		}
		this.appid = _app_id;
	}
	
	public String getAppId()
	{
		return this.appid;
	}
	
	public String toString()
	{
		StringBuilder build =  new StringBuilder();
		build.append("{");
//		build.append("\"sign\":");
//		if(sign == null)
//		{
//			sign = "";
//		}
//		build.append("\""+sign+"\"");
//		build.append(",");
		
		if(uid == null)
		{
			uid = "";
		}
		build.append("\"uid\":");
		build.append("\""+uid+"\"");
		build.append(",");
		
		
//		if(sns_uid == null)
//		{
//			sns_uid = "";
//		}
//		build.append("\"sns_uid\":");
//		build.append("\""+sns_uid+"\"");
//		build.append(",");
//		
//		
		if(timestamp == null)
		{
			timestamp = String.valueOf(System.currentTimeMillis());
		}
		build.append("\"timestamp\":");
		build.append("\""+timestamp+"\"");
		build.append(",");
		
		if(appid == null)
		{
			appid = "";
		}
		build.append("\"appid\":");
		build.append("\""+appid+"\"");
		//build.append(",");
		
		build.append("}");
		
		return build.toString();
	}
}
