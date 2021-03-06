package com.zyitong.AppStore.downloadthread;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;

import android.util.Log;

import com.zyitong.AppStore.AppStoreApplication;
import com.zyitong.AppStore.bean.FileDownloadJob;
import com.zyitong.AppStore.bean.ItemData;
import com.zyitong.AppStore.tools.AppLogger;
import com.zyitong.AppStore.tools.FileOpt;
import com.zyitong.AppStore.tools.UtilFun;

/***
 * app下载 线程
 * @author 周双
 *
 * @function 主要实现功能
 *
 */
public class ProgressThread extends Thread {
	/**请求超时时间*/
	private static final int REQUEST_TIMEROUT = 30 * 1000;
	private long blockSize, downloadSizeMore;
	/**支持断点下载     threadNum：断点下载线程数； progress：下载进度*/
	private int threadNum = 5, progress = 0;
	/**
	 * urlStr:下载链接
	 * fileuri：文件存储路径
	 * */
	private String urlStr, fileuri;
	/**已经下载大小*/
	private long downloadedSize = 0;
	private FileDownloadJob dldata;
	/**文件总大小*/
	private long fileSize = 0;
	private FileOpt opt = new FileOpt();

	private UtilFun util;
	/**应用包名*/
	private String packagename;
	FileDownloadJob fdData;
	/**是否下载完成*/
	boolean finished = false;
	
	public ProgressThread(FileDownloadJob fdData, int threadNum) {
		this.fdData = fdData;
		dldata = fdData;
		this.urlStr = dldata.getUrl();
		this.threadNum = threadNum;
		this.fileuri = fdData.getFileuri();
		dldata.setStatus(1);
		dldata.setRun(true);
		util = new UtilFun();
		this.packagename = fdData.getPackageName();
	}
	
	
	@Override
	public void run() {
		FileDownloadThread[] fds = new FileDownloadThread[threadNum];
		AppStoreApplication.getInstance().getDownloadLink().getSize();
		try {
			AppLogger.d( "ProgressThread is Runing");

			File file = new File(fileuri);
			if (file.exists()) {
				opt.deleteFile(fileuri);
			}
			util.addCurrentDownloadJob(packagename, 0, ItemData.APP_LOADING, fdData);
			
			
			///此处可以进行一些网络优化............
			
			URL url = new URL(urlStr);
			URLConnection conn = null;

			try {
				conn = url.openConnection();
				conn.setConnectTimeout(REQUEST_TIMEROUT);
				fileSize = conn.getContentLength();
				blockSize = fileSize / threadNum;
				downloadSizeMore = (fileSize % threadNum);
				for (int i = 0; i < threadNum; i++) {
					int endpos = (int)((i + 1) * blockSize - 1);
					if (i == (threadNum - 1)) {
						endpos += downloadSizeMore;
					}
					FileDownloadThread fdt = new FileDownloadThread(url, file, (int)(i * blockSize), endpos);
					fdt.start();
					fds[i] = fdt;
				}

			} catch (Exception e) {
				stopDownload();
				for (int i = 0; i < fds.length; i++) {
					fds[i].setFinished(true);
				}
				util.addCurrentDownloadJob(packagename, progress,
						ItemData.APP_NETWORKEX, fdData);
				util.DowloadComplete(dldata);
				
				AppLogger.e("=============   ConnectTimeoutException");
			}
			
			
			while (!finished) {
				downloadedSize = 0;
				finished = true;
				for (int i = 0; i < fds.length; i++) {
					downloadedSize += fds[i].getDownloadSize();
					if (!fds[i].isFinished()) {
						finished = false;
					}
				}

				progress = (int) ((downloadedSize * 100) / fileSize);
				
				/*int radio = AppStoreApplication.getInstance().getCurrentDownloadJobManager().getRatio(dldata.getPackageName());
				Log.d("root", "radio: ===== " + radio);*/
				
				if (!dldata.isRun()) {
					for (int i = 0; i < fds.length; i++) {
						fds[i].setFinished(true);
					}
					if(!finished){
						stopDownload();
						util.addCurrentDownloadJob(packagename, progress,
								ItemData.APP_NETWORKEX, fdData);
						util.DowloadComplete(dldata);
						
					}
					
				}

				if (dldata.isRun()) {
					sleep(300);
					boolean isReDownLoad = util.isAppReDownload(packagename);
					if (isReDownLoad) {
						AppLogger.e("ProgressThread" + packagename
								+ "need redownload");
						for (int i = 0; i < fds.length; i++) {
							fds[i].setFinished(true);
						}
						if(!finished){
							stopDownload();
							
							util.addCurrentDownloadJob(packagename, progress, ItemData.APP_INSTALL, fdData);
							util.DowloadComplete(dldata);
						}
						
					} else {
						if (progress != 100) {
							if (!finished) {
								util.addCurrentDownloadJob(packagename, progress, ItemData.APP_LOADING, fdData);
							}
						}
						if (progress == 100) {//下载完成
							finished = true;
							String result = util.install(fileuri);
							result = result.replaceAll("\n", "");
							
							if (result.endsWith("Success")) {
								AppLogger.e("progress == 100 , result = " + result);
								util.addCurrentDownloadJob(packagename, progress, ItemData.APP_OPEN, fdData);
							} else {
								AppLogger.e("progress == 100 , result = " + result);
								util.addCurrentDownloadJob(packagename, progress, ItemData.APP_FAIL, fdData);
							}
							util.DowloadComplete(dldata);
							AppLogger.e("ProgressThread install result = " + result);
							opt.deleteFile(fileuri);//删除文件
						}
					}

				}
			}
		} catch (Exception e) {
			stopDownload();
			for (int i = 0; i < fds.length; i++) {
				fds[i].setFinished(true);
			}
			util.addCurrentDownloadJob(packagename, progress,
					ItemData.APP_NETWORKEX, fdData);
			util.DowloadComplete(dldata);
			AppLogger.e("=============   Exception  33333");
		}
	}

	/***
	 * 当个app多线程下载
	 * @author 周双
	 *
	 * @function 主要实现功能
	 *
	 */
	private class FileDownloadThread extends Thread {
		private static final int BUFFER_SIZE = 1024;
		private URL url;
		private File file;
		private int startPosition;
		private int endPosition;
		private int curPosition;
		private boolean innerfinished = false;
		private int downloadSize = 0;

		public FileDownloadThread(URL url, File file, int startPosition,
				int endPosition) {
			this.url = url;
			this.file = file;
			this.startPosition = startPosition;
			this.curPosition = startPosition;
			this.endPosition = endPosition;
		}

		@Override
		public void run() {
			BufferedInputStream bis = null;
			RandomAccessFile fos = null;
			byte[] buf = new byte[BUFFER_SIZE];
			URLConnection con = null;
			try {
				con = url.openConnection();
				con.setConnectTimeout(REQUEST_TIMEROUT);
				con.setAllowUserInteraction(true);
				con.setRequestProperty("RANGE", "bytes=" + startPosition + "-" + endPosition);
				fos = new RandomAccessFile(file, "rw");
				fos.seek(startPosition);
				bis = new BufferedInputStream(con.getInputStream());
				while (curPosition < endPosition && !innerfinished && !finished ) {
					int len = bis.read(buf, 0, BUFFER_SIZE);
					if (len == -1) {
						break;
					}
					fos.write(buf, 0, len);
					curPosition = curPosition + len;
					if (curPosition > endPosition) {
						downloadSize += len - (curPosition - endPosition) + 1;
					} else {
						downloadSize += len;
					}
				}
				this.innerfinished = true;
				bis.close();
				fos.close();
				
			}catch (Exception e){
				e.printStackTrace();
				stopDownload();
				util.addCurrentDownloadJob(packagename, progress,
						ItemData.APP_NETWORKEX, fdData);
				util.DowloadComplete(dldata);
				AppLogger.e("=============   Exception  66666");
			}
		}

		public void setFinished(boolean finished) {
			this.innerfinished = finished;
		}

		public boolean isFinished() {
			return innerfinished;
		}

		public int getDownloadSize() {
			return downloadSize;
		}
	}
	
	private void stopDownload(){
		finished = true;
		dldata.setStatus(4);
		dldata.setRun(false);
	}
}