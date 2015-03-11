package com.zyitong.AppStore.dao;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.zyitong.AppStore.AppStoreApplication;
import com.zyitong.AppStore.bean.CurrentDownloadJob;
import com.zyitong.AppStore.bean.FileDownloadJob;
import com.zyitong.AppStore.bean.ItemData;
import com.zyitong.AppStore.tools.UtilFun;

public class CurrentDownloadJobManager {

	private Map<String, CurrentDownloadJob> currentDownJobs = null;
	private Context context;
	private String AppStoreRoot = android.os.Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/AppStore/soft";
	private UtilFun util = new UtilFun();

	public CurrentDownloadJobManager(Context context) {
		this.context = context;
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

	public void removeDownloadJob(String packagename) {

		if (currentDownJobs.containsKey(packagename))
			currentDownJobs.remove(packagename);

	}

	public int downloadjobnum() {

		return currentDownJobs.size();

	}

	/*public void initDownloadjob() {
		File mfile = new File(AppStoreRoot);
		File files[] = mfile.listFiles();

		for (int i = 0; i < files.length; i++) {
			if (!util.getUninatllApkInfo(context, files[i].getPath())) {
				String filename = util.getFileName(files[i].getPath());
				String packagename = util.getPackageName(files[i].getPath(),
						context);
				if (packagename != null) {
					Log.e("CurrentDownloadManager filename = ", filename);
					CurrentDownloadJob currentDownloadJob = new CurrentDownloadJob();
					currentDownloadJob.setPackageName(packagename);
					currentDownloadJob.setFilestatus(ItemData.APP_READ);
					currentDownloadJob.setRatio(-2);
					addDownloadJob(currentDownloadJob);
				}
			}
		}
	}*/

	public void addJobToDownloadLink() {
		Iterator iter = currentDownJobs.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
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
	
	public void setStatus(String packagename,int status){
		if(currentDownJobs.containsKey(packagename)){
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
}
