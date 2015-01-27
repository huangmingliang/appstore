package com.zyitong.AppStore.thread;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.zyitong.AppStore.AppStoreApplication;
import com.zyitong.AppStore.common.DownloadLink;
import com.zyitong.AppStore.common.ItemData;
import com.zyitong.AppStore.common.NoticData;
import com.zyitong.AppStore.util.UtilFun;

public class FileDownLoadMonitorThread extends Thread {
	private boolean isRuning = false;
	private int MAXDOWN = 4;// 最多文件下载数
	private int MAXTHREADNUM = 4;// 下载每个文件时最多开的线程数
	private Context context;
	//定时器，每隔5秒查询下载列表中是否有因为异常中断下载的app，如果有，就让它重新下载\
	

	public FileDownLoadMonitorThread(Context context) {
		this.context = context;
		
	}
	
	

	public boolean isRuning() {
		return isRuning;

	}

	public void setRuning(boolean Runing) {
		this.isRuning = Runing;
	}

	public void run() {
		DownloadLink download = AppStoreApplication.getInstance()
				.getDownloadLink();
		while (isRuning) {
			int i = 1;
			try {
				int downloadingnum = download.getDownloadNum();
				int size = download.getSize();
				Log.d("FileDownLoadMonitorThread", "size=" + size
						+ "\tdownloadingnum=" + downloadingnum);
				if (size > 0 && downloadingnum < MAXDOWN) {
					Log.d("FileDownLoadMonitorThread", "start=" + i);
					NoticData data = download.getNode();
					if (data != null) {
						new ProgressThread(data, MAXTHREADNUM, context).start();
						i++;
					}
				}
				//线程暂停两秒
				Thread.sleep(2 * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}

		}
	}
}
