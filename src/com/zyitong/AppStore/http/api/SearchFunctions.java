package com.zyitong.AppStore.http.api;

import org.json.JSONArray;
import org.json.JSONException;

import com.zyitong.AppStore.common.ItemData;
import com.zyitong.AppStore.common.TypeData;
import com.zyitong.AppStore.common.VersionData;
import com.zyitong.AppStore.http.ItemListBuilder;
import com.zyitong.AppStore.http.TypeListBuilder;
import com.zyitong.AppStore.http.VersionBuilder;



public class SearchFunctions {
	public static ItemData[] getListItem(JSONArray jsonArrayAlbums) throws JSONException {
		int n = jsonArrayAlbums.length();
		ItemData[] itemData = new ItemData[n];
		ItemListBuilder builder = new ItemListBuilder();
		for(int i=0; i < n; i++){
			itemData[i] = builder.build(jsonArrayAlbums.getJSONObject(i));
		}
		
		return itemData;
	}
	
	public static TypeData[] getListType(JSONArray jsonArrayAlbums) throws JSONException {
		int n = jsonArrayAlbums.length();
		TypeData[] itemData = new TypeData[n];
		TypeListBuilder builder = new TypeListBuilder();
		for(int i=0; i < n; i++){
			itemData[i] = builder.build(jsonArrayAlbums.getJSONObject(i));
		}
		
		return itemData;
	}
	public static VersionData getVersionItem(JSONArray jsonArrayAlbums) throws JSONException {
		int n = jsonArrayAlbums.length();
		VersionData[] itemData = new VersionData[n];
		VersionBuilder builder = new VersionBuilder();
		for(int i=0; i < n; i++){
			itemData[i] = builder.build(jsonArrayAlbums.getJSONObject(i));
		}
		
		return itemData[0];
	}
	
}
