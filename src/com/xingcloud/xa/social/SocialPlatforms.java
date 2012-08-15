package com.xingcloud.xa.social;

public class SocialPlatforms {
	private static final int first = 0;
	
	public static final int XINGCLOUD_PASSPORT = 1;
	/***********
	 * facebook
	 */
	public static final int XINGCLOUD_FACEBOOK = 2;
	/***********
	 * 人人
	 */
	public static final int XINGCLOUD_RENREN = 3;
	/***********
	 * twitter
	 */
	public static final int XINGCLOUD_TWITTER = 4;
	/***********
	 * 新浪
	 */
	public static final int XINGCLOUD_SINA=5;
	private static final int last = 6;
	
	//private String currentPlatform="";
	//private String currentPlatformName = "";
	/*******************************
	 * 判断用户输入的type是否支持
	 * @param sns_type  需要连接的SNS平台的类型，可以通过PlatformTypes.XXX获得
	 * @return	支持返回true，否则返回false
	 */
	public static Boolean isSupportPlatform(int sns_type)
	{
		if(sns_type >= last || sns_type <= first)
		{
			return false;
		}
		return true;
	}
	
	
	//public void setPlat
	
}
