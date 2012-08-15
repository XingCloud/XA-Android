package com.xingcloud.xa.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URLConnection;

import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import org.apache.http.util.ByteArrayBuffer;

//import com.xingcloud.analytic.CloudAnalytic;

import android.os.Message;
import android.os.SystemClock;
import android.util.Log;




public class DownloadTester {

	public static  String TAG="XingCloud";
	public static  String hostListUrl="http://119.254.82.71/fronttest.txt";
	public static  String downloadUrl ="";// "http://119.254.82.71/fronttestfile/10k.jpg"
	public static  String uploadUrl = "http://119.254.82.71/fronttestfile";
	public static String fileUrl="/sdcard/speedtest.rtf";
	private static DownloadTester _instantce;
	double downloadSpeed = -1;
	double uploadSpeed = -1;
	private ArrayList<String> hosts;
	
	private static final double BYTE_TO_KILOBIT = 0.0078125;
	
	
//	public double getDownloadSpeed()
//	{
//		return downloadSpeed;
//	}
	public static DownloadTester getInstance()
	{
		if(null == _instantce)
		{
			_instantce = new DownloadTester();
		}
		return _instantce;
	}
	public  void downloadHostList(GetHostListener dListener)
	{
		new GetHostThread(dListener).start();	
	}

	public  void getDownloadSpeed(NetworkListener dListener)
	{
		new DownloadThread(dListener).start();
	}
	
	public void getUploadSpeed(NetworkListener uListener)
	{
		new uploadThread(uListener).start();
	}
	class GetHostThread extends Thread
	{
		GetHostListener listener;
		GetHostThread(GetHostListener nListener)
		{
			listener = nListener;
		}
		@Override
		public void run() { 
            super.run(); 
            if(null == hostListUrl || hostListUrl.trim().length() <=0)
			{
            	listener.onCancel();
				return;
			}
			//StringBuffer sb = new StringBuffer();
			String line = null;
			BufferedReader buffer = null;
			try {
				URL url = new URL(hostListUrl);
				HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();
				buffer = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
				hosts = new ArrayList<String>();
				while( (line = buffer.readLine()) != null){
					//sb.append(line);
					if(line.contains("http://"))
					{
						hosts.add(line);
					}
					
				}
				
			} 
			catch (Exception e) {
				listener.onException(e);
				e.printStackTrace();
			}
			finally{
				try {
					if(null == buffer)
					{
						listener.onCancel();
						return;
					}
					buffer.close();
				} catch (IOException e) {
					listener.onException(e);
					e.printStackTrace();
				}
			}
			int n =hosts.size();
			if(n <=0)
			{
				downloadUrl = null;
				
			}
			Random ran =  new Random();
			int pick =  ran.nextInt(n);
			if(pick < n && pick >=0)
			{
				downloadUrl= hosts.get(pick);
			}
			listener.onComplete();
		}
	}
	class DownloadThread extends Thread
	{
		NetworkListener listener;
		DownloadThread(NetworkListener dListener)
		{
			listener = dListener;
		}
		
		@Override
		public void run() { 
            super.run(); 
            if(hosts == null)
            {
            	listener.onCancel();
				
				return;
            }
            int n =hosts.size();
			if(n <=0)
			{
			
				listener.onCancel();
				downloadUrl = null;
				return;
			}
			Random ran =  new Random();
			int pick =  ran.nextInt(n);
			if(pick < n && pick >=0)
			{
				downloadUrl= hosts.get(pick);
			}
            if(null == downloadUrl || downloadUrl.trim().length() <=0)
			{
            	listener.onCancel();
				return;
			}
			InputStream stream=null;
			try
			{
				
				//String downloadFileUrl="http://www.gregbugaj.com/wp-content/uploads/2009/03/dummy.txt";	
				
				URL url=new URL(downloadUrl);
				URLConnection con=url.openConnection();
				con.setUseCaches(false);
				//long connectionLatency=System.currentTimeMillis()- startCon;
				stream=con.getInputStream();
				
				long time_begin = SystemClock.uptimeMillis();
				int bytesIn=0;
				while((stream.read())!=-1){	
					bytesIn++;
				}
				if(0 == bytesIn)
				{
					listener.onCancel();
					return;
				}
				downloadSpeed = calculate(SystemClock.uptimeMillis()-time_begin,bytesIn);
				String temp =downloadUrl.substring(downloadUrl.indexOf("//")+2, downloadUrl.length());
            	temp = temp.substring(0, temp.indexOf("/"));
            	Log.d(TAG, "download completed");
				listener.onComplete(downloadSpeed,bytesIn,temp);
			}
			catch(Exception e)
			{
				//Log.e("XingCloud", e.getMessage());
				listener.onException(e);
			}
 
        } 
	}
	
	class uploadThread extends Thread
	{
		NetworkListener listener;
		uploadThread(NetworkListener uListener)
		{
			listener = uListener;
		}
		
		@Override
		public void run() { 
            super.run(); 
            try
			 {
            	if(null == uploadUrl || uploadUrl.trim().length() <=0)
            	{
            		listener.onCancel();
            		return;
            	}
            	HttpURLConnection conn = null;
            	DataOutputStream dos = null;
            	DataInputStream inStream = null;
            	String existingFileName =fileUrl;
            	String lineEnd = "\r\n";
            	String twoHyphens = "--";
            	String boundary =  "*****";
            	int bytesRead, bytesAvailable, bufferSize;
            	byte[] buffer;
            	int maxBufferSize = 1*1024*1024;
            	//String responseFromServer = "";
            	String urlString=uploadUrl;

            	try
            	{
            		File f= new File(existingFileName);
            		if(!f.exists())
            		{
            			listener.onCancel();
            			return;
            		}
	            	FileInputStream fileInputStream = new FileInputStream( f);
	            	 // open a URL connection to the Servlet
	            	 URL url = new URL(urlString);
	
	            	 conn = (HttpURLConnection) url.openConnection();
	
	            	 conn.setDoInput(true);
	
	            	 conn.setDoOutput(true);
	
	            	 conn.setUseCaches(false);
	
	            	 conn.setRequestMethod("POST");
	            	 conn.setRequestProperty("Connection", "Keep-Alive");
	            	 conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
	            	 dos = new DataOutputStream( conn.getOutputStream() );
	            	 long time_begin = SystemClock.uptimeMillis();
	            	 dos.writeBytes(twoHyphens + boundary + lineEnd);
	            	 dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + existingFileName + "\"" + lineEnd);
	            	 dos.writeBytes(lineEnd);
	
	            	 bytesAvailable = fileInputStream.available();
	            	 bufferSize = Math.min(bytesAvailable, maxBufferSize);
	            	 buffer = new byte[bufferSize];
	
	            	 bytesRead = fileInputStream.read(buffer, 0, bufferSize);
	            	 while (bytesRead > 0)
	            	 {
	            	  dos.write(buffer, 0, bufferSize);
	            	  bytesAvailable = fileInputStream.available();
	            	  bufferSize = Math.min(bytesAvailable, maxBufferSize);
	            	  bytesRead = fileInputStream.read(buffer, 0, bufferSize);
	            	 }
	
	            	 dos.writeBytes(lineEnd);
	            	 dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
	            	 uploadSpeed = calculate(SystemClock.uptimeMillis()-time_begin,buffer.length+1);
	            	 String temp =uploadUrl.substring(uploadUrl.indexOf("//")+2, uploadUrl.length());
	            	 temp = temp.substring(0, temp.indexOf("/"));
	            	 listener.onComplete(uploadSpeed,buffer.length,temp);
	            	 Log.d(TAG, "upload completed");
	            	 fileInputStream.close();
	            	 dos.flush();
	            	 dos.close();
            	// Toast.makeText(Upload.this, "Files have been uploaded successfully",Toast.LENGTH_SHORT).show();
            	}
            	catch (MalformedURLException ex)
            	{
            		listener.onException(ex);
            	    // Log.e("XingCloud", "error: " + ex.getMessage(), ex);
            	}
            	catch (IOException ioe)
            	{
            		listener.onException(ioe);
            	    // Log.e("XingCloud", "error: " + ioe.getMessage(), ioe);
            	}

	        }
	
	        catch (Exception e) {
	
	            e.printStackTrace();
	            listener.onException(e);
	
	           }

		}
		
	}
	
	private double calculate(final long downloadTime, final long bytesIn){
		//SpeedInfo info=new SpeedInfo();
		//from mil to sec
		if(downloadTime <= 0)
		{
			return 0;
		}
		long bytespersecond   =(bytesIn / downloadTime) * 1000;
		double kilobits=bytespersecond * BYTE_TO_KILOBIT;
		//double megabits=kilobits  * KILOBIT_TO_MEGABIT;
		return kilobits;
	}
	
	

	private final  Runnable mGetHostWorker=new Runnable(){
		
		@Override
		public void run() {
			
			
		}
	};
   
	
	 public  interface NetworkListener {

	        /**
	         * 调用成功后调用，返回结果数据
	         *
	         * 这个后台是在异步执行，不要做UI的更形等操作
	         */
	        public void onComplete(double downloadSpeed,long size,String ip);

	        
	      
	        /********************************
	         * 调用出错时调用，返回错误信息
	         * @param e
	         */
	        public void onException(Exception e);

	        
	        /**********************
	         * Action被取消时调用
	         */
	        public void onCancel();
	        

	    }
	 
	 public  interface GetHostListener {

	        /**
	         * 调用成功后调用，返回结果数据
	         *
	         * 这个后台是在异步执行，不要做UI的更形等操作
	         */
	        public void onComplete();

	        
	      
	        /********************************
	         * 调用出错时调用，返回错误信息
	         * @param e
	         */
	        public void onException(Exception e);

	        
	        /**********************
	         * Action被取消时调用
	         */
	        public void onCancel();
	        

	    }
}


