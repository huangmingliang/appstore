package com.zyitong.AppStore.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class InstalledAppDao {
	private Context context;
	private Map<String, String> installedAppMap = new HashMap<String, String>();

	public InstalledAppDao(Context context) {
		this.context = context;
		getInstalledAppMap();
	}

	public void getInstalledAppMap() {
		PackageManager packageManager = context.getPackageManager();
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
		List<String> pName = new ArrayList<String>();

		if (null == pinfo) {
			return;
		}

		for (int i = 0; i < pinfo.size(); i++) {
			String pn = pinfo.get(i).packageName;
			pName.add(pn);
			installedAppMap.put(pn, pn);
		}

	}

	public boolean isAppExist(String packageName) {
		if (packageName.equals("")) {
			return false;
		}

		if (installedAppMap.containsKey(packageName)) {
			return true;
		}
		return false;
	}

	public void clearMap() {
		installedAppMap.clear();
	}
	

}
