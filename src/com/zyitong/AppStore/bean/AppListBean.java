package com.zyitong.AppStore.bean;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class AppListBean {
	@SerializedName("status")
	public String status;

	@SerializedName("result")
	public ResultClass result;
	
	@SerializedName("errors")
	public List<ErrorClass> errors;
	
	public class ResultClass {
		@SerializedName("searchtime")
		public String searchtime;

		@SerializedName("total")
		public String total;

		@SerializedName("num")
		public String num;

		@SerializedName("viewtotal")
		public String viewtotal;

		@SerializedName("items")
		public ArrayList<AppVerbaseBean> items;
	}
	
	public class ErrorClass{
		@SerializedName("code")
		public String code;
		
		@SerializedName("message")
		public String message;
	}
}
