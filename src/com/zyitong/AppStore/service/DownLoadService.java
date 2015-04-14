package com.zyitong.AppStore.service;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.zyitong.AppStore.AppStoreApplication;
import com.zyitong.AppStore.downloadthread.FileDownLoadMonitorThread;
import com.zyitong.AppStore.tools.AppLogger;

public class DownLoadService extends Service {

	private FileDownLoadMonitorThread fileThread = null;
	private Timer timer;
	private TimerTask updateDownloadListTask;
	private int DELAY_TIME = 2 * 1000;
	private int PERIOD_TIME = 3 * 1000;

	@Override
	public void onCreate() {
		AppLogger.d("DownLoadNewService START");
		fileThread = new FileDownLoadMonitorThread(this);
		init();

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		if (fileThread.isRuning() == false) {
			AppLogger.d("fileThread is Runing");
			fileThread.setRuning(true);
			fileThread.start();
		}
	}

	@Override
	public void onDestroy() {
		fileThread.setRuning(false);
		timer.cancel();
		updateDownloadListTask.cancel();
		AppLogger.e("===DownLoadService onDestroy===");
	}

	private void init() {
		updateDownloadListTask = new TimerTask() {

			@Override
			public void run() {
				if (AppStoreApplication.getInstance().isNetWorkConnected) {
					AppStoreApplication.getInstance()
							.getCurrentDownloadJobManager()
							.addJobToDownloadLink();
				}

			}
		};
		timer = new Timer(true);
		timer.schedule(updateDownloadListTask, DELAY_TIME, PERIOD_TIME);
	}

}
