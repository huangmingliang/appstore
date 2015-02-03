package com.zyitong.AppStore.dao;

import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

import com.zyitong.AppStore.bean.AppListBean;


public class AppListDao extends CommonDao{
	private static AppListDao appList = new AppListDao();

	private AppListDao() {
		super();
	}

	public static AppListDao getInstance() {
		return appList;
	}
	
	interface AppListDaoInterface {

		@POST("/{SoftVersion}/Accounts/{accountSid}/clientList")
		AppListBean getClientList(@Path("SoftVersion") String softVersion,
				@Path("accountSid") String accountSid,
				@Query("sig") String sigParameter);
	}

	
}
