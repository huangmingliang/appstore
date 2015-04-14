package com.zyitong.AppStore.dao;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


import android.content.Context;

import com.zyitong.AppStore.AppStoreApplication;
import com.zyitong.AppStore.bean.CurrentDownloadJob;
import com.zyitong.AppStore.bean.FileDownloadJob;
import com.zyitong.AppStore.bean.ItemData;
import com.zyitong.AppStore.tools.UtilFun;

public class CurrentDownloadJobManager {

	private Map<String, CurrentDownloadJob> currentDownJobs = null;
	private UtilFun util = new UtilFun();

	public CurrentDownloadJobManager(Context context) {
		currentDownJobs = new HashMap<String, CurrentDownloadJob>();
	}

	public int getStatus(String packagename) {

		if (currentDownJobs.containsKey(packagename))
			return currentDownJobs.get(packagename).getFilestatus();
		else
			return -1;

	}

	public int getRatio(String packagename) {
		if (currentDownJobs.containsKey(packagename))
			return currentDownJobs.get(packagename).getRatio();
		else
			return -1;

	}

	public void addDownloadJob(CurrentDownloadJob currentDownloadJob) {

		if (currentDownloadJob != null) {
			currentDownJobs.put(currentDownloadJob.getPackageName(),
					currentDownloadJob);
		}
	}
	
	public void updateDownloadJob(String packageName, int ratio,
			int status, FileDownloadJob notic){
		currentDownJobs.get(packageName).setRatio(ratio);
		currentDownJobs.get(packageName).setFilestatus(status);
		currentDownJobs.get(packageName).setData(notic);
		
	}

	public void removeDownloadJob(String packagename) {

		if (currentDownJobs.containsKey(packagename))
			currentDownJobs.remove(packagename);

	}

	public int downloadjobnum() {

		return currentDownJobs.size();

	}

	public void addJobToDownloadLink() {
		Iterator<Map.Entry<String, CurrentDownloadJob>> iter = currentDownJobs.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, CurrentDownloadJob> entry = iter.next();
			CurrentDownloadJob temp = (CurrentDownloadJob) entry.getValue();
			if (temp.getFilestatus() == ItemData.APP_NETWORKEX) {
				FileDownloadJob notic = temp.getData();
				int id = notic.getId();
				if (notic != null
						&& !AppStoreApplication.getInstance().getDownloadLink()
								.findNode(id)) {
					notic.setStatus(0);
					AppStoreApplication.getInstance().getDownloadLink()
							.addNode(notic);
				}
			}
		}

	}

	public void completeCurrentDownLoadInfo(ItemData itemData) {
		int length = downloadjobnum();
		for (int i = 0; i < length; i++) {
			String packagename = itemData.getAppInfoBean().getPackagename();
			if (currentDownJobs.containsKey(packagename)) {
				currentDownJobs.get(packagename).setRatio(0);
				currentDownJobs.get(packagename).setFilestatus(
						ItemData.APP_NETWORKEX);
				FileDownloadJob data = util.DataChange(itemData);
				currentDownJobs.get(packagename).setData(data);
			}
		}
	}

	public boolean isAppReDownload(String packagename) {
		boolean isappredownload = false;
		if (currentDownJobs.containsKey(packagename)) {
			if (currentDownJobs.get(packagename).getFilestatus() == ItemData.APP_REDOWNLOAD) {
				isappredownload = true;
			}

			else
				isappredownload = false;
		} else
			isappredownload = false;
		return isappredownload;
	}

	public void setStatus(String packagename, int status) {
		if (currentDownJobs.containsKey(packagename)) {
			currentDownJobs.get(packagename).setFilestatus(status);
		}
	}

	public void setAppReDownload(String packagename) {

		if (currentDownJobs.containsKey(packagename)) {
			currentDownJobs.get(packagename).setFilestatus(
					ItemData.APP_REDOWNLOAD);
		}
	}

	public void removeall() {
		currentDownJobs.clear();
	}
	
	public boolean isCurrJobExist(String packageName){
		if(currentDownJobs.containsKey(packageName)){
			return true;
		}
		return false;
	}
}
