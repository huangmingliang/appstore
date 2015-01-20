package com.zyitong.AppStore.http;

import org.json.JSONException;
import org.json.JSONObject;

import com.zyitong.AppStore.common.VersionData;

public class VersionBuilder  extends JSONBuilder<VersionData> {
	@Override
	public VersionData build(JSONObject jsonObject) throws JSONException {
		VersionData itemData = new VersionData();
		String version="";
		String url="";
		String description="";
	
		
		try{
			version = jsonObject.getString(root+"version");
		}catch(Exception e){}
		try{
			url = jsonObject.getString(root+"url");
		}catch(Exception e){}
		try{
			description = jsonObject.getString(root+"description");
		}catch(Exception e){}
		
		itemData.setDescription(description);
		itemData.setUrl(url);
		itemData.setVersion(version);
		return itemData;
	}
}
