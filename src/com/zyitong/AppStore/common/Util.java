package com.zyitong.AppStore.common;

public class Util {
	public static String getForMartSize(int size,int num)
	{
		double data =size/(1024*1024.00);
		 String result = String .format("%."+num+"f",data);
		 result = result+"M";
		 return result;
	}
	public static String getDownloadNum(int num)
	{
		 int data = num/10000;
		 String result = "";
		 if(data>0)
		 {
			 result = data+"万次下载";
		 }else
		 {
			 result = num%10000+"次下载";
		 }
		
		 return result;
	}
	public static String getPlotSize(int size,int num)
	{
		double data =size/(100.00);
		 String result = String .format("%."+num+"f",data);
		 return result;
	}
	
	public static String getContext(String text,int num)
	{
		int len = text.length();
		if(len>18)
		{
			text = text.substring(0,16)+"...";
		}
		return text;
	}
}
