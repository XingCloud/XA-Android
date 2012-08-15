package com.xingcloud.analytic.collector;





import java.lang.reflect.Field;

import android.content.Context;
import android.os.Build;

public class Compatibility {

	 public static int getAPILevel() {
	        int apiLevel;
	        try {
	            // This field has been added in Android 1.6
	            final Field SDK_INT = Build.VERSION.class.getField("SDK_INT");
	            apiLevel = SDK_INT.getInt(null);
	        } catch (SecurityException e) {
	            apiLevel = Integer.parseInt(Build.VERSION.SDK);
	        } catch (NoSuchFieldException e) {
	            apiLevel = Integer.parseInt(Build.VERSION.SDK);
	        } catch (IllegalArgumentException e) {
	            apiLevel = Integer.parseInt(Build.VERSION.SDK);
	        } catch (IllegalAccessException e) {
	            apiLevel = Integer.parseInt(Build.VERSION.SDK);
	        }

	        return apiLevel;
	    }
	 

	    public static String getDropBoxServiceName() throws NoSuchFieldException, IllegalAccessException {
	        final Field serviceName = Context.class.getField("DROPBOX_SERVICE");
	        if (serviceName != null) {
	            return (String) serviceName.get(null);
	        }
	        return null;
	    }
}
