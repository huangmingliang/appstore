package com.zyitong.AppStore.thread;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.zyitong.AppStore.WeiBoApplication;
import com.zyitong.AppStore.common.DownloadLink;
import com.zyitong.AppStore.common.NoticData;


public class FileDownLoadMonitorThread extends Thread{
	private boolean isRuning =false;
	private int MAXDOWN =4;
	private int MAXTHREADNUM =4;
	private Context context;
	public FileDownLoadMonitorThread(Context context)
	{
		this.context = context;
	}
	public boolean isRuning() {
		return isRuning;
		
		
	}

	public void setRuning(boolean Runing) {
		this.isRuning = Runing;
	}
	public void run() {
		DownloadLink  download = WeiBoApplication.getInstance().getDownloadLink();
		while(isRuning)
		{
			int i=1;
			try {
				int downloadingnum = download.getDownloadNum();
				int size = download.getSize();
				Log.d("FileDownLoadMonitorThread", "size="+size+"\tdownloadingnum="+downloadingnum);
				if(size>0 && downloadingnum<MAXDOWN)
				{
					Log.d("FileDownLoadMonitorThread", "start="+i);
					NoticData data = download.getNode();
					if(data !=null)
					{
						new ProgressThread(data,MAXTHREADNUM,context).start();
						i++;
					}
				}
				Thread.sleep(5*1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
