package com.zyitong.AppStore.downloadthread;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.conn.ConnectTimeoutException;

import android.content.Context;
import android.util.Log;

import com.zyitong.AppStore.AppStoreApplication;
import com.zyitong.AppStore.bean.FileDownloadJob;
import com.zyitong.AppStore.bean.ItemData;
import com.zyitong.AppStore.bean.NoticData;
import com.zyitong.AppStore.tools.FileOpt;
import com.zyitong.AppStore.tools.UtilFun;

public class ProgressThread extends Thread {

	private static final int REQUEST_TIMEROUT = 30 * 1000;
	private int blockSize, downloadSizeMore;
	private int threadNum = 5, progress = 0;
	private String urlStr, filename;
	private long prevsize = 0, downloadedSize = 0;
	private FileDownloadJob dldata;
	private int fileSize = 0;
	private FileOpt opt = new FileOpt();
	private Context context;
	private UtilFun util;
	private String appName;
	private NoticData noticData;
	boolean finished = false;

	public ProgressThread(NoticData noticData, int threadNum, Context context) {
		this.context = context;
		this.noticData = noticData;
		dldata = noticData.getFileDownloadJob();

		this.urlStr = dldata.getUrl();
		this.threadNum = threadNum;
		this.filename = dldata.getFilename();

		dldata.setStatus(1);
		dldata.setRun(true);
		util = new UtilFun();
		this.appName = util.getFileName(filename);

	}

	@Override
	public void run() {
		FileDownloadThread[] fds = new FileDownloadThread[threadNum];
		AppStoreApplication.getInstance().getDownloadLink().getSize();
		try {
			Log.d("ProgressThread", "ProgressThread is Runing");

			if (util.getUninatllApkInfo(context, filename)) {
				opt.deleteFile(filename);
			}

			URL url = new URL(urlStr);
			URLConnection conn = null;

			try {
				conn = url.openConnection();
				conn.setConnectTimeout(REQUEST_TIMEROUT);
				fileSize = conn.getContentLength();
				blockSize = fileSize / threadNum;
				downloadSizeMore = (fileSize % threadNum);
				File file = new File(filename);
				for (int i = 0; i < threadNum; i++) {
					int endpos = (i + 1) * blockSize - 1;
					if (i == (threadNum - 1)) {
						endpos += downloadSizeMore;
					}
					FileDownloadThread fdt = new FileDownloadThread(url, file,
							i * blockSize, endpos);
					fdt.start();
					fds[i] = fdt;
				}

			} catch (ConnectTimeoutException e) {
				util.addCurrentDownloadJob(appName, progress,
						ItemData.APP_NETWORKEX, -1, noticData);
				util.DowloadComplete(dldata);
				dldata.setStatus(4);
				dldata.setRun(false);
				finished = true;
			} catch (Exception e) {
				util.addCurrentDownloadJob(appName, progress,
						ItemData.APP_NETWORKEX, -1, noticData);
				util.DowloadComplete(dldata);
				dldata.setStatus(4);
				dldata.setRun(false);
				finished = true;
			}

			while (!finished) {
				try {
					downloadedSize = 0;
					// downloadedSize = downloadSizeMore;
					finished = true;
					for (int i = 0; i < fds.length; i++) {
						downloadedSize += fds[i].getDownloadSize();
						if (!fds[i].isFinished()) {
							finished = false;
						}
					}

					if (((downloadedSize - prevsize) * 100 / fileSize >= threadNum)
							|| (downloadedSize == fileSize && fileSize > 0)) {
						progress = (int) ((downloadedSize * 100) / fileSize);
						prevsize = downloadedSize;

					}

					if (!dldata.isRun()) {
						for (int i = 0; i < fds.length; i++) {
							fds[i].setFinished(true);
						}
						util.addCurrentDownloadJob(appName, progress,
								ItemData.APP_FAIL, -1, noticData);
						util.DowloadComplete(dldata);
						finished = true;
						dldata.setStatus(4);
						dldata.setRun(false);
					}
					sleep(300);
					if (progress != 100) {
						if (!finished) {
							util.addCurrentDownloadJob(appName, progress,
									ItemData.APP_LOADING, fileSize, noticData);
						}

					}
					if (progress > 90) {
						if (util.getUninatllApkInfo(context, filename))
							progress = 100;

					}
					if (progress == 100) {
						String result = util.install(filename);
						if (result.equals("Success")) {
							util.addCurrentDownloadJob(appName, progress,
									ItemData.APP_INSTALED, -1, noticData);
						} else {
							util.addCurrentDownloadJob(appName, progress,
									ItemData.APP_FAIL, -1, noticData);

						}
						util.DowloadComplete(dldata);
						Log.e("ProgressThread install result = ", result);
					}

				} catch (Exception e) {
					util.addCurrentDownloadJob(appName, progress,
							ItemData.APP_NETWORKEX, -1, noticData);
					util.DowloadComplete(dldata);
					dldata.setStatus(4);
					dldata.setRun(false);
				}

			}
		} catch (Exception e) {
			util.addCurrentDownloadJob(appName, progress,
					ItemData.APP_NETWORKEX, -1, noticData);
			util.DowloadComplete(dldata);
			dldata.setStatus(4);
			dldata.setRun(false);
		}
	}

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

				con.setRequestProperty("Range", "bytes=" + startPosition + "-"
						+ endPosition);

				fos = new RandomAccessFile(file, "rw");
				fos.seek(startPosition);
				bis = new BufferedInputStream(con.getInputStream());
				while (curPosition < endPosition && !innerfinished) {
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
			} catch (IOException e) {
				e.printStackTrace();
				util.addCurrentDownloadJob(appName, progress,
						ItemData.APP_NETWORKEX, -1, noticData);
				util.DowloadComplete(dldata);
				dldata.setStatus(4);
				dldata.setRun(false);
				finished = true;
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
}