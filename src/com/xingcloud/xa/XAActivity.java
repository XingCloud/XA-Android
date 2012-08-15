package com.xingcloud.xa;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.xingcloud.xa.custom.ReportField;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class XAActivity extends Activity implements OnClickListener{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        XA.instance().setHeartbeatTimeOffset(10);
        XA.instance().setPolicy(XAPolicy.BATCH_AT_TERMINATE, this);
        XA.instance().onCreate(this);
        setUpUI();
       
    }
    protected void setUpUI()
    {
    	Button login = (Button) findViewById(R.id.login);
    	login.setOnClickListener(this);
    	Button buy = (Button) findViewById(R.id.buy);
    	buy.setOnClickListener(this);
//    	Button crash = (Button) findViewById(R.id.crash);
//    	crash.setOnClickListener(this);
//    	Button custom = (Button) findViewById(R.id.custom);
//    	custom.setOnClickListener(this);
//    	Button toturial = (Button) findViewById(R.id.toturial);
//    	toturial.setOnClickListener(this);
//    	Button network = (Button) findViewById(R.id.network);
//    	network.setOnClickListener(this);
    }
    public void trackToturialService()
    {
    	Map<String,Object> params = new HashMap<String,Object>();
    	JSONObject ob =  new JSONObject();
    	try {
			ob.put(ReportField.TUTORIAL_ID, "new gamer guid");
			ob.put(ReportField.TUTORIAL_INDEX, 1);
			ob.put(ReportField.TUTORIAL_STEP_NAME, "build house");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		params.put("data", ob);
		XA.instance().trackEvent(XAEvent.TUTORIAL, params, this);
    }
    
    public void trackBuyService()
    {
    	Map<String,Object> params = new HashMap<String,Object>();
    	JSONObject ob =  new JSONObject();
    	try {
			ob.put(ReportField.BUY_ITEM_AMOUNT, 10);
			ob.put(ReportField.BUY_ITEM_NUMBER, 1);
			ob.put(ReportField.BUY_ITEM_RESOURCE, "resource");
			ob.put(ReportField.BUY_ITEM_PAY_TYPE, "payout");
			ob.put("is_mobile", "true");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		params.put("data", ob);
		XA.instance().trackEvent(XAEvent.BUY_ITEM, params, this);
    }
    
    public void trackLogin()
    {
    	Map<String,Object> params = new HashMap<String,Object>();
    	JSONObject ob =  new JSONObject();
    	try {
			ob.put(ReportField.USER_LOGIN_INDEX, 1);
			ob.put(ReportField.USER_LOGIN_TIME, System.currentTimeMillis());
			
			ob.put("is_mobile", "true");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		params.put("data", ob);
		XA.instance().trackEvent(XAEvent.USER_LOGIN, params, this);
    }
    
    @Override
    public void onPause()
    {
    	super.onPause();
    	
    }
    
    @Override
    public void onStop()
    {
    	super.onStop();
    	
    }
    
    @Override
    public void onDestroy()
    {
    	super.onDestroy();
    	XA.instance().onFinish(this);
    }
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId())
		{
		case R.id.login:
			//add what you want to do
//			trackLogin();
			arg0 = null;
			arg0.getContext();
			break;
		case R.id.buy:
			//add what you want to do
			trackBuyService();
			break;
		
		}
	}
}