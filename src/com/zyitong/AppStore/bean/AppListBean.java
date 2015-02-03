package com.zyitong.AppStore.bean;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class AppListBean {
	@SerializedName("status")
	public String status;
	
	@SerializedName("result")
	public ResultClass result;
	
	public class ResultClass{
		@SerializedName("searchtime")
		public String searchtime;
		
		@SerializedName("total")
		public String total;
		
		@SerializedName("num")
		public String num;
		
		@SerializedName("viewtotal")
		public String viewtotal;
		
		@SerializedName("items")
		private List<AppVerboseBean> items;
		
	}
}
