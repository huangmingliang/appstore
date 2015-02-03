package com.zyitong.AppStore.service;

import java.util.Timer;
import java.util.TimerTask;
import com.zyitong.AppStore.AppStoreApplication;
import com.zyitong.AppStore.downloadthread.FileDownLoadMonitorThread;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class DownLoadService extends Service {

	private FileDownLoadMonitorThread fileThread = null;
	private Timer timer;
	private TimerTask updateDownloadListTask;
	private int DELAY_TIME = 2 * 1000;
	private int PERIOD_TIME = 3 * 1000;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		Log.d("DownLoadNewService", "DownLoadNewService START");
		fileThread = new FileDownLoadMonitorThread(this);
		/*
		 * IntentFilter intentFilter = new IntentFilter();
		 * intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		 * registerReceiver(connectionReceiver, intentFilter);
		 */
		init();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		if (fileThread.isRuning() == false) {
			Log.d("DownLoadNewService", "fileThread is Runing");
			fileThread.setRuning(true);
			fileThread.start();
		}
	}

	@Override
	public void onDestroy() {
		fileThread.setRuning(false);
		timer.cancel();
		updateDownloadListTask.cancel();
		// unregisterReceiver(connectionReceiver);
	}

	private void init() {
		updateDownloadListTask = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				// Log.e("DownLoadNewService", "查询下载队列，看是否有符合要求的下载队列");
				// 如果当前有网络连接
				if (AppStoreApplication.getInstance().isNetWorkConnected) {
					// 查询下载列表中是否有因为异常中断下载的app，如果有，就把它放在下载队列中重新下载
					AppStoreApplication.getInstance()
							.getCurrentDownloadJobManager()
							.addJobToDownloadLink();
					// AppStoreApplication.getInstance().getCurrentDownloadJobManager().initDownloadjob();
				}

			}
		};
		timer = new Timer(true);
		timer.schedule(updateDownloadListTask, DELAY_TIME, PERIOD_TIME);
	}

}
