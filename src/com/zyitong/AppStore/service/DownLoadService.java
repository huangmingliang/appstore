package com.zyitong.AppStore.service;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.zyitong.AppStore.AppStoreApplication;
import com.zyitong.AppStore.bean.FileDownloadJob;
import com.zyitong.AppStore.dao.DownloadLink;
import com.zyitong.AppStore.downloadthread.ProgressThread;
import com.zyitong.AppStore.tools.AppLogger;
import com.zyitong.AppStore.tools.CommonConstant;

public class DownLoadService extends Service {

	private Timer timer;
	private TimerTask updateDownloadListTask;
	private boolean hasThead = false;
	private ExecutorService executorService = null;

	@Override
	public void onCreate() {
		AppLogger.i("== DownLoadService->onCreate");
		if (null == executorService) {
			executorService = Executors.newCachedThreadPool();
		}

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		AppLogger.i("== DownLoadService->onStartCommand");
		if (null == intent) {
			return super.onStartCommand(intent, flags, startId);
		}

		if (hasThead) {
			return super.onStartCommand(intent, flags, startId);
		}

		if (!AppStoreApplication.getInstance().isNetWorkConnected) {
			return super.onStartCommand(intent, flags, startId);
		}

		AppLogger.e("start fileThread now.");

		hasThead = true;

		Thread filedownloadThread = new Thread() {
			@Override
			public void run() {
				while (hasThead) {
					DownloadLink download = AppStoreApplication.getInstance()
							.getDownloadLink();
					int i = 1;
					int downloadingnum = download.getDownloadNum();

					AppLogger.w("FileDownLoadMonitorThread" + "    size="
							+ download.getSize() + "\t  downloadingnum="
							+ downloadingnum);

					if (download.getSize() > 0
							&& downloadingnum < CommonConstant.MAXDOWN) {
						AppLogger.d("start=" + i);
						FileDownloadJob data = download.getNode();
						if (data != null) {
							new ProgressThread(data,
									CommonConstant.MAXTHREADNUM).start();
							i++;
						}
					}
					/*if(!AppStoreApplication.getInstance().isNetWorkConnected){
						AppStoreApplication.getInstance()
						.getCurrentDownloadJobManager()
						.addJobToDownloadLink();
					}*/
					
					if (download.getSize() > 0) {
						try {
							sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						continue;
					}

					hasThead = false;
				}
			}

		};
		filedownloadThread.start();
		AppLogger.e("fileThread.start() after");

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		timer.cancel();
		updateDownloadListTask.cancel();
		AppLogger.e("===DownLoadService onDestroy===");
	}

}
