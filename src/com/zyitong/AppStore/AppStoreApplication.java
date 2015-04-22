package com.zyitong.AppStore;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

import com.zyitong.AppStore.bean.ItemData;
import com.zyitong.AppStore.dao.CurrentDownloadJobManager;
import com.zyitong.AppStore.dao.DownloadLink;
import com.zyitong.AppStore.dao.InstalledAppDao;
import com.zyitong.AppStore.tools.AppLogger;

public class AppStoreApplication extends Application {
	private static AppStoreApplication instance;
	private static String vebRoot = null;
	private DownloadLink mDownloadLink;
	private static CurrentDownloadJobManager currentDownloadJobManager = null;
	public boolean isNetWorkConnected = true;
	public List<ItemData> itemData = new ArrayList<ItemData>();

	public InstalledAppDao installedAppDao = null;

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
		mDownloadLink = new DownloadLink();
		if (null == installedAppDao) {
			installedAppDao = new InstalledAppDao(this);
		}
		installedAppDao.clearMap();
		installedAppDao.getInstalledAppMap();

		if (null == currentDownloadJobManager) {
			currentDownloadJobManager = new CurrentDownloadJobManager(this);
		}

		instance = this;
	}

	public DownloadLink getDownloadLink() {
		synchronized (this) {
			return mDownloadLink;
		}
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

	public void setAppState(List<ItemData> itemDataList) {

		for (int i = 0; i < itemDataList.size(); i++) {
			String packagename = itemDataList.get(i).getAppInfoBean()
					.getPackagename();
			if (!installedAppDao.isAppExist(packagename)) {

				itemDataList.get(i).setButtonFileflag(ItemData.APP_INSTALL);
				continue;
			}
			int appGrade = getAppGrade(packagename);

			if (itemDataList.get(i).getAppInfoBean().getVersion_num() > appGrade) {
				itemDataList.get(i).setButtonFileflag(ItemData.APP_UPDATE);
				continue;
			} else {
				itemDataList.get(i).setButtonFileflag(ItemData.APP_OPEN);
				continue;
			}

		}

	}

	public void setResumeAppState(List<ItemData> itemDataList) {
		installedAppDao.clearMap();
		installedAppDao.getInstalledAppMap();
		setAppState(itemDataList);
	}

	public int getAppGrade(String packageName) {

		if (installedAppDao.isAppExist(packageName)) {
			PackageInfo info;
			int versionCode = -1;
			try {
				info = getPackageManager().getPackageInfo(packageName, 0);
				versionCode = info.versionCode;

			} catch (NameNotFoundException e) {
				e.printStackTrace();
				return versionCode;
			}
			return versionCode;
		}
		return -1;

	}

	public void clearCache() {
		// stopService();
		mDownloadLink.moveAll();
		currentDownloadJobManager.removeall();
	}

}
