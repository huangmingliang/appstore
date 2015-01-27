package com.zyitong.AppStore.common;

public class CurrentDownloadJob {
	
	private String filename;//文件名
	private int ratio;//当前下载的文件的百分比
	private int filestatus;//当前下载状态
	private long filelength;//当前文件大小
	public long getFilelength() {
		return filelength;
	}
	public void setFilelength(long filelength) {
		this.filelength = filelength;
	}
	public NoticData getData() {
		return data;
	}
	public void setData(NoticData data) {
		this.data = data;
	}
	private NoticData data;//携带的下载信息
	
	
	public int getFilestatus() {
		return filestatus;
	}
	public void setFilestatus(int filestatus) {
		this.filestatus = filestatus;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public int getRatio() {
		return ratio;
	}
	public void setRatio(int ratio) {
		this.ratio = ratio;
	}
	

}
