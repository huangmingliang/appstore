package com.zyitong.AppStore.downloadthread;

import android.content.Context;
import android.util.Log;

import com.zyitong.AppStore.AppStoreApplication;
import com.zyitong.AppStore.bean.NoticData;
import com.zyitong.AppStore.dao.DownloadLink;

public class FileDownLoadMonitorThread extends Thread {
	private boolean isRuning = false;
	private int MAXDOWN = 4;// ����ļ�������
	private int MAXTHREADNUM = 4;// ����ÿ���ļ�ʱ��࿪���߳���
	private Context context;

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
				// �߳���ͣ����
				Thread.sleep(2 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}
}