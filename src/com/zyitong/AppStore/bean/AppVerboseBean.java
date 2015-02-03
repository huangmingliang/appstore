package com.zyitong.AppStore.bean;

import com.google.gson.annotations.SerializedName;

public class AppVerboseBean {
	@SerializedName("fields")
	public FieldsClass fields;
	
	public class FieldsClass{
		@SerializedName("id")
		public String id;
	}

}
