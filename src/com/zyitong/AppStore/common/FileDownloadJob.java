package com.zyitong.AppStore.common;

import android.app.Notification;
import android.widget.ProgressBar;

public class FileDownloadJob {
	private String filename;
	private String url;
	private String updateurl;
	private String name;
	private String path;
	//private Notification notification;
	private ProgressBar loadProgress;
	
	private int Id;
	private int typeid;
	private boolean isRun ;
	private int status ; //1׼�����أ�2�����У�3�������,4ʧ��
	
	public ProgressBar getLoadProgress() {
		return loadProgress;
	}
	public void setLoadProgress(ProgressBar loadProgress) {
		this.loadProgress = loadProgress;
	}
	public FileDownloadJob()
	{
		
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUpdateurl() {
		return updateurl;
	}
	public void setUpdateurl(String updateurl) {
		this.updateurl = updateurl;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
		FileOpt opt = new FileOpt();
 		opt.CreateFilePath(path);
	}
	/*public Notification getNotification() {
		return notification;
	}*/
	/*public void setNotification(Notification notification) {
		this.notification = notification;
	}*/
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public int getTypeid() {
		return typeid;
	}
	public void setTypeid(int typeid) {
		this.typeid = typeid;
	}
	public boolean isRun() {
		return isRun;
	}
	public void setRun(boolean isRun) {
		this.isRun = isRun;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
}
