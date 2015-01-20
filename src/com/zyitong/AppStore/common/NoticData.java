package com.zyitong.AppStore.common;

import android.os.Handler;

public class NoticData {
	private FileDownloadJob fileDownloadJob;
	private Handler handler;
	public FileDownloadJob getFileDownloadJob() {
		return fileDownloadJob;
	}
	public void setFileDownloadJob(FileDownloadJob fileDownloadJob) {
		this.fileDownloadJob = fileDownloadJob;
	}
	public Handler getHandler() {
		return handler;
	}
	public void setHandler(Handler handler) {
		this.handler = handler;
	}
}
