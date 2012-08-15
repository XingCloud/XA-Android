package com.xingcloud.analytic.xnative;

public class XCNative {

	   static 
		{
			System.loadLibrary("analyticglue");
		}   
	   public static native int sendReport(String url);
	   public static native int sendPageReport(String url);
	   public static native int sendBaseReport(String url,String timestamp);
	   public static native void initCurl(String init);
}
