package com.zyitong.AppStore;

import android.app.Application;
import android.content.Intent;

import com.zyitong.AppStore.dao.CurrentDownloadJobManager;
import com.zyitong.AppStore.dao.DownloadLink;
import com.zyitong.AppStore.dao.ImageCache;
import com.zyitong.AppStore.dao.RequestCache;
import com.zyitong.AppStore.http.api.Caller;
import com.zyitong.AppStore.service.DownLoadService;
import com.zyitong.AppStore.tools.AppLogger;

public class AppStoreApplication extends Application {
	public static String TAG = "AppStore";
	// public static String GET_API="http://192.168.1.104:8080/";
	public static String GET_API = "http://wap.vebclub.com/";
	public static String GETIMAGE_API = "http://www.vebclub.com/";
	public static String UserHeader = "v2";
	public static String Version = "v1.0.0";
	public static int perPageNum = 6;
	private static AppStoreApplication instance;
	private static String WeiBoRoot = android.os.Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/AppStore/";
	private ImageCache mImageCache;
	private RequestCache mRequestCache;
	private DownloadLink mDownloadLink;
	private String imei;
	private CurrentDownloadJobManager currentDownloadJobList;
	public boolean isNetWorkConnected = true;

	public String getFilePath() {
		String rootPath = "";
		rootPath = "soft/";
		rootPath = WeiBoRoot + rootPath;
		return rootPath;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public static AppStoreApplication getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		AppLogger.e("=======AppStoreApplication onCreate=======");
		super.onCreate();
		mImageCache = new ImageCache();
		mRequestCache = new RequestCache();
		Caller.setRequestCache(mRequestCache);
		mDownloadLink = new DownloadLink();
		currentDownloadJobList = new CurrentDownloadJobManager(this);
		instance = this;
		startService();
	}

	public ImageCache getImageCache() {
		return mImageCache;
	}

	public DownloadLink getDownloadLink() {
		synchronized (mDownloadLink) {
			return mDownloadLink;
		}
	}

	public CurrentDownloadJobManager getCurrentDownloadJobManager() {
		synchronized (currentDownloadJobList) {
			return currentDownloadJobList;
		}
	}

	public String getFileName(String filename) {
		int index = filename.lastIndexOf("/");
		String substr = filename;
		if (index > 0) {
			substr = filename.substring(index + 1);
		}
		return substr;
	}

	private void startService() {
		this.startService(new Intent(this, DownLoadService.class));
	}

	public void stopService() {
		this.stopService(new Intent(this, DownLoadService.class));
	}

	public void clearCache() {
		//stopService();
		mImageCache.clear();
		mRequestCache.clear();
		mDownloadLink.moveAll();
	}

}
