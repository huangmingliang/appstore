package com.zyitong.AppStore.http;

import org.json.JSONArray;
import org.json.JSONException;

import com.zyitong.AppStore.AppStoreApplication;
import com.zyitong.AppStore.bean.ItemData;

import com.zyitong.AppStore.http.api.Caller;
import com.zyitong.AppStore.http.api.SearchFunctions;
import com.zyitong.AppStore.loading.WSError;

//GET_API="http://wap.vebclub.com/";//����ͼƬ
//GETIMAGE_API="http://www.vebclub.com/";
public class HttpApiImple {
	private static String GET_API = AppStoreApplication.GET_API;

	private String doGet(String query) throws WSError {
		if (query.startsWith("http://"))
			return Caller.doGet(query);
		return Caller.doGet(GET_API + query);
	}

	public void uploadDownNum(String url) throws JSONException, WSError {
		doGet(url);

	}

	public ItemData[] getListItem(String url) throws JSONException, WSError {

		String jsonString = doGet(url);
		if (jsonString == null)
			return null;
		JSONArray jsonArray = new JSONArray(jsonString);
		System.out.println("HttpApiImple.itemdata = " + jsonString);
		System.out.println("HttpApiImple.itemdata.length = "
				+ jsonArray.length());
		return SearchFunctions.getListItem(jsonArray);

	}

}
