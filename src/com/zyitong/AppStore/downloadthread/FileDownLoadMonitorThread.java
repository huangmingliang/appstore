package com.zyitong.AppStore.downloadthread;

import android.content.Context;

import com.zyitong.AppStore.AppStoreApplication;
import com.zyitong.AppStore.bean.FileDownloadJob;
import com.zyitong.AppStore.dao.DownloadLink;
import com.zyitong.AppStore.tools.AppLogger;

public class FileDownLoadMonitorThread extends Thread {
	private boolean isRuning = false;
	public  static final int MAXDOWN = 2;
	public static final int MAXTHREADNUM = 4;

	public FileDownLoadMonitorThread(Context context) {
	}

	public boolean isRuning() {
		return isRuning;

	}

	public void setRuning(boolean Runing) {
		this.isRuning = Runing;
	}

	public void run() {
		while (isRuning) {
			DownloadLink download = AppStoreApplication.getInstance()
					.getDownloadLink();
			int i = 1;
			int downloadingnum = download.getDownloadNum();
			int size = download.getSize();
			AppLogger.d("FileDownLoadMonitorThread"+ "    size=" + size
					+ "\t  downloadingnum=" + downloadingnum);
			
			if (size > 0 && downloadingnum < MAXDOWN) {
				AppLogger.d( "start=" + i);
				FileDownloadJob data = download.getNode();
				if (data != null) {
					new ProgressThread(data, MAXTHREADNUM).start();
					i++;
				}
			}
			try {
				Thread.sleep(1*1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	
}
