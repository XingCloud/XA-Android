package com.xingcloud.xa.report;



import com.xingcloud.xa.utils.Xutils;

import android.app.Activity;


public class ReferenceField {

	public final static String NORMAL_REF_FRAGMENT = "nonads=";
	public final static  String AD_REF_FRAGMENT = "xafrom=";
	
	public static String parseReference(String ref,Activity context)
	{
		String result = "";
		if(ref == null)
		{
			return result;		
		}
	
		String[] refString = ref.split(";");
		if(refString != null && refString.length >= 2)
		{
			String temp = refString[1];
			if(temp != null)
			{
				String[] atstring = temp.split("@");
				if(atstring != null && atstring.length>=2)
				{
					atstring[1] = Xutils.getMetaChanel(context);
				}
				temp =atstring[0]+"@"+atstring[1];
			}
			
			
			refString[1] = temp;
		}
		for(int i=0; i < refString.length;i++)
		{
			if(i != 0 )
			{
				result += ";";
			}
			result += refString[i];
		}	
		result = AD_REF_FRAGMENT+result;
		return result;
	}
	
	public static String getNormalRef(Activity context)
	{
		return ReferenceField.NORMAL_REF_FRAGMENT + Xutils.getMetaChanel(context);
	}
}
