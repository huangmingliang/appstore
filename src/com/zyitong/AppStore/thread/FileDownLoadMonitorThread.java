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
	private int MAXDOWN = 4;// ����ļ�������
	private int MAXTHREADNUM = 4;// ����ÿ���ļ�ʱ��࿪���߳���
	private Context context;
	//��ʱ����ÿ��5���ѯ�����б����Ƿ�����Ϊ�쳣�ж����ص�app������У���������������\
	

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
				//�߳���ͣ����
				Thread.sleep(2 * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}

		}
	}
}
