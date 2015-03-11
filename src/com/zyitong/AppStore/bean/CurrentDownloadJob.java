package com.zyitong.AppStore.bean;

public class CurrentDownloadJob {
	
	private String packagename;
	private int ratio;
	private int filestatus;
	private FileDownloadJob data;
	public FileDownloadJob getData() {
		return data;
	}
	public void setData(FileDownloadJob data) {
		this.data = data;
	}
	
	public int getFilestatus() {
		return filestatus;
	}
	public void setFilestatus(int filestatus) {
		this.filestatus = filestatus;
	}
	public String getPackageName() {
		return packagename;
	}
	public void setPackageName(String packagename) {
		this.packagename = packagename;
	}
	public int getRatio() {
		return ratio;
	}
	public void setRatio(int ratio) {
		this.ratio = ratio;
	}
	

}
