package com.zyitong.AppStore.http.api;

import org.json.JSONArray;
import org.json.JSONException;

import com.zyitong.AppStore.bean.ItemData;
import com.zyitong.AppStore.http.ItemListBuilder;

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
}
