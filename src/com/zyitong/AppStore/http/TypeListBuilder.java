package com.zyitong.AppStore.http;

import org.json.JSONException;
import org.json.JSONObject;


import com.zyitong.AppStore.common.TypeData;

public class TypeListBuilder  extends JSONBuilder<TypeData> {
	@Override
	public TypeData build(JSONObject jsonObject) throws JSONException {
		TypeData itemData = new TypeData();
		String name="";
		int id=0;
		String image="";
		String intro="";
		
		try{
			name = jsonObject.getString(root+"name");
		}catch(Exception e){}
		try{
			intro = jsonObject.getString(root+"intro");
		}catch(Exception e){}
		try{
			image = jsonObject.getString(root+"image");
		}catch(Exception e){}
		try{
			id = jsonObject.getInt(root+"id");
		}catch(Exception e){}
		
		itemData.setId(id);
		itemData.setIntro(intro);
		itemData.setName(name);
		itemData.setImage(image);
		
		return itemData;
	}
}
