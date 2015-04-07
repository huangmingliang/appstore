package com.zyitong.AppStore;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.content.Intent;

import com.zyitong.AppStore.bean.ItemData;
import com.zyitong.AppStore.dao.CurrentDownloadJobManager;
import com.zyitong.AppStore.dao.DownloadLink;
import com.zyitong.AppStore.service.DownLoadService;
import com.zyitong.AppStore.tools.AppLogger;

public class AppStoreApplication extends Application {
	private static AppStoreApplication instance;
	private static String vebRoot = null;
	private DownloadLink mDownloadLink;
	private static CurrentDownloadJobManager currentDownloadJobManager = null;
	public boolean isNetWorkConnected = true;
	public List<ItemData> itemData = new ArrayList<ItemData>();

	public String getFilePath() {
		String rootPath = "";
		rootPath = "soft/";
		rootPath = vebRoot + rootPath;
		return rootPath;
	}

	public static AppStoreApplication getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		AppLogger.e("==== AppStoreApplication onCreate ====");
		if (null == vebRoot) {
			vebRoot = android.os.Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/AppStore/";
		}
		/*CrashHandler handler = CrashHandler.getInstance();
        handler.init(getApplicationContext());*/
		mDownloadLink = new DownloadLink();
		if (null == currentDownloadJobManager) {
			currentDownloadJobManager = new CurrentDownloadJobManager(this);
		}
		
		instance = this;
		startService();
	}

	public DownloadLink getDownloadLink() {
		return mDownloadLink;
	}

	public CurrentDownloadJobManager getCurrentDownloadJobManager() {
		return currentDownloadJobManager;
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
		// stopService();
		mDownloadLink.moveAll();
		currentDownloadJobManager.removeall();
	}

}
